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

import com.helger.commons.ValueEnforcer;

/**
 * Interface for a simple URL that works around the issues with the Java default
 * URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL extends IURLData
{
  /**
   * @return The final string representation of this URL not encoding the
   *         request parameters.
   */
  @Nonnull
  default String getAsStringWithoutEncodedParameters ()
  {
    return URLHelper.getURLString (this, (Charset) null);
  }

  /**
   * @return The final string representation of this URL with encoded URL
   *         parameter keys and values. Using the default URL charset as
   *         determined by {@link URLHelper#CHARSET_URL_OBJ}.
   */
  @Nonnull
  default String getAsStringWithEncodedParameters ()
  {
    return URLHelper.getURLString (this, URLHelper.CHARSET_URL_OBJ);
  }

  /**
   * @param aCharset
   *        The charset used for encoding the parameters. May not be
   *        <code>null</code>.
   * @return The final string representation of this URL with encoded URL
   *         parameter keys and values.
   */
  @Nonnull
  default String getAsStringWithEncodedParameters (@Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "ParameterCharset");
    return URLHelper.getURLString (this, aCharset);
  }
}
