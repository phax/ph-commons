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
package com.helger.commons.collection.attr;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Base class for all kind of string-object mapping container. This
 * implementation is not thread-safe!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 */
@NotThreadSafe
public class MapBasedAttributeContainerAny <KEYTYPE> extends MapBasedAttributeContainer <KEYTYPE, Object>
                                           implements IMutableAttributeContainerAny <KEYTYPE>
{
  public MapBasedAttributeContainerAny ()
  {
    super ();
  }

  public MapBasedAttributeContainerAny (@Nonnull final KEYTYPE sKey, @Nullable final Object aValue)
  {
    super (sKey, aValue);
  }

  public MapBasedAttributeContainerAny (@Nonnull final Map <? extends KEYTYPE, ? extends Object> aMap)
  {
    super (aMap);
  }

  public MapBasedAttributeContainerAny (@Nonnull final IAttributeContainer <? extends KEYTYPE, ? extends Object> aCont)
  {
    super (aCont);
  }

  protected MapBasedAttributeContainerAny (final boolean bDummy, @Nonnull final Map <KEYTYPE, Object> aAttrMap)
  {
    super (bDummy, aAttrMap);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public MapBasedAttributeContainerAny <KEYTYPE> getClone ()
  {
    return new MapBasedAttributeContainerAny <KEYTYPE> (this);
  }
}
