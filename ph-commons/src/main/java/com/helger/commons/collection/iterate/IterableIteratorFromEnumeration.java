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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.string.ToStringGenerator;

/**
 * Create an iterable iterator from an existing enumeration.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The element type to be enumerated.
 */
public class IterableIteratorFromEnumeration <ELEMENTTYPE> implements IIterableIterator <ELEMENTTYPE>
{
  private final Enumeration <? extends ELEMENTTYPE> m_aEnum;

  public IterableIteratorFromEnumeration (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    m_aEnum = aEnum;
  }

  public boolean hasNext ()
  {
    return m_aEnum != null && m_aEnum.hasMoreElements ();
  }

  @Nullable
  public ELEMENTTYPE next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();
    return m_aEnum.nextElement ();
  }

  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  @Nonnull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return this;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("enum", m_aEnum).toString ();
  }
}
