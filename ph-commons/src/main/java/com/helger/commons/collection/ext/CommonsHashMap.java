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
package com.helger.commons.collection.ext;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

public class CommonsHashMap <KEYTYPE, VALUETYPE> extends HashMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsHashMap ()
  {}

  public CommonsHashMap (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashMap (@Nonnegative final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsHashMap <> (this);
  }
}
