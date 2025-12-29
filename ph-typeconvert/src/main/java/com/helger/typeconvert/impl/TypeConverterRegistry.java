/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.typeconvert.impl;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.base.state.EContinue;
import com.helger.base.wrapper.Wrapper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.CommonsWeakHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.typeconvert.ITypeConverter;
import com.helger.typeconvert.ITypeConverterCallback;
import com.helger.typeconvert.ITypeConverterRegistrarSPI;
import com.helger.typeconvert.ITypeConverterRegistry;
import com.helger.typeconvert.ITypeConverterRule;
import com.helger.typeconvert.util.ClassHierarchyCache;

/**
 * This class contains all the default type converters for the default types that are required. The
 * {@link TypeConverter} class uses this factory for converting objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class TypeConverterRegistry implements ITypeConverterRegistry
{
  private static final class SingletonHolder
  {
    private static final TypeConverterRegistry INSTANCE = new TypeConverterRegistry ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (TypeConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Use a weak hash map, because the key is a class
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <Class <?>, ICommonsMap <Class <?>, ITypeConverter <?, ?>>> m_aConverter = new CommonsWeakHashMap <> ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsSortedMap <ITypeConverterRule.ESubType, ICommonsList <ITypeConverterRule <?, ?>>> m_aRules = new CommonsTreeMap <> ();

  private TypeConverterRegistry ()
  {
    _reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @NonNull
  public static TypeConverterRegistry getInstance ()
  {
    final TypeConverterRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @NonNull
  @ReturnsMutableObject ("internal use only")
  private ICommonsMap <Class <?>, ITypeConverter <?, ?>> _getOrCreateConverterMap (@NonNull final Class <?> aClass)
  {
    ICommonsMap <Class <?>, ITypeConverter <?, ?>> ret = m_aRWLock.readLockedGet ( () -> m_aConverter.get (aClass));
    if (ret == null)
    {
      // Try again in write lock
      // Weak hash map because key is a class
      ret = m_aRWLock.writeLockedGet ( () -> m_aConverter.computeIfAbsent (aClass, k -> new CommonsWeakHashMap <> ()));
    }
    return ret;
  }

  /**
   * Register a default type converter.
   *
   * @param aSrcClass
   *        A non-<code>null</code> source class to convert from. Must be an instancable class.
   * @param aDstClass
   *        A non-<code>null</code> destination class to convert to. Must be an instancable class.
   *        May not equal the source class.
   * @param aConverter
   *        The convert to use. May not be <code>null</code>.
   */
  private void _registerTypeConverter (@NonNull final Class <?> aSrcClass,
                                       @NonNull final Class <?> aDstClass,
                                       @NonNull final ITypeConverter <?, ?> aConverter)
  {
    ValueEnforcer.notNull (aSrcClass, "SrcClass");
    ValueEnforcer.isTrue (ClassHelper.isPublic (aSrcClass), () -> "Source " + aSrcClass + " is no public class!");
    ValueEnforcer.notNull (aDstClass, "DstClass");
    ValueEnforcer.isTrue (ClassHelper.isPublic (aDstClass), () -> "Destination " + aDstClass + " is no public class!");
    ValueEnforcer.isFalse (aSrcClass.equals (aDstClass),
                           "Source and destination class are equal and therefore no converter is required.");
    ValueEnforcer.notNull (aConverter, "Converter");
    ValueEnforcer.isFalse (aConverter instanceof ITypeConverterRule,
                           "Type converter rules must be registered via registerTypeConverterRule");
    if (ClassHelper.areConvertibleClasses (aSrcClass, aDstClass))
      LOGGER.warn ("No type converter needed between " +
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
            {
              LOGGER.warn ("Overwriting converter from " + aSrcClass + " to " + aCurDstClass);
            }
            else
            {
              if (LOGGER.isTraceEnabled ())
                LOGGER.trace ("Registered type converter from '" +
                              aSrcClass.toString () +
                              "' to '" +
                              aCurDstClass.toString () +
                              "'");
            }
          }
      }
    });
  }

  public <SRC, DST> void registerTypeConverter (@NonNull final Class <SRC> aSrcClass,
                                                @NonNull final Class <DST> aDstClass,
                                                @NonNull final ITypeConverter <SRC, DST> aConverter)
  {
    _registerTypeConverter (aSrcClass, aDstClass, aConverter);
  }

  public <DST> void registerTypeConverter (@NonNull final Class <?> [] aSrcClasses,
                                           @NonNull final Class <DST> aDstClass,
                                           @NonNull final ITypeConverter <?, DST> aConverter)
  {
    for (final Class <?> aSrcClass : aSrcClasses)
      _registerTypeConverter (aSrcClass, aDstClass, aConverter);
  }

  /**
   * Get the converter that can convert objects from aSrcClass to aDstClass. Thereby no fuzzy logic
   * is applied.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getExactConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    return m_aRWLock.readLockedGet ( () -> {
      final Map <Class <?>, ITypeConverter <?, ?>> aConverterMap = m_aConverter.get (aSrcClass);
      return aConverterMap == null ? null : aConverterMap.get (aDstClass);
    });
  }

  /**
   * Get the converter that can convert objects from aSrcClass to aDstClass using the registered
   * rules. The first match is returned.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getRuleBasedConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    if (aSrcClass == null || aDstClass == null)
      return null;

    return m_aRWLock.readLockedGet ( () -> {
      // Check all rules in the correct order
      for (final Map.Entry <ITypeConverterRule.ESubType, ICommonsList <ITypeConverterRule <?, ?>>> aEntry : m_aRules.entrySet ())
        for (final ITypeConverterRule <?, ?> aRule : aEntry.getValue ())
          if (aRule.canConvert (aSrcClass, aDstClass))
            return aRule;

      return null;
    });
  }

  /**
   * Iterate all possible fuzzy converters from source class to destination class.
   *
   * @param aSrcClass
   *        Source class.
   * @param aDstClass
   *        Destination class.
   * @param aCallback
   *        The callback to be invoked once a converter was found. Must return either
   *        {@link EContinue#CONTINUE} to continue iteration or {@link EContinue#BREAK} to break
   *        iteration at the current position.
   */
  private void _iterateFuzzyConverters (@NonNull final Class <?> aSrcClass,
                                        @NonNull final Class <?> aDstClass,
                                        @NonNull final ITypeConverterCallback aCallback)
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
   * Get the converter that can convert objects from aSrcClass to aDstClass. If no exact match is
   * found, the super-classes and interface of source and destination class are searched for
   * matching type converters. The first match is returned.
   *
   * @param aSrcClass
   *        Source class. May not be <code>null</code>.
   * @param aDstClass
   *        Destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter exists, the converter object otherwise.
   */
  @Nullable
  ITypeConverter <?, ?> getFuzzyConverter (@Nullable final Class <?> aSrcClass, @Nullable final Class <?> aDstClass)
  {
    if (aSrcClass == null || aDstClass == null)
      return null;

    return m_aRWLock.readLockedGet ( () -> {
      if (GlobalDebug.isDebugMode ())
      {
        // Perform a check, whether there is more than one potential converter
        // present!
        final ICommonsList <String> aAllConverters = new CommonsArrayList <> ();
        _iterateFuzzyConverters (aSrcClass, aDstClass, (aCurSrcClass, aCurDstClass, aConverter) -> {
          final boolean bExact = aSrcClass.equals (aCurSrcClass) && aDstClass.equals (aCurDstClass);
          aAllConverters.add ("[" + aCurSrcClass.getName () + "->" + aCurDstClass.getName () + "]");
          return bExact ? EContinue.BREAK : EContinue.CONTINUE;
        });
        if (aAllConverters.size () > 1)
          LOGGER.warn ("The fuzzy type converter resolver returned more than 1 match for the conversion from " +
                       aSrcClass +
                       " to " +
                       aDstClass +
                       ": " +
                       aAllConverters);
      }
      // Iterate and find the first matching type converter
      final Wrapper <ITypeConverter <?, ?>> ret = Wrapper.empty ();
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
  public void iterateAllRegisteredTypeConverters (@NonNull final ITypeConverterCallback aCallback)
  {
    // Create a copy of the map
    final Map <Class <?>, Map <Class <?>, ITypeConverter <?, ?>>> aCopy = m_aRWLock.readLockedGet ( () -> new CommonsHashMap <> (m_aConverter));

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
    return m_aRWLock.readLockedInt ( () -> {
      int ret = 0;
      for (final Map <Class <?>, ITypeConverter <?, ?>> aMap : m_aConverter.values ())
        ret += aMap.size ();
      return ret;
    });
  }

  public void registerTypeConverterRule (@NonNull final ITypeConverterRule <?, ?> aTypeConverterRule)
  {
    ValueEnforcer.notNull (aTypeConverterRule, "TypeConverterRule");

    m_aRWLock.writeLockedBoolean ( () -> m_aRules.computeIfAbsent (aTypeConverterRule.getSubType (),
                                                                   x -> new CommonsArrayList <> ())
                                                 .add (aTypeConverterRule));

    if (LOGGER.isTraceEnabled ())
      LOGGER.trace ("Registered type converter rule " +
                    ClassHelper.getClassLocalName (aTypeConverterRule) +
                    " with type " +
                    aTypeConverterRule.getSubType ());
  }

  public <DST> void registerTypeConverterRuleAnySourceFixedDestination (@NonNull final Class <DST> aDstClass,
                                                                        @NonNull final Function <? super Object, ? extends DST> aConverter)
  {
    registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination <> (aDstClass, aConverter));
  }

  public <SRC, DST> void registerTypeConverterRuleAssignableSourceFixedDestination (@NonNull final Class <SRC> aSrcClass,
                                                                                    @NonNull final Class <DST> aDstClass,
                                                                                    @NonNull final Function <? super SRC, ? extends DST> aConverter)
  {
    registerTypeConverterRule (new TypeConverterRuleAssignableSourceFixedDestination <> (aSrcClass,
                                                                                         aDstClass,
                                                                                         aConverter));
  }

  public <SRC> void registerTypeConverterRuleFixedSourceAnyDestination (@NonNull final Class <SRC> aSrcClass,
                                                                        @NonNull final Function <? super SRC, ? extends Object> aInBetweenConverter)
  {
    registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination <> (aSrcClass, aInBetweenConverter));
  }

  public <SRC, DST> void registerTypeConverterRuleFixedSourceAssignableDestination (@NonNull final Class <SRC> aSrcClass,
                                                                                    @NonNull final Class <DST> aDstClass,
                                                                                    @NonNull final Function <? super SRC, ? extends DST> aConverter)
  {
    registerTypeConverterRule (new TypeConverterRuleFixedSourceAssignableDestination <> (aSrcClass,
                                                                                         aDstClass,
                                                                                         aConverter));
  }

  @Nonnegative
  public long getRegisteredTypeConverterRuleCount ()
  {
    return m_aRWLock.readLockedInt ( () -> {
      int ret = 0;
      for (final ICommonsList <?> aValue : m_aRules.values ())
        ret += aValue.size ();
      return ret;
    });
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
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Calling registerTypeConverter on " + aSPI.getClass ().getName ());
        aSPI.registerTypeConverter (this);
      }
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getRegisteredTypeConverterCount () +
                    " type converters and " +
                    getRegisteredTypeConverterRuleCount () +
                    " rules registered");
  }

  public void reinitialize ()
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitializing " + getClass ().getName ());

    _reinitialize ();
  }
}
