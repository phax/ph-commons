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
package com.helger.collection.enumeration;

import java.util.Enumeration;
import java.util.Iterator;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This is a helper class to create an {@link Enumeration} from an existing
 * {@link Iterator}
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Type to be enumerated
 */
public final class EnumerationFromIterator <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  private final Iterator <? extends ELEMENTTYPE> m_aIter;

  public EnumerationFromIterator (@NonNull final Iterable <? extends ELEMENTTYPE> aCont)
  {
    this (aCont.iterator ());
  }

  public EnumerationFromIterator (@NonNull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    m_aIter = ValueEnforcer.notNull (aIter, "Iterator");
  }

  public boolean hasMoreElements ()
  {
    return m_aIter.hasNext ();
  }

  public ELEMENTTYPE nextElement ()
  {
    return m_aIter.next ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter", m_aIter).getToString ();
  }

  @NonNull
  public static <ELEMENTTYPE> EnumerationFromIterator <ELEMENTTYPE> create (@NonNull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return new EnumerationFromIterator <> (aIter);
  }

  @NonNull
  public static <ELEMENTTYPE> EnumerationFromIterator <ELEMENTTYPE> create (@NonNull final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return new EnumerationFromIterator <> (aCont);
  }
}
