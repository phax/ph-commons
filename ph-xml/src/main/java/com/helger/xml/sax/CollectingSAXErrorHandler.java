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
package com.helger.xml.sax;

import java.util.function.Supplier;

import org.xml.sax.SAXParseException;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.state.EChange;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;

import jakarta.annotation.Nonnull;

/**
 * An error handler implementation that stores all warnings, errors and fatal
 * errors.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingSAXErrorHandler extends AbstractSAXErrorHandler
{
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  protected final ErrorList m_aErrors;

  public CollectingSAXErrorHandler ()
  {
    this (ErrorList::new);
  }

  /**
   * Protected constructor to use a different {@link ErrorList} - e.g. for
   * existing error lists.<br>
   *
   * @param aErrorListProvider
   *        The error list provider. May not be <code>null</code>.
   * @since 9.2.0
   */
  protected CollectingSAXErrorHandler (@Nonnull final Supplier <? extends ErrorList> aErrorListProvider)
  {
    m_aErrors = aErrorListProvider.get ();
    if (m_aErrors == null)
      throw new IllegalStateException ("ErrorListProvider returned a null value");
  }

  @Override
  protected void internalLog (@Nonnull final IErrorLevel aErrorLevel, final SAXParseException aException)
  {
    final IError aError = getSaxParseError (aErrorLevel, aException);
    m_aRWLock.writeLockedBoolean ( () -> m_aErrors.add (aError));
  }

  @Nonnull
  @ReturnsMutableCopy
  public IErrorList getErrorList ()
  {
    return m_aRWLock.readLockedGet (m_aErrors::getClone);
  }

  public boolean containsAtLeastOneError ()
  {
    return m_aRWLock.readLockedBoolean (m_aErrors::containsAtLeastOneError);
  }

  /**
   * Clear all currently stored errors.
   *
   * @return {@link EChange#CHANGED} if at least one item was cleared.
   */
  @Nonnull
  public EChange clearResourceErrors ()
  {
    return m_aRWLock.writeLockedGet (m_aErrors::removeAll);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("errors", m_aErrors).getToString ();
  }
}
