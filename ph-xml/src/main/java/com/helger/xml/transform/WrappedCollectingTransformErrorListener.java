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
package com.helger.xml.transform;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;

import jakarta.annotation.Nonnull;

/**
 * This implementation of {@link javax.xml.transform.ErrorListener} saves all
 * occurred warnings/errors/fatals in a list for later evaluation.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@NotThreadSafe
public class WrappedCollectingTransformErrorListener extends AbstractTransformErrorListener
{
  private final ErrorList m_aErrorList;

  public WrappedCollectingTransformErrorListener (@Nonnull final ErrorList aErrorList)
  {
    m_aErrorList = ValueEnforcer.notNull (aErrorList, "ErrorList");
  }

  /**
   * @return The error list object passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  public ErrorList wrappedErrorList ()
  {
    return m_aErrorList;
  }

  @Override
  protected void internalLog (@Nonnull final IError aResError)
  {
    m_aErrorList.add (aResError);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("ErrorList", m_aErrorList).getToString ();
  }
}
