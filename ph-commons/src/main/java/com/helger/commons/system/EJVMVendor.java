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

import javax.annotation.Nonnull;

/**
 * Determine the current JVM (Java Virtual Machine) vendor.
 *
 * @author Philip Helger
 */
public enum EJVMVendor
{
  SUN_CLIENT
  {
    @Override
    protected boolean determineIfItIsCurrentJVMVendor ()
    {
      final boolean bIsSunJVM = SUN_VENDOR_NAME.equals (SystemProperties.getJavaVmVendor ());
      return bIsSunJVM && !_isSunOrOracleServerJVM ();
    }
  },

  SUN_SERVER
  {
    @Override
    protected boolean determineIfItIsCurrentJVMVendor ()
    {
      final boolean bIsSunJVM = SUN_VENDOR_NAME.equals (SystemProperties.getJavaVmVendor ());
      return bIsSunJVM && _isSunOrOracleServerJVM ();
    }
  },

  ORACLE_CLIENT
  {
    @Override
    protected boolean determineIfItIsCurrentJVMVendor ()
    {
      final boolean bIsOracleJVM = ORACLE_VENDOR_NAME.equals (SystemProperties.getJavaVmVendor ());
      return bIsOracleJVM && !_isSunOrOracleServerJVM ();
    }
  },

  ORACLE_SERVER
  {
    @Override
    protected boolean determineIfItIsCurrentJVMVendor ()
    {
      final boolean bIsOracleJVM = ORACLE_VENDOR_NAME.equals (SystemProperties.getJavaVmVendor ());
      return bIsOracleJVM && _isSunOrOracleServerJVM ();
    }
  },

  UNKNOWN
  {
    @Override
    protected boolean determineIfItIsCurrentJVMVendor ()
    {
      // Never determined as such :)
      return false;
    }
  };

  private static final String SUN_VENDOR_NAME = "Sun Microsystems Inc.";
  private static final String ORACLE_VENDOR_NAME = "Oracle Corporation";

  private static boolean _isSunOrOracleServerJVM ()
  {
    final String sVM = SystemProperties.getJavaVmName ();
    // Dev machine, Windows Vista 32-bit:
    if ("Java HotSpot(TM) Server VM".equals (sVM))
      return true;

    // Server machine, CentOS 64-bit:
    if ("Java HotSpot(TM) 64-Bit Server VM".equals (sVM))
      return true;

    return false;
  }

  /** The current vendor. */
  private static volatile EJVMVendor s_aInstance = null;

  private final boolean m_bIsIt;

  private EJVMVendor ()
  {
    m_bIsIt = determineIfItIsCurrentJVMVendor ();
  }

  protected abstract boolean determineIfItIsCurrentJVMVendor ();

  public final boolean isJVMVendor ()
  {
    return m_bIsIt;
  }

  /**
   * @return <code>true</code> if this is a Sun JVM (usually for Java &le; 1.6).
   */
  public final boolean isSun ()
  {
    return this == SUN_CLIENT || this == SUN_SERVER;
  }

  /**
   * @return <code>true</code> if this is an Oracle JVM (for Java &ge; 1.7).
   */
  public final boolean isOracle ()
  {
    return this == ORACLE_CLIENT || this == ORACLE_SERVER;
  }

  /**
   * @return The current JVM vendor. If the vendor could not be determined,
   *         {@link #UNKNOWN} is returned and never <code>null</code>.
   */
  @Nonnull
  public static EJVMVendor getCurrentVendor ()
  {
    EJVMVendor ret = s_aInstance;
    if (ret == null)
    {
      // Note: double initialization doesn't matter here

      // Check
      for (final EJVMVendor eVendor : values ())
        if (eVendor.isJVMVendor ())
        {
          ret = eVendor;
          break;
        }
      if (ret == null)
        ret = UNKNOWN;
      s_aInstance = ret;
    }
    return ret;
  }
}
