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
package com.helger.collection.map;

import java.io.Serializable;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.lang.IHasSize;

/**
 * Special int-Set. Uses {@link IntObjectMap} internally.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
@NotThreadSafe
public class IntSet implements IHasSize, Serializable
{
  private final IntObjectMap <Boolean> m_aMap;

  public IntSet ()
  {
    this (16);
  }

  public IntSet (final int nSize)
  {
    this (nSize, 0.75f);
  }

  public IntSet (final int nSize, final float fFillFactor)
  {
    m_aMap = new IntObjectMap <> (nSize, fFillFactor);
  }

  public int size ()
  {
    return m_aMap.size ();
  }

  public boolean isEmpty ()
  {
    return m_aMap.isEmpty ();
  }

  public boolean contains (final int key)
  {
    return m_aMap.get (key) != null;
  }

  public boolean add (final int key)
  {
    return m_aMap.put (key, Boolean.TRUE) == null;
  }

  public void forEach (@Nonnull final IntConsumer aConsumer)
  {
    m_aMap.forEach ( (k, v) -> aConsumer.accept (k));
  }
}
