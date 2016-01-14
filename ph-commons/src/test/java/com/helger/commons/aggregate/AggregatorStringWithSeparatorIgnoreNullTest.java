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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link AggregatorStringWithSeparatorIgnoreNull}.
 *
 * @author Philip Helger
 */
public final class AggregatorStringWithSeparatorIgnoreNullTest
{

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetStringCombinatorWithSeparatorIgnoreNull ()
  {
    final AggregatorStringWithSeparatorIgnoreNull c = new AggregatorStringWithSeparatorIgnoreNull (";");
    assertEquals ("a;b", c.aggregate ("a", "b"));
    assertEquals ("a", c.aggregate ("a", null));
    assertEquals ("b", c.aggregate (null, "b"));
    assertEquals ("", c.aggregate (null, null));

    try
    {
      // null separator not allowed
      new AggregatorStringWithSeparatorIgnoreNull (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new AggregatorStringWithSeparatorIgnoreNull (";"),
                                                                       new AggregatorStringWithSeparatorIgnoreNull (";"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new AggregatorStringWithSeparatorIgnoreNull (";"),
                                                                           new AggregatorStringWithSeparatorIgnoreNull (","));
  }
}
