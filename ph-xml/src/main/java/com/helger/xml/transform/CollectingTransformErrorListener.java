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
package com.helger.xml.transform;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * This implementation of {@link javax.xml.transform.ErrorListener} saves all
 * occurred warnings/errors/fatals in a list for later evaluation.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingTransformErrorListener extends AbstractTransformErrorListener
{
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ErrorList m_aErrors = new ErrorList ();

  public CollectingTransformErrorListener ()
  {
    super ();
  }

  @Override
  protected void internalLog (@Nonnull final IError aResError)
  {
    m_aRWLock.writeLocked ( () -> m_aErrors.add (aResError));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getErrorList ()
  {
    return m_aRWLock.readLocked (m_aErrors::getClone);
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
