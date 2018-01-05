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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Read-only implementation of {@link ISimpleURL}
 *
 * @author Philip Helger
 */
@Immutable
public class URLData implements ISimpleURL
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
    m_aParams = aParams != null ? aParams : new URLParameterList ();
    m_sAnchor = sAnchor;
  }

  @Nonnull
  public final String getPath ()
  {
    return m_sPath;
  }

  @Nonnull
  @ReturnsMutableObject
  public final URLParameterList params ()
  {
    return m_aParams;
  }

  @Nullable
  public final String getAnchor ()
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
    return new ToStringGenerator (this).append ("Path", m_sPath)
                                       .appendIf ("Params", m_aParams, ICommonsList::isNotEmpty)
                                       .appendIfNotNull ("Anchor", m_sAnchor)
                                       .getToString ();
  }
}
