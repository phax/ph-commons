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
package com.helger.http.csp;

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
 * Test class for class {@link CSPDirective}.
 *
 * @author Philip Helger
 */
public final class CSPDirectiveTest
{
  @Test
  public void testIsValidName ()
  {
    assertTrue (CSPDirective.isValidName ("default-src"));
    assertTrue (CSPDirective.isValidName ("abc123"));

    assertFalse (CSPDirective.isValidName (null));
    assertFalse (CSPDirective.isValidName (""));
    assertFalse (CSPDirective.isValidName ("with space"));
    assertFalse (CSPDirective.isValidName ("with;semicolon"));
  }

  @Test
  public void testIsValidValue ()
  {
    // Empty values are allowed
    assertTrue (CSPDirective.isValidValue (null));
    assertTrue (CSPDirective.isValidValue (""));
    assertTrue (CSPDirective.isValidValue ("'self'"));
    assertTrue (CSPDirective.isValidValue ("'self' http://www.helger.com"));

    assertFalse (CSPDirective.isValidValue ("with;semicolon"));
    assertFalse (CSPDirective.isValidValue ("with,comma"));
  }

  @Test
  public void testBasic ()
  {
    final CSPDirective aDirective = new CSPDirective ("default-src", "'self'");
    assertEquals ("default-src", aDirective.getName ());
    assertEquals ("'self'", aDirective.getValue ());
    assertNotNull (aDirective.toString ());

    assertEquals (aDirective, aDirective);
    assertEquals (aDirective, new CSPDirective ("default-src", "'self'"));
    assertEquals (aDirective.hashCode (), new CSPDirective ("default-src", "'self'").hashCode ());
    assertNotEquals (aDirective, null);
    assertNotEquals (aDirective, "any other type");
    assertNotEquals (aDirective, new CSPDirective ("img-src", "'self'"));
    assertNotEquals (aDirective, new CSPDirective ("default-src", "*"));

    // A null source list is allowed
    assertNull (new CSPDirective ("default-src", (AbstractCSPSourceList <?>) null).getValue ());

    final CSPSourceList aSrcList = new CSPSourceList ().addKeywordSelf ();
    assertEquals (aSrcList.getAsString (), new CSPDirective ("default-src", aSrcList).getValue ());
  }

  @Test
  public void testInvalidCtor ()
  {
    try
    {
      new CSPDirective ("with space", "'self'");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new CSPDirective ("default-src", "with;semicolon");
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
    final CSPSourceList aSrcList = new CSPSourceList ().addKeywordSelf ();

    int nCount = 0;
    for (final Method aMethod : CSPDirective.class.getDeclaredMethods ())
      if (Modifier.isStatic (aMethod.getModifiers ()) &&
          aMethod.getName ().startsWith ("create") &&
          aMethod.getParameterCount () == 1)
      {
        final Class <?> aParamClass = aMethod.getParameterTypes ()[0];
        final Object aParam = aParamClass.equals (String.class) ? "'self'" : aSrcList;
        final CSPDirective aDirective = (CSPDirective) aMethod.invoke (null, aParam);
        assertNotNull (aMethod.getName (), aDirective);
        assertTrue (aMethod.getName (), CSPDirective.isValidName (aDirective.getName ()));
        assertEquals (aMethod.getName (), "'self'", aDirective.getValue ());
        nCount++;
      }
    // Just to be sure that something was tested at all
    assertTrue (Integer.toString (nCount), nCount > 15);
  }
}
