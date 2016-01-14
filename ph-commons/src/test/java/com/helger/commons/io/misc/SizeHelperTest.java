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
package com.helger.commons.io.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.CGlobal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for {@link SizeHelper}
 *
 * @author Philip Helger
 */
public final class SizeHelperTest
{
  /**
   * Test constructors
   */
  @Test
  public void testCtor ()
  {
    final SizeHelper sh = SizeHelper.getSizeHelperOfLocale (Locale.CANADA);
    assertNotNull (sh);
    assertNotNull (sh.toString ());

    try
    {
      SizeHelper.getSizeHelperOfLocale (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test method getAsKB
   */
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testGetAsKB ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    try
    {
      aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // default
    assertEquals ("0KB", aSH.getAsKB (0));
    assertEquals ("5KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE));
    assertEquals ("2KB", aSH.getAsKB (2 * CGlobal.BYTES_PER_KILOBYTE));
    assertEquals ("1KB", aSH.getAsKB (2 * CGlobal.BYTES_PER_KILOBYTE - 1));
    assertEquals ("1024KB", aSH.getAsKB (1 * CGlobal.BYTES_PER_MEGABYTE));

    // with decimals
    assertEquals ("0.00KB", aSH.getAsKB (0, 2));
    assertEquals ("5KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, 0));
    assertEquals ("5.0KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, 1));
    assertEquals ("5.00KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, 2));
    assertEquals ("5.000KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, 3));
    assertEquals ("5.0000KB", aSH.getAsKB (5 * CGlobal.BYTES_PER_KILOBYTE, 4));
    assertEquals ("2.00KB", aSH.getAsKB (2 * CGlobal.BYTES_PER_KILOBYTE, 2));
    assertEquals ("1.99KB", aSH.getAsKB (2 * CGlobal.BYTES_PER_KILOBYTE - 10, 2));
    assertEquals ("1024.00KB", aSH.getAsKB (1 * CGlobal.BYTES_PER_MEGABYTE, 2));
  }

  /**
   * Test method getAsMB
   */
  @Test
  public void testGetAsMB ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    // default
    assertEquals ("0MB", aSH.getAsMB (0));
    assertEquals ("5MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE));
    assertEquals ("2MB", aSH.getAsMB (2 * CGlobal.BYTES_PER_MEGABYTE));
    assertEquals ("1MB", aSH.getAsMB (2 * CGlobal.BYTES_PER_MEGABYTE - 1));
    assertEquals ("1024MB", aSH.getAsMB (1 * CGlobal.BYTES_PER_GIGABYTE));

    // with decimals
    assertEquals ("0.00MB", aSH.getAsMB (0, 2));
    assertEquals ("5MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE, 0));
    assertEquals ("5.0MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE, 1));
    assertEquals ("5.00MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE, 2));
    assertEquals ("5.000MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE, 3));
    assertEquals ("5.0000MB", aSH.getAsMB (5 * CGlobal.BYTES_PER_MEGABYTE, 4));
    assertEquals ("2.00MB", aSH.getAsMB (2 * CGlobal.BYTES_PER_MEGABYTE, 2));
    assertEquals ("1.99MB", aSH.getAsMB (2 * CGlobal.BYTES_PER_MEGABYTE - 10 * 1024, 2));
    assertEquals ("1024.00MB", aSH.getAsMB (1 * CGlobal.BYTES_PER_GIGABYTE, 2));
  }

  /**
   * Test method getAsGB
   */
  @Test
  public void testGetAsGB ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    // default
    assertEquals ("0GB", aSH.getAsGB (0));
    assertEquals ("5GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE));

    // with decimals
    assertEquals ("0.00GB", aSH.getAsGB (0, 2));
    assertEquals ("5GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE, 0));
    assertEquals ("5.0GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE, 1));
    assertEquals ("5.00GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE, 2));
    assertEquals ("5.000GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE, 3));
    assertEquals ("5.0000GB", aSH.getAsGB (5 * CGlobal.BYTES_PER_GIGABYTE, 4));
  }

  /**
   * Test method getAsTB
   */
  @Test
  public void testGetAsTB ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    // default
    assertEquals ("0TB", aSH.getAsTB (0));
    assertEquals ("5TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE));

    // with decimals
    assertEquals ("0.00TB", aSH.getAsTB (0, 2));
    assertEquals ("5TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE, 0));
    assertEquals ("5.0TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE, 1));
    assertEquals ("5.00TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE, 2));
    assertEquals ("5.000TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE, 3));
    assertEquals ("5.0000TB", aSH.getAsTB (5 * CGlobal.BYTES_PER_TERABYTE, 4));
  }

  /**
   * Test method getAsPB
   */
  @Test
  public void testGetAsPB ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    // default
    assertEquals ("0PB", aSH.getAsPB (0));
    assertEquals ("5PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE));

    // with decimals
    assertEquals ("0.00PB", aSH.getAsPB (0, 2));
    assertEquals ("5PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE, 0));
    assertEquals ("5.0PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE, 1));
    assertEquals ("5.00PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE, 2));
    assertEquals ("5.000PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE, 3));
    assertEquals ("5.0000PB", aSH.getAsPB (5 * CGlobal.BYTES_PER_PETABYTE, 4));
  }

  @Test
  public void testGetAsMatching ()
  {
    // Use fixed locale for constant decimal separator
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.ENGLISH);

    // default
    assertEquals ("5PB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_PETABYTE));
    assertEquals ("5TB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_TERABYTE));
    assertEquals ("5GB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_GIGABYTE));
    assertEquals ("5MB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_MEGABYTE));
    assertEquals ("5KB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_KILOBYTE));
    assertEquals ("5B", aSH.getAsMatching (5));

    // with decimals
    assertEquals ("5.0PB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_PETABYTE, 1));
    assertEquals ("5.00TB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_TERABYTE, 2));
    assertEquals ("5.000GB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_GIGABYTE, 3));
    assertEquals ("5.0000MB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_MEGABYTE, 4));
    assertEquals ("5.00000KB", aSH.getAsMatching (5 * CGlobal.BYTES_PER_KILOBYTE, 5));
    assertEquals ("5.000000B", aSH.getAsMatching (5, 6));

    // BigInteger
    assertEquals ("5PB", aSH.getAsMatching (new BigInteger ("14000000000000", 16)));
    assertEquals ("5TB", aSH.getAsMatching (new BigInteger ("50000000000", 16)));
    assertEquals ("5.00PB", aSH.getAsMatching (new BigInteger ("14000000000000", 16), 2));
    assertEquals ("5.000TB", aSH.getAsMatching (new BigInteger ("50000000000", 16), 3));

    // BigDecimal
    assertEquals ("5PB", aSH.getAsMatching (new BigDecimal (new BigInteger ("14000000000000", 16))));
    assertEquals ("5TB", aSH.getAsMatching (new BigDecimal (new BigInteger ("50000000000", 16))));
    assertEquals ("5.00PB", aSH.getAsMatching (new BigDecimal (new BigInteger ("14000000000000", 16)), 2));
    assertEquals ("5.000TB", aSH.getAsMatching (new BigDecimal (new BigInteger ("50000000000", 16)), 3));
  }

  @Test
  public void testOutOfBounds ()
  {
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.KOREAN);
    assertNotNull (aSH.getAsMatching (CGlobal.BIGINT_MAX_LONG));
    assertNotNull (aSH.getAsMatching (CGlobal.BIGINT_MIN_LONG));
    assertNotNull (aSH.getAsMatching (CGlobal.BIGDEC_MAX_LONG));
    assertNotNull (aSH.getAsMatching (CGlobal.BIGDEC_MIN_LONG));
    // Too large
    BigInteger aValueInt = CGlobal.BIGINT_MAX_LONG.multiply (CGlobal.BIGINT_MAX_LONG);
    try
    {
      aSH.getAsMatching (aValueInt);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    // Too small
    aValueInt = CGlobal.BIGINT_MIN_LONG.multiply (CGlobal.BIGINT_MAX_LONG);
    try
    {
      aSH.getAsMatching (aValueInt);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    // Too large
    BigDecimal aValueDbl = CGlobal.BIGDEC_MAX_LONG.multiply (CGlobal.BIGDEC_MAX_LONG);
    try
    {
      aSH.getAsMatching (aValueDbl);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    // Too small
    aValueDbl = CGlobal.BIGDEC_MIN_LONG.multiply (CGlobal.BIGDEC_MAX_LONG);
    try
    {
      aSH.getAsMatching (aValueDbl);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
