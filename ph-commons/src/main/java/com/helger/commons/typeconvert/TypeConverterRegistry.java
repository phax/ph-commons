/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.typeconvert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.collection.multimap.MultiTreeMapArrayListBased;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.commons.state.EContinue;
import com.helger.commons.wrapper.Wrapper;

/**
 * This class contains all the default type converters for the default types
 * that are required. The {@link TypeConverter} class uses this factory for
 * converting objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class TypeConverterRegistry implements ITypeConverterRegistry
{
  private static final class SingletonHolder
  {
    static final TypeConverterRegistry s_aInstance = new TypeConverterRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (TypeConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Use a weak hash map, because the key is a class
  @GuardedBy ("m_aRWLock")
  private final Map <Class <?>, Map <Class <?>, ITypeConverter <?, ?>>> m_aConverter = new WeakHashMap <> ();
  @GuardedBy ("m_aRWLock")
  private final IMultiMapListBased <ITypeConverterRule.ESubType, ITypeConverterRule <?, ?>> m_aRules = new MultiTreeMapArrayListBased <> ();

  private TypeConverterRegistry ()
  {
    _reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static TypeConverterRegistry getInstance ()
  {
    final TypeConverterRegistry ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Nonnull
  @ReturnsMutableObject ("internal use only")
  private Map <Class <?>, ITypeConverter <?, ?>> _getOrCreateConverterMap (@Nonnull final Class <?> aClass)
  {
    Map <Class <?>, ITypeConverter <?, ?>> ret = m_aRWLock.readLocked ( () -> m_aConverter.get (aClass));

    if (ret == null)
    {
      ret = m_aRWLock.writeLocked ( () -> {
        // Try again in write lock
        Map <Class <?>, ITypeConverter <?, ?>> aWLRet = m_aConverter.get (aClass);
        if (aWLRet == null)
        {
          // Weak hash map because key is a class
          aWLRet = new WeakHashMap <> ();
          m_aConverter.put (aClass, aWLRet);
        }
        return aWLRet;
      });
    }
    return ret;
  }

  /**
   * Register a default type converter.
   *
   * @param aSrcClass
   *        A non-<code>null</code> source class to convert from. Must be an
   *        instancable class.
   * @param aDstClass
   *        A non-<code>null</code> destination class to convert to. Must be an
   *        instancable class. May not equal the source class.
   * @param aConverter
   *        The convert to use. May not be <code>null</code>.
   */
  private void _registerTypeConverter (@Nonnull final Class <?> aSrcClass,
                                       @Nonnull final Class <?> aDstClass,
                                       @Nonnull final ITypeConverter <?, ?> aConverter)
  {
    ValueEnforcer.notNull (aSrcClass, "SrcClass");
    if (!ClassHelper.isPublic (aSrcClass))
      throw new IllegalArgumentException ("Source " + aSrcClass + " is no public class!");
    ValueEnforcer.notNull (aDstClass, "DstClass");
    if (!ClassHelper.isPublic (aDstClass))
      throw new IllegalArgumentException ("Destination " + aDstClass + " is no public class!");
    if (aSrcClass.equals (aDstClass))
      throw new IllegalArgumentException ("Source and destination class are equal and therefore no converter is required.");
    ValueEnforcer.notNull (aConverter, "Converter");
    if (aConverter instanceof ITypeConverterRule)
      throw new IllegalArgumentException ("Type converter rules must be registered via registerTypeConverterRule");
    if (ClassHelper.areConvertibleClasses (aSrcClass, aDstClass))
      s_aLogger.warn ("No type converter needed between " +
                      aSrcClass +
                      " and " +
                      aDstClass +
                      " because types are convertible!");

    // The main class should not already be registered
    final Map <Class <?>, ITypeConverter <?, ?>> aSrcMap = _getOrCreateConverterMap (aSrcClass);
    if (aSrcMap.containsKey (aDstClass))
      throw new IllegalArgumentException ("A mapping from " + aSrcClass + " to " + aDstClass + " is already defined!");

    m_aRWLock.writeLocked ( () -> {
      // Automatically register the destination class, and all parent
      // classes/interfaces
      for (final WeakReference <Class <?>> aCurWRDstClass : ClassHierarchyCache.getClassHierarchyIterator (aDstClass))
      {
        final Class <?> aCurDstClass = aCurWRDstClass.get ();
        if (aCurDstClass != null)
          if (!aSrcMap.containsKey (aCurDstClass))
          {
            if (aSrcMap.put (aCurDstClass, aConverter) != null)
              s_aLogger.warn ("Overwriting converter from " + aSrcClass + " to " + aCurDstClass);
            else
              if (s_aLogger.isTraceEnabled ())
                s_aLogger.trace ("Registered type converter from '" +
                                 aSrcClass.toString () +
                                 "' to '" +
                                 aCurDstClass.toString () +
                                 "'");
          }
      }
    });
  }

  public <SRC, DST> void registerTypeConverter (@Nonnull final Class <SRC> aSrcClass,
                                                @Nonnull final Class <DST> aDstClass,
                                                @Nonnull final ITypeConverter <SRC, DST> aConverter)
  {
    _registerTypeConverter (aSrcClass, aDstClass, aConverter);
  }

  public <DST> void registerTypeConverter (@Nonnull final Class <?> [] aSrcClasses,
                                           @Nonnull final Class <DST> aDstClass,
                                           @Nonnull final ITypeConverter <?, DST> aConverter)
  {
    for (final Class <?> aSrcClass : aSrcClasses)
      _registerTypeConverter (aSrcClass, aDstClass, aConverter);
  }

  /**
   * Get the converter that can convert objects from aSrcClass to aDstClass.
   * Thereby no fuzzy logic is applied.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter
   *         object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getExactConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    return m_aRWLock.readLocked ( () -> {
      final Map <Class <?>, ITypeConverter <?, ?>> aConverterMap = m_aConverter.get (aSrcClass);
      return aConverterMap == null ? null : aConverterMap.get (aDstClass);
    });
  }

  /**
   * Get the converter that can convert objects from aSrcClass to aDstClass
   * using the registered rules. The first match is returned.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter
   *         object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getRuleBasedConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    if (aSrcClass == null || aDstClass == null)
      return null;

    return m_aRWLock.readLocked ( () -> {
      // Check all rules in the correct order
      for (final Map.Entry <ITypeConverterRule.ESubType, List <ITypeConverterRule <?, ?>>> aEntry : m_aRules.entrySet ())
        for (final ITypeConverterRule <?, ?> aRule : aEntry.getValue ())
          if (aRule.canConvert (aSrcClass, aDstClass))
            return aRule;

      return null;
    });
  }

  /**
   * Iterate all possible fuzzy converters from source class to destination
   * class.
   *
   * @param aSrcClass
   *        Source class.
   * @param aDstClass
   *        Destination class.
   * @param aCallback
   *        The callback to be invoked once a converter was found. Must return
   *        either {@link EContinue#CONTINUE} to continue iteration or
   *        {@link EContinue#BREAK} to break iteration at the current position.
   */
  private void _iterateFuzzyConverters (@Nonnull final Class <?> aSrcClass,
                                        @Nonnull final Class <?> aDstClass,
                                        @Nonnull final ITypeConverterCallback aCallback)
  {
    // For all possible source classes
    for (final WeakReference <Class <?>> aCurWRSrcClass : ClassHierarchyCache.getClassHierarchyIterator (aSrcClass))
    {
      final Class <?> aCurSrcClass = aCurWRSrcClass.get ();
      if (aCurSrcClass != null)
      {
        // Do we have a source converter?
        final Map <Class <?>, ITypeConverter <?, ?>> aConverterMap = m_aConverter.get (aCurSrcClass);
        if (aConverterMap != null)
        {
          // Check explicit destination classes
          final ITypeConverter <?, ?> aConverter = aConverterMap.get (aDstClass);
          if (aConverter != null)
          {
            // We found a match -> invoke the callback!
            if (aCallback.call (aCurSrcClass, aDstClass, aConverter).isBreak ())
              break;
          }
        }
      }
    }
  }

  /**
   * Get the converter that can convert objects from aSrcClass to aDstClass. If
   * no exact match is found, the super-classes and interface of source and
   * destination class are searched for matching type converters. The first
   * match is returned.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter
   *         object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getFuzzyConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    if (aSrcClass == null || aDstClass == null)
      return null;

    return m_aRWLock.readLocked ( () -> {
      if (GlobalDebug.isDebugMode ())
      {
        // Perform a check, whether there is more than one potential converter
        // present!
        final List <String> aAllConverters = new ArrayList <String> ();
        _iterateFuzzyConverters (aSrcClass, aDstClass, (aCurSrcClass, aCurDstClass, aConverter) -> {
          final boolean bExact = aSrcClass.equals (aCurSrcClass) && aDstClass.equals (aCurDstClass);
          aAllConverters.add ("[" + aCurSrcClass.getName () + "->" + aCurDstClass.getName () + "]");
          return bExact ? EContinue.BREAK : EContinue.CONTINUE;
        });
        if (aAllConverters.size () > 1)
          s_aLogger.warn ("The fuzzy type converter resolver returned more than 1 match for the conversion from " +
                          aSrcClass +
                          " to " +
                          aDstClass +
                          ": " +
                          aAllConverters);
      }

      // Iterate and find the first matching type converter
      final Wrapper <ITypeConverter <?, ?>> ret = new Wrapper <> ();
      _iterateFuzzyConverters (aSrcClass, aDstClass, (aCurSrcClass, aCurDstClass, aConverter) -> {
        ret.set (aConverter);
        return EContinue.BREAK;
      });
      return ret.get ();
    });
  }

  /**
   * Iterate all registered type converters. For informational purposes only.
   *
   * @param aCallback
   *        The callback invoked for all iterations.
   */
  public void iterateAllRegisteredTypeConverters (@Nonnull final ITypeConverterCallback aCallback)
  {
    // Create a copy of the map
    final Map <Class <?>, Map <Class <?>, ITypeConverter <?, ?>>> aCopy = m_aRWLock.readLocked ( () -> CollectionHelper.newMap (m_aConverter));

    // And iterate the copy
    outer: for (final Map.Entry <Class <?>, Map <Class <?>, ITypeConverter <?, ?>>> aSrcEntry : aCopy.entrySet ())
    {
      final Class <?> aSrcClass = aSrcEntry.getKey ();
      for (final Map.Entry <Class <?>, ITypeConverter <?, ?>> aDstEntry : aSrcEntry.getValue ().entrySet ())
        if (aCallback.call (aSrcClass, aDstEntry.getKey (), aDstEntry.getValue ()).isBreak ())
          break outer;
    }
  }

  @Nonnegative
  public int getRegisteredTypeConverterCount ()
  {
    return m_aRWLock.readLocked ( () -> {
      int ret = 0;
      for (final Map <Class <?>, ITypeConverter <?, ?>> aMap : m_aConverter.values ())
        ret += aMap.size ();
      return ret;
    });
  }

  public void registerTypeConverterRule (@Nonnull final ITypeConverterRule <?, ?> aTypeConverterRule)
  {
    ValueEnforcer.notNull (aTypeConverterRule, "TypeConverterRule");

    m_aRWLock.writeLocked ( () -> m_aRules.putSingle (aTypeConverterRule.getSubType (), aTypeConverterRule));

    if (s_aLogger.isTraceEnabled ())
      s_aLogger.trace ("Registered type converter rule " +
                       ClassHelper.getClassLocalName (aTypeConverterRule) +
                       " with type " +
                       aTypeConverterRule.getSubType ());
  }

  @Nonnegative
  public long getRegisteredTypeConverterRuleCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aRules.getTotalValueCount ());
  }

  private void _reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aConverter.clear ();
      m_aRules.clear ();

      // Register all custom type converter.
      // Must be in writeLock to ensure no reads happen during initialization
      for (final ITypeConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (ITypeConverterRegistrarSPI.class))
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Calling registerTypeConverter on " + aSPI.getClass ().getName ());
        aSPI.registerTypeConverter (this);
      }
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (getRegisteredTypeConverterCount () +
                       " type converters and " +
                       getRegisteredTypeConverterRuleCount () +
                       " rules registered");
  }

  public void reinitialize ()
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitializing " + getClass ().getName ());

    _reinitialize ();
  }
}
