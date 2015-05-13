/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.attrs;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.state.EChange;

/**
 * Base class for all kind of string-object mapping container. This
 * implementation is not thread-safe!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedAttributeContainer extends MapBasedGenericAttributeContainer <String, Object> implements
                                                                                                  IMutableAttributeContainer
{
  public MapBasedAttributeContainer ()
  {}

  public MapBasedAttributeContainer (@Nonnull final String sKey, @Nullable final Object aValue)
  {
    super (sKey, aValue);
  }

  public MapBasedAttributeContainer (@Nonnull final Map <? extends String, ? extends Object> aMap)
  {
    super (aMap);
  }

  public MapBasedAttributeContainer (@Nonnull final IGenericAttributeContainer <? extends String, ? extends Object> aCont)
  {
    super (aCont);
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final boolean dValue)
  {
    return setAttribute (sName, Boolean.valueOf (dValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final int nValue)
  {
    return setAttribute (sName, Integer.valueOf (nValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final long nValue)
  {
    return setAttribute (sName, Long.valueOf (nValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final double dValue)
  {
    return setAttribute (sName, Double.valueOf (dValue));
  }

  public boolean getAndSetAttributeFlag (@Nonnull final String sName)
  {
    final Object aOldValue = getAttributeObject (sName);
    if (aOldValue != null)
    {
      // Attribute flag is already present
      return true;
    }
    // Attribute flag is not yet present -> set it
    setAttribute (sName, Boolean.TRUE);
    return false;
  }

  @Override
  public MapBasedAttributeContainer getClone ()
  {
    return new MapBasedAttributeContainer (this);
  }
}
