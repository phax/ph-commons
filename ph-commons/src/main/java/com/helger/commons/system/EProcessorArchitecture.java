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
package com.helger.commons.system;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;

import com.helger.commons.CGlobal;
import com.helger.commons.string.StringParser;

/**
 * Enum for representing the current processor architecture. Works only on Sun
 * JVMs using the proprietary system property <code>sun.arch.data.model</code>.
 *
 * @author Philip Helger
 */
public enum EProcessorArchitecture
{
  /** Unknown architecture - could not be determined. */
  UNKNOWN (CGlobal.ILLEGAL_UINT),
  /** 32 bit architecture. */
  ARCH_32 (32),
  /** 64 bit architecture. */
  ARCH_64 (64);

  // This still holds true for current Oracle runtime 1.8.0_40
  private static final String SYSTEM_PROPERTY_SUN_ARCH_DATA_MODEL = "sun.arch.data.model";

  /** The current architecture. */
  private static volatile EProcessorArchitecture s_aInstance = null;

  private final int m_nBits;

  private EProcessorArchitecture (final int nBits)
  {
    if (nBits > 0 && (nBits % CGlobal.BITS_PER_BYTE) != 0)
      throw new IllegalArgumentException ("Passed bit count is illegal: " + nBits);
    m_nBits = nBits;
  }

  /**
   * @return The number of bits of this architecture. May be
   *         {@link CGlobal#ILLEGAL_UINT} for the unknown architecture.
   */
  public int getBits ()
  {
    return m_nBits;
  }

  /**
   * @return The number of bytes of this architecture (=bits/8). May be
   *         {@link CGlobal#ILLEGAL_UINT} for the unknown architecture.
   */
  public int getBytes ()
  {
    return m_nBits == CGlobal.ILLEGAL_UINT ? m_nBits : m_nBits / CGlobal.BITS_PER_BYTE;
  }

  /**
   * @return The number of bits in the current architecture or
   *         {@link CGlobal#ILLEGAL_UINT} if this is undetermined.
   */
  @CheckForSigned
  public static int getCurrentArchitectureBits ()
  {
    final String sPropertyValue = SystemProperties.getPropertyValue (SYSTEM_PROPERTY_SUN_ARCH_DATA_MODEL);
    return StringParser.parseInt (sPropertyValue, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the processor architecture based on the passed number of bits.
   *
   * @param nBits
   *        The number of bits to get the processor architecture from.
   * @return {@link #UNKNOWN} if no processor architecture could be determined.
   */
  @Nonnull
  public static EProcessorArchitecture forBits (final int nBits)
  {
    for (final EProcessorArchitecture eArch : values ())
      if (nBits == eArch.getBits ())
        return eArch;
    return UNKNOWN;
  }

  /**
   * @return The current processor architecture if running inside a Sun JVM. If
   *         no processor architecture could be determined, {@link #UNKNOWN} is
   *         returned and never <code>null</code>.
   */
  @Nonnull
  static EProcessorArchitecture getCurrentArchitecture ()
  {
    EProcessorArchitecture ret = s_aInstance;
    if (ret == null)
    {
      // Note: double initialization doesn't matter here
      ret = forBits (getCurrentArchitectureBits ());
      s_aInstance = ret;
    }
    return ret;
  }
}
