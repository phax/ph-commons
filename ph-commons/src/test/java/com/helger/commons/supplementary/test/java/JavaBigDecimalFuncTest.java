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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.equals.EqualsHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class JavaBigDecimalFuncTest
{
  @SuppressFBWarnings ("RV_RETURN_VALUE_IGNORED")
  @Test
  public void testDivide ()
  {
    final BigDecimal aBD100 = CGlobal.BIGDEC_100;
    final BigDecimal aPerc = new BigDecimal ("20.0");
    final BigDecimal a100AndPerc = aBD100.add (aPerc);
    try
    {
      // 100/120 -> indefinite precision
      aBD100.divide (a100AndPerc);
      fail ();
    }
    catch (final ArithmeticException ex)
    {}

    // With rounding mode it is fine
    assertNotNull (aBD100.divide (a100AndPerc, 2, RoundingMode.HALF_UP));
  }

  @Test
  public void testCompareEquals ()
  {
    // compareTo with different scale
    assertEquals (0, new BigDecimal ("20").compareTo (new BigDecimal ("20.0")));

    // equals with different scale
    assertFalse (new BigDecimal ("20").equals (new BigDecimal ("20.0")));
    assertTrue (EqualsHelper.equals (new BigDecimal ("20"), new BigDecimal ("20.0")));
    assertTrue (new BigDecimal ("20").setScale (1).equals (new BigDecimal ("20.0")));
  }
}
