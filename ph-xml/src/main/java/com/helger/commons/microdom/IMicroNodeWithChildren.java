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
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;

/**
 * A special marker interface that is implemented by classes, that really
 * support having children!
 *
 * @author Philip Helger
 */
public interface IMicroNodeWithChildren extends IMicroNode
{
  /**
   * Get the concatenated text content of all direct {@link IMicroText} child
   * nodes of this element.
   *
   * @return <code>null</code> if the element contains no text node as child
   */
  @Nullable
  String getTextContent ();

  /**
   * Get the concatenated text content of all direct {@link IMicroText} child
   * nodes of this element. After concatenation, all leading and trailing spaces
   * are removed.
   *
   * @return <code>null</code> if the element contains no text node as child
   */
  @Nullable
  default String getTextContentTrimmed ()
  {
    return StringHelper.trim (getTextContent ());
  }

  /**
   * Get the concatenated text content of all direct {@link IMicroText} child
   * nodes of this element. The value is converted via the
   * {@link com.helger.commons.typeconvert.TypeConverter} to the desired
   * destination class.
   *
   * @param <DSTTYPE>
   *        The destination type to convert the String textContent to.
   * @param aDstClass
   *        The destination class to which the text content should be converted.
   * @return <code>null</code> if the element contains no text node as child
   */
  @Nullable
  <DSTTYPE> DSTTYPE getTextContentWithConversion (@Nonnull Class <DSTTYPE> aDstClass);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroNodeWithChildren getClone ();
}
