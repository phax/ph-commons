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
package com.helger.commons.equals;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.UseDirectEqualsAndHashCode;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.lang.ClassHelper;
import com.helger.base.lang.GenericReflection;
import com.helger.base.state.EChange;
import com.helger.commons.cache.AnnotationUsageCache;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsWeakHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.ServiceLoaderHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * The default implementation of {@link IEqualsImplementationRegistry}.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class EqualsImplementationRegistry implements IEqualsImplementationRegistry
{
  private static final class SingletonHolder
  {
    private static final EqualsImplementationRegistry INSTANCE = new EqualsImplementationRegistry ();
  }

  private static final class ArrayEqualsImplementation implements IEqualsImplementation <Object []>
  {
    public boolean areEqual (@Nonnull final Object [] aObj1, @Nonnull final Object [] aObj2)
    {
      // Size check
      final int nLength = aObj1.length;
      if (nLength != aObj2.length)
        return false;
      // Content check
      if (nLength > 0)
      {
        for (int i = 0; i < nLength; i++)
          if (!EqualsImplementationRegistry.areEqual (aObj1[i], aObj2[i]))
            return false;
      }
      return true;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (EqualsImplementationRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Use a weak hash map, because the key is a class
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <Class <?>, IEqualsImplementation <?>> m_aMap = new CommonsWeakHashMap <> ();

  // Cache for classes where direct implementation should be used
  private final AnnotationUsageCache m_aDirectEquals = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);

  // Cache for classes that implement equals directly
  private final ICommonsMap <String, Boolean> m_aImplementsEquals = new CommonsHashMap <> ();

  private EqualsImplementationRegistry ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static EqualsImplementationRegistry getInstance ()
  {
    final EqualsImplementationRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  public <T> void registerEqualsImplementation (@Nonnull final Class <T> aClass,
                                                @Nonnull final IEqualsImplementation <T> aImpl)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aImpl, "Implementation");

    if (aClass.equals (Object.class))
      throw new IllegalArgumentException ("You cannot provide an equals implementation for Object.class!");

    m_aRWLock.writeLocked ( () -> {
      final IEqualsImplementation <?> aOldImpl = m_aMap.get (aClass);
      if (aOldImpl == null)
        m_aMap.put (aClass, aImpl);
      else
      {
        // Avoid the warning when the passed implementation equals the stored
        // implementation
        if (EqualsHelper.identityDifferent (aOldImpl, aImpl))
          LOGGER.warn ("Another equals implementation for class " +
                       aClass +
                       " is already registered (" +
                       aOldImpl.toString () +
                       ") so it is not overwritten with " +
                       aImpl.toString ());
      }
    });
  }

  @Nonnull
  public EChange unregisterEqualsImplementation (@Nonnull final Class <?> aClass)
  {
    return m_aRWLock.writeLockedGet ( () -> m_aMap.removeObject (aClass));
  }

  private boolean _isUseDirectEquals (@Nonnull final Class <?> aClass)
  {
    return m_aDirectEquals.hasAnnotation (aClass);
  }

  private boolean _implementsEqualsItself (@Nonnull final Class <?> aClass)
  {
    final String sClassName = aClass.getName ();

    Boolean aImplementsEqualsItself = m_aRWLock.readLockedGet ((Supplier <Boolean>) () -> m_aImplementsEquals.get (sClassName));

    if (aImplementsEqualsItself == null)
    {
      aImplementsEqualsItself = m_aRWLock.writeLockedGet ((Supplier <Boolean>) () -> {
        // Try again in write lock
        Boolean aWLImplementsEqualsItself = m_aImplementsEquals.get (sClassName);
        if (aWLImplementsEqualsItself == null)
        {
          // Determine
          boolean bRet = false;
          try
          {
            final Method aMethod = aClass.getDeclaredMethod ("equals", Object.class);
            if (aMethod != null && aMethod.getReturnType ().equals (boolean.class))
              bRet = true;
          }
          catch (final NoSuchMethodException ex)
          {
            // ignore
          }
          aWLImplementsEqualsItself = Boolean.valueOf (bRet);
          m_aImplementsEquals.put (sClassName, aWLImplementsEqualsItself);
        }
        return aWLImplementsEqualsItself;
      });
    }

    return aImplementsEqualsItself.booleanValue ();
  }

  @Nullable
  public <T> IEqualsImplementation <T> getBestMatchingEqualsImplementation (@Nullable final Class <T> aClass)
  {
    if (aClass != null)
    {
      IEqualsImplementation <T> aMatchingImplementation = null;
      Class <?> aMatchingClass = null;

      // No check required?
      if (_isUseDirectEquals (aClass))
        return null;

      m_aRWLock.readLock ().lock ();
      try
      {
        // Check for an exact match first
        aMatchingImplementation = GenericReflection.uncheckedCast (m_aMap.get (aClass));
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
              final IEqualsImplementation <?> aImpl = m_aMap.get (aCurClass);
              if (aImpl != null)
              {
                aMatchingImplementation = GenericReflection.uncheckedCast (aImpl);
                aMatchingClass = aCurClass;
                if (LOGGER.isDebugEnabled ())
                  LOGGER.debug ("Found hierarchical match with class " +
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
        // implementation class implements equals, use the one from the class
        // Example: a converter for "Map" is registered, but "LRUCache" comes
        // with its own "equals" implementation
        if (aMatchingImplementation.implementationEqualsOverridesInterface () &&
            ClassHelper.isInterface (aMatchingClass) &&
            _implementsEqualsItself (aClass))
        {
          // Remember to use direct implementation
          m_aDirectEquals.setAnnotation (aClass, true);
          return null;
        }

        if (!aMatchingClass.equals (aClass))
        {
          // We found a match by walking the hierarchy -> put that match in the
          // direct hit list for further speed up
          registerEqualsImplementation (aClass, aMatchingImplementation);
        }

        return aMatchingImplementation;
      }

      // Handle arrays specially, because we cannot register a converter for
      // every potential array class (but we allow for special implementations)
      if (ClassHelper.isArrayClass (aClass))
        return GenericReflection.uncheckedCast (new ArrayEqualsImplementation ());

      // Remember to use direct implementation
      m_aDirectEquals.setAnnotation (aClass, true);
    }

    // No special handler found
    if (LOGGER.isTraceEnabled ())
      LOGGER.trace ("Found no equals implementation for " + aClass);

    // Definitely no special implementation
    return null;
  }

  public static <T> boolean areEqual (@Nullable final T aObj1, @Nullable final T aObj2)
  {
    // Same object - check first
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    // Check whether the implementation classes are identical
    final Class <?> aClass1 = aObj1.getClass ();
    final Class <?> aClass2 = aObj2.getClass ();
    if (!aClass1.equals (aClass2))
    {
      // Not the same class -> not equal!
      return false;
    }

    // Same class
    final IEqualsImplementation <T> aImpl = getInstance ().getBestMatchingEqualsImplementation (GenericReflection.uncheckedCast (aClass1));

    // Start the main equals check
    final boolean bAreEqual;
    if (aImpl == null)
    {
      // No special implementation found
      bAreEqual = aObj1.equals (aObj2);
    }
    else
    {
      // A special implementation was found!
      bAreEqual = aImpl.areEqual (aObj1, aObj2);
    }
    return bAreEqual;
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aMap.clear ();
      m_aDirectEquals.clearCache ();
      m_aImplementsEquals.clear ();
    });

    // Register all implementations via SPI
    for (final IEqualsImplementationRegistrarSPI aRegistrar : ServiceLoaderHelper.getAllSPIImplementations (IEqualsImplementationRegistrarSPI.class))
      aRegistrar.registerEqualsImplementations (this);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitialized " + EqualsImplementationRegistry.class.getName ());
  }
}
