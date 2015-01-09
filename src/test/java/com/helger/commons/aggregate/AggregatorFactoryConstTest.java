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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link AggregatorFactoryConst}.
 * 
 * @author Philip Helger
 */
public final class AggregatorFactoryConstTest
{
  @Test
  public void testAll ()
  {
    final IAggregator <String, String> aAgg1 = new AggregatorUseFirst <String> ();
    final IAggregator <String, String> aAgg2 = new AggregatorUseLast <String> ();

    final AggregatorFactoryConst <String, String> a1 = new AggregatorFactoryConst <String, String> (aAgg1);
    final AggregatorFactoryConst <String, String> a21 = new AggregatorFactoryConst <String, String> (aAgg1);
    final AggregatorFactoryConst <String, String> a22 = new AggregatorFactoryConst <String, String> (aAgg2);
    assertEquals (a1, a1);
    assertEquals (a1, a21);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertFalse (a1.equals (a22));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertEquals (a1.hashCode (), a21.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertFalse (a1.hashCode () == a22.hashCode ());
    assertNotNull (a1.toString ());
    assertFalse (a1.toString ().equals (a21.toString ()));
    assertSame (aAgg1, a1.create ());
    assertSame (aAgg1, a21.create ());
    assertSame (aAgg2, a22.create ());

    try
    {
      new AggregatorFactoryConst <String, String> (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
