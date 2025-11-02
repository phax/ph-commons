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
package com.helger.typeconvert.collection;

import java.util.Map;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.typeconvert.impl.TypeConverter;

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

  public StringMap (@NonNull final Map <String, String> aMap)
  {
    super (aMap);
  }

  public StringMap (@NonNull final String sName, @NonNull final String sValue)
  {
    put (sName, sValue);
  }

  @NonNull
  public final StringMap addIfNotNull (@NonNull final String sName, @NonNull final Object aValue)
  {
    return addIfNotNull (sName, TypeConverter.convert (aValue, String.class));
  }

  @NonNull
  public final StringMap addIfNotNull (@NonNull final String sName, @Nullable final String sValue)
  {
    super.putIfNotNull (sName, sValue);
    return this;
  }

  @NonNull
  public final StringMap addIf (@NonNull final String sName,
                                @Nullable final String sValue,
                                @NonNull final Predicate <? super String> aFilter)
  {
    super.putIf (sName, sValue, aFilter);
    return this;
  }

  @NonNull
  public final StringMap add (@NonNull final String sName, @NonNull final Object aValue)
  {
    return add (sName, TypeConverter.convert (aValue, String.class));
  }

  @NonNull
  public final StringMap add (@NonNull final String sName, @Nullable final String sValue)
  {
    putIn (sName, sValue);
    return this;
  }

  @NonNull
  public final StringMap add (@NonNull final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @NonNull
  public final StringMap add (@NonNull final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @NonNull
  public final StringMap add (@NonNull final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @NonNull
  public final StringMap addWithoutValue (@NonNull final String sName)
  {
    return add (sName, "");
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public StringMap getClone ()
  {
    return new StringMap (this);
  }
}
