/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.text.display;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link HasDisplayTextWithArgs}.
 *
 * @author Philip Helger
 */
public final class HasDisplayTextWithArgsTest
{
  private static final Locale L_DE = new Locale ("de");
  private static final Locale L_FR = new Locale ("fr");

  @Test
  public void testAll ()
  {
    final IHasDisplayText t = new MockHasDisplayText (L_DE, "Hallo {0}");
    final HasDisplayTextWithArgs x = new HasDisplayTextWithArgs (t, "Welt");
    assertEquals ("Hallo Welt", x.getDisplayText (L_DE));
    assertNull (x.getDisplayText (L_FR));
    CommonsTestHelper.testToStringImplementation (x);

    try
    {
      // null parent
      new HasDisplayTextWithArgs (null, "abc");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // no arguments
      new HasDisplayTextWithArgs (new MockHasDisplayText (L_DE, "de"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
