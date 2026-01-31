/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.data.IURLData;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;

/**
 * Read-only implementation of {@link ISimpleURL} which is immutable
 *
 * @author Philip Helger
 * @since 12.0.0
 */
@Immutable
public class ReadOnlyURL implements ISimpleURL
{
  private final URLData m_aData;

  protected ReadOnlyURL (@NonNull final URLData aURLData)
  {
    m_aData = aURLData;
  }

  @NonNull
  public final String getPath ()
  {
    return m_aData.getPath ();
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsList <URLParameter> getAllParams ()
  {
    return m_aData.params ().getClone ();
  }

  @Nullable
  public String getFirstParamValue (@Nullable final String sParamName)
  {
    return m_aData.getFirstParamValue (sParamName);
  }

  @Nullable
  public final String getAnchor ()
  {
    return m_aData.getAnchor ();
  }

  @NonNull
  public final Charset getCharset ()
  {
    return m_aData.getCharset ();
  }

  @NonNull
  public final ReadOnlyURL getWithPath (@NonNull final String sPath)
  {
    if (m_aData.getPath ().equals (sPath))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setPath (sPath));
  }

  @NonNull
  public final ReadOnlyURL getWithParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    if (EqualsHelper.equals (m_aData.params (), aParams))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setParams (aParams));
  }

  @NonNull
  public final ReadOnlyURL getWithAnchor (@Nullable final String sAnchor)
  {
    if (m_aData.hasAnchor (sAnchor))
      return this;
    return new ReadOnlyURL (m_aData.getClone ().setAnchor (sAnchor));
  }

  @NonNull
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

  @NonNull
  public static ReadOnlyURL of (@NonNull final IURLData aURLData)
  {
    return new ReadOnlyURL (new URLData (aURLData));
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final URL aURL)
  {
    return of (aURL, URLData.DEFAULT_CHARSET);
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final URL aURL, @Nullable final Charset aCharset)
  {
    return of (aURL.toExternalForm (), aCharset);
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final URI aURI)
  {
    return of (aURI, URLData.DEFAULT_CHARSET);
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final URI aURI, @Nullable final Charset aCharset)
  {
    return of (aURI.toString (), aCharset);
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final String sHref)
  {
    return of (sHref, URLData.DEFAULT_CHARSET);
  }

  @NonNull
  public static ReadOnlyURL of (@NonNull final String sHref, @Nullable final Charset aCharset)
  {
    return new ReadOnlyURL (SimpleURLHelper.getAsURLData (sHref, aCharset));
  }
}
