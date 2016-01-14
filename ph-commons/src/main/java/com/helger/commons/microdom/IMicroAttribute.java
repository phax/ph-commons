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

import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;

/**
 * Represents a single attribute within an element (=tag).
 *
 * @author Philip Helger
 */
public interface IMicroAttribute extends Serializable, ICloneable <IMicroAttribute>
{
  /**
   * Get the namespace URI of this attribute
   *
   * @return May be <code>null</code> if this attribute has no namespace URI.
   */
  @Nullable
  String getNamespaceURI ();

  /**
   * Check if this attribute has a specified namespace URI.
   *
   * @return <code>true</code> if this attribute has a specified namespace URI,
   *         <code>false</code> otherwise
   */
  boolean hasNamespaceURI ();

  /**
   * Check if this attribute has no namespace URI.
   *
   * @return <code>true</code> if this attribute has no namespace URI,
   *         <code>false</code> otherwise
   */
  boolean hasNoNamespaceURI ();

  /**
   * Check if this attribute has the specified namespace URI.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if this attribute has the specified namespace
   *         URI, <code>false</code> otherwise
   */
  boolean hasNamespaceURI (@Nullable String sNamespaceURI);

  /**
   * Get the name of the attribute. It never contains XML schema prefixes or the
   * like.
   *
   * @return The name of the attribute and never <code>null</code>.
   */
  @Nonnull
  String getAttributeName ();

  /**
   * Get the qualified name of the attribute. It never contains XML schema
   * prefixes or the like.
   *
   * @return The qualified name (namespace URI + local name) of the attribute
   *         and never <code>null</code>.
   */
  @Nonnull
  IMicroQName getAttributeQName ();

  /**
   * @return The regular XML QName of this attribute using an empty prefix.
   */
  @Nonnull
  QName getAsXMLQName ();

  /**
   * @param sPrefix
   *        the namespace prefix to be used in the resulting object. May not be
   *        <code>null</code> but maybe empty.
   * @return The regular XML QName of this attribute using the provided prefix.
   */
  @Nonnull
  QName getAsXMLQName (@Nonnull String sPrefix);

  /**
   * @return The value associated with this attribute. Never <code>null</code>.
   */
  @Nonnull
  String getAttributeValue ();

  /**
   * Set the value of the attribute.
   *
   * @param sAttributeValue
   *        The new value to be set. May not be <code>null</code>.
   * @return {@link EChange}.
   */
  @Nonnull
  EChange setAttributeValue (@Nonnull String sAttributeValue);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroAttribute getClone ();
}
