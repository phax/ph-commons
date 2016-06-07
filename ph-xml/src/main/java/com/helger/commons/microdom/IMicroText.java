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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;

/**
 * Represents a text node.
 *
 * @author Philip Helger
 */
public interface IMicroText extends IMicroNode, IMicroDataAware
{
  /**
   * Check whether the text node consists solely of whitespaces. This may be
   * helpful in detecting XML that has been indented :)
   *
   * @return <code>true</code> if the text node consists solely of whitespaces
   *         (blank, tab etc.).
   */
  boolean isElementContentWhitespace ();

  /**
   * @return <code>true</code> if the content of this text node should be
   *         masked, and <code>false</code> if not. By default a text node
   *         should always be masked.
   */
  boolean isEscape ();

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroText getClone ();
}
