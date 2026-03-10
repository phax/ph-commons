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
package com.helger.url.data;

import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.ISimpleURL;
import com.helger.url.codec.URLCoder;
import com.helger.url.param.URLParameter;

/**
 * Read-only implementation of {@link ISimpleURL}
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class URLData implements IMutableURLData <URLData>, ICloneable <URLData>
{
  public static final Charset DEFAULT_CHARSET = URLCoder.CHARSET_URL_OBJ;

  /**
   * The string representing an empty URL. Must contain at least one character.
   */
  public static final URLData EMPTY_URL_DATA = new URLData ("", null, null, DEFAULT_CHARSET);

  private String m_sPath;
  private final ICommonsList <URLParameter> m_aParams;
  private String m_sAnchor;
  private Charset m_aCharset;

  /**
   * Copy constructor from any {@link IURLData}.
   *
   * @param aOther
   *        The URL data to copy. May not be <code>null</code>.
   */
  public URLData (@NonNull final IURLData aOther)
  {
    // Create a copy of the parameters
    this (aOther.getPath (), aOther.getAllParams (), aOther.getAnchor (), aOther.getCharset ());
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The URL data to copy. May not be <code>null</code>.
   */
  public URLData (@NonNull final URLData aOther)
  {
    // Create a copy of the parameters
    this (aOther.m_sPath, aOther.m_aParams.getClone (), aOther.m_sAnchor, aOther.m_aCharset);
  }

  /**
   * Constructor with all URL parts.
   *
   * @param sPath
   *        The URL path. May not be <code>null</code>.
   * @param aParams
   *        The URL parameters. May be <code>null</code>.
   * @param sAnchor
   *        The anchor. May be <code>null</code>.
   * @param aCharset
   *        The charset. May be <code>null</code>.
   */
  public URLData (@NonNull final String sPath,
                  @Nullable final ICommonsList <URLParameter> aParams,
                  @Nullable final String sAnchor,
                  @Nullable final Charset aCharset)
  {
    ValueEnforcer.notNull (sPath, "Path");

    m_sPath = sPath;
    m_aParams = aParams != null ? aParams : new CommonsArrayList <> ();
    m_sAnchor = sAnchor;
    m_aCharset = aCharset;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public String getPath ()
  {
    return m_sPath;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public URLData setPath (@NonNull final String sPath)
  {
    ValueEnforcer.notNull (sPath, "Path");
    m_sPath = sPath;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <URLParameter> params ()
  {
    return m_aParams;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <URLParameter> getAllParams ()
  {
    return m_aParams.getClone ();
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getFirstParamValue (@Nullable final String sParamName)
  {
    return m_aParams.findFirstMapped (x -> x.hasName (sParamName), URLParameter::getValue);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public URLData setParams (@Nullable final ICommonsList <URLParameter> aParams)
  {
    m_aParams.setAll (aParams);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getAnchor ()
  {
    return m_sAnchor;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public URLData setAnchor (@Nullable final String sAnchor)
  {
    m_sAnchor = sAnchor;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public URLData setCharset (@Nullable final Charset aCharset)
  {
    m_aCharset = aCharset;
    return this;
  }

  /**
   * @return A deep clone of this URL data. Never <code>null</code>.
   */
  @NonNull
  public URLData getClone ()
  {
    return new URLData (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final URLData rhs = (URLData) o;
    return m_sPath.equals (rhs.m_sPath) &&
           m_aParams.equals (rhs.m_aParams) &&
           EqualsHelper.equals (m_sAnchor, rhs.m_sAnchor) &&
           EqualsHelper.equals (m_aCharset, rhs.m_aCharset);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPath)
                                       .append (m_aParams)
                                       .append (m_sAnchor)
                                       .append (m_aCharset)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Path", m_sPath)
                                       .appendIf ("Params", m_aParams, ICommonsList::isNotEmpty)
                                       .appendIfNotNull ("Anchor", m_sAnchor)
                                       .appendIfNotNull ("Charset", m_aCharset)
                                       .getToString ();
  }

  /**
   * @return A new empty {@link URLData} instance. Never <code>null</code>.
   */
  @NonNull
  public static URLData createEmpty ()
  {
    return EMPTY_URL_DATA.getClone ();
  }
}
