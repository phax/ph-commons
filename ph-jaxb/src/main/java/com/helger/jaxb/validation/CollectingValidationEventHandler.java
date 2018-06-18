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
package com.helger.jaxb.validation;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of the JAXB {@link javax.xml.bind.ValidationEventHandler}
 * interface. It collects all events that occurred!
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingValidationEventHandler extends AbstractValidationEventHandler
{
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ErrorList m_aErrors = new ErrorList ();

  public CollectingValidationEventHandler ()
  {}

  @Override
  protected void onEvent (@Nonnull final IError aEvent)
  {
    m_aRWLock.writeLocked ( () -> m_aErrors.add (aEvent));
  }

  @Nonnull
  @ReturnsMutableCopy
  public IErrorList getErrorList ()
  {
    return m_aRWLock.readLocked (m_aErrors::getClone);
  }

  /**
   * Call the provided consumer for all contained resource errors.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading actions!
   */
  public void forEachResourceError (@Nonnull final Consumer <? super IError> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aRWLock.readLocked ( () -> m_aErrors.forEach (aConsumer));
  }

  /**
   * Clear all currently stored errors.
   *
   * @return {@link EChange#CHANGED} if at least one item was cleared.
   */
  @Nonnull
  public EChange clearResourceErrors ()
  {
    return m_aRWLock.writeLocked ((Supplier <EChange>) m_aErrors::removeAll);
  }

  @Override
  public String toString ()
  {
    return m_aRWLock.readLocked ( () -> ToStringGenerator.getDerived (super.toString ())
                                                         .append ("errors", m_aErrors)
                                                         .getToString ());
  }
}
