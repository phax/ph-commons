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
package com.helger.commons.collections.flags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link FlagContainerThreadSafe}.
 *
 * @author Philip Helger
 */
@Deprecated
public class FlagContainerThreadSafeTest
{
  @Test
  public void testBasic ()
  {
    final FlagContainerThreadSafe aFC = new FlagContainerThreadSafe ();
    assertTrue (aFC.containsNoFlag ());
    assertEquals (0, aFC.getFlagCount ());
    assertFalse (aFC.containsFlag ("any"));
    assertNotNull (aFC.getAllFlags ());
    assertTrue (aFC.getAllFlags ().isEmpty ());

    assertTrue (aFC.addFlag ("any").isChanged ());
    assertFalse (aFC.containsNoFlag ());
    assertEquals (1, aFC.getFlagCount ());
    assertFalse (aFC.addFlag ("any").isChanged ());
    assertEquals (1, aFC.getFlagCount ());
    assertNotNull (aFC.getAllFlags ());
    assertEquals (1, aFC.getAllFlags ().size ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC, new FlagContainerThreadSafe ("any"));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC,
                                                                 new FlagContainerThreadSafe (ContainerHelper.newList ("any")));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFC, new FlagContainerThreadSafe (aFC));

    assertTrue (aFC.addFlags ("other", "flag").isChanged ());
    assertEquals (3, aFC.getFlagCount ());
    assertEquals (3, aFC.getAllFlags ().size ());
    assertFalse (aFC.addFlags ("other", "flag").isChanged ());
    assertEquals (3, aFC.getFlagCount ());
    assertEquals (3, aFC.getAllFlags ().size ());
    assertTrue (aFC.addFlags ("other", "flag", "dummy").isChanged ());
    assertEquals (4, aFC.getFlagCount ());
    assertEquals (4, aFC.getAllFlags ().size ());
    assertFalse (aFC.addFlags (new String [0]).isChanged ());
    assertEquals (4, aFC.getFlagCount ());
    assertEquals (4, aFC.getAllFlags ().size ());

    assertFalse (aFC.addFlags (ContainerHelper.newList ("other", "flag")).isChanged ());
    assertEquals (4, aFC.getFlagCount ());
    assertEquals (4, aFC.getAllFlags ().size ());
    assertTrue (aFC.addFlags (ContainerHelper.newList ("phloc", "flag")).isChanged ());
    assertEquals (5, aFC.getFlagCount ());
    assertEquals (5, aFC.getAllFlags ().size ());
    assertFalse (aFC.addFlags (ContainerHelper.<String> newList ()).isChanged ());
    assertEquals (5, aFC.getFlagCount ());
    assertEquals (5, aFC.getAllFlags ().size ());

    assertTrue (aFC.removeFlag ("phloc").isChanged ());
    assertEquals (4, aFC.getFlagCount ());
    assertFalse (aFC.removeFlag ("phloc").isChanged ());
    assertEquals (4, aFC.getFlagCount ());

    assertTrue (aFC.clear ().isChanged ());
    assertEquals (0, aFC.getFlagCount ());
    assertFalse (aFC.clear ().isChanged ());
    assertEquals (0, aFC.getFlagCount ());
  }
}
