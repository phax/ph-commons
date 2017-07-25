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
package com.helger.commons.collection.attr;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.impl.CommonsConcurrentHashMap;
import com.helger.commons.hashcode.HashCodeGenerator;

/**
 * Base class for all kind of any-any mapping container. This implementation is
 * thread-safe!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@ThreadSafe
public class AttributeContainerConcurrent <KEYTYPE, VALUETYPE> extends CommonsConcurrentHashMap <KEYTYPE, VALUETYPE>
                                          implements
                                          IMutableAttributeContainer <KEYTYPE, VALUETYPE>
{
  private final CallbackList <IBeforeSetAttributeCallback <KEYTYPE, VALUETYPE>> m_aCallbacks = new CallbackList <> ();

  public AttributeContainerConcurrent ()
  {}

  public AttributeContainerConcurrent (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  public CallbackList <IBeforeSetAttributeCallback <KEYTYPE, VALUETYPE>> beforeSetAttributeCallbacks ()
  {
    return m_aCallbacks;
  }

  @Override
  public boolean containsKey (@Nullable final Object aKey)
  {
    // Cannot handle null keys!
    return aKey != null && super.containsKey (aKey);
  }

  @Override
  @Nullable
  public VALUETYPE get (@Nullable final Object aKey)
  {
    // Cannot handle null keys!
    return aKey != null ? super.get (aKey) : null;
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    final HashCodeGenerator aGen = new HashCodeGenerator (this);
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : entrySet ())
      aGen.append (aEntry.getKey ()).append (aEntry.getValue ());
    return aGen.getHashCode ();
  }
}
