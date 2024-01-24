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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.Test;

public final class JavaUnicodeFuncTest
{
  @Test
  public void testLeftToRightMark ()
  {
    final String s = "\ub002" + "abc";
    assertEquals (4, s.length ());

    final String sUC = s.toUpperCase (Locale.US);
    assertEquals (4, sUC.length ());

    assertFalse (Pattern.compile ("[a-z]+").matcher (s).matches ());

    final String sClean = Pattern.compile ("[^a-z]+").matcher (s).replaceAll ("");
    assertEquals (3, sClean.length ());

    assertTrue (Pattern.compile ("[a-z]+").matcher (sClean).matches ());
  }
}
