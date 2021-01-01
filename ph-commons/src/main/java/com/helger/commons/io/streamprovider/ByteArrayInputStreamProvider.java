/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.io.streamprovider;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.io.ByteArrayWrapper;
import com.helger.commons.io.IHasByteArray;
import com.helger.commons.string.ToStringGenerator;

/**
 * An {@link java.io.InputStream} provider based on a byte array.
 *
 * @author Philip Helger
 */
public class ByteArrayInputStreamProvider implements IHasByteArray
{
  public static final boolean DEFAULT_COPY_NEEDED = false;

  private final ByteArrayWrapper m_aBytes;

  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData)
  {
    this (aData, 0, aData.length, DEFAULT_COPY_NEEDED);
  }

  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData, final boolean bCopyNeeded)
  {
    this (aData, 0, aData.length, bCopyNeeded);
  }

  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    this (aData, nOfs, nLen, DEFAULT_COPY_NEEDED);
  }

  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen,
                                       final boolean bCopyNeeded)
  {
    ValueEnforcer.isArrayOfsLen (aData, nOfs, nLen);
    m_aBytes = new ByteArrayWrapper (aData, nOfs, nLen, bCopyNeeded);
  }

  public final boolean isCopy ()
  {
    return m_aBytes.isCopy ();
  }

  @Nonnull
  @ReturnsMutableObject
  public final byte [] bytes ()
  {
    return m_aBytes.bytes ();
  }

  @Nonnegative
  public final int getOffset ()
  {
    return m_aBytes.getOffset ();
  }

  @Nonnegative
  public final int size ()
  {
    return m_aBytes.size ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Bytes", m_aBytes).getToString ();
  }
}
