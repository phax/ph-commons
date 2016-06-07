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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.annotation.MustImplementComparable;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;

/**
 * Represents a simple qualified name. A combination of namespace URI and local
 * name.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
@MustImplementComparable
public interface IMicroQName extends Serializable
{
  /**
   * Get the namespace URI
   *
   * @return May be <code>null</code> if no namespace URI is present.
   */
  @Nullable
  String getNamespaceURI ();

  /**
   * Check if a namespace URI is present.
   *
   * @return <code>true</code> if a specified namespace URI is present,
   *         <code>false</code> otherwise
   */
  boolean hasNamespaceURI ();

  /**
   * Check if no namespace URI is present.
   *
   * @return <code>true</code> if no namespace URI is present,
   *         <code>false</code> otherwise
   */
  boolean hasNoNamespaceURI ();

  /**
   * Check if the specified namespace URI is present.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if the specified namespace URI matches the URI of
   *         this name, <code>false</code> otherwise
   */
  boolean hasNamespaceURI (@Nullable String sNamespaceURI);

  /**
   * @return The local name without the namespace URI or the prefix. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getName ();

  /**
   * @return This micro QName as a regular XML QName using an empty prefix.
   */
  @Nonnull
  QName getAsXMLQName ();

  /**
   * @param sPrefix
   *        the namespace prefix to be used in the resulting object. May not be
   *        <code>null</code> but maybe empty.
   * @return This micro QName as a regular XML QName using the provided prefix.
   */
  @Nonnull
  QName getAsXMLQName (@Nonnull String sPrefix);
}
