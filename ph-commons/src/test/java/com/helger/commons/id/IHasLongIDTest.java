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
package com.helger.commons.id;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Test class for class {@link IHasLongID}.
 *
 * @author Philip Helger
 */
public final class IHasLongIDTest
{
  @Test
  public void testAll ()
  {
    final ICommonsList <? extends IHasLongID> aList = new CommonsArrayList <> (new MockHasLongID (5),
                                                                               new MockHasLongID (3),
                                                                               new MockHasLongID (7));
    aList.getSortedInline (IHasLongID.getComparatorID ());
    assertEquals (3, aList.get (0).getID ());
    assertEquals (5, aList.get (1).getID ());
    assertEquals (7, aList.get (2).getID ());

    aList.getSortedInline (IHasLongID.getComparatorID ().reversed ());
    assertEquals (7, aList.get (0).getID ());
    assertEquals (5, aList.get (1).getID ());
    assertEquals (3, aList.get (2).getID ());
  }
}
