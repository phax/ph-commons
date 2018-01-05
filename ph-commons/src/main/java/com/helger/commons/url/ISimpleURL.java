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
package com.helger.commons.url;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringHelper;

/**
 * Interface for a simple URL that works around the usability issues with the
 * Java default java.net.URL.
 *
 * @author Philip Helger
 */
public interface ISimpleURL extends Serializable
{
  /**
   * @return The protocol used. May be <code>null</code> for an unknown
   *         protocol.
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
   * @return The path part of the URL (everything before the "?" and the "#",
   *         incl. the protocol). Never <code>null</code> but maybe empty (e.g.
   *         for "?x=y").
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
   * @return The name of the anchor (everything after the "#") or
   *         <code>null</code> if none is defined.
   */
  @Nullable
  String getAnchor ();

  /**
   * @return <code>true</code> if an anchor is present, <code>false</code>
   *         otherwise.
   */
  default boolean hasAnchor ()
  {
    return StringHelper.hasText (getAnchor ());
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
