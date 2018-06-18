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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.ValidationEventHandler;

/**
 * An extended version of {@link ValidationEventHandler} with chaining
 * possibilities.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@FunctionalInterface
public interface IValidationEventHandler extends ValidationEventHandler, Serializable
{
  @Nonnull
  default IValidationEventHandler andThen (@Nullable final ValidationEventHandler aOther)
  {
    return and (this, aOther);
  }

  /**
   * Create an instance of {@link IValidationEventHandler} that invokes both
   * passed event handlers.
   *
   * @param aFirst
   *        The first event handler. May be <code>null</code>.
   * @param aSecond
   *        The second event handler. May be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 8.6.0
   */
  @Nonnull
  static IValidationEventHandler and (@Nullable final ValidationEventHandler aFirst,
                                      @Nullable final ValidationEventHandler aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return x -> {
          if (!aFirst.handleEvent (x))
          {
            // We should not continue
            return false;
          }
          return aSecond.handleEvent (x);
        };
      return aFirst::handleEvent;
    }

    if (aSecond != null)
      return aSecond::handleEvent;

    return x -> true;
  }
}
