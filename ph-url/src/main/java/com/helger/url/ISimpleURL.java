/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.url;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import com.helger.base.url.URLHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.data.IURLData;
import com.helger.url.param.URLParameter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Interface for a simple URL that works around the usability issues with the Java default
 * java.net.URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL extends IURLData
{
  @Nonnull
  ISimpleURL getWithPath (@Nonnull String sPath);

  @Nonnull
  ISimpleURL getWithParams (@Nullable ICommonsList <URLParameter> aParams);

  @Nonnull
  ISimpleURL getWithAnchor (@Nullable String sAnchor);

  @Nonnull
  ISimpleURL getWithCharset (@Nullable Charset aCharset);

  /**
   * @return The final string representation of this URL, encoding the request parameters with the
   *         charset of this URL.
   */
  @Nonnull
  default String getAsString ()
  {
    return SimpleURLHelper.getURLString (this);
  }

  @Deprecated (forRemoval = true, since = "12.0.0-rc2")
  @Nonnull
  default String getAsStringWithEncodedParameters ()
  {
    return getAsString ();
  }

  /**
   * Encode parameter with a specific charset
   *
   * @param aCharset
   *        Charset to use
   * @return encoded string
   */
  @Deprecated (forRemoval = true, since = "12.0.0-rc2")
  @Nonnull
  default String getAsStringWithEncodedParameters (@Nullable final Charset aCharset)
  {
    return getWithCharset (aCharset).getAsString ();
  }

  @Deprecated (forRemoval = true, since = "12.0.0-rc2")
  @Nonnull
  default String getAsStringWithoutEncodedParameters ()
  {
    return getAsString ();
  }

  @Nullable
  default URL getAsURL ()
  {
    return URLHelper.getAsURL (getAsString ());
  }

  @Nullable
  default URI getAsURI ()
  {
    return URLHelper.getAsURI (getAsString ());
  }
}
