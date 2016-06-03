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

import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * A special URL parameter map that is made for best inline usage. It's simply a
 * Map&lt;String,String&gt; with more nifty API :)<br>
 * SMap is short for String-Map
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SMap extends CommonsLinkedHashMap <String, String>
{
  /**
   * Create an empty map.
   */
  public SMap ()
  {}

  /**
   * Create a map base on the given map. Kind of a copy-constructor.
   *
   * @param rhs
   *        The map to add. May not be <code>null</code>.
   */
  public SMap (@Nullable final Map <String, String> rhs)
  {
    if (rhs != null)
      super.putAll (rhs);
  }

  public SMap (@Nonnull final String sName, @Nonnull final String sValue)
  {
    add (sName, sValue);
  }

  public SMap (@Nonnull final String sName, final int nValue)
  {
    add (sName, nValue);
  }

  /**
   * Important: this method must be present, because the underlying AbstractMap
   * otherwise throws an exception if this method is not overridden!!!
   *
   * @param sName
   *        Key
   * @param sValue
   *        Value
   * @return The previously assigned value for the given key or
   *         <code>null</code> if no previously assigned value is present.
   */
  @Override
  @Deprecated
  @DevelopersNote ("Use add instead - only for API compliance!")
  public String put (@Nonnull final String sName, @Nonnull final String sValue)
  {
    return super.put (sName, sValue);
  }

  @Deprecated
  @Override
  @DevelopersNote ("Use add instead - only for API compliance!")
  public void putAll (final Map <? extends String, ? extends String> aMap)
  {
    super.putAll (aMap);
  }

  @Nonnull
  public SMap addIfNotNull (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    return addIfNotNull (sName, TypeConverter.convertIfNecessary (aValue, String.class));
  }

  @Nonnull
  public SMap addIfNotNull (@Nonnull final String sName, @Nullable final String sValue)
  {
    super.putIfNotNull (sName, sValue);
    return this;
  }

  @Nonnull
  public SMap addIf (@Nonnull final String sName,
                     @Nullable final String sValue,
                     @Nonnull final Predicate <String> aFilter)
  {
    super.putIf (sName, sValue, aFilter);
    return this;
  }

  @Nonnull
  public SMap add (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    return add (sName, TypeConverter.convertIfNecessary (aValue, String.class));
  }

  @Nonnull
  public SMap add (@Nonnull final String sName, @Nonnull final String sValue)
  {
    super.put (sName, sValue);
    return this;
  }

  @Nonnull
  public SMap add (@Nonnull final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  public SMap add (@Nonnull final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  public SMap add (@Nonnull final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  public SMap addWithoutValue (@Nonnull final String sName)
  {
    return add (sName, "");
  }

  @Override
  @Nonnull
  public SMap getClone ()
  {
    return new SMap (this);
  }
}
