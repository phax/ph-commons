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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Base interface for an URL protocol descriptor. See {@link EURLProtocol} for
 * an implementation.
 *
 * @author Philip Helger
 */
public interface IURLProtocol extends Serializable
{
  /**
   * @return The underlying text representation of the protocol.
   */
  @Nonnull
  @Nonempty
  String getProtocol ();

  /**
   * Tells if the passed String (URL) belongs to this protocol.
   *
   * @param sURL
   *        The URL to check. May be <code>null</code>.
   * @return <code>true</code> if the passed URL starts with this protocol
   */
  boolean isUsedInURL (@Nullable String sURL);

  /**
   * Prefix the passed URL with this protocol.
   *
   * @param sURL
   *        The URL to be prefixed. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>. The
   *         protocol is prepended independent whether the URL already has a
   *         protocol or not.
   */
  @Nullable
  String getWithProtocol (@Nullable String sURL);

  /**
   * Prefix the passed URL with this protocol if the URL does not have a
   * protocol yet.
   *
   * @param sURL
   *        The URL to be prefixed. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>. The
   *         protocol is prepended if the URL not already has a protocol.
   */
  @Nullable
  String getWithProtocolIfNone (@Nullable String sURL);

  /**
   * @return <code>true</code> if this protocol can handle HTTP query parameters
   *         (?x=y&amp;z=1)
   */
  boolean allowsForQueryParameters ();
}
