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
package com.helger.commons.io.resource.inmemory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.IHasByteArray;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An in-memory {@link IReadableResource} based on a byte array.
 *
 * @author Philip Helger
 */
public class ReadableResourceByteArray extends AbstractMemoryReadableResource implements IHasByteArray
{
  public static final boolean DEFAULT_COPY_NEEDED = true;

  private final byte [] m_aBytes;
  private final int m_nOfs;
  private final int m_nLen;
  private final boolean m_bIsCopy;

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
    super (StringHelper.hasText (sResourceID) ? sResourceID : "byte[]");
    ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLen);
    // Create a copy to avoid outside modifications
    m_aBytes = bCopyNeeded ? ArrayHelper.getCopy (aBytes, nOfs, nLen) : aBytes;
    m_nOfs = bCopyNeeded ? 0 : nOfs;
    m_nLen = nLen;
    m_bIsCopy = bCopyNeeded;
  }

  public final boolean isCopy ()
  {
    return m_bIsCopy;
  }

  @Nonnull
  @ReturnsMutableObject
  public final byte [] bytes ()
  {
    return m_aBytes;
  }

  @Nonnegative
  public final int getOffset ()
  {
    return m_nOfs;
  }

  @Nonnegative
  public final int size ()
  {
    return m_nLen;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("byte#", m_aBytes.length)
                            .append ("Offset", m_nOfs)
                            .append ("Length", m_nLen)
                            .append ("IsCopy", m_bIsCopy)
                            .getToString ();
  }
}
