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

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.string.StringHelper;
import com.helger.base.url.URLHelper;
import com.helger.url.protocol.IURLProtocol;
import com.helger.url.protocol.URLProtocolRegistry;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Interface for a simple URL that works around the usability issues with the Java default
 * java.net.URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL
{
  /**
   * @return The protocol used. May be <code>null</code> for an unknown protocol.
   */
  @Nullable
  default IURLProtocol getProtocol ()
  {
    return URLProtocolRegistry.getInstance ().getProtocol (getPath ());
  }

  /**
   * @return <code>true</code> if the URL has a known protocol
   */
  default boolean hasKnownProtocol ()
  {
    return URLProtocolRegistry.getInstance ().hasKnownProtocol (getPath ());
  }

  /**
   * @return The path part of the URL (everything before the "?" and the "#", incl. the protocol).
   *         Never <code>null</code> but maybe empty (e.g. for "?x=y").
   */
  @Nonnull
  String getPath ();

  /**
   * @return A map of all query string parameters. May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject
  URLParameterList params ();

  /**
   * @return The name of the anchor (everything after the "#") or <code>null</code> if none is
   *         defined.
   */
  @Nullable
  String getAnchor ();

  /**
   * @return <code>true</code> if an anchor is present, <code>false</code> otherwise.
   */
  default boolean hasAnchor ()
  {
    return StringHelper.isNotEmpty (getAnchor ());
  }

  /**
   * Check if this URL has an anchor with the passed name.
   *
   * @param sAnchor
   *        The anchor name to check.
   * @return <code>true</code> if the passed anchor is present.
   */
  default boolean hasAnchor (@Nullable final String sAnchor)
  {
    return EqualsHelper.equals (sAnchor, getAnchor ());
  }

  /**
   * @return The final string representation of this URL not encoding the request parameters.
   */
  @Nonnull
  default String getAsStringWithoutEncodedParameters ()
  {
    return SimpleURLHelper.getURLString (this, (Charset) null);
  }

  /**
   * @return The final string representation of this URL with encoded URL parameter keys and values.
   *         Using the default URL charset as determined by {@link URLCoder#CHARSET_URL_OBJ}.
   */
  @Nonnull
  default String getAsStringWithEncodedParameters ()
  {
    return SimpleURLHelper.getURLString (this, URLCoder.CHARSET_URL_OBJ);
  }

  /**
   * @param aCharset
   *        The charset used for encoding the parameters. May not be <code>null</code>.
   * @return The final string representation of this URL with encoded URL parameter keys and values.
   */
  @Nonnull
  default String getAsStringWithEncodedParameters (@Nonnull final Charset aCharset)
  {
    return SimpleURLHelper.getURLString (this, aCharset);
  }

  @Nullable
  default URL getAsURL ()
  {
    return URLHelper.getAsURL (getAsStringWithEncodedParameters ());
  }

  @Nullable
  default URI getAsURI ()
  {
    return URLHelper.getAsURI (getAsStringWithEncodedParameters ());
  }
}
