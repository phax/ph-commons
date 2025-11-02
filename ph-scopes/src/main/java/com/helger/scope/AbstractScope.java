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
package com.helger.scope;

import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsList;
import com.helger.typeconvert.collection.AttributeContainerAnyConcurrent;
import com.helger.typeconvert.collection.IAttributeContainerAny;

/**
 * Abstract scope implementation based on a Map containing the attribute values.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractScope implements IScope
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractScope.class);

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  /** ID of the scope */
  private final String m_sScopeID;
  /** Is the scope currently in pre destruction? */
  private boolean m_bInPreDestruction = false;
  /** Is the scope currently in destruction? */
  private boolean m_bInDestruction = false;
  /** Is the scope already completely destroyed? */
  private boolean m_bDestroyed = false;
  private final AttributeContainerAnyConcurrent <String> m_aAttrs = new AttributeContainerAnyConcurrent <> ();

  /**
   * Ctor.
   *
   * @param sScopeID
   *        The scope ID. May neither be <code>null</code> nor empty.
   */
  protected AbstractScope (@NonNull @Nonempty final String sScopeID)
  {
    m_sScopeID = ValueEnforcer.notEmpty (sScopeID, "ScopeID");
  }

  @NonNull
  @Nonempty
  public final String getID ()
  {
    return m_sScopeID;
  }

  public final boolean isValid ()
  {
    return m_aRWLock.readLockedBoolean ( () -> !m_bInPreDestruction && !m_bInDestruction && !m_bDestroyed);
  }

  public final boolean isInPreDestruction ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bInPreDestruction);
  }

  public final boolean isInDestruction ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bInDestruction);
  }

  public final boolean isDestroyed ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bDestroyed);
  }

  /**
   * Override this method to perform further actions BEFORE the scope is
   * destroyed. The state is "in pre destruction".
   */
  @OverrideOnDemand
  protected void preDestroy ()
  {}

  /**
   * Override this method to perform further actions AFTER the scope was
   * destroyed. The state is "destroyed".
   */
  @OverrideOnDemand
  protected void postDestroy ()
  {}

  public final void destroyScope ()
  {
    m_aRWLock.writeLocked ( () -> {
      if (m_bInPreDestruction)
        throw new IllegalStateException ("Scope " + getID () + " is already in pre destruction!");
      m_bInPreDestruction = true;
    });

    preDestroy ();

    final ICommonsList <IScopeDestructionAware> aDestructionAware = m_aAttrs.copyOfValuesMapped (IScopeDestructionAware.class::isInstance,
                                                                                                 IScopeDestructionAware.class::cast);

    // Call callback (if special interface is implemented)
    for (final IScopeDestructionAware aValue : aDestructionAware)
      try
      {
        aValue.onBeforeScopeDestruction (this);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to call onBeforeScopeDestruction in scope " + getID () + " for " + aValue, ex);
      }

    m_aRWLock.writeLocked ( () -> {
      if (m_bDestroyed)
        throw new IllegalStateException ("Scope " + getID () + " is already destroyed!");
      if (m_bInDestruction)
        throw new IllegalStateException ("Scope " + getID () + " is already in destruction!");

      m_bInDestruction = true;
      m_bInPreDestruction = false;
    });

    // Call callback (if special interface is implemented)
    for (final IScopeDestructionAware aValue : aDestructionAware)
      try
      {
        aValue.onScopeDestruction (this);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to call onScopeDestruction in scope " + getID () + " for " + aValue, ex);
      }

    // Finished destruction process -> remember this
    m_aRWLock.writeLocked ( () -> {
      // remove all attributes (double write lock is no problem)
      m_aAttrs.clear ();

      m_bDestroyed = true;
      m_bInDestruction = false;
    });

    postDestroy ();
  }

  @Nullable
  public final <T> T runAtomic (@NonNull final Function <? super IScope, ? extends T> aFunction)
  {
    ValueEnforcer.notNull (aFunction, "Function");
    return m_aRWLock.writeLockedGet ( () -> aFunction.apply (this));
  }

  public final void runAtomic (@NonNull final Consumer <? super IScope> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aRWLock.writeLocked ( () -> aConsumer.accept (this));
  }

  @NonNull
  @ReturnsMutableObject
  public final IAttributeContainerAny <String> attrs ()
  {
    return m_aAttrs;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractScope rhs = (AbstractScope) o;
    return m_sScopeID.equals (rhs.m_sScopeID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sScopeID).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ScopeID", m_sScopeID)
                                       .append ("InPreDestruction", m_bInPreDestruction)
                                       .append ("InDestruction", m_bInDestruction)
                                       .append ("Destroyed", m_bDestroyed)
                                       .append ("Attrs", m_aAttrs)
                                       .getToString ();
  }
}
