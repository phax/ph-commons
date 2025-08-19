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
package com.helger.collection.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A reverse iterator for an existing list
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The element type to be iterated
 */
@NotThreadSafe
public class ReverseListIterator <ELEMENTTYPE> implements Iterator <ELEMENTTYPE>
{
  @CodingStyleguideUnaware
  private final List <? extends ELEMENTTYPE> m_aList;
  private int m_nIndex;

  public ReverseListIterator (@Nonnull final List <? extends ELEMENTTYPE> aList)
  {
    m_aList = ValueEnforcer.notNull (aList, "List");
    m_nIndex = aList.size () - 1;
  }

  public boolean hasNext ()
  {
    return m_nIndex >= 0;
  }

  @Nullable
  public ELEMENTTYPE next ()
  {
    if (m_nIndex < 0)
      throw new NoSuchElementException ();
    final ELEMENTTYPE ret = m_aList.get (m_nIndex);
    --m_nIndex;
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("list", m_aList).append ("index", m_nIndex).getToString ();
  }
}
