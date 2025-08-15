/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.resource.inmemory;

import java.io.InputStream;
import java.util.Arrays;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.array.bytes.ByteArrayWrapper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.iface.IHasByteArray;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An in-memory {@link IReadableResource} based on a byte array.
 *
 * @author Philip Helger
 */
public class ReadableResourceByteArray extends AbstractMemoryReadableResource implements IHasByteArray
{
  public static final boolean DEFAULT_COPY_NEEDED = true;

  private final ByteArrayWrapper m_aBytes;

  public ReadableResourceByteArray (@Nonnull final byte [] aBytes)
  {
    this (null, aBytes, DEFAULT_COPY_NEEDED);
  }

  public ReadableResourceByteArray (@Nonnull final byte [] aBytes,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    this (null, aBytes, nOfs, nLen, DEFAULT_COPY_NEEDED);
  }

  public ReadableResourceByteArray (@Nonnull final byte [] aBytes, final boolean bCopyNeeded)
  {
    this ((String) null, aBytes, bCopyNeeded);
  }

  public ReadableResourceByteArray (@Nonnull final byte [] aBytes,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    final boolean bCopyNeeded)
  {
    this ((String) null, aBytes, nOfs, nLen, bCopyNeeded);
  }

  public ReadableResourceByteArray (@Nullable final String sResourceID, @Nonnull final byte [] aBytes)
  {
    this (sResourceID, aBytes, DEFAULT_COPY_NEEDED);
  }

  public ReadableResourceByteArray (@Nullable final String sResourceID,
                                    @Nonnull final byte [] aBytes,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    this (sResourceID, aBytes, nOfs, nLen, DEFAULT_COPY_NEEDED);
  }

  public ReadableResourceByteArray (@Nullable final String sResourceID,
                                    @Nonnull final byte [] aBytes,
                                    final boolean bCopyNeeded)
  {
    this (sResourceID, aBytes, 0, aBytes.length, bCopyNeeded);
  }

  public ReadableResourceByteArray (@Nullable final String sResourceID,
                                    @Nonnull final byte [] aBytes,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    final boolean bCopyNeeded)
  {
    // Ensure a unique resource ID
    super (StringHelper.isNotEmpty (sResourceID) ? sResourceID : "bytes-" + nLen + "-" + Arrays.hashCode (aBytes));
    ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLen);
    // Create a copy to avoid outside modifications
    m_aBytes = new ByteArrayWrapper (aBytes, nOfs, nLen, bCopyNeeded);
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
  public int getOffset ()
  {
    return m_aBytes.getOffset ();
  }

  @Nonnegative
  public final int size ()
  {
    return m_aBytes.size ();
  }

  @Override
  public boolean isEmpty ()
  {
    return m_aBytes.isEmpty ();
  }

  @Nonnull
  @Override
  public InputStream getInputStream ()
  {
    return m_aBytes.getInputStream ();
  }

  @Override
  public boolean isReadMultiple ()
  {
    return m_aBytes.isReadMultiple ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Bytes", m_aBytes).getToString ();
  }
}
