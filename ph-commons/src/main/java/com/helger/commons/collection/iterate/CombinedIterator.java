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
package com.helger.commons.collection.iterate;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.ToStringGenerator;

/**
 * A specific enumeration iterating over two consecutive enumerations.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to be enumerated
 */
public final class CombinedIterator <ELEMENTTYPE> implements Iterator <ELEMENTTYPE>
{
  private final Iterator <? extends ELEMENTTYPE> m_aIter1;
  private final Iterator <? extends ELEMENTTYPE> m_aIter2;
  private boolean m_bFirstIter;

  public CombinedIterator (@Nullable final Iterator <? extends ELEMENTTYPE> aIter1,
                           @Nullable final Iterator <? extends ELEMENTTYPE> aIter2)
  {
    m_aIter1 = aIter1;
    m_aIter2 = aIter2;
    m_bFirstIter = aIter1 != null;
  }

  public boolean hasNext ()
  {
    boolean ret = false;
    if (m_bFirstIter)
    {
      ret = m_aIter1.hasNext ();
      if (!ret)
        m_bFirstIter = false;
    }
    if (!m_bFirstIter)
      ret = m_aIter2 != null && m_aIter2.hasNext ();
    return ret;
  }

  @Nullable
  public ELEMENTTYPE next ()
  {
    if (m_bFirstIter)
      return m_aIter1.next ();
    if (m_aIter2 == null)
      throw new NoSuchElementException ();
    return m_aIter2.next ();
  }

  public void remove ()
  {
    if (m_bFirstIter)
      m_aIter1.remove ();
    else
      if (m_aIter2 == null)
        throw new UnsupportedOperationException ();
      else
        m_aIter2.remove ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter1", m_aIter1).append ("iter2", m_aIter2).toString ();
  }

  @SuppressWarnings ("unchecked")
  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> create (@Nullable final Iterator <? extends ELEMENTTYPE> aIter1,
                                                             @Nullable final Iterator <? extends ELEMENTTYPE> aIter2)
  {
    if (aIter1 == null && aIter2 == null)
      return new EmptyIterator <ELEMENTTYPE> ();
    if (aIter1 == null)
      return (Iterator <ELEMENTTYPE>) aIter2;
    if (aIter2 == null)
      return (Iterator <ELEMENTTYPE>) aIter1;
    return new CombinedIterator <ELEMENTTYPE> (aIter1, aIter2);
  }
}
