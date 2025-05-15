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
package com.helger.commons.collection.attr;

import java.util.Map;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.impl.CommonsConcurrentHashMap;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

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
public class AttributeContainerConcurrent <KEYTYPE, VALUETYPE> extends CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> implements
                                          IAttributeContainer <KEYTYPE, VALUETYPE>
{
  private final transient CallbackList <IBeforeSetValueCallback <KEYTYPE, VALUETYPE>> m_aBeforeCallbacks = new CallbackList <> ();
  private final transient CallbackList <IAfterSetValueCallback <KEYTYPE, VALUETYPE>> m_aAfterCallbacks = new CallbackList <> ();

  public AttributeContainerConcurrent ()
  {}

  public AttributeContainerConcurrent (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableObject
  public final CallbackList <IBeforeSetValueCallback <KEYTYPE, VALUETYPE>> beforeSetValueCallbacks ()
  {
    return m_aBeforeCallbacks;
  }

  @Nonnull
  @ReturnsMutableObject
  public final CallbackList <IAfterSetValueCallback <KEYTYPE, VALUETYPE>> afterSetValueCallbacks ()
  {
    return m_aAfterCallbacks;
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
  @Nonnull
  @ReturnsMutableCopy
  public AttributeContainerConcurrent <KEYTYPE, VALUETYPE> getClone ()
  {
    return new AttributeContainerConcurrent <> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
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

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("BeforeCallbacks", m_aBeforeCallbacks)
                            .append ("AfterCallbacks", m_aAfterCallbacks)
                            .getToString ();
  }
}
