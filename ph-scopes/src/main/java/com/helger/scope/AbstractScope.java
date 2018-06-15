/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.attr.AttributeContainerAnyConcurrent;
import com.helger.commons.collection.attr.IAttributeContainerAny;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract scope implementation based on a Map containing the attribute values.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractScope implements IScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractScope.class);

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
  public AbstractScope (@Nonnull @Nonempty final String sScopeID)
  {
    m_sScopeID = ValueEnforcer.notEmpty (sScopeID, "ScopeID");
  }

  @Nonnull
  @Nonempty
  public final String getID ()
  {
    return m_sScopeID;
  }

  public final boolean isValid ()
  {
    return m_aRWLock.readLocked ( () -> !m_bInPreDestruction && !m_bInDestruction && !m_bDestroyed);
  }

  public final boolean isInPreDestruction ()
  {
    return m_aRWLock.readLocked ( () -> m_bInPreDestruction);
  }

  public final boolean isInDestruction ()
  {
    return m_aRWLock.readLocked ( () -> m_bInDestruction);
  }

  public final boolean isDestroyed ()
  {
    return m_aRWLock.readLocked ( () -> m_bDestroyed);
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

    final ICommonsList <IScopeDestructionAware> aDestructionAware = m_aAttrs.copyOfValuesMapped (x -> x instanceof IScopeDestructionAware,
                                                                                                 x -> (IScopeDestructionAware) x);

    // Call callback (if special interface is implemented)
    for (final IScopeDestructionAware aValue : aDestructionAware)
      try
      {
        aValue.onBeforeScopeDestruction (this);
      }
      catch (final Exception ex)
      {
        s_aLogger.error ("Failed to call onBeforeScopeDestruction in scope " + getID () + " for " + aValue, ex);
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
        s_aLogger.error ("Failed to call onScopeDestruction in scope " + getID () + " for " + aValue, ex);
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
  public final <T> T runAtomic (@Nonnull final Function <? super IScope, ? extends T> aFunction)
  {
    ValueEnforcer.notNull (aFunction, "Function");
    return m_aRWLock.writeLocked ( () -> aFunction.apply (this));
  }

  public final void runAtomic (@Nonnull final Consumer <? super IScope> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aRWLock.writeLocked ( () -> aConsumer.accept (this));
  }

  @Nonnull
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
