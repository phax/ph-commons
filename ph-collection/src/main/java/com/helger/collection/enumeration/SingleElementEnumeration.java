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
package com.helger.collection.enumeration;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.ToStringGenerator;

import jakarta.annotation.Nullable;

/**
 * Specialized enumeration for enumerating exactly one element.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of object to enumerate.
 */
@NotThreadSafe
public class SingleElementEnumeration <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  private boolean m_bHasMoreElements;
  private final ELEMENTTYPE m_aElement;

  public SingleElementEnumeration (@Nullable final ELEMENTTYPE aElement)
  {
    m_bHasMoreElements = true;
    m_aElement = aElement;
  }

  public boolean hasMoreElements ()
  {
    return m_bHasMoreElements;
  }

  @Nullable
  public ELEMENTTYPE nextElement ()
  {
    if (m_bHasMoreElements)
    {
      m_bHasMoreElements = false;
      return m_aElement;
    }
    throw new NoSuchElementException ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleElementEnumeration <?> rhs = (SingleElementEnumeration <?>) o;
    return m_bHasMoreElements == rhs.m_bHasMoreElements && EqualsHelper.equals (m_aElement, rhs.m_aElement);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bHasMoreElements).append (m_aElement).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("hasMoreElements", m_bHasMoreElements).append ("element", m_aElement).getToString ();
  }
}
