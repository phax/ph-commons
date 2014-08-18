/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.combine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.PhlocTestUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CombinatorStringWithSeparator}.
 * 
 * @author Philip Helger
 */
public final class CombinatorStringWithSeparatorTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetStringCombinatorWithSeparator ()
  {
    final ICombinator <String> c = new CombinatorStringWithSeparator (";");
    assertEquals ("a;b", c.combine ("a", "b"));
    assertEquals ("a;null", c.combine ("a", null));
    assertEquals ("null;b", c.combine (null, "b"));
    assertEquals ("null;null", c.combine (null, null));

    try
    {
      // null separator not allowed
      new CombinatorStringWithSeparator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new CombinatorStringWithSeparator (";"),
                                                                    new CombinatorStringWithSeparator (";"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new CombinatorStringWithSeparator (";"),
                                                                        new CombinatorStringWithSeparator (","));
  }
}
