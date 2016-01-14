/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nullable;
import javax.xml.bind.ValidationEventHandler;

/**
 * Implementation of {@link IValidationEventHandlerFactory} using a constant
 * {@link javax.xml.bind.ValidationEventHandler} object.
 *
 * @author Philip Helger
 */
public class ConstantValidationEventHandlerFactory implements IValidationEventHandlerFactory
{
  private final ValidationEventHandler m_aEventHandler;

  public ConstantValidationEventHandlerFactory (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
  }

  @Nullable
  public ValidationEventHandler getEventHandler ()
  {
    return m_aEventHandler;
  }

  @Nullable
  public ValidationEventHandler create (@Nullable final ValidationEventHandler aOldEventHandler)
  {
    return m_aEventHandler;
  }
}
