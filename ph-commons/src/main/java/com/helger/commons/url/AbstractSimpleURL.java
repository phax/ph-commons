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

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than
 * {@link java.net.URL}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractSimpleURL implements ISimpleURL
{
  private final String m_sPath;
  protected Map <String, String> m_aParams;
  protected String m_sAnchor;

  public AbstractSimpleURL ()
  {
    this (URLData.EMPTY_URL_DATA);
  }

  public AbstractSimpleURL (@Nonnull final String sHref)
  {
    this (URLHelper.getAsURLData (sHref));
  }

  public AbstractSimpleURL (@Nonnull final String sHref, @Nullable final Map <String, String> aParams)
  {
    this (sHref);
    if (CollectionHelper.isNotEmpty (aParams))
    {
      // m_aParams may already be non-null
      if (m_aParams == null)
        m_aParams = new LinkedHashMap <String, String> ();
      m_aParams.putAll (aParams);
    }
  }

  public AbstractSimpleURL (@Nonnull final String sHref,
                            @Nullable final Map <String, String> aParams,
                            @Nullable final String sAnchor)
  {
    this (sHref, aParams);
    m_sAnchor = sAnchor;
  }

  public AbstractSimpleURL (@Nonnull final IURLData aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");

    m_sPath = aURL.getPath ();
    if (aURL.hasParams ())
      m_aParams = aURL.getAllParams ();
    m_sAnchor = aURL.getAnchor ();
  }

  @Nullable
  public final IURLProtocol getProtocol ()
  {
    return URLProtocolRegistry.getInstance ().getProtocol (m_sPath);
  }

  public final boolean hasKnownProtocol ()
  {
    return URLProtocolRegistry.getInstance ().hasKnownProtocol (m_sPath);
  }

  @Nonnull
  public final String getPath ()
  {
    return m_sPath;
  }

  public final boolean hasParams ()
  {
    return CollectionHelper.isNotEmpty (m_aParams);
  }

  @Nonnegative
  public final int getParamCount ()
  {
    return CollectionHelper.getSize (m_aParams);
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public final Map <String, String> directGetAllParams ()
  {
    return m_aParams;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Map <String, String> getAllParams ()
  {
    return CollectionHelper.newOrderedMap (m_aParams);
  }

  public final boolean hasAnchor ()
  {
    return StringHelper.hasText (m_sAnchor);
  }

  @Nullable
  public final String getAnchor ()
  {
    return m_sAnchor;
  }

  @Nullable
  public final String getParam (@Nullable final String sKey)
  {
    return m_aParams == null ? null : m_aParams.get (sKey);
  }

  @Nonnull
  public final String getAsString ()
  {
    return URLHelper.getURLString (this, (Charset) null);
  }

  @Nonnull
  public final String getAsStringWithEncodedParameters ()
  {
    return getAsStringWithEncodedParameters (URLHelper.CHARSET_URL_OBJ);
  }

  @Nonnull
  public final String getAsStringWithEncodedParameters (@Nonnull final Charset aParameterCharset)
  {
    ValueEnforcer.notNull (aParameterCharset, "ParameterCharset");

    return URLHelper.getURLString (this, aParameterCharset);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractSimpleURL rhs = (AbstractSimpleURL) o;
    return m_sPath.equals (rhs.m_sPath) &&
           EqualsHelper.equals (m_aParams, rhs.m_aParams) &&
           EqualsHelper.equals (m_sAnchor, rhs.m_sAnchor);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPath).append (m_aParams).append (m_sAnchor).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("path", m_sPath)
                                       .appendIfNotNull ("params", m_aParams)
                                       .appendIfNotNull ("anchor", m_sAnchor)
                                       .toString ();
  }
}
