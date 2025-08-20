/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.text.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link FormatterStringSkipPrefixAndSuffix}.
 *
 * @author Philip Helger
 */
public final class FormatterStringSkipPrefixAndSuffixTest
{
  @Test
  public void testAll ()
  {
    final FormatterStringSkipPrefixAndSuffix fp = new FormatterStringSkipPrefixAndSuffix ("a", "o");
    assertEquals ("bc", fp.apply ("abco"));
    assertEquals ("bc", fp.apply ("abc"));
    assertEquals ("bc", fp.apply ("bco"));
    assertEquals ("bc", fp.apply ("bc"));
    TestHelper.testToStringImplementation (fp);
  }

  @Test
  public void testPrefix ()
  {
    final FormatterStringSkipPrefixAndSuffix fp = FormatterStringSkipPrefixAndSuffix.createPrefixOnly ("a");
    assertEquals ("bco", fp.apply ("abco"));
    assertEquals ("bc", fp.apply ("abc"));
    assertEquals ("bco", fp.apply ("bco"));
    assertEquals ("bc", fp.apply ("bc"));
    TestHelper.testToStringImplementation (fp);
  }

  @Test
  public void testSuffix ()
  {
    final FormatterStringSkipPrefixAndSuffix fp = FormatterStringSkipPrefixAndSuffix.createSuffixOnly ("o");
    assertEquals ("abc", fp.apply ("abco"));
    assertEquals ("abc", fp.apply ("abc"));
    assertEquals ("bc", fp.apply ("bco"));
    assertEquals ("bc", fp.apply ("bc"));
    TestHelper.testToStringImplementation (fp);
  }
}
