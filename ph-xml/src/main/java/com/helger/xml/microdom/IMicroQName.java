/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.annotation.MustImplementComparable;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.compare.IComparable;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringHelper;

/**
 * Represents a simple qualified name. A combination of namespace URI and local
 * name.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
@MustImplementComparable
public interface IMicroQName extends IComparable <IMicroQName>
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
  default boolean hasNamespaceURI ()
  {
    return StringHelper.hasText (getNamespaceURI ());
  }

  /**
   * Check if no namespace URI is present.
   *
   * @return <code>true</code> if no namespace URI is present,
   *         <code>false</code> otherwise
   */
  default boolean hasNoNamespaceURI ()
  {
    return StringHelper.hasNoText (getNamespaceURI ());
  }

  /**
   * Check if the specified namespace URI is present.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if the specified namespace URI matches the URI of
   *         this name, <code>false</code> otherwise
   */
  default boolean hasNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return EqualsHelper.equals (sNamespaceURI, getNamespaceURI ());
  }

  /**
   * @return The local name without the namespace URI or the prefix. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getName ();

  default int compareTo (@Nonnull final IMicroQName o)
  {
    int ret = CompareHelper.compare (getNamespaceURI (), o.getNamespaceURI ());
    if (ret == 0)
      ret = getName ().compareTo (o.getName ());
    return ret;
  }

  /**
   * @return This micro QName as a regular XML QName using an empty prefix.
   */
  @Nonnull
  default QName getAsXMLQName ()
  {
    return new QName (getNamespaceURI (), getName ());
  }

  /**
   * @param sPrefix
   *        the namespace prefix to be used in the resulting object. May not be
   *        <code>null</code> but maybe empty.
   * @return This micro QName as a regular XML QName using the provided prefix.
   */
  @Nonnull
  default QName getAsXMLQName (@Nonnull final String sPrefix)
  {
    return new QName (getNamespaceURI (), getName (), sPrefix);
  }
}
