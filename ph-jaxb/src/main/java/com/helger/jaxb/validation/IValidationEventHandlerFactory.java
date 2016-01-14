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
 * Factory interface for {@link ValidationEventHandler} objects.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IValidationEventHandlerFactory
{
  /**
   * Create a validation event handler
   *
   * @param aOldEventHandler
   *        The previous event handler that optionally may be encapsulated.
   * @return The created validation event handler. Maybe <code>null</code> to
   *         indicate that the default event handler should be used.
   */
  @Nullable
  ValidationEventHandler create (@Nullable ValidationEventHandler aOldEventHandler);
}
