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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;

/**
 * Test class for class {@link AggregatorUseFirst}.
 * 
 * @author Philip Helger
 */
public final class AggregatorUseFirstTest
{
  @Test
  public void testAll ()
  {
    final AggregatorUseFirst <String> a1 = new AggregatorUseFirst <String> ();
    final AggregatorUseFirst <String> a2 = new AggregatorUseFirst <String> ();
    assertEquals (a1, a1);
    assertEquals (a1, a2);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertEquals (a1.hashCode (), a2.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertNotNull (a1.toString ());
    assertFalse (a1.toString ().equals (a2.toString ()));
    final List <String> l = ContainerHelper.newList ("a", null, "b", "", "c");
    assertEquals ("a", a1.aggregate (l));
    assertNull (a1.aggregate (new ArrayList <String> ()));
    assertNull (a1.aggregate (null));
  }
}
