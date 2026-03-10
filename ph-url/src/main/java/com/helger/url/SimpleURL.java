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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

  /**
   * Default constructor creating an empty URL.
   */
  public SimpleURL ()
  {
    m_aData = URLData.createEmpty ();
  }

  /**
   * Constructor from a {@link URL} using the default charset.
   *
   * @param aURL
   *        The URL to use. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final URL aURL)
  {
    this (aURL, URLData.DEFAULT_CHARSET);
  }

  /**
   * Constructor from a {@link URL} with a specific charset.
   *
   * @param aURL
   *        The URL to use. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use for parameter encoding. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final URL aURL, @NonNull final Charset aCharset)
  {
    this (aURL.toExternalForm (), aCharset);
  }

  /**
   * Constructor from a {@link URI} using the default charset.
   *
   * @param aURI
   *        The URI to use. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final URI aURI)
  {
    this (aURI, URLData.DEFAULT_CHARSET);
  }

  /**
   * Constructor from a {@link URI} with a specific charset.
   *
   * @param aURI
   *        The URI to use. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use for parameter encoding. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final URI aURI, @NonNull final Charset aCharset)
  {
    this (aURI.toString (), aCharset);
  }

  /**
   * Constructor from existing URL data.
   *
   * @param aURLData
   *        The URL data to copy. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final IURLData aURLData)
  {
    m_aData = new URLData (aURLData);
  }

  /**
   * Constructor parsing the provided URL string using the default charset.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref)
  {
    this (sHref, URLData.DEFAULT_CHARSET);
  }

  /**
   * Constructor parsing the provided URL string with a specific charset.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use for parameter encoding. May be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref, @Nullable final Charset aCharset)
  {
    this (SimpleURLHelper.getAsURLData (sHref, aCharset, aCharset == null ? null : new URLParameterDecoder (aCharset)));
  }

  /**
   * Constructor from a URL string with additional parameters.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   * @param aParams
   *        Additional parameters to add. May be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref, @Nullable final Map <String, String> aParams)
  {
    this (sHref, aParams, null);
  }

  /**
   * Constructor from a URL string with additional parameters and an anchor.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   * @param aParams
   *        Additional parameters to add. May be <code>null</code>.
   * @param sAnchor
   *        The anchor to set. May be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref,
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

  /**
   * Constructor from a URL string with additional URL parameters.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   * @param aParams
   *        Additional URL parameters to add. May be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref, @Nullable final Iterable <URLParameter> aParams)
  {
    this (sHref, aParams, null);
  }

  /**
   * Constructor from a URL string with additional URL parameters and an anchor.
   *
   * @param sHref
   *        The URL string to parse. May not be <code>null</code>.
   * @param aParams
   *        Additional URL parameters to add. May be <code>null</code>.
   * @param sAnchor
   *        The anchor to set. May be <code>null</code>.
   */
  public SimpleURL (@NonNull final String sHref,
                    @Nullable final Iterable <URLParameter> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref);
    m_aData.params ().addAll (aParams);
    if (StringHelper.isNotEmpty (sAnchor))
      m_aData.setAnchor (sAnchor);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final String getPath ()
  {
    return m_aData.getPath ();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public SimpleURL setPath (@NonNull final String sPath)
  {
    m_aData.setPath (sPath);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @ReturnsMutableObject
  public final ICommonsList <URLParameter> params ()
  {
    return m_aData.params ();
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getFirstParamValue (@Nullable final String sParamName)
  {
    return m_aData.getFirstParamValue (sParamName);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public SimpleURL setParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    m_aData.setParams (aParams);
    return this;
  }

  /**
   * Apply a consumer to the mutable parameter list.
   *
   * @param aParamConsumer
   *        The consumer to apply. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL withParams (@NonNull final Consumer <? super ICommonsList <URLParameter>> aParamConsumer)
  {
    ValueEnforcer.notNull (aParamConsumer, "ParamConsumer");
    aParamConsumer.accept (m_aData.params ());
    return this;
  }

  /**
   * Add a parameter with only a name and no value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL add (@NonNull @Nonempty final String sParamName)
  {
    m_aData.params ().add (new URLParameter (sParamName));
    return this;
  }

  /**
   * Add a parameter with a boolean value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param bParamValue
   *        The parameter value.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL add (@NonNull @Nonempty final String sParamName, final boolean bParamValue)
  {
    add (sParamName, Boolean.toString (bParamValue));
    return this;
  }

  /**
   * Add a parameter with an int value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param nParamValue
   *        The parameter value.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL add (@NonNull @Nonempty final String sParamName, final int nParamValue)
  {
    add (sParamName, Integer.toString (nParamValue));
    return this;
  }

  /**
   * Add a parameter with a long value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param nParamValue
   *        The parameter value.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL add (@NonNull @Nonempty final String sParamName, final long nParamValue)
  {
    add (sParamName, Long.toString (nParamValue));
    return this;
  }

  /**
   * Add a parameter with a String value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param sParamValue
   *        The parameter value. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL add (@NonNull @Nonempty final String sParamName, @Nullable final String sParamValue)
  {
    m_aData.params ().add (new URLParameter (sParamName, StringHelper.getNotNull (sParamValue)));
    return this;
  }

  /**
   * Add a parameter only if the value is not <code>null</code>.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param sParamValue
   *        The parameter value. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL addIfNotNull (@NonNull @Nonempty final String sParamName, @Nullable final String sParamValue)
  {
    if (sParamValue != null)
      add (sParamName, sParamValue);
    return this;
  }

  /**
   * Add a parameter only if the predicate matches the value.
   *
   * @param sParamName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param sParamValue
   *        The parameter value. May be <code>null</code>.
   * @param aPredicate
   *        The predicate to evaluate. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL addIf (@NonNull @Nonempty final String sParamName,
                          @Nullable final String sParamValue,
                          @NonNull final Predicate <String> aPredicate)
  {
    if (aPredicate.test (sParamValue))
      add (sParamName, sParamValue);
    return this;
  }

  /**
   * Add all parameters from the provided map.
   *
   * @param aParams
   *        The parameters to add. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SimpleURL addAll (@Nullable final Map <String, String> aParams)
  {
    if (aParams != null)
      for (final var e : aParams.entrySet ())
        add (e.getKey (), e.getValue ());
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public final String getAnchor ()
  {
    return m_aData.getAnchor ();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public SimpleURL setAnchor (@Nullable final String sAnchor)
  {
    m_aData.setAnchor (sAnchor);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final Charset getCharset ()
  {
    return m_aData.getCharset ();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public SimpleURL setCharset (@Nullable final Charset aCharset)
  {
    m_aData.setCharset (aCharset);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final SimpleURL getWithPath (@NonNull final String sPath)
  {
    if (m_aData.getPath ().equals (sPath))
      return this;
    return new SimpleURL (m_aData.getClone ().setPath (sPath));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final SimpleURL getWithParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    if (EqualsHelper.equals (m_aData.params (), aParams))
      return this;
    return new SimpleURL (m_aData.getClone ().setParams (aParams));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final SimpleURL getWithAnchor (@Nullable final String sAnchor)
  {
    if (m_aData.hasAnchor (sAnchor))
      return this;
    return new SimpleURL (m_aData.getClone ().setAnchor (sAnchor));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final SimpleURL getWithCharset (@Nullable final Charset aCharset)
  {
    if (EqualsHelper.equals (m_aData.getCharset (), aCharset))
      return this;
    return new SimpleURL (m_aData.getClone ().setCharset (aCharset));
  }

  /**
   * @return A deep clone of this URL. Never <code>null</code>.
   */
  @NonNull
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
