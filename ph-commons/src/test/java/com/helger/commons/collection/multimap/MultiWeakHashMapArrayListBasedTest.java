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
package com.helger.commons.collection.multimap;

import org.junit.Test;

/**
 * Test class for class {@link MultiWeakHashMapArrayListBased}.
 *
 * @author Philip Helger
 */
public final class MultiWeakHashMapArrayListBasedTest extends AbstractMultiMapTestCase
{
  @Test
  public void testAll ()
  {
    MultiWeakHashMapArrayListBased <String, String> aMultiMap = new MultiWeakHashMapArrayListBased <String, String> ();
    testEmpty (aMultiMap);
    aMultiMap = new MultiWeakHashMapArrayListBased <String, String> (getKey1 (), getValue1 ());
    testOne (aMultiMap);
    aMultiMap = new MultiWeakHashMapArrayListBased <String, String> (getKey1 (), getValueList1 ());
    testOne (aMultiMap);
    aMultiMap = new MultiWeakHashMapArrayListBased <String, String> (getMapList1 ());
    testOne (aMultiMap);
    aMultiMap = new MultiWeakHashMapArrayListBased <String, String> ();
    testList (aMultiMap);
  }
}
