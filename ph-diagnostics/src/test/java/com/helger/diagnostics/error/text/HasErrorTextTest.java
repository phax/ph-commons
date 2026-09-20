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
package com.helger.diagnostics.error.text;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.text.IHasText;
import com.helger.text.display.IHasDisplayText;

/**
 * Test class for the {@link IHasErrorText} implementations.
 *
 * @author Philip Helger
 */
public final class HasErrorTextTest
{
  private static final Locale L = Locale.US;
  private static final IHasText TEXT = aLocale -> "Text in " + aLocale.getLanguage ();

  @Test
  public void testConstant ()
  {
    final ConstantHasErrorText a = new ConstantHasErrorText ("abc");
    assertEquals ("abc", a.getDisplayText (L));
    assertFalse (a.isMultiLingual ());
    assertNotNull (a.toString ());

    assertEquals (a, a);
    assertEquals (a, new ConstantHasErrorText ("abc"));
    assertEquals (a.hashCode (), new ConstantHasErrorText ("abc").hashCode ());
    assertNotEquals (a, null);
    assertNotEquals (a, "any other type");
    assertNotEquals (a, new ConstantHasErrorText ("def"));
    assertNotEquals (a, new ConstantHasErrorText (null));

    assertNull (new ConstantHasErrorText (null).getDisplayText (L));

    assertNull (ConstantHasErrorText.createOnDemand (null));
    assertEquals (a, ConstantHasErrorText.createOnDemand ("abc"));
    // Empty error text is remembered
    assertNotNull (ConstantHasErrorText.createOnDemand (""));
  }

  @Test
  public void testDynamic ()
  {
    final DynamicHasErrorText a = new DynamicHasErrorText (TEXT);
    assertEquals ("Text in en", a.getDisplayText (L));
    assertEquals ("Text in de", a.getDisplayText (Locale.GERMAN));
    assertTrue (a.isMultiLingual ());
    assertNotNull (a.toString ());

    assertEquals (a, a);
    assertEquals (a, new DynamicHasErrorText (TEXT));
    assertEquals (a.hashCode (), new DynamicHasErrorText (TEXT).hashCode ());
    assertNotEquals (a, null);
    assertNotEquals (a, "any other type");
    assertNotEquals (a, new DynamicHasErrorText ( (IHasText) aLocale -> "other"));

    // Constructor with IHasDisplayText
    assertEquals ("Display", new DynamicHasErrorText ((IHasDisplayText) aLocale -> "Display").getDisplayText (L));

    try
    {
      new DynamicHasErrorText ((IHasText) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testDynamicWithArgs ()
  {
    final IHasText aParent = aLocale -> "Hello {0} and {1}";
    final DynamicHasErrorTextWithArgs a = new DynamicHasErrorTextWithArgs (aParent, "Peter", "Paul");
    assertSame (aParent, a.getParentText ());
    assertArrayEquals (new Object [] { "Peter", "Paul" }, a.getAllArgs ());
    assertEquals ("Hello Peter and Paul", a.getDisplayText (L));
    assertTrue (a.isMultiLingual ());
    assertNotNull (a.toString ());

    // Constructor with IHasDisplayText
    assertEquals ("Hello Peter and Paul",
                  new DynamicHasErrorTextWithArgs ((IHasDisplayText) aLocale -> "Hello {0} and {1}",
                                                                   "Peter",
                                                                   "Paul").getDisplayText (L));

    try
    {
      new DynamicHasErrorTextWithArgs ((IHasText) null, "arg");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // Empty arguments are not allowed
      new DynamicHasErrorTextWithArgs (aParent);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
