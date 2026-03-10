/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.url.URLHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.data.IURLData;
import com.helger.url.param.URLParameter;

/**
 * Interface for a simple URL that works around the usability issues with the Java default
 * java.net.URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL extends IURLData
{
  /**
   * Create a new URL with a different path but the same parameters and anchor.
   *
   * @param sPath
   *        The new path. May not be <code>null</code>.
   * @return A new {@link ISimpleURL} instance. Never <code>null</code>.
   */
  @NonNull
  ISimpleURL getWithPath (@NonNull String sPath);

  /**
   * Create a new URL with different parameters but the same path and anchor.
   *
   * @param aParams
   *        The new parameters. May be <code>null</code>.
   * @return A new {@link ISimpleURL} instance. Never <code>null</code>.
   */
  @NonNull
  ISimpleURL getWithParams (@Nullable ICommonsList <URLParameter> aParams);

  /**
   * Create a new URL with a different anchor but the same path and parameters.
   *
   * @param sAnchor
   *        The new anchor. May be <code>null</code>.
   * @return A new {@link ISimpleURL} instance. Never <code>null</code>.
   */
  @NonNull
  ISimpleURL getWithAnchor (@Nullable String sAnchor);

  /**
   * Create a new URL with a different charset but the same path, parameters and
   * anchor.
   *
   * @param aCharset
   *        The new charset. May be <code>null</code>.
   * @return A new {@link ISimpleURL} instance. Never <code>null</code>.
   */
  @NonNull
  ISimpleURL getWithCharset (@Nullable Charset aCharset);

  /**
   * @return The final string representation of this URL, encoding the request parameters with the
   *         charset of this URL.
   */
  @NonNull
  default String getAsString ()
  {
    return SimpleURLHelper.getURLString (this);
  }

  @Deprecated (forRemoval = true, since = "12.0.0-rc2")
  @NonNull
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
  @NonNull
  default String getAsStringWithEncodedParameters (@Nullable final Charset aCharset)
  {
    return getWithCharset (aCharset).getAsString ();
  }

  @Deprecated (forRemoval = true, since = "12.0.0-rc2")
  @NonNull
  default String getAsStringWithoutEncodedParameters ()
  {
    return getAsString ();
  }

  /**
   * @return This URL as a {@link URL} object, or <code>null</code> if
   *         conversion fails.
   */
  @Nullable
  default URL getAsURL ()
  {
    return URLHelper.getAsURL (getAsString ());
  }

  /**
   * @return This URL as a {@link URI} object, or <code>null</code> if
   *         conversion fails.
   */
  @Nullable
  default URI getAsURI ()
  {
    return URLHelper.getAsURI (getAsString ());
  }
}
