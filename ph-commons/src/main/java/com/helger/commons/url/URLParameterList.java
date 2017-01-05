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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsLinkedHashSet;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsOrderedSet;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.collection.multimap.MultiLinkedHashMapArrayListBased;

/**
 * A list of URL parameters with a sanity API. It allows for multiple URL
 * parameters with the same name and thereby maintaining the order of the URL
 * parameters.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class URLParameterList extends CommonsArrayList <URLParameter>
{
  public URLParameterList ()
  {}

  public URLParameterList (@Nullable final List <? extends URLParameter> aOther)
  {
    super (aOther);
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
  public URLParameterList add (@Nonnull @Nonempty final String sName)
  {
    return add (sName, "");
  }

  @Nonnull
  public URLParameterList add (@Nonnull final Map.Entry <String, String> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, @Nonnull final BigInteger aValue)
  {
    return add (sName, aValue.toString ());
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    add (new URLParameter (sName, sValue));
    return this;
  }

  @Nonnull
  public final URLParameterList addAll (@Nullable final Map <String, String> aParams)
  {
    if (aParams != null)
      for (final Map.Entry <String, String> aEntry : aParams.entrySet ())
        add (aEntry);
    return this;
  }

  @Nonnull
  public final URLParameterList addAll (@Nullable final List <? extends URLParameter> aParams)
  {
    if (aParams != null)
      for (final URLParameter aParam : aParams)
        add (aParam);
    return this;
  }

  @Nonnull
  public URLParameterList addAll (@Nonnull @Nonempty final String sName, @Nullable final String... aValues)
  {
    if (aValues != null)
      for (final String sValue : aValues)
        add (sName, sValue);
    return this;
  }

  /**
   * Remove all parameter with the given name.
   *
   * @param sName
   *        The key to remove
   * @return this
   */
  @Nonnull
  public URLParameterList remove (@Nullable final String sName)
  {
    removeIf (aParam -> aParam.hasName (sName));
    return this;
  }

  /**
   * Remove all parameter with the given name and value.
   *
   * @param sName
   *        The key to remove. May be <code>null</code>.
   * @param sValue
   *        The value to be removed. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public URLParameterList remove (@Nullable final String sName, @Nullable final String sValue)
  {
    removeIf (aParam -> aParam.hasName (sName) && aParam.hasValue (sValue));
    return this;
  }

  public boolean contains (@Nullable final String sName)
  {
    return sName != null && containsAny (aParam -> aParam.hasName (sName));
  }

  public boolean contains (@Nullable final String sName, @Nullable final String sValue)
  {
    return sName != null &&
           sValue != null &&
           containsAny (aParam -> aParam.hasName (sName) && aParam.hasValue (sValue));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllParamNames ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet<> ();
    forEach (aParam -> ret.add (aParam.getName ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllParamValues (@Nullable final String sName)
  {
    final ICommonsList <String> ret = new CommonsArrayList<> ();
    if (sName != null)
      findAll (aParam -> aParam.hasName (sName), aParam -> ret.add (aParam.getValue ()));
    return ret;
  }

  /**
   * Get the value of the first parameter with the provided name
   *
   * @param sName
   *        The parameter name to search
   * @return <code>null</code> if no such parameter is present.
   */
  @Nullable
  public String getFirstParamValue (@Nullable final String sName)
  {
    return sName == null ? null : findFirstMapped (aParam -> aParam.hasName (sName), aParam -> aParam.getValue ());
  }

  /**
   * @return A new multi map (map from String to List of String) with all
   *         values. Order may be lost. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public IMultiMapListBased <String, String> getAsMultiMap ()
  {
    final IMultiMapListBased <String, String> ret = new MultiLinkedHashMapArrayListBased<> ();
    forEach (aParam -> ret.putSingle (aParam.getName (), aParam.getValue ()));
    return ret;
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public URLParameterList getClone ()
  {
    return new URLParameterList (this);
  }
}
