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
package com.helger.commons.locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link ELocaleName}.
 *
 * @author Philip Helger
 */
public final class ELocaleNameTest
{
  private static final Locale L_DE = new Locale ("de");
  private static final Locale L_EN = new Locale ("en");

  @Test
  public void testAll ()
  {
    for (final ELocaleName eName : ELocaleName.values ())
    {
      assertNotNull (eName.getDisplayText (L_DE));
      assertNotNull (eName.getDisplayText (L_EN));
      assertSame (eName, ELocaleName.valueOf (eName.name ()));
    }
  }
}
