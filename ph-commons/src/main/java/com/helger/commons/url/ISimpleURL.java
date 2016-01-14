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
package com.helger.commons.url;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.lang.IHasStringRepresentation;

/**
 * Interface for a simple URL that works around the issues with the Java default
 * URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL extends IURLData, IHasStringRepresentation
{
  /**
   * The string representing an empty URL. Must contain at least one character.
   */
  @Deprecated
  String EMPTY_URL_STRING = "?";

  /**
   * Get the parameter value of the given key.
   *
   * @param sKey
   *        The key to check. May be <code>null</code>.
   * @return <code>null</code> if no such parameter is present.
   */
  @Nullable
  String getParam (@Nullable String sKey);

  /**
   * @return The final string representation of this URL not encoding the
   *         request parameters.
   */
  @Nonnull
  String getAsString ();

  /**
   * @return The final string representation of this URL with encoded URL
   *         parameter keys and values. Using the default URL charset as
   *         determined by {@link URLHelper#CHARSET_URL}.
   */
  @Nonnull
  String getAsStringWithEncodedParameters ();

  /**
   * @param aCharset
   *        The charset used for encoding the parameters. May not be
   *        <code>null</code>.
   * @return The final string representation of this URL with encoded URL
   *         parameter keys and values.
   */
  @Nonnull
  String getAsStringWithEncodedParameters (@Nonnull Charset aCharset);
}
