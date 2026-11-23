/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link Version}: the string form can be parsed back, and the comparison
 * fulfils the contract of {@link Comparable}.
 *
 * @author Philip Helger
 */
public final class VersionPropertyTest
{
  private static final IGenerator <String> QUALIFIERS = Generators.oneOf (Generators.constant (null),
                                                                          Generators.strings ("abAZ09-_", 1, 4));

  /** Small numbers and short qualifiers, so that equal and neighbouring versions are frequent */
  private static final IGenerator <Version> VERSIONS = Generators.lists (Generators.ints (0, 20), 3, 3)
                                                                 .flatMap (aNumbers -> QUALIFIERS.map (sQualifier -> new Version (aNumbers.get (0)
                                                                                                                                         .intValue (),
                                                                                                                                 aNumbers.get (1)
                                                                                                                                         .intValue (),
                                                                                                                                 aNumbers.get (2)
                                                                                                                                         .intValue (),
                                                                                                                                 sQualifier)));

  @Test
  public void testParseAsString ()
  {
    new PropertyChecker ().setTries (1000).forAll (VERSIONS, aVersion -> {
      assertEquals (aVersion, Version.parse (aVersion.getAsString ()));
      assertEquals (aVersion, Version.parse (aVersion.getAsString (true)));
    });
  }

  @Test
  public void testCompareToContract ()
  {
    new PropertyChecker ().setTries (2000).forAll (VERSIONS, VERSIONS, VERSIONS, (aA, aB, aC) -> {
      final int nAB = Integer.signum (aA.compareTo (aB));
      // Antisymmetric
      assertEquals (aA + " vs. " + aB, -nAB, Integer.signum (aB.compareTo (aA)));
      // Consistent with equals, and equal objects have equal hash codes
      assertEquals (aA + " vs. " + aB, nAB == 0, aA.equals (aB));
      if (aA.equals (aB))
        assertEquals (aA + " vs. " + aB, aA.hashCode (), aB.hashCode ());
      // Transitive
      if (aA.compareTo (aB) <= 0 && aB.compareTo (aC) <= 0)
        assertTrue (aA + " <= " + aB + " <= " + aC, aA.compareTo (aC) <= 0);
    });
  }
}
