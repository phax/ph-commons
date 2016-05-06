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
import java.net.URL;
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
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
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
public class SimpleURL implements ISimpleURL, ICloneable <SimpleURL>
{
  private final String m_sPath;
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
    addAll (aParams);
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
    addAll (aParams);
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
    addAll (aParams);
    m_sAnchor = sAnchor;
  }

  public SimpleURL (@Nonnull final IURLData aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");

    m_sPath = aURL.getPath ();
    if (aURL.hasParams ())
      m_aParams.addAll (aURL.directGetAllParams ());
    m_sAnchor = aURL.getAnchor ();
  }

  @Nonnull
  public final String getPath ()
  {
    return m_sPath;
  }

  public final boolean hasParams ()
  {
    return m_aParams.isNotEmpty ();
  }

  @Nonnegative
  public final int getParamCount ()
  {
    return m_aParams.size ();
  }

  public final boolean containsParam (@Nullable final String sName)
  {
    return getParam (sName) != null;
  }

  @Nullable
  public final String getParam (@Nullable final String sName)
  {
    for (final URLParameter aParam : m_aParams)
      if (aParam.hasName (sName))
        return aParam.getValue ();
    return null;
  }

  @Nullable
  public final ICommonsList <String> getAllParams (@Nullable final String sName)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    for (final URLParameter aParam : m_aParams)
      if (aParam.hasName (sName))
        ret.add (aParam.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public final URLParameterList directGetAllParams ()
  {
    return m_aParams;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final URLParameterList getAllParams ()
  {
    return m_aParams.getClone ();
  }

  /**
   * Add a parameter without a value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor
   *        empty.
   * @return this
   */
  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName)
  {
    return add (sName, "");
  }

  @Nonnull
  public SimpleURL add (@Nonnull final Map.Entry <String, String> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    final String sRealValue = sValue != null ? sValue : "";
    return add (new URLParameter (sName, sRealValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull final URLParameter aParam)
  {
    ValueEnforcer.notNull (aParam, "Param");
    m_aParams.add (aParam);
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  public SimpleURL add (@Nonnull @Nonempty final String sName, @Nullable final BigInteger aValue)
  {
    return add (sName, aValue != null ? aValue.toString () : null);
  }

  /**
   * Add the parameter of the passed value predicate evaluates to true.
   *
   * @param sName
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
  public SimpleURL addIf (@Nonnull @Nonempty final String sName,
                          @Nullable final String sValue,
                          @Nonnull final Predicate <String> aFilter)
  {
    if (aFilter.test (sValue))
      add (sName, sValue);
    return this;
  }

  @Nonnull
  public final SimpleURL addAll (@Nullable final Map <String, String> aParams)
  {
    if (CollectionHelper.isNotEmpty (aParams))
      for (final Map.Entry <String, String> aEntry : aParams.entrySet ())
        add (aEntry.getKey (), aEntry.getValue ());
    return this;
  }

  @Nonnull
  public final SimpleURL addAll (@Nullable final Iterable <? extends URLParameter> aParams)
  {
    if (CollectionHelper.isNotEmpty (aParams))
      for (final URLParameter aParam : aParams)
        add (aParam);
    return this;
  }

  /**
   * Remove all parameters with the given name.
   *
   * @param sName
   *        The key to remove. May be <code>null</code> in which case nothing
   *        happens.
   * @return this
   */
  @Nonnull
  public SimpleURL remove (@Nullable final String sName)
  {
    m_aParams.remove (sName);
    return this;
  }

  /**
   * Remove all parameter with the given name.
   *
   * @param sName
   *        The key to remove. May be <code>null</code>.
   * @param sValue
   *        The value to be removed. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public SimpleURL remove (@Nullable final String sName, @Nullable final String sValue)
  {
    m_aParams.remove (sName, sValue);
    return this;
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
    return new ToStringGenerator (null).append ("path", m_sPath)
                                       .appendIf ("params", m_aParams, ICommonsList::isNotEmpty)
                                       .appendIfNotNull ("anchor", m_sAnchor)
                                       .toString ();
  }
}
