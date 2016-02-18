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

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than
 * {@link java.net.URL}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SimpleURL implements ISimpleURL, ICloneable <SimpleURL>
{
  private final String m_sPath;
  private ICommonsMap <String, String> m_aParams;
  private String m_sAnchor;

  public SimpleURL ()
  {
    this (URLData.EMPTY_URL_DATA);
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
    if (CollectionHelper.isNotEmpty (aParams))
    {
      // m_aParams may already be non-null
      if (m_aParams == null)
        m_aParams = new CommonsLinkedHashMap <> ();
      m_aParams.putAll (aParams);
    }
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nonnull final Charset aCharset,
                    @Nullable final Map <String, String> aParams)
  {
    this (sHref, aCharset);
    if (CollectionHelper.isNotEmpty (aParams))
    {
      // m_aParams may already be non-null
      if (m_aParams == null)
        m_aParams = new CommonsLinkedHashMap <> ();
      m_aParams.putAll (aParams);
    }
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, aParams);
    m_sAnchor = sAnchor;
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nonnull final Charset aCharset,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    this (sHref, aCharset, aParams);
    m_sAnchor = sAnchor;
  }

  public SimpleURL (@Nonnull final IURLData aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");

    m_sPath = aURL.getPath ();
    if (aURL.hasParams ())
      m_aParams = aURL.getAllParams ();
    m_sAnchor = aURL.getAnchor ();
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

  @Nullable
  public final String getParam (@Nullable final String sKey)
  {
    return m_aParams == null ? null : m_aParams.get (sKey);
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public final ICommonsMap <String, String> directGetAllParams ()
  {
    return m_aParams;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsMap <String, String> getAllParams ()
  {
    return CollectionHelper.newOrderedMap (m_aParams);
  }

  /**
   * Add a parameter without a value
   *
   * @param sKey
   *        The name of the parameter. May neither be <code>null</code> nor
   *        empty.
   * @return this
   */
  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey)
  {
    return add (sKey, "");
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sKey, "Key");
    ValueEnforcer.notNull (sValue, "Value");

    if (m_aParams == null)
      m_aParams = new CommonsLinkedHashMap <> ();
    m_aParams.put (sKey, sValue);
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey, final boolean bValue)
  {
    return add (sKey, Boolean.toString (bValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey, final int nValue)
  {
    return add (sKey, Integer.toString (nValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey, final long nValue)
  {
    return add (sKey, Long.toString (nValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sKey, @Nonnull final BigInteger aValue)
  {
    return add (sKey, aValue.toString ());
  }

  @Nonnull
  public SimpleURL addIfNonNull (@Nonnull @Nonempty final String sKey, @Nullable final String sValue)
  {
    if (sValue != null)
      add (sKey, sValue);
    return this;
  }

  /**
   * Add the parameter of the passed value predicate evaluates to true.
   *
   * @param sKey
   *        Parameter name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        Parameter value. May not be <code>null</code> if the predicate
   *        evaluates to <code>true</code>.
   * @param aFilter
   *        The predicate to be evaluated on the value. May not be
   *        <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public SimpleURL addIf (@Nonnull @Nonempty final String sKey,
                          @Nullable final String sValue,
                          @Nonnull final Predicate <String> aFilter)
  {
    if (aFilter.test (sValue))
      add (sKey, sValue);
    return this;
  }

  @Nonnull
  public SimpleURL addAll (@Nullable final Map <String, String> aParams)
  {
    if (CollectionHelper.isNotEmpty (aParams))
    {
      if (m_aParams == null)
        m_aParams = new CommonsLinkedHashMap <> ();
      m_aParams.putAll (aParams);
    }
    return this;
  }

  /**
   * Remove the parameter with the given key.
   *
   * @param sKey
   *        The key to remove
   * @return this
   */
  @Nonnull
  public SimpleURL remove (@Nullable final String sKey)
  {
    if (m_aParams != null)
      m_aParams.remove (sKey);
    return this;
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
