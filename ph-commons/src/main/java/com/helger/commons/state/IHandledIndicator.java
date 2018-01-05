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
package com.helger.commons.state;

import javax.annotation.Nonnull;

/**
 * Very simple interface for an object that has handled indication.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public interface IHandledIndicator
{
  /**
   * @return <code>true</code> if handled and <code>false</code> if not.
   */
  boolean isHandled ();

  /**
   * @return <code>true</code> if not handled and <code>false</code> if so.
   */
  default boolean isUnhandled ()
  {
    return !isHandled ();
  }

  @Nonnull
  default EHandled or (@Nonnull final IHandledIndicator aOther)
  {
    return EHandled.valueOf (isHandled () || aOther.isHandled ());
  }

  @Nonnull
  default EHandled and (@Nonnull final IHandledIndicator aOther)
  {
    return EHandled.valueOf (isHandled () && aOther.isHandled ());
  }
}
