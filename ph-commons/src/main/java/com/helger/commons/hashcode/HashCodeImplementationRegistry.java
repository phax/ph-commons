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
package com.helger.commons.hashcode;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.annotation.UseDirectEqualsAndHashCode;
import com.helger.commons.cache.AnnotationUsageCache;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.commons.state.EChange;

/**
 * The main registry for the different {@link IHashCodeImplementation}
 * implementations.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class HashCodeImplementationRegistry implements IHashCodeImplementationRegistry
{
  private static final class ArrayHashCodeImplementation implements IHashCodeImplementation
  {
    public ArrayHashCodeImplementation ()
    {}

    public int getHashCode (final Object aObj)
    {
      final Object [] aArray = (Object []) aObj;
      final int nLength = aArray.length;

      HashCodeGenerator aHC = new HashCodeGenerator (aObj).append (nLength);
      for (int i = 0; i < nLength; i++)
        aHC = aHC.append (aArray[i]);
      return aHC.getHashCode ();
    }
  }

  private static final class SingletonHolder
  {
    static final HashCodeImplementationRegistry s_aInstance = new HashCodeImplementationRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (HashCodeImplementationRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Use a weak hash map, because the key is a class
  @GuardedBy ("m_aRWLock")
  private final Map <Class <?>, IHashCodeImplementation> m_aMap = new WeakHashMap <Class <?>, IHashCodeImplementation> ();

  // Cache for classes where direct implementation should be used
  private final AnnotationUsageCache m_aDirectHashCode = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);

  // Cache for classes that implement hashCode directly
  private final Map <String, Boolean> m_aImplementsHashCode = new HashMap <String, Boolean> ();

  private HashCodeImplementationRegistry ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static HashCodeImplementationRegistry getInstance ()
  {
    final HashCodeImplementationRegistry ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  public void registerHashCodeImplementation (@Nonnull final Class <?> aClass,
                                              @Nonnull final IHashCodeImplementation aImpl)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aImpl, "Implementation");

    if (aClass.equals (Object.class))
      throw new IllegalArgumentException ("You cannot provide a hashCode implementation for Object.class!");

    m_aRWLock.writeLocked ( () -> {
      final IHashCodeImplementation aOldImpl = m_aMap.get (aClass);
      if (aOldImpl == null)
        m_aMap.put (aClass, aImpl);
      else
        if (aOldImpl != aImpl)
        {
          // Avoid the warning when the passed implementation equals the stored
          // implementation
          s_aLogger.warn ("Another hashCode implementation for class " +
                          aClass +
                          " is already registered (" +
                          aOldImpl.toString () +
                          ") so it is not overwritten with " +
                          aImpl.toString ());
        }
    });
  }

  @Nonnull
  public EChange unregisterHashCodeImplementation (@Nonnull final Class <?> aClass)
  {
    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aMap.remove (aClass) != null));
  }

  private boolean _isUseDirectHashCode (@Nonnull final Class <?> aClass)
  {
    return m_aDirectHashCode.hasAnnotation (aClass);
  }

  private boolean _implementsHashCodeItself (@Nonnull final Class <?> aClass)
  {
    final String sClassName = aClass.getName ();

    Boolean aImplementsHashCodeItself = m_aRWLock.readLocked ((Supplier <Boolean>) () -> m_aImplementsHashCode.get (sClassName));
    if (aImplementsHashCodeItself == null)
    {
      aImplementsHashCodeItself = m_aRWLock.writeLocked ((Supplier <Boolean>) () -> {
        // Try again in write lock
        Boolean aWLImplementsHashCodeItself = m_aImplementsHashCode.get (sClassName);
        if (aWLImplementsHashCodeItself == null)
        {
          // Determine
          boolean bRet = false;
          try
          {
            final Method aMethod = aClass.getDeclaredMethod ("hashCode");
            if (aMethod != null && aMethod.getReturnType ().equals (int.class))
              bRet = true;
          }
          catch (final NoSuchMethodException ex)
          {
            // ignore
          }
          aWLImplementsHashCodeItself = Boolean.valueOf (bRet);
          m_aImplementsHashCode.put (sClassName, aWLImplementsHashCodeItself);
        }
        return aWLImplementsHashCodeItself;
      });
    }
    return aImplementsHashCodeItself.booleanValue ();
  }

  @Nullable
  public IHashCodeImplementation getBestMatchingHashCodeImplementation (@Nullable final Class <?> aClass)
  {
    if (aClass != null)
    {
      IHashCodeImplementation aMatchingImplementation = null;
      Class <?> aMatchingClass = null;

      // No check required?
      if (_isUseDirectHashCode (aClass))
        return null;

      m_aRWLock.readLock ().lock ();
      try
      {
        // Check for an exact match first
        aMatchingImplementation = m_aMap.get (aClass);
        if (aMatchingImplementation != null)
          aMatchingClass = aClass;
        else
        {
          // Scan hierarchy in most efficient way
          for (final WeakReference <Class <?>> aCurWRClass : ClassHierarchyCache.getClassHierarchyIterator (aClass))
          {
            final Class <?> aCurClass = aCurWRClass.get ();
            if (aCurClass != null)
            {
              final IHashCodeImplementation aImpl = m_aMap.get (aCurClass);
              if (aImpl != null)
              {
                aMatchingImplementation = aImpl;
                aMatchingClass = aCurClass;
                if (s_aLogger.isDebugEnabled ())
                  s_aLogger.debug ("Found hierarchical match with class " +
                                   aMatchingClass +
                                   " when searching for " +
                                   aClass);
                break;
              }
            }
          }
        }
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }

      // Do this outside of the lock for performance reasons
      if (aMatchingImplementation != null)
      {
        // If the matching implementation is for an interface and the
        // implementation class implements hashCode, use the one from the class
        // Example: a converter for "Map" is registered, but "LRUCache" comes
        // with its own "hashCode" implementation
        if (ClassHelper.isInterface (aMatchingClass) && _implementsHashCodeItself (aClass))
        {
          // Remember to use direct implementation
          m_aDirectHashCode.setAnnotation (aClass, true);
          return null;
        }

        if (!aMatchingClass.equals (aClass))
        {
          // We found a match by walking the hierarchy -> put that match in the
          // direct hit list for further speed up
          registerHashCodeImplementation (aClass, aMatchingImplementation);
        }

        return aMatchingImplementation;
      }

      // Handle arrays specially, because we cannot register a converter for
      // every potential array class (but we allow for special implementations)
      if (ClassHelper.isArrayClass (aClass))
        return new ArrayHashCodeImplementation ();

      // Remember to use direct implementation
      m_aDirectHashCode.setAnnotation (aClass, true);
    }

    // No special handler found
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Found no hashCode implementation for " + aClass);

    // Definitely no special implementation
    return null;
  }

  public static int getHashCode (@Nullable final Object aObj)
  {
    if (aObj == null)
      return HashCodeCalculator.HASHCODE_NULL;

    // Get the best matching implementation
    final Class <?> aClass = aObj.getClass ();
    final IHashCodeImplementation aImpl = getInstance ().getBestMatchingHashCodeImplementation (aClass);
    return aImpl == null ? aObj.hashCode () : aImpl.getHashCode (aObj);
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aMap.clear ();
      m_aDirectHashCode.clearCache ();
      m_aImplementsHashCode.clear ();
    });

    // Register all implementations via SPI
    for (final IHashCodeImplementationRegistrarSPI aRegistrar : ServiceLoaderHelper.getAllSPIImplementations (IHashCodeImplementationRegistrarSPI.class))
      aRegistrar.registerHashCodeImplementations (this);

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitialized " + HashCodeImplementationRegistry.class.getName ());
  }
}
