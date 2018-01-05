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
package com.helger.commons.collection.attr;

import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Base class for all kind of string-string mapping container. This
 * implementation is not thread-safe!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StringMap extends AttributeContainer <String, String> implements IStringMap
{
  public StringMap ()
  {
    super ();
  }

  public StringMap (@Nonnull final Map <String, String> aMap)
  {
    super (aMap);
  }

  public StringMap (@Nonnull final String sName, @Nonnull final String sValue)
  {
    put (sName, sValue);
  }

  @Nonnull
  public final StringMap addIfNotNull (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    return addIfNotNull (sName, TypeConverter.convert (aValue, String.class));
  }

  @Nonnull
  public final StringMap addIfNotNull (@Nonnull final String sName, @Nullable final String sValue)
  {
    super.putIfNotNull (sName, sValue);
    return this;
  }

  @Nonnull
  public final StringMap addIf (@Nonnull final String sName,
                                @Nullable final String sValue,
                                @Nonnull final Predicate <? super String> aFilter)
  {
    super.putIf (sName, sValue, aFilter);
    return this;
  }

  @Nonnull
  public final StringMap add (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    return add (sName, TypeConverter.convert (aValue, String.class));
  }

  @Nonnull
  public final StringMap add (@Nonnull final String sName, @Nullable final String sValue)
  {
    putIn (sName, sValue);
    return this;
  }

  @Nonnull
  public final StringMap add (@Nonnull final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  public final StringMap add (@Nonnull final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  public final StringMap add (@Nonnull final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  public final StringMap addWithoutValue (@Nonnull final String sName)
  {
    return add (sName, "");
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public StringMap getClone ()
  {
    return new StringMap (this);
  }
}
