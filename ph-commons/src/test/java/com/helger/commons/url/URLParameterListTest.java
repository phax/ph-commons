/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link URLParameterList}.
 *
 * @author Philip Helger
 */
public final class URLParameterListTest
{
  @Test
  public void testBasic ()
  {
    final URLParameterList aList = new URLParameterList ();
    aList.add ("a", 1).add ("a", 2).add ("a", 4);
    assertEquals (3, aList.size ());
    assertTrue (aList.contains ("a"));
    assertTrue (aList.contains ("a", "1"));
    assertTrue (aList.contains ("a", "2"));
    assertTrue (aList.contains ("a", "4"));
    assertFalse (aList.contains ("b"));
    assertFalse (aList.contains ("a", "3"));
    aList.remove ("a", "2");
    assertEquals (2, aList.size ());
    assertTrue (aList.contains ("a"));
    assertTrue (aList.contains ("a", "1"));
    assertTrue (aList.contains ("a", "4"));
    aList.remove ("a");
    assertEquals (0, aList.size ());
    assertFalse (aList.contains ("a"));
  }
}
