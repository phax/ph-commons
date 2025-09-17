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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.codec.URLParameterDecoder;
import com.helger.url.data.IMutableURLData;
import com.helger.url.data.IURLData;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than {@link java.net.URL}.
 * This class is mutable and not thread-safe. See {@link ReadOnlyURL} for a simple read-only URL.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SimpleURL implements ISimpleURL, IMutableURLData <SimpleURL>, ICloneable <SimpleURL>
{
  private final URLData m_aData;

  public SimpleURL ()
  {
    m_aData = URLData.createEmpty ();
  }

  public SimpleURL (@Nonnull final URL aURL)
  {
    this (aURL, URLData.DEFAULT_CHARSET);
  }

  public SimpleURL (@Nonnull final URL aURL, @Nonnull final Charset aCharset)
  {
    this (aURL.toExternalForm (), aCharset);
  }

  public SimpleURL (@Nonnull final URI aURI)
  {
    this (aURI, URLData.DEFAULT_CHARSET);
  }

  public SimpleURL (@Nonnull final URI aURI, @Nonnull final Charset aCharset)
  {
    this (aURI.toString (), aCharset);
  }

  public SimpleURL (@Nonnull final IURLData aURLData)
  {
    m_aData = new URLData (aURLData);
  }

  public SimpleURL (@Nonnull final String sHref)
  {
    this (sHref, URLData.DEFAULT_CHARSET);
  }

  public SimpleURL (@Nonnull final String sHref, @Nullable final Charset aCharset)
  {
    this (SimpleURLHelper.getAsURLData (sHref, aCharset, aCharset == null ? null : new URLParameterDecoder (aCharset)));
  }

  public SimpleURL (@Nonnull final String sHref, @Nullable final Map <String, String> aParams)
  {
    this (sHref, aParams, null);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref);
    if (aParams != null)
      for (final var e : aParams.entrySet ())
        add (e.getKey (), e.getValue ());
    if (StringHelper.isNotEmpty (sAnchor))
      m_aData.setAnchor (sAnchor);
  }

  public SimpleURL (@Nonnull final String sHref, @Nullable final Iterable <URLParameter> aParams)
  {
    this (sHref, aParams, null);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Iterable <URLParameter> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref);
    m_aData.params ().addAll (aParams);
    if (StringHelper.isNotEmpty (sAnchor))
      m_aData.setAnchor (sAnchor);
  }

  @Nonnull
  public final String getPath ()
  {
    return m_aData.getPath ();
  }

  @Nonnull
  public SimpleURL setPath (@Nonnull final String sPath)
  {
    m_aData.setPath (sPath);
    return this;
  }

  @Nonnull
  @ReturnsMutableObject
  public final ICommonsList <URLParameter> params ()
  {
    return m_aData.params ();
  }

  @Nullable
  public String getFirstParamValue (@Nullable final String sParamName)
  {
    return m_aData.getFirstParamValue (sParamName);
  }

  @Nonnull
  public SimpleURL setParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    m_aData.setParams (aParams);
    return this;
  }

  @Nonnull
  public SimpleURL withParams (@Nonnull final Consumer <? super ICommonsList <URLParameter>> aParamConsumer)
  {
    ValueEnforcer.notNull (aParamConsumer, "ParamConsumer");
    aParamConsumer.accept (m_aData.params ());
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sParamName)
  {
    m_aData.params ().add (new URLParameter (sParamName));
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sParamName, final boolean bParamValue)
  {
    add (sParamName, Boolean.toString (bParamValue));
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sParamName, final int nParamValue)
  {
    add (sParamName, Integer.toString (nParamValue));
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sParamName, final long nParamValue)
  {
    add (sParamName, Long.toString (nParamValue));
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sParamName, @Nullable final String sParamValue)
  {
    m_aData.params ().add (new URLParameter (sParamName, StringHelper.getNotNull (sParamValue)));
    return this;
  }

  @Nonnull
  public SimpleURL addIfNotNull (@Nonnull @Nonempty final String sParamName, @Nullable final String sParamValue)
  {
    if (sParamValue != null)
      add (sParamName, sParamValue);
    return this;
  }

  @Nonnull
  public SimpleURL addIf (@Nonnull @Nonempty final String sParamName,
                          @Nullable final String sParamValue,
                          @Nonnull final Predicate <String> aPredicate)
  {
    if (aPredicate.test (sParamValue))
      add (sParamName, sParamValue);
    return this;
  }

  @Nonnull
  public SimpleURL addAll (@Nullable final Map <String, String> aParams)
  {
    if (aParams != null)
      for (final var e : aParams.entrySet ())
        add (e.getKey (), e.getValue ());
    return this;
  }

  @Nullable
  public final String getAnchor ()
  {
    return m_aData.getAnchor ();
  }

  @Nonnull
  public SimpleURL setAnchor (@Nullable final String sAnchor)
  {
    m_aData.setAnchor (sAnchor);
    return this;
  }

  @Nonnull
  public final Charset getCharset ()
  {
    return m_aData.getCharset ();
  }

  @Nonnull
  public SimpleURL setCharset (@Nullable final Charset aCharset)
  {
    m_aData.setCharset (aCharset);
    return this;
  }

  @Nonnull
  public final SimpleURL getWithPath (@Nonnull final String sPath)
  {
    if (m_aData.getPath ().equals (sPath))
      return this;
    return new SimpleURL (m_aData.getClone ().setPath (sPath));
  }

  @Nonnull
  public final SimpleURL getWithParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    if (EqualsHelper.equals (m_aData.params (), aParams))
      return this;
    return new SimpleURL (m_aData.getClone ().setParams (aParams));
  }

  @Nonnull
  public final SimpleURL getWithAnchor (@Nullable final String sAnchor)
  {
    if (m_aData.hasAnchor (sAnchor))
      return this;
    return new SimpleURL (m_aData.getClone ().setAnchor (sAnchor));
  }

  @Nonnull
  public final SimpleURL getWithCharset (@Nullable final Charset aCharset)
  {
    if (EqualsHelper.equals (m_aData.getCharset (), aCharset))
      return this;
    return new SimpleURL (m_aData.getClone ().setCharset (aCharset));
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
}
