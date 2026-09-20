/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.permissionspolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Test class for class {@link PermissionsPolicyDirective}.
 *
 * @author Philip Helger
 */
public final class PermissionsPolicyDirectiveTest
{
  @Test
  public void testIsValidName ()
  {
    assertTrue (PermissionsPolicyDirective.isValidName ("camera"));
    assertTrue (PermissionsPolicyDirective.isValidName ("ch-ua-high-entropy-values"));
    assertTrue (PermissionsPolicyDirective.isValidName ("abc123"));

    assertFalse (PermissionsPolicyDirective.isValidName (null));
    assertFalse (PermissionsPolicyDirective.isValidName (""));
    assertFalse (PermissionsPolicyDirective.isValidName ("with space"));
    assertFalse (PermissionsPolicyDirective.isValidName ("with;semicolon"));
  }

  @Test
  public void testIsValidValue ()
  {
    // Empty values are allowed
    assertTrue (PermissionsPolicyDirective.isValidValue (null));
    assertTrue (PermissionsPolicyDirective.isValidValue (""));
    assertTrue (PermissionsPolicyDirective.isValidValue ("(self)"));
    assertTrue (PermissionsPolicyDirective.isValidValue ("with space"));

    assertFalse (PermissionsPolicyDirective.isValidValue ("with;semicolon"));
    assertFalse (PermissionsPolicyDirective.isValidValue ("with,comma"));
  }

  @Test
  public void testBasic ()
  {
    final PermissionsPolicyDirective aPPD = new PermissionsPolicyDirective ("camera", "(self)");
    assertEquals ("camera", aPPD.getName ());
    assertEquals ("(self)", aPPD.getValue ());
    assertNull (aPPD.getReportTo ());
    assertNotNull (aPPD.toString ());

    final PermissionsPolicyDirective aWithReport = aPPD.getWithReportTo ("endpoint");
    assertEquals ("camera", aWithReport.getName ());
    assertEquals ("(self)", aWithReport.getValue ());
    assertEquals ("endpoint", aWithReport.getReportTo ());

    assertEquals (aPPD, aPPD);
    assertEquals (aPPD, new PermissionsPolicyDirective ("camera", "(self)", null));
    assertEquals (aPPD.hashCode (), new PermissionsPolicyDirective ("camera", "(self)").hashCode ());
    assertNotEquals (aPPD, null);
    assertNotEquals (aPPD, "any other type");
    assertNotEquals (aPPD, new PermissionsPolicyDirective ("microphone", "(self)"));
    assertNotEquals (aPPD, new PermissionsPolicyDirective ("camera", "*"));
    assertNotEquals (aPPD, aWithReport);
  }

  @Test
  public void testAllowListCtor ()
  {
    final PermissionsPolicyAllowList aAllowList = new PermissionsPolicyAllowList ().addKeywordSelf ();
    final PermissionsPolicyDirective aPPD = new PermissionsPolicyDirective ("camera", aAllowList);
    assertEquals (aAllowList.getAsString (), aPPD.getValue ());

    final PermissionsPolicyDirective aPPD2 = new PermissionsPolicyDirective ("camera", aAllowList, "endpoint");
    assertEquals (aAllowList.getAsString (), aPPD2.getValue ());
    assertEquals ("endpoint", aPPD2.getReportTo ());

    // A null allow list is allowed
    assertNull (new PermissionsPolicyDirective ("camera", (AbstractPermissionsPolicyAllowList <?>) null).getValue ());
    assertNull (new PermissionsPolicyDirective ("camera", (AbstractPermissionsPolicyAllowList <?>) null, null)
                                                                                                              .getValue ());
  }

  @Test
  public void testInvalidCtor ()
  {
    try
    {
      new PermissionsPolicyDirective ("with space", "(self)");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PermissionsPolicyDirective ("camera", "with;semicolon");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PermissionsPolicyDirective ("camera", "(self)", "with;semicolon");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testAllFactoryMethods () throws Exception
  {
    final PermissionsPolicyAllowList aAllowList = new PermissionsPolicyAllowList ().addKeywordSelf ();

    int nCount = 0;
    for (final Method aMethod : PermissionsPolicyDirective.class.getDeclaredMethods ())
      if (Modifier.isStatic (aMethod.getModifiers ()) &&
          aMethod.getName ().startsWith ("create") &&
          aMethod.getParameterCount () == 1)
      {
        final PermissionsPolicyDirective aPPD = (PermissionsPolicyDirective) aMethod.invoke (null, aAllowList);
        assertNotNull (aMethod.getName (), aPPD);
        assertTrue (aMethod.getName (), PermissionsPolicyDirective.isValidName (aPPD.getName ()));
        assertEquals (aMethod.getName (), aAllowList.getAsString (), aPPD.getValue ());
        nCount++;
      }
    // Just to be sure that something was tested at all
    assertTrue (Integer.toString (nCount), nCount > 40);
  }
}
