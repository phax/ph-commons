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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of the JAXB {@link javax.xml.bind.ValidationEventHandler}
 * interface. It collects all events that occurred!
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@NotThreadSafe
public class WrappedCollectingValidationEventHandler extends AbstractValidationEventHandler
{
  private final ErrorList m_aErrorList;

  public WrappedCollectingValidationEventHandler (@Nonnull final ErrorList aErrorList)
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
  protected void onEvent (@Nonnull final IError aEvent)
  {
    m_aErrorList.add (aEvent);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("ErrorList", m_aErrorList).getToString ();
  }
}
