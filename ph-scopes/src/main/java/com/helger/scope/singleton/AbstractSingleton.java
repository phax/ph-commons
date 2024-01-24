/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.scope.singleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.BitSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;
import com.helger.scope.IScope;
import com.helger.scope.IScopeDestructionAware;

/**
 * Base class for all singletons.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractSingleton implements IScopeDestructionAware
{
  private static final int STATUS_IN_INSTANTIATION = 0;
  private static final int STATUS_INSTANTIATED = 1;
  private static final int STATUS_IN_PRE_DESTRUCTION = 2;
  private static final int STATUS_IN_DESTRUCTION = 3;
  private static final int STATUS_DESTROYED = 4;

  private static final int DEFAULT_KEY_LENGTH = 255;
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractSingleton.class);
  private static final IMutableStatisticsHandlerKeyedCounter STATS_INSTANCE_COUNTER = StatisticsManager.getKeyedCounterHandler (AbstractSingleton.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private BitSet m_aStatus = new BitSet (16);

  /**
   * Write the internal status variables to the passed
   * {@link ObjectOutputStream}. This can be used to make singletons
   * serializable.
   *
   * @param aOOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case writing failed
   */
  protected final void writeAbstractSingletonFields (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    aOOS.writeObject (m_aStatus);
  }

  /**
   * Set all internal status variables to the values read from the specified
   * {@link ObjectInputStream}. This can be used to make singletons
   * serializable.
   *
   * @param aOIS
   *        The input stream to read from. May not be <code>null</code>.
   * @throws IOException
   *         In case reading failed
   * @throws ClassNotFoundException
   *         In case reading failed
   */
  protected final void readAbstractSingletonFields (@Nonnull final ObjectInputStream aOIS) throws IOException,
                                                                                           ClassNotFoundException
  {
    m_aStatus = (BitSet) aOIS.readObject ();
  }

  /**
   * Ctor.
   */
  @UsedViaReflection
  protected AbstractSingleton ()
  {
    // Check the call stack to avoid manual instantiation
    // Only required while developing
    if (GlobalDebug.isDebugMode ())
    {
      boolean bFound = false;
      final String sRequiredMethodName = "getSingleton";

      // check if this method is called indirectly via the correct method
      for (final StackTraceElement aStackTraceElement : Thread.currentThread ().getStackTrace ())
      {
        final String sMethodName = aStackTraceElement.getMethodName ();
        if (sMethodName.equals (sRequiredMethodName))
        {
          bFound = true;
          break;
        }
        // Special handling when deserializing from a stream
        if (aStackTraceElement.getClassName ().equals (ObjectInputStream.class.getName ()) &&
            sMethodName.equals ("readOrdinaryObject"))
        {
          bFound = true;
          break;
        }
      }
      if (!bFound)
      {
        // Required method name was not found - error
        throw new IllegalStateException ("You cannot instantiate the class " +
                                         getClass ().getName () +
                                         " manually! Use the method " +
                                         sRequiredMethodName +
                                         " instead!");
      }
    }
  }

  /**
   * Called after the singleton was instantiated. The constructor has finished,
   * and calling getInstance will work! This method is present to init the
   * object with a virtual table present.
   *
   * @param aScope
   *        The scope in which the object was instantiated. Never
   *        <code>null</code>.
   */
  @OverrideOnDemand
  protected void onAfterInstantiation (@Nonnull final IScope aScope)
  {}

  protected final void setInInstantiation (final boolean bInInstantiation)
  {
    m_aRWLock.writeLocked ( () -> m_aStatus.set (STATUS_IN_INSTANTIATION, bInInstantiation));
  }

  /**
   * @return <code>true</code> if this singleton is currently in the phase of
   *         instantiation, <code>false</code> if it is instantiated or already
   *         destroyed.
   */
  public final boolean isInInstantiation ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aStatus.get (STATUS_IN_INSTANTIATION));
  }

  protected final void setInstantiated (final boolean bInstantiated)
  {
    m_aRWLock.writeLocked ( () -> m_aStatus.set (STATUS_INSTANTIATED, bInstantiated));
  }

  /**
   * @return <code>true</code> if this singleton was already instantiated,
   *         <code>false</code> if it is active.
   */
  public final boolean isInstantiated ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aStatus.get (STATUS_INSTANTIATED));
  }

  protected final void setInPreDestruction (final boolean bInPreDestruction)
  {
    m_aRWLock.writeLocked ( () -> m_aStatus.set (STATUS_IN_PRE_DESTRUCTION, bInPreDestruction));
  }

  /**
   * @return <code>true</code> if this singleton is currently in the phase of
   *         pre destruction, <code>false</code> if it is active or already
   *         destroyed.
   */
  public final boolean isInPreDestruction ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aStatus.get (STATUS_IN_PRE_DESTRUCTION));
  }

  protected final void setInDestruction (final boolean bInDestruction)
  {
    m_aRWLock.writeLocked ( () -> m_aStatus.set (STATUS_IN_DESTRUCTION, bInDestruction));
  }

  /**
   * @return <code>true</code> if this singleton is currently in the phase of
   *         destruction, <code>false</code> if it is active or already
   *         destroyed.
   */
  public final boolean isInDestruction ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aStatus.get (STATUS_IN_DESTRUCTION));
  }

  protected final void setDestroyed (final boolean bDestroyed)
  {
    m_aRWLock.writeLocked ( () -> m_aStatus.set (STATUS_DESTROYED, bDestroyed));
  }

  /**
   * @return <code>true</code> if this singleton was already destroyed,
   *         <code>false</code> if it is active.
   */
  public final boolean isDestroyed ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aStatus.get (STATUS_DESTROYED));
  }

  /**
   * Called before this singleton is destroyed. This method is called when
   * "inPreDestruction" is <code>true</code>, "inDestruction" is still
   * <code>false</code> and "isDestroyed" is <code>false</code>.
   *
   * @param aScopeToBeDestroyed
   *        The scope that will be destroyed. Never <code>null</code>.
   * @throws Exception
   *         If something goes wrong
   */
  @OverrideOnDemand
  protected void onBeforeDestroy (@Nonnull final IScope aScopeToBeDestroyed) throws Exception
  {}

  /*
   * Implementation of {@link IScopeDestructionAware}. Calls the protected
   * {@link #onBeforeDestroy()} method.
   */
  @Override
  public final void onBeforeScopeDestruction (@Nonnull final IScope aScopeToBeDestroyed) throws Exception
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("onBeforeScopeDestruction for '" + toString () + "' in scope " + aScopeToBeDestroyed.toString ());

    // Check init state
    if (isInInstantiation ())
      LOGGER.warn ("Object currently in instantiation is destroyed soon: " + toString ());
    else
      if (!isInstantiated ())
        LOGGER.warn ("Object not instantiated is destroyed soon: " + toString ());

    // Check destruction state
    if (isInPreDestruction ())
      LOGGER.error ("Object already in pre destruction is destroyed soon again: " + toString ());
    else
      if (isInDestruction ())
        LOGGER.error ("Object already in destruction is destroyed soon again: " + toString ());
      else
        if (isDestroyed ())
          LOGGER.error ("Object already destroyed is destroyed soon again: " + toString ());

    setInPreDestruction (true);

    onBeforeDestroy (aScopeToBeDestroyed);

    // do not reset PreDestruction - happens in onScopeDestruction
  }

  /**
   * Called when this singleton is destroyed. Perform all cleanup in this
   * method. This method is called when "inPreDestruction" is <code>false</code>
   * , "inDestruction" is <code>true</code> and "isDestroyed" is
   * <code>false</code>.
   *
   * @param aScopeInDestruction
   *        The scope in destruction. Never <code>null</code>.
   * @throws Exception
   *         If something goes wrong
   */
  @OverrideOnDemand
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction) throws Exception
  {}

  /*
   * Implementation of {@link IScopeDestructionAware}. Calls the protected
   * {@link #onDestroy()} method.
   */
  @Override
  public final void onScopeDestruction (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("onScopeDestruction for '" + toString () + "' in scope " + aScopeInDestruction.toString ());

    // Check init state
    if (isInInstantiation ())
      LOGGER.warn ("Object currently in instantiation is now destroyed: " + toString ());
    else
      if (!isInstantiated ())
        LOGGER.warn ("Object not instantiated is now destroyed: " + toString ());

    // Check destruction state
    if (!isInPreDestruction ())
      LOGGER.error ("Object should be in pre destruction phase but is not: " + toString ());

    if (isInDestruction ())
      LOGGER.error ("Object already in destruction is now destroyed again: " + toString ());
    else
      if (isDestroyed ())
        LOGGER.error ("Object already destroyed is now destroyed again: " + toString ());

    setInDestruction (true);
    // Set after destruction is set to true
    setInPreDestruction (false);
    try
    {
      onDestroy (aScopeInDestruction);
    }
    finally
    {
      // Ensure scope is marked as "destroyed"
      setDestroyed (true);

      // Ensure field is reset even in case of an exception
      setInDestruction (false);
    }
  }

  /**
   * @return <code>true</code> if the object is instantiated and neither in
   *         destruction nor destroyed.
   */
  public final boolean isUsableObject ()
  {
    return isInstantiated () && !isInDestruction () && !isDestroyed ();
  }

  /**
   * Create the key which is used to reference the object within the scope.
   *
   * @param aClass
   *        The class for which the key is to be created. May not be
   *        <code>null</code>.
   * @return The non-<code>null</code> key.
   */
  @Nonnull
  public static final String getSingletonScopeKey (@Nonnull final Class <? extends AbstractSingleton> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    // Preallocate some bytes
    return new StringBuilder (DEFAULT_KEY_LENGTH).append ("singleton.").append (aClass.getName ()).toString ();
  }

  /**
   * Get the singleton object if it is already instantiated inside a scope or
   * <code>null</code> if it is not instantiated.
   *
   * @param <T>
   *        The type to be returned
   * @param aScope
   *        The scope to check. May be <code>null</code> to avoid constructing a
   *        scope.
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   */
  @Nullable
  public static final <T extends AbstractSingleton> T getSingletonIfInstantiated (@Nullable final IScope aScope,
                                                                                  @Nonnull final Class <T> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");
    if (aScope != null)
    {
      final String sSingletonScopeKey = getSingletonScopeKey (aClass);
      final Object aObject = RW_LOCK.readLockedGet ( () -> aScope.attrs ().get (sSingletonScopeKey));
      if (aObject != null)
      {
        // Object is in the scope
        final T aCastedObject = aClass.cast (aObject);
        if (aCastedObject.isUsableObject ())
        {
          // Object has finished initialization
          return aCastedObject;
        }
      }
    }
    return null;
  }

  /**
   * Check if a singleton is already instantiated inside a scope
   *
   * @param aScope
   *        The scope to check. May be <code>null</code> to avoid constructing a
   *        scope.
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   */
  public static final boolean isSingletonInstantiated (@Nullable final IScope aScope,
                                                       @Nonnull final Class <? extends AbstractSingleton> aClass)
  {
    return getSingletonIfInstantiated (aScope, aClass) != null;
  }

  /**
   * Instantiate singleton using reflection. The passed class must be a public
   * class. First a public constructor with a single argument of type
   * {@link IScope} is searched. If not found the public no-argument constructor
   * is searched.
   *
   * @param aClass
   *        The class to instantiate. May not be <code>null</code>.
   * @param aScope
   *        The scope to be passed to the instantiated class. Never
   *        <code>null</code>.,
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *         If instantiation failed
   */
  @Nonnull
  private static <T extends AbstractSingleton> T _instantiateSingleton (@Nonnull final Class <T> aClass,
                                                                        @Nonnull final IScope aScope)
  {
    // create new object in passed scope
    try
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Created singleton for '" + aClass.toString () + "' in scope " + aScope.toString ());

      // Check if class is public, non-abstract etc.
      if (!ClassHelper.isPublicClass (aClass))
        throw new IllegalStateException ("Class " + aClass.toString () + " is not instancable!");

      // First check, if constructor is present, that takes an IScope argument
      try
      {
        final Constructor <T> aCtor = aClass.getDeclaredConstructor (IScope.class);

        // Invoke ctor with scope
        return aCtor.newInstance (aScope);
      }
      catch (final NoSuchMethodException ex)
      {
        // Fall through to default ctor
      }
      // Alternatively find the no-argument constructor
      final Constructor <T> aCtor = aClass.getDeclaredConstructor ((Class <?> []) null);

      // Don't call "aCtor.setAccessible ()" because on
      // Ubuntu: java.security.AccessControlException: access denied
      // (java.lang.reflect.ReflectPermission suppressAccessChecks)

      // Invoke default ctor
      return aCtor.newInstance ((Object []) null);
    }
    catch (final RuntimeException ex)
    {
      throw ex;
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException ("Error instantiating singleton of class " +
                                       aClass.getName () +
                                       " in scope " +
                                       aScope.toString (),
                                       ex);
    }
  }

  /**
   * Get the singleton object in the passed scope, using the passed class. If
   * the singleton is not yet instantiated, a new instance is created.
   *
   * @param <T>
   *        The singleton type
   * @param aScope
   *        The scope to be used. May not be <code>null</code>.
   * @param aClass
   *        The class to be used. May not be <code>null</code>. The class must
   *        be public as needs to have a public no-argument constructor.
   * @return The singleton object and never <code>null</code>.
   */
  @Nonnull
  public static final <T extends AbstractSingleton> T getSingleton (@Nonnull final IScope aScope,
                                                                    @Nonnull final Class <T> aClass)
  {
    ValueEnforcer.notNull (aScope, "aScope");
    ValueEnforcer.notNull (aClass, "Class");

    final String sSingletonScopeKey = getSingletonScopeKey (aClass);

    // check if already contained in passed scope
    T aInstance = RW_LOCK.readLockedGet ( () -> aScope.attrs ().getCastedValue (sSingletonScopeKey));
    if (aInstance == null || aInstance.isInInstantiation ())
    {
      // Not yet present or just in instantiation

      // Safe instantiation check in write lock
      RW_LOCK.writeLock ().lock ();
      try
      {
        // Check again in write lock
        aInstance = aScope.attrs ().getCastedValue (sSingletonScopeKey);
        if (aInstance == null)
        {
          // Main instantiation
          aInstance = _instantiateSingleton (aClass, aScope);

          // Set in scope so that recursive calls to the same singleton are
          // caught appropriately
          aScope.attrs ().putIn (sSingletonScopeKey, aInstance);

          // Start the initialization process
          // Do this after the instance was added to the scope
          aInstance.setInInstantiation (true);
          try
          {
            // Invoke callback method
            aInstance.onAfterInstantiation (aScope);

            // Set "instantiated" only if no exception was thrown
            aInstance.setInstantiated (true);
          }
          finally
          {
            // Ensure field is reset even in case of an exception
            aInstance.setInInstantiation (false);
          }
          // And some statistics
          STATS_INSTANCE_COUNTER.increment (sSingletonScopeKey);
        }
        else
        {
          // May not be instantiated if this method is called from the same
          // thread as the original instantiation
        }
        // We have the instance - maybe from re-querying the scope, maybe from
        // instantiation
      }
      finally
      {
        RW_LOCK.writeLock ().unlock ();
      }
    }
    // This happens too often in practice, therefore this is disabled
    if (SingletonHelper.isDebugConsistency ())
    {
      // Just a small note in case we're returning an unusable object
      if (!aInstance.isUsableObject ())
        LOGGER.warn ("Singleton '" +
                     aClass.getName () +
                     "' is not usable - please check your calling order: " +
                     aInstance.toString (),
                     SingletonHelper.getDebugStackTrace ());
    }
    return aInstance;
  }

  /**
   * Get all singleton objects registered in the respective sub-class of this
   * class.
   *
   * @param <T>
   *        The singleton type to be retrieved
   * @param aScope
   *        The scope to use. May be <code>null</code> to avoid creating a new
   *        scope.
   * @param aDesiredClass
   *        The desired sub-class of this class. May not be <code>null</code>.
   * @return A non-<code>null</code> list with all instances of the passed class
   *         in the passed scope.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static final <T extends AbstractSingleton> ICommonsList <T> getAllSingletons (@Nullable final IScope aScope,
                                                                                       @Nonnull final Class <T> aDesiredClass)
  {
    ValueEnforcer.notNull (aDesiredClass, "DesiredClass");

    final ICommonsList <T> ret = new CommonsArrayList <> ();
    if (aScope != null)
      for (final Object aScopeValue : aScope.attrs ().values ())
        if (aScopeValue != null && aDesiredClass.isAssignableFrom (aScopeValue.getClass ()))
          ret.add (aDesiredClass.cast (aScopeValue));
    return ret;
  }

  @Override
  @Nonnull
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Status", m_aStatus).getToString ();
  }
}
