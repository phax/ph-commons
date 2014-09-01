/**
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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link AggregatorAlwaysNull}.
 * 
 * @author Philip Helger
 */
public final class AggregatorAlwaysNullTest
{
  @Test
  public void testAll ()
  {
    final AggregatorAlwaysNull <String, String> a1 = new AggregatorAlwaysNull <String, String> ();
    PHTestUtils.testDefaultImplementationWithEqualContentObject (a1, new AggregatorAlwaysNull <String, String> ());
    assertNull (a1.aggregate (ContainerHelper.newList ("a", "b")));
    assertNull (a1.aggregate (new ArrayList <String> ()));
  }
}
