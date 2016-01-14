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
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.string.ToStringGenerator;

/**
 * A simple iterator that changes the object type from a source type to a
 * destination type.
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The type of the source iterator
 * @param <ELEMENTTYPE>
 *        The type of this iterator
 */
public class ConvertIterator <SRCTYPE, ELEMENTTYPE> implements IIterableIterator <ELEMENTTYPE>
{
  // base iterator
  private final Iterator <? extends SRCTYPE> m_aBaseIter;
  // the converter to use
  private final Function <SRCTYPE, ELEMENTTYPE> m_aConverter;

  /**
   * Constructor.
   *
   * @param aBaseIter
   *        The base iterable iterator to use. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   */
  public ConvertIterator (@Nonnull final IIterableIterator <? extends SRCTYPE> aBaseIter,
                          @Nonnull final Function <SRCTYPE, ELEMENTTYPE> aConverter)
  {
    this (aBaseIter.iterator (), aConverter);
  }

  /**
   * Constructor.
   *
   * @param aBaseCont
   *        The collection to iterate. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   */
  public ConvertIterator (@Nonnull final Iterable <? extends SRCTYPE> aBaseCont,
                          @Nonnull final Function <SRCTYPE, ELEMENTTYPE> aConverter)
  {
    this (aBaseCont.iterator (), aConverter);
  }

  /**
   * Constructor.
   *
   * @param aBaseIter
   *        The base iterator to use. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   */
  public ConvertIterator (@Nonnull final Iterator <? extends SRCTYPE> aBaseIter,
                          @Nonnull final Function <SRCTYPE, ELEMENTTYPE> aConverter)
  {
    m_aBaseIter = ValueEnforcer.notNull (aBaseIter, "BaseIterator");
    m_aConverter = ValueEnforcer.notNull (aConverter, "Filter");
  }

  /**
   * @return The converter as specified in the constructor.
   */
  @Nonnull
  public Function <SRCTYPE, ELEMENTTYPE> getConverter ()
  {
    return m_aConverter;
  }

  public boolean hasNext ()
  {
    return m_aBaseIter.hasNext ();
  }

  @Nullable
  public ELEMENTTYPE next ()
  {
    final SRCTYPE ret = m_aBaseIter.next ();
    return m_aConverter.apply (ret);
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

  // equals and hashCode wont work, because standard Java iterators don't
  // implement this!

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("baseIter", m_aBaseIter).append ("converter", m_aConverter).toString ();
  }
}
