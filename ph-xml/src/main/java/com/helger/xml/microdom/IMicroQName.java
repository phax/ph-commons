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
package com.helger.xml.microdom;

import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.MustImplementComparable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.compare.CompareHelper;
import com.helger.base.compare.IComparable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.string.StringHelper;

/**
 * Represents a simple qualified name. A combination of namespace URI and local name.
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
   * @return <code>true</code> if a specified namespace URI is present, <code>false</code> otherwise
   */
  default boolean hasNamespaceURI ()
  {
    return StringHelper.isNotEmpty (getNamespaceURI ());
  }

  /**
   * Check if no namespace URI is present.
   *
   * @return <code>true</code> if no namespace URI is present, <code>false</code> otherwise
   */
  default boolean hasNoNamespaceURI ()
  {
    return StringHelper.isEmpty (getNamespaceURI ());
  }

  /**
   * Check if the specified namespace URI is present.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if the specified namespace URI matches the URI of this name,
   *         <code>false</code> otherwise
   */
  default boolean hasNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return EqualsHelper.equals (sNamespaceURI, getNamespaceURI ());
  }

  /**
   * @return The local name without the namespace URI or the prefix. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  String getName ();

  default int compareTo (final IMicroQName o)
  {
    int ret = CompareHelper.compare (getNamespaceURI (), o.getNamespaceURI ());
    if (ret == 0)
      ret = getName ().compareTo (o.getName ());
    return ret;
  }

  /**
   * @return This micro QName as a regular XML QName using an empty prefix.
   */
  @NonNull
  default QName getAsXMLQName ()
  {
    return new QName (getNamespaceURI (), getName ());
  }

  /**
   * @param sPrefix
   *        the namespace prefix to be used in the resulting object. May not be <code>null</code>
   *        but maybe empty.
   * @return This micro QName as a regular XML QName using the provided prefix.
   */
  @NonNull
  default QName getAsXMLQName (@NonNull final String sPrefix)
  {
    return new QName (getNamespaceURI (), getName (), sPrefix);
  }
}
