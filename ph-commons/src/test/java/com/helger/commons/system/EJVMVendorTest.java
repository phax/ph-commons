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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EJVMVendor}.
 *
 * @author Philip Helger
 */
public final class EJVMVendorTest
{
  @Test
  public void testBasic ()
  {
    for (final EJVMVendor e : EJVMVendor.values ())
      assertSame (e, EJVMVendor.valueOf (e.name ()));

    final EJVMVendor eVendor = EJVMVendor.getCurrentVendor ();
    assertNotNull (eVendor);
  }

  @Test
  public void testIsSun ()
  {
    assertTrue (EJVMVendor.SUN_CLIENT.isSun ());
    assertTrue (EJVMVendor.SUN_SERVER.isSun ());
    assertFalse (EJVMVendor.ORACLE_CLIENT.isSun ());
    assertFalse (EJVMVendor.ORACLE_SERVER.isSun ());
    assertFalse (EJVMVendor.UNKNOWN.isSun ());
  }

  @Test
  public void testIsOracle ()
  {
    assertFalse (EJVMVendor.SUN_CLIENT.isOracle ());
    assertFalse (EJVMVendor.SUN_SERVER.isOracle ());
    assertTrue (EJVMVendor.ORACLE_CLIENT.isOracle ());
    assertTrue (EJVMVendor.ORACLE_SERVER.isOracle ());
    assertFalse (EJVMVendor.UNKNOWN.isOracle ());
  }
}
