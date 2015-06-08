/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.scope;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.callback.INonThrowingCallableWithParameter;
import com.helger.commons.exception.LoggedRuntimeException;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.priviledged.PrivilegedActionAccessibleObjectSetAccessible;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all singletons.
 *
 * @author Philip Helger
 */
public abstract class AbstractSingleton implements IScopeDestructionAware
{
  private static final int DEFAULT_KEY_LENGTH = 255;
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSingleton.class);
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsCounterInstantiate = StatisticsManager.getKeyedCounterHandler (AbstractSingleton.class);

  private boolean m_bInInstantiation = false;
  private boolean m_bInstantiated = false;
  private boolean m_bInDestruction = false;
  private boolean m_bDestroyed = false;

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
    aOOS.writeBoolean (m_bInInstantiation);
    aOOS.writeBoolean (m_bInstantiated);
    aOOS.writeBoolean (m_bInDestruction);
    aOOS.writeBoolean (m_bDestroyed);
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
   */
  protected final void readAbstractSingletonFields (@Nonnull final ObjectInputStream aOIS) throws IOException
  {
    m_bInInstantiation = aOIS.readBoolean ();
    m_bInstantiated = aOIS.readBoolean ();
    m_bInDestruction = aOIS.readBoolean ();
    m_bDestroyed = aOIS.readBoolean ();
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
        throw new IllegalStateException ("You cannot instantiate the class " +
                                         getClass ().getName () +
                                         " manually! Use the method " +
                                         sRequiredMethodName +
                                         " instead!");
    }
  }

  /**
   * Called after the singleton was instantiated. The constructor has finished,
   * and calling getInstance will work!
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
    m_bInInstantiation = bInInstantiation;
  }

  /**
   * @return <code>true</code> if this singleton is currently in the phase of
   *         instantiation, <code>false</code> if it is instantiated or already
   *         destroyed.
   */
  public final boolean isInInstantiation ()
  {
    return m_bInInstantiation;
  }

  protected final void setInstantiated (final boolean bInstantiated)
  {
    m_bInstantiated = bInstantiated;
  }

  /**
   * @return <code>true</code> if this singleton was already instantiated,
   *         <code>false</code> if it is active.
   */
  public final boolean isInstantiated ()
  {
    return m_bInstantiated;
  }

  protected final void setInDestruction (final boolean bInDestruction)
  {
    m_bInDestruction = bInDestruction;
  }

  /**
   * @return <code>true</code> if this singleton is currently in the phase of
   *         destruction, <code>false</code> if it is active or already
   *         destroyed.
   */
  public final boolean isInDestruction ()
  {
    return m_bInDestruction;
  }

  protected final void setDestroyed (final boolean bDestroyed)
  {
    m_bDestroyed = bDestroyed;
  }

  /**
   * @return <code>true</code> if this singleton was already destroyed,
   *         <code>false</code> if it is active.
   */
  public final boolean isDestroyed ()
  {
    return m_bDestroyed;
  }

  /**
   * Called when this singleton is destroyed. Perform all cleanup in this
   * method. This method is called when "inDestruction" is <code>true</code> and
   * "isDestroyed" is <code>false</code>.
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
  public final void onScopeDestruction (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    // Check init state
    if (isInInstantiation ())
      s_aLogger.warn ("Object currently in instantiation is now destroyed: " + toString ());
    else
      if (!isInstantiated ())
        s_aLogger.warn ("Object not instantiated is now destroyed: " + toString ());

    // Check destruction state
    if (isInDestruction ())
      s_aLogger.error ("Object already in destruction is now destroyed again: " + toString ());
    else
      if (isDestroyed ())
        s_aLogger.error ("Object already destroyed is now destroyed again: " + toString ());

    setInDestruction (true);
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
      final Object aObject = aScope.getAttributeObject (sSingletonScopeKey);
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

  @Nonnull
  private static <T extends AbstractSingleton> T _instantiateSingleton (@Nonnull final Class <T> aClass,
                                                                        @Nonnull final IScope aScope)
  {
    // create new object in passed scope
    try
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Created singleton for '" + aClass + "' in scope " + aScope.toString ());

      // Check if class is public, non-abstract etc.
      if (!ClassHelper.isPublicClass (aClass))
        throw new IllegalStateException ("Class " + aClass + " is not instancable!");

      // First check, if constructor is present, that takes an IScope argument
      try
      {
        final Constructor <T> aCtor = aClass.getDeclaredConstructor (IScope.class);

        // Invoke ctor with scope
        final T ret = aCtor.newInstance (aScope);
        return ret;
      }
      catch (final NoSuchMethodException ex)
      {
        // Fall through to default ctor
      }

      // Alternatively find the no-argument constructor
      final Constructor <T> aCtor = aClass.getDeclaredConstructor ((Class <?> []) null);

      // Ubuntu: java.security.AccessControlException: access denied
      // (java.lang.reflect.ReflectPermission suppressAccessChecks)
      if (false)
        AccessController.doPrivileged (new PrivilegedActionAccessibleObjectSetAccessible (aCtor));

      // Invoke default ctor
      final T ret = aCtor.newInstance ((Object []) null);
      return ret;
    }
    catch (final Throwable t)
    {
      throw LoggedRuntimeException.newException (t);
    }
  }

  /**
   * Get the singleton object in the passed scope, using the passed class. If
   * the singleton is not yet instantiated, a new instance is created.
   *
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
    T aInstance = aClass.cast (aScope.getAttributeObject (sSingletonScopeKey));
    if (aInstance == null)
    {
      // Some final objects to access them from the nested inner class
      final MutableBoolean aFinalWasInstantiated = new MutableBoolean (false);

      // Safe instantiation:
      aInstance = aScope.runAtomic (new INonThrowingCallableWithParameter <T, IScope> ()
      {
        @Nonnull
        public T call (@Nullable final IScope aInnerScope)
        {
          // try to resolve again in case it was set in the meantime
          T aInnerInstance = aClass.cast (aScope.getAttributeObject (sSingletonScopeKey));
          if (aInnerInstance == null)
          {
            // Main instantiation
            aInnerInstance = _instantiateSingleton (aClass, aScope);

            // Set in scope
            aScope.setAttribute (sSingletonScopeKey, aInnerInstance);

            // Remember that we instantiated the object
            aFinalWasInstantiated.set (true);

            // And some statistics
            s_aStatsCounterInstantiate.increment (sSingletonScopeKey);
          }

          // We have the instance - maybe from re-querying the scope, maybe from
          // instantiation
          return aInnerInstance;
        }
      });

      // Call outside the scope sync block, and after the instance was
      // registered in the scope
      if (aFinalWasInstantiated.booleanValue ())
      {
        aInstance.setInInstantiation (true);
        try
        {
          // Invoke virtual method
          aInstance.onAfterInstantiation (aScope);
          // Set "instantiated" only if no exception was thrown
          aInstance.setInstantiated (true);
        }
        finally
        {
          // Ensure field is reset even in case of an exception
          aInstance.setInInstantiation (false);
        }
      }
    }

    if (false)
    {
      // Just a small note in case we're returning an incomplete object
      if (aInstance.isInInstantiation ())
        s_aLogger.warn ("Singleton is not yet ready - still in instantiation: " + aInstance.toString ());
    }

    return aInstance;
  }

  /**
   * Get all singleton objects registered in the respective sub-class of this
   * class.
   *
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
  public static final <T extends AbstractSingleton> List <T> getAllSingletons (@Nullable final IScope aScope,
                                                                               @Nonnull final Class <T> aDesiredClass)
  {
    ValueEnforcer.notNull (aDesiredClass, "DesiredClass");

    final List <T> ret = new ArrayList <T> ();
    if (aScope != null)
      for (final Object aScopeValue : aScope.getAllAttributeValues ())
        if (aScopeValue != null && aDesiredClass.isAssignableFrom (aScopeValue.getClass ()))
          ret.add (aDesiredClass.cast (aScopeValue));
    return ret;
  }

  @Override
  @Nonnull
  public String toString ()
  {
    return new ToStringGenerator (this).append ("InInstantiation", m_bInInstantiation)
                                       .append ("Instantiated", m_bInstantiated)
                                       .append ("InDestruction", m_bInDestruction)
                                       .append ("Destroyed", m_bDestroyed)
                                       .toString ();
  }
}
