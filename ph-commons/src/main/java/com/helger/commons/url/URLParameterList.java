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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;

/**
 * A list of URL parameters with a sanity API. It allows for multiple URL
 * parameters with the same name and thereby maintaining the order of the URL
 * parameters.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class URLParameterList extends CommonsArrayList <URLParameter> implements IURLParameterList <URLParameterList>
{
  public URLParameterList ()
  {}

  public URLParameterList (@Nullable final List <? extends URLParameter> aOther)
  {
    super (aOther);
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
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    forEach (aParam -> ret.add (aParam.getName ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllParamValues (@Nullable final String sName)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
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
    return sName == null ? null : findFirstMapped (aParam -> aParam.hasName (sName), URLParameter::getValue);
  }

  /**
   * @return A new multi map (map from String to List of String) with all
   *         values. Order may be lost. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, ICommonsList <String>> getAsMultiMap ()
  {
    final ICommonsOrderedMap <String, ICommonsList <String>> ret = new CommonsLinkedHashMap <> ();
    forEach (aParam -> ret.computeIfAbsent (aParam.getName (), x -> new CommonsArrayList <> ())
                          .add (aParam.getValue ()));
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
