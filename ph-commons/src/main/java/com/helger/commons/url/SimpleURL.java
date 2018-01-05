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

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than
 * {@link java.net.URL}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SimpleURL implements ISimpleURL, ICloneable <SimpleURL>, IURLParameterList <SimpleURL>
{
  private String m_sPath;
  private final URLParameterList m_aParams = new URLParameterList ();
  private String m_sAnchor;

  public SimpleURL ()
  {
    this (URLData.EMPTY_URL_DATA);
  }

  public SimpleURL (@Nonnull final URL aURL)
  {
    this (aURL, URLHelper.CHARSET_URL_OBJ);
  }

  public SimpleURL (@Nonnull final URL aURL, @Nonnull final Charset aCharset)
  {
    this (aURL.toExternalForm (), aCharset);
  }

  public SimpleURL (@Nonnull final URI aURI)
  {
    this (aURI, URLHelper.CHARSET_URL_OBJ);
  }

  public SimpleURL (@Nonnull final URI aURI, @Nonnull final Charset aCharset)
  {
    this (aURI.toString (), aCharset);
  }

  public SimpleURL (@Nonnull final String sHref)
  {
    this (sHref, URLHelper.CHARSET_URL_OBJ);
  }

  public SimpleURL (@Nonnull final String sHref, @Nonnull final Charset aCharset)
  {
    this (URLHelper.getAsURLData (sHref, new URLParameterDecoder (aCharset)));
  }

  public SimpleURL (@Nonnull final String sHref, @Nullable final Map <String, String> aParams)
  {
    this (sHref);
    m_aParams.addAll (aParams);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, URLHelper.CHARSET_URL_OBJ, aParams, sAnchor);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nonnull final Charset aCharset,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, aCharset);
    m_aParams.addAll (aParams);
    m_sAnchor = sAnchor;
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Iterable <? extends URLParameter> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, URLHelper.CHARSET_URL_OBJ, aParams, sAnchor);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nonnull final Charset aCharset,
                    @Nullable final Iterable <? extends URLParameter> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, aCharset);
    m_aParams.addAll (aParams);
    m_sAnchor = sAnchor;
  }

  public SimpleURL (@Nonnull final ISimpleURL aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");

    m_sPath = aURL.getPath ();
    m_aParams.addAll (aURL.params ());
    m_sAnchor = aURL.getAnchor ();
  }

  @Nonnull
  public final String getPath ()
  {
    return m_sPath;
  }

  @Nonnull
  public SimpleURL setPath (@Nonnull final String sPath)
  {
    ValueEnforcer.notNull (sPath, "Path");
    m_sPath = sPath;
    return this;
  }

  @Nonnull
  @ReturnsMutableObject
  public final URLParameterList params ()
  {
    return m_aParams;
  }

  public boolean add (@Nonnull final URLParameter aParam)
  {
    return m_aParams.add (aParam);
  }

  @Nullable
  public final String getAnchor ()
  {
    return m_sAnchor;
  }

  @Nonnull
  public SimpleURL setAnchor (@Nullable final String sAnchor)
  {
    m_sAnchor = sAnchor;
    return this;
  }

  @Nonnull
  public SimpleURL getClone ()
  {
    return new SimpleURL (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SimpleURL rhs = (SimpleURL) o;
    return m_sPath.equals (rhs.m_sPath) &&
           m_aParams.equals (rhs.m_aParams) &&
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
    return new ToStringGenerator (null).append ("Path", m_sPath)
                                       .appendIf ("Params", m_aParams, ICommonsList::isNotEmpty)
                                       .appendIfNotNull ("Anchor", m_sAnchor)
                                       .getToString ();
  }
}
