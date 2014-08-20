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
package com.helger.commons.collections.flags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link ReadonlyFlagContainer}.
 * 
 * @author Philip Helger
 */
public class ReadonlyFlagContainerTest
{
  @Test
  public void testBasic ()
  {
    ReadonlyFlagContainer aFC = new ReadonlyFlagContainer ();
    assertTrue (aFC.containsNoFlag ());
    assertEquals (0, aFC.getFlagCount ());
    assertFalse (aFC.containsFlag ("any"));
    assertNotNull (aFC.getAllFlags ());
    assertTrue (aFC.getAllFlags ().isEmpty ());

    aFC = new ReadonlyFlagContainer ("any");
    assertFalse (aFC.containsNoFlag ());
    assertEquals (1, aFC.getFlagCount ());
    assertNotNull (aFC.getAllFlags ());
    assertEquals (1, aFC.getAllFlags ().size ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC, new ReadonlyFlagContainer ("any"));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC,
                                                                    new ReadonlyFlagContainer (ContainerHelper.newList ("any")));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC, new ReadonlyFlagContainer (aFC));
  }
}
