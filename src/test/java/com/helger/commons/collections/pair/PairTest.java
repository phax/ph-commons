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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link Pair}.
 * 
 * @author Philip Helger
 */
public final class PairTest
{
  @Test
  public void testCtor ()
  {
    Pair <String, Double> aPair = new Pair <String, Double> (null, null);
    assertNull (aPair.getFirst ());
    assertNull (aPair.getSecond ());

    IPair <String, Double> aPair2 = new Pair <String, Double> (aPair);
    assertNull (aPair2.getFirst ());
    assertNull (aPair2.getSecond ());

    aPair = new Pair <String, Double> ("x", Double.valueOf (1.2));
    assertEquals ("x", aPair.getFirst ());
    assertEquals (Double.valueOf (1.2), aPair.getSecond ());

    aPair2 = new Pair <String, Double> (aPair);
    assertEquals ("x", aPair2.getFirst ());
    assertEquals (Double.valueOf (1.2), aPair2.getSecond ());

    aPair2 = new Pair <String, Double> ();
    assertNull (aPair2.getFirst ());
    assertNull (aPair2.getSecond ());

    assertTrue (aPair2.setFirst ("first").isChanged ());
    assertEquals ("first", aPair2.getFirst ());
    assertNull (aPair2.getSecond ());
    assertFalse (aPair2.setFirst ("first").isChanged ());
    assertTrue (aPair2.setSecond (Double.valueOf (1)).isChanged ());
    assertEquals ("first", aPair2.getFirst ());
    assertEquals (Double.valueOf (1), aPair2.getSecond ());
    assertFalse (aPair2.setSecond (Double.valueOf (1)).isChanged ());
  }

  @Test
  public void testEquals ()
  {
    Pair <String, Double> aPair = new Pair <String, Double> (null, null);

    Pair <String, Double> aPair2 = new Pair <String, Double> ("Hallo", null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new Pair <String, Double> (null, Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    aPair2 = new Pair <String, Double> ("Nice text", Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    // change pair1
    aPair = new Pair <String, Double> ("Nix da", null);

    aPair2 = new Pair <String, Double> (null, null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new Pair <String, Double> ("Hallo", null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new Pair <String, Double> (null, Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    aPair2 = new Pair <String, Double> ("Nice text", Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new Pair <String, String> (null, null),
                                                                    new Pair <String, Double> (null, null));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new Pair <String, String> ("Nix da", null),
                                                                    new Pair <String, Double> ("Nix da", null));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new Pair <String, String> (null, "Nix da"),
                                                                    new Pair <String, String> (null, "Nix da"));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new Pair <String, String> ("Nix", "da"),
                                                                    new Pair <String, String> ("Nix", "da"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new Pair <String, String> ("Nix", "da"),
                                                                        new Pair <String, String> ("Nix", "da2"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (Pair.create ("Nix", "da"),
                                                                        Pair.create ("Nix2", "da"));
  }
}
