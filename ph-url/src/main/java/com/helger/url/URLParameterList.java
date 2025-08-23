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

import java.util.Iterator;
import java.util.List;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A list of URL parameters with a sanity API. It allows for multiple URL parameters with the same
 * name and thereby maintaining the order of the URL parameters.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class URLParameterList implements IMutableURLParameterList <URLParameterList>, ICloneable <URLParameterList>
{
  private final ICommonsList <URLParameter> m_aList;

  public URLParameterList ()
  {
    m_aList = new CommonsArrayList <> ();
  }

  public URLParameterList (@Nullable final List <? extends URLParameter> aOther)
  {
    m_aList = new CommonsArrayList <> (aOther);
  }

  protected URLParameterList (@Nonnull final URLParameterList aOther)
  {
    m_aList = aOther.m_aList.getClone ();
  }

  @Nonnegative
  public int size ()
  {
    return m_aList.size ();
  }

  public boolean isEmpty ()
  {
    return m_aList.isEmpty ();
  }

  public boolean contains (@Nullable final String sName)
  {
    return sName != null && m_aList.containsAny (aParam -> aParam.hasName (sName));
  }

  public boolean contains (@Nullable final String sName, @Nullable final String sValue)
  {
    return sName != null &&
           sValue != null &&
           m_aList.containsAny (aParam -> aParam.hasName (sName) && aParam.hasValue (sValue));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllParamNames ()
  {
    return new CommonsLinkedHashSet <> (m_aList, URLParameter::getName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllParamValues (@Nullable final String sName)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    if (sName != null)
      m_aList.findAll (aParam -> aParam.hasName (sName), aParam -> ret.add (aParam.getValue ()));
    return ret;
  }

  @Nullable
  public String getFirstParamValue (@Nullable final String sName)
  {
    return sName == null ? null : m_aList.findFirstMapped (aParam -> aParam.hasName (sName), URLParameter::getValue);
  }

  /**
   * @return A new multi map (map from String to List of String) with all values. Order may be lost.
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, ICommonsList <String>> getAsMultiMap ()
  {
    final ICommonsOrderedMap <String, ICommonsList <String>> ret = new CommonsLinkedHashMap <> ();
    m_aList.forEach (aParam -> ret.computeIfAbsent (aParam.getName (), x -> new CommonsArrayList <> ())
                                  .add (aParam.getValue ()));
    return ret;
  }

  @Nonnull
  public Iterator <URLParameter> iterator ()
  {
    return m_aList.iterator ();
  }

  @Nonnull
  public URLParameterList add (final URLParameter aURLParam)
  {
    ValueEnforcer.notNull (aURLParam, "URLParam");
    m_aList.add (aURLParam);
    return this;
  }

  @Nonnull
  public EChange remove (@Nullable final String sName)
  {
    return EChange.valueOf (m_aList.removeIf (aParam -> aParam.hasName (sName)));
  }

  @Nonnull
  public EChange remove (@Nullable final String sName, @Nullable final String sValue)
  {
    return EChange.valueOf (m_aList.removeIf (aParam -> aParam.hasName (sName) && aParam.hasValue (sValue)));
  }

  @Nonnull
  public EChange removeAll ()
  {
    return m_aList.removeAll ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public URLParameterList getClone ()
  {
    return new URLParameterList (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final URLParameterList rhs = (URLParameterList) o;
    return m_aList.equals (rhs.m_aList);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aList).getHashCode ();
  }
}
