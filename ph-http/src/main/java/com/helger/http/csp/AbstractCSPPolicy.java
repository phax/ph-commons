/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.csp;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Abstract CSP policy declaration.
 *
 * @author Philip Helger
 * @param <T>
 *        The CSP directive type
 */
@NotThreadSafe
public abstract class AbstractCSPPolicy <T extends ICSPDirective>
{
  private final ICommonsList <T> m_aList = new CommonsArrayList <> ();

  public AbstractCSPPolicy ()
  {}

  public boolean isEmpty ()
  {
    return m_aList.isEmpty ();
  }

  public boolean isNotEmpty ()
  {
    return m_aList.isNotEmpty ();
  }

  @Nonnegative
  public int getDirectiveCount ()
  {
    return m_aList.size ();
  }

  @NonNull
  public AbstractCSPPolicy <T> addDirective (@NonNull final T aDirective)
  {
    ValueEnforcer.notNull (aDirective, "Directive");
    m_aList.add (aDirective);
    return this;
  }

  @NonNull
  public EChange removeDirective (@Nullable final T aDirective)
  {
    return m_aList.removeObject (aDirective);
  }

  @NonNull
  public EChange removeDirectiveAtIndex (final int nIndex)
  {
    return m_aList.removeAtIndex (nIndex);
  }

  @NonNull
  public EChange removeAllDirectives ()
  {
    return m_aList.removeAll ();
  }

  @NonNull
  public String getAsString ()
  {
    return StringImplode.getImplodedMappedNonEmpty ("; ", m_aList, ICSPDirective::getAsStringIfHasValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractCSPPolicy <?> rhs = (AbstractCSPPolicy <?>) o;
    return m_aList.equals (rhs.m_aList);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aList).getHashCode ();
  }

  @Override
  @NonNull
  public String toString ()
  {
    return new ToStringGenerator (this).append ("list", m_aList).getToString ();
  }
}
