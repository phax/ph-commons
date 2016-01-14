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
package com.helger.commons.collection.multimap;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;

/**
 * Abstract multi map based on {@link java.util.concurrent.ConcurrentHashMap}.
 * <br>
 * Important note: <code>null</code> keys are not allowed here!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        key type
 * @param <VALUETYPE>
 *        value type
 * @param <COLLTYPE>
 *        contained collection type
 */
@NotThreadSafe
public abstract class AbstractMultiConcurrentHashMap <KEYTYPE, VALUETYPE, COLLTYPE extends Collection <VALUETYPE>>
                                                     extends ConcurrentHashMap <KEYTYPE, COLLTYPE>
                                                     implements IMultiMap <KEYTYPE, VALUETYPE, COLLTYPE>
{
  public AbstractMultiConcurrentHashMap ()
  {}

  public AbstractMultiConcurrentHashMap (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    putSingle (aKey, aValue);
  }

  public AbstractMultiConcurrentHashMap (@Nonnull final KEYTYPE aKey, @Nonnull final COLLTYPE aCollection)
  {
    put (aKey, aCollection);
  }

  public AbstractMultiConcurrentHashMap (@Nullable final Map <? extends KEYTYPE, ? extends COLLTYPE> aCont)
  {
    if (aCont != null)
      putAll (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  protected abstract COLLTYPE createNewCollection ();

  @Nonnull
  @ReturnsMutableObject ("design")
  public COLLTYPE getOrCreate (@Nonnull final KEYTYPE aKey)
  {
    COLLTYPE aCont = get (aKey);
    if (aCont == null)
    {
      aCont = createNewCollection ();
      super.put (aKey, aCont);
    }
    return aCont;
  }
}
