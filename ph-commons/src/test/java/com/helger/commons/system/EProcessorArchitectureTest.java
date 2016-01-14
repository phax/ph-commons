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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.CGlobal;

/**
 * Test class for class {@link EProcessorArchitecture}.
 *
 * @author Philip Helger
 */
public final class EProcessorArchitectureTest
{
  @Test
  public void testAll ()
  {
    for (final EProcessorArchitecture e : EProcessorArchitecture.values ())
    {
      assertSame (e, EProcessorArchitecture.valueOf (e.name ()));
      assertSame (e, EProcessorArchitecture.forBits (e.getBits ()));
    }

    final EProcessorArchitecture eArch = EProcessorArchitecture.getCurrentArchitecture ();
    assertNotNull (eArch);
    assertTrue (eArch.getBits () > 0);
    assertTrue (eArch.getBytes () > 0);

    if (EJVMVendor.getCurrentVendor ().isSun ())
    {
      // For Sun JVMs the architecture must be determined!
      assertTrue (eArch != EProcessorArchitecture.UNKNOWN);
    }
    assertEquals (CGlobal.ILLEGAL_UINT, EProcessorArchitecture.UNKNOWN.getBytes ());
    assertEquals (CGlobal.ILLEGAL_UINT, EProcessorArchitecture.UNKNOWN.getBits ());
    assertSame (EProcessorArchitecture.UNKNOWN, EProcessorArchitecture.forBits (1));
  }
}
