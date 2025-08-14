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

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.hashcode.IHashCodeGenerator;
import com.helger.base.string.Strings;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.equals.EqualsHelperExt;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.xml.CXML;
import com.helger.xml.CXMLRegEx;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Represents a simple qualified name. A combination of namespace URI and local name.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroQName implements IMicroQName
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MicroQName.class);

  private final String m_sNamespaceURI;
  private final String m_sName;
  // Status vars
  private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  public MicroQName (@Nonnull @Nonempty final String sName)
  {
    this (XMLConstants.NULL_NS_URI, sName);
  }

  public MicroQName (@Nullable final String sNamespaceURI, @Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, sName);
    // Unify empty string to null
    m_sNamespaceURI = Strings.isEmpty (sNamespaceURI) ? null : sNamespaceURI;

    // Store only the local name (cut the prefix) if a namespace is present
    final int nPrefixEnd = m_sNamespaceURI != null ? sName.indexOf (CXML.XML_PREFIX_NAMESPACE_SEP) : -1;
    if (nPrefixEnd == -1)
      m_sName = sName;
    else
    {
      // Cut the prefix
      LOGGER.warn ("Removing namespace prefix '" +
                   sName.substring (0, nPrefixEnd) +
                   "' from micro XML name '" +
                   sName +
                   "'");
      m_sName = sName.substring (nPrefixEnd + 1);
    }
    // Only for the debug version, as this slows things down heavily
    if (GlobalDebug.isDebugMode ())
      if (!CXMLRegEx.PATTERN_NAME_QUICK.matcher (m_sName).matches ())
        if (!CXMLRegEx.PATTERN_NAME.matcher (m_sName).matches ())
          throw new IllegalArgumentException ("The micro XML name '" + m_sName + "' is not a valid XML name!");
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroQName rhs = (MicroQName) o;
    return EqualsHelperExt.extEquals (m_sNamespaceURI, rhs.m_sNamespaceURI) && m_sName.equals (rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (m_sNamespaceURI).append (m_sName).getHashCode ();
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).appendIfNotNull ("NamespaceURI", m_sNamespaceURI)
                                       .append ("Name", m_sName)
                                       .getToString ();
  }
}
