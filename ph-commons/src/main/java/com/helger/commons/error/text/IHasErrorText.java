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
package com.helger.commons.error.text;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Base interface for objects having an error text. Compared to
 * {@link IHasDisplayText} it is required to implement equals and hashCode.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IHasErrorText extends IHasDisplayText
{
  /**
   * @return <code>true</code> if the error text is multilingual,
   *         <code>false</code> otherwise.
   */
  boolean isMultiLingual ();
}
