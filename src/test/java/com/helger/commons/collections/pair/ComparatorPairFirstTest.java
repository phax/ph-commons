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
package com.helger.commons.collections.pair;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.compare.ESortOrder;

/**
 * Test class for class {@link ComparatorPairFirst}.
 * 
 * @author Philip Helger
 */
public final class ComparatorPairFirstTest
{
  @Test
  public void testAll ()
  {
    @SuppressWarnings ("unchecked")
    final List <IReadonlyPair <String, String>> aList = ContainerHelper.newList (ReadonlyPair.create ("k3", "v3"),
                                                                                 ReadonlyPair.create ("k2", "v2"),
                                                                                 ReadonlyPair.create ("k1", "v1"));
    assertEquals (3, aList.size ());

    ContainerHelper.getSortedInline (aList, new ComparatorPairFirst <String, String> ());
    assertEquals (3, aList.size ());
    assertEquals ("k1", aList.get (0).getFirst ());
    assertEquals ("k2", aList.get (1).getFirst ());
    assertEquals ("k3", aList.get (2).getFirst ());

    ContainerHelper.getSortedInline (aList, new ComparatorPairFirst <String, String> (ESortOrder.DESCENDING));
    assertEquals (3, aList.size ());
    assertEquals ("k3", aList.get (0).getFirst ());
    assertEquals ("k2", aList.get (1).getFirst ());
    assertEquals ("k1", aList.get (2).getFirst ());

    final List <IReadonlyPair <String, String>> aList2 = PairUtils.getSortedByPairFirst (aList);
    assertEquals (3, aList2.size ());
    assertEquals ("k1", aList2.get (0).getFirst ());
    assertEquals ("k2", aList2.get (1).getFirst ());
    assertEquals ("k3", aList2.get (2).getFirst ());
  }
}
