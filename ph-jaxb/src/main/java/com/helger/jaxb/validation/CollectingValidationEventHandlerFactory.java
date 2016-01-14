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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.ValidationEventHandler;

/**
 * Implementation of {@link IValidationEventHandlerFactory} creating
 * {@link CollectingValidationEventHandler} objects.
 *
 * @author Philip Helger
 */
public class CollectingValidationEventHandlerFactory implements IValidationEventHandlerFactory
{
  /**
   * By default the old event handler is encapsulated into the created
   * {@link CollectingValidationEventHandler}.
   */
  public static final boolean DEFAULT_ENCAPSULATE_HANDLER = true;

  private final boolean m_bEncapsulateHandler;

  public CollectingValidationEventHandlerFactory ()
  {
    this (DEFAULT_ENCAPSULATE_HANDLER);
  }

  public CollectingValidationEventHandlerFactory (final boolean bEncapsulateHandler)
  {
    m_bEncapsulateHandler = bEncapsulateHandler;
  }

  public boolean isEncapsulateHandler ()
  {
    return m_bEncapsulateHandler;
  }

  @Nonnull
  public CollectingValidationEventHandler create (@Nullable final ValidationEventHandler aOldEventHandler)
  {
    return new CollectingValidationEventHandler (m_bEncapsulateHandler ? aOldEventHandler : null);
  }
}
