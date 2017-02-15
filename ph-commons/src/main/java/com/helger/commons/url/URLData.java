/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IURLData} interface.
 *
 * @author Philip Helger
 */
@Immutable
public class URLData implements IURLData
{
  /**
   * The string representing an empty URL. Must contain at least one character.
   */
  public static final URLData EMPTY_URL_DATA = new URLData ("");

  private final String m_sPath;
  private final URLParameterList m_aParams;
  private final String m_sAnchor;

  public URLData (@Nonnull final String sPath)
  {
    this (sPath, null, null);
  }

  public URLData (@Nonnull final String sPath, @Nullable final URLParameterList aParams)
  {
    this (sPath, aParams, null);
  }

  public URLData (@Nonnull final String sPath, @Nullable final URLParameterList aParams, @Nullable final String sAnchor)
  {
    m_sPath = ValueEnforcer.notNull (sPath, "Path");
    m_aParams = aParams;
    m_sAnchor = sAnchor;
  }

  @Nonnull
  public String getPath ()
  {
    return m_sPath;
  }

  public boolean hasParams ()
  {
    return CollectionHelper.isNotEmpty (m_aParams);
  }

  @Nonnegative
  public int getParamCount ()
  {
    return CollectionHelper.getSize (m_aParams);
  }

  public final boolean containsParam (@Nullable final String sName)
  {
    return getParam (sName) != null;
  }

  @Nullable
  public final String getParam (@Nullable final String sName)
  {
    if (m_aParams != null)
      for (final URLParameter aParam : m_aParams)
        if (aParam.hasName (sName))
          return aParam.getValue ();
    return null;
  }

  @Nullable
  public final ICommonsList <String> getAllParams (@Nullable final String sName)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    if (m_aParams != null)
      for (final URLParameter aParam : m_aParams)
        if (aParam.hasName (sName))
          ret.add (aParam.getValue ());
    return ret;
  }

  @Nullable
  @ReturnsMutableObject ("design")
  public final URLParameterList directGetAllParams ()
  {
    return m_aParams;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final URLParameterList getAllParams ()
  {
    return new URLParameterList (m_aParams);
  }

  @Nullable
  public String getAnchor ()
  {
    return m_sAnchor;
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
    return new ToStringGenerator (this).append ("path", m_sPath)
                                       .appendIfNotNull ("params", m_aParams)
                                       .appendIfNotNull ("anchor", m_sAnchor)
                                       .getToString ();
  }
}
