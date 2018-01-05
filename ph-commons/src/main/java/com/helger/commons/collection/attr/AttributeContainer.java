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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all kind of any-any mapping container. This implementation is
 * not thread-safe!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public class AttributeContainer <KEYTYPE, VALUETYPE> extends CommonsLinkedHashMap <KEYTYPE, VALUETYPE> implements
                                IAttributeContainer <KEYTYPE, VALUETYPE>
{
  private final CallbackList <IBeforeSetValueCallback <KEYTYPE, VALUETYPE>> m_aBeforeCallbacks = new CallbackList <> ();
  private final CallbackList <IAfterSetValueCallback <KEYTYPE, VALUETYPE>> m_aAfterCallbacks = new CallbackList <> ();

  public AttributeContainer ()
  {}

  public AttributeContainer (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
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
  @Nonnull
  @ReturnsMutableCopy
  public AttributeContainer <KEYTYPE, VALUETYPE> getClone ()
  {
    return new AttributeContainer <> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    // Required for correct content checking with equalsHelper
    return EqualsHelper.equalsMap (this, (AttributeContainer <?, ?>) o);
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
                            .appendIf ("BeforeCallbacks", m_aBeforeCallbacks, CallbackList::isNotEmpty)
                            .appendIf ("AfterCallbacks", m_aAfterCallbacks, CallbackList::isNotEmpty)
                            .getToString ();
  }
}
