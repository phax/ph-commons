/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.format.StringSkipSuffixFormatter;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link StringSkipSuffixFormatter}.
 * 
 * @author Philip Helger
 */
public final class StringSkipSuffixFormatterTest
{
  @Test
  public void testAll ()
  {
    final StringSkipSuffixFormatter fp = new StringSkipSuffixFormatter ("o");
    assertEquals ("abc", fp.getFormattedValue ("abco"));
    assertEquals ("abc", fp.getFormattedValue ("abc"));
    assertEquals ("bc", fp.getFormattedValue ("bco"));
    assertEquals ("bc", fp.getFormattedValue ("bc"));
    PHTestUtils.testToStringImplementation (fp);
  }
}
