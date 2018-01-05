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

import javax.annotation.Nonnull;

import com.helger.commons.state.EChange;

/**
 * Special mutable attribute container with a String key and a String value.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public interface IStringMap extends IAttributeContainer <String, String>
{
  @Nonnull
  default EChange putIn (@Nonnull final String sName, final boolean bValue)
  {
    return putIn (sName, Boolean.toString (bValue));
  }

  @Nonnull
  default EChange putIn (@Nonnull final String sName, final int nValue)
  {
    return putIn (sName, Integer.toString (nValue));
  }

  @Nonnull
  default EChange putIn (@Nonnull final String sName, final long nValue)
  {
    return putIn (sName, Long.toString (nValue));
  }

  @Nonnull
  default EChange putIn (@Nonnull final String sName, final short nValue)
  {
    return putIn (sName, Short.toString (nValue));
  }

  @Nonnull
  default EChange putIn (@Nonnull final String sName, final double dValue)
  {
    return putIn (sName, Double.toString (dValue));
  }

  @Nonnull
  default EChange putIn (@Nonnull final String sName, final float fValue)
  {
    return putIn (sName, Float.toString (fValue));
  }

  // Change return type
  @Nonnull
  IStringMap getClone ();
}
