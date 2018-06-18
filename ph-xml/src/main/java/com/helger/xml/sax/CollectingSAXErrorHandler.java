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
package com.helger.xml.sax;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.xml.sax.SAXParseException;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

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
  private final ErrorList m_aErrors = new ErrorList ();

  public CollectingSAXErrorHandler ()
  {}

  @Override
  protected void internalLog (@Nonnull final IErrorLevel aErrorLevel, final SAXParseException aException)
  {
    final IError aError = getSaxParseError (aErrorLevel, aException);
    m_aRWLock.writeLocked ( () -> m_aErrors.add (aError));
  }

  @Nonnull
  @ReturnsMutableCopy
  public IErrorList getErrorList ()
  {
    return m_aRWLock.readLocked (m_aErrors::getClone);
  }

  public boolean containsAtLeastOneError ()
  {
    return m_aRWLock.readLocked (m_aErrors::containsAtLeastOneError);
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
    return ToStringGenerator.getDerived (super.toString ()).append ("errors", m_aErrors).getToString ();
  }
}
