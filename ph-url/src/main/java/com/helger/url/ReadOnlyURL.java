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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.url.codec.URLCoder;
import com.helger.url.param.IURLParameterList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Read-only implementation of {@link ISimpleURL} which is immutable
 *
 * @author Philip Helger
 */
@Immutable
public class ReadOnlyURL implements ISimpleURL
{
  private final URLData m_aData;

  protected ReadOnlyURL (@Nonnull final URLData aURLData)
  {
    m_aData = aURLData;
  }

  @Nonnull
  public final String getPath ()
  {
    return m_aData.getPath ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final IURLParameterList params ()
  {
    return m_aData.params ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final IURLParameterList getAllParams ()
  {
    return m_aData.params ().getClone ();
  }

  @Nullable
  public final String getAnchor ()
  {
    return m_aData.getAnchor ();
  }

  @Nonnull
  public final Charset getCharset ()
  {
    return m_aData.getCharset ();
  }

  @Nonnull
  public final ReadOnlyURL getWithPath (@Nonnull final String sPath)
  {
    if (m_aData.getPath ().equals (sPath))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setPath (sPath));
  }

  @Nonnull
  public final ReadOnlyURL getWithParams (@Nullable final IURLParameterList aParams)
  {
    if (EqualsHelper.equals (m_aData.params (), aParams))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setParams (aParams));
  }

  @Nonnull
  public final ReadOnlyURL getWithAnchor (@Nullable final String sAnchor)
  {
    if (m_aData.hasAnchor (sAnchor))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setAnchor (sAnchor));
  }

  @Nonnull
  public final ReadOnlyURL getWithCharset (@Nullable final Charset aCharset)
  {
    if (EqualsHelper.equals (m_aData.getCharset (), aCharset))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setCharset (aCharset));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ReadOnlyURL rhs = (ReadOnlyURL) o;
    return m_aData.equals (rhs.m_aData);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aData).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Data", m_aData).getToString ();
  }

  @Nonnull
  public static ReadOnlyURL of (@Nonnull final URL aURL)
  {
    return of (aURL, URLCoder.CHARSET_URL_OBJ);
  }

  @Nonnull
  public static ReadOnlyURL of (@Nonnull final URL aURL, @Nullable final Charset aCharset)
  {
    return parse (aURL.toExternalForm (), aCharset);
  }

  @Nonnull
  public static ReadOnlyURL of (@Nonnull final URI aURI)
  {
    return of (aURI, URLCoder.CHARSET_URL_OBJ);
  }

  @Nonnull
  public static ReadOnlyURL of (@Nonnull final URI aURI, @Nullable final Charset aCharset)
  {
    return parse (aURI.toString (), aCharset);
  }

  @Nonnull
  public static ReadOnlyURL parse (@Nonnull final String sHref)
  {
    return parse (sHref, URLData.DEFAULT_CHARSET);
  }

  @Nonnull
  public static ReadOnlyURL parse (@Nonnull final String sHref, @Nullable final Charset aCharset)
  {
    return new ReadOnlyURL (SimpleURLHelper.getAsURLData (sHref, aCharset));
  }
}
