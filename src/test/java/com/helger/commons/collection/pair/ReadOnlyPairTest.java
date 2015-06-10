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
package com.helger.commons.collection.pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.collection.pair.IPair;
import com.helger.commons.collection.pair.ReadOnlyPair;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ReadOnlyPair}.
 * 
 * @author Philip Helger
 */
public final class ReadOnlyPairTest
{
  @Test
  public void testCtor ()
  {
    ReadOnlyPair <String, Double> aPair = new ReadOnlyPair <String, Double> (null, null);
    assertNull (aPair.getFirst ());
    assertNull (aPair.getSecond ());

    IPair <String, Double> aPair2 = new ReadOnlyPair <String, Double> (aPair);
    assertNull (aPair2.getFirst ());
    assertNull (aPair2.getSecond ());

    aPair = new ReadOnlyPair <String, Double> ("x", Double.valueOf (1.2));
    assertEquals ("x", aPair.getFirst ());
    assertEquals (Double.valueOf (1.2), aPair.getSecond ());

    aPair2 = new ReadOnlyPair <String, Double> (aPair);
    assertEquals ("x", aPair2.getFirst ());
    assertEquals (Double.valueOf (1.2), aPair2.getSecond ());
  }

  @Test
  public void testEquals ()
  {
    ReadOnlyPair <String, Double> aPair = new ReadOnlyPair <String, Double> (null, null);

    ReadOnlyPair <String, Double> aPair2 = new ReadOnlyPair <String, Double> ("Hallo", null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new ReadOnlyPair <String, Double> (null, Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    aPair2 = new ReadOnlyPair <String, Double> ("Nice text", Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    // change pair1
    aPair = new ReadOnlyPair <String, Double> ("Nix da", null);

    aPair2 = new ReadOnlyPair <String, Double> (null, null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new ReadOnlyPair <String, Double> ("Hallo", null);
    assertFalse (aPair.equals (aPair2));

    aPair2 = new ReadOnlyPair <String, Double> (null, Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    aPair2 = new ReadOnlyPair <String, Double> ("Nice text", Double.valueOf (3.14));
    assertFalse (aPair.equals (aPair2));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyPair <String, String> (null, null),
                                                                 new ReadOnlyPair <String, Double> (null, null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyPair <String, String> ("Nix da", null),
                                                                 new ReadOnlyPair <String, Double> ("Nix da", null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyPair <String, String> (null, "Nix da"),
                                                                 new ReadOnlyPair <String, String> (null, "Nix da"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyPair <String, String> ("Nix", "da"),
                                                                 new ReadOnlyPair <String, String> ("Nix", "da"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ReadOnlyPair <String, String> ("Nix", "da"),
                                                                     new ReadOnlyPair <String, String> ("Nix", "da2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ReadOnlyPair.create ("Nix", "da"),
                                                                     ReadOnlyPair.create ("Nix2", "da"));
  }
}
