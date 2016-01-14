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
package com.helger.commons.string.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for class {@link LevenshteinDistance}.
 *
 * @author Philip Helger
 */
public final class LevenshteinDistanceTest
{
  @Test
  public void testGetDistanceString ()
  {
    assertEquals (0, LevenshteinDistance.getDistance ("abc", "abc"));
    assertEquals (0, LevenshteinDistance.getDistance ("", ""));
    assertEquals (0, LevenshteinDistance.getDistance ((String) null, (String) null));
    assertEquals (3, LevenshteinDistance.getDistance ("", "abc"));
    assertEquals (3, LevenshteinDistance.getDistance (null, "abc"));
    assertEquals (3, LevenshteinDistance.getDistance ("abc", ""));
    assertEquals (3, LevenshteinDistance.getDistance ("abc", null));
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "abd"));
    assertEquals (2, LevenshteinDistance.getDistance ("abc", "abdd"));
    assertEquals (4, LevenshteinDistance.getDistance ("apple", "play"));
    assertEquals (1, LevenshteinDistance.getDistance ("perl", "pearl"));
    assertEquals (4, LevenshteinDistance.getDistance ("meilenstein", "levenshtein"));
  }

  @Test
  public void testGetDistanceCharArray ()
  {
    assertEquals (0, LevenshteinDistance.getDistance ("abc".toCharArray (), "abc".toCharArray ()));
    assertEquals (0, LevenshteinDistance.getDistance (new char [0], new char [0]));
    assertEquals (0, LevenshteinDistance.getDistance ((char []) null, (char []) null));
    assertEquals (3, LevenshteinDistance.getDistance (new char [0], "abc".toCharArray ()));
    assertEquals (3, LevenshteinDistance.getDistance (null, "abc".toCharArray ()));
    assertEquals (3, LevenshteinDistance.getDistance ("abc".toCharArray (), new char [0]));
    assertEquals (3, LevenshteinDistance.getDistance ("abc".toCharArray (), null));
    assertEquals (1, LevenshteinDistance.getDistance ("abc".toCharArray (), "abd".toCharArray ()));
    assertEquals (2, LevenshteinDistance.getDistance ("abc".toCharArray (), "abdd".toCharArray ()));
    assertEquals (4, LevenshteinDistance.getDistance ("apple".toCharArray (), "play".toCharArray ()));
    assertEquals (1, LevenshteinDistance.getDistance ("perl".toCharArray (), "pearl".toCharArray ()));
    assertEquals (4, LevenshteinDistance.getDistance ("meilenstein".toCharArray (), "levenshtein".toCharArray ()));
  }

  @Test
  public void testCostsString ()
  {
    assertEquals (0, LevenshteinDistance.getDistance ("", ""));
    assertEquals (0, LevenshteinDistance.getDistance (null, ""));
    assertEquals (0, LevenshteinDistance.getDistance ("", "", 1, 1, 1));
    assertEquals (0, LevenshteinDistance.getDistance (null, "", 1, 1, 1));
    assertEquals (3, LevenshteinDistance.getDistance ("", "abc", 1, 1, 1));
    assertEquals (3, LevenshteinDistance.getDistance (null, "abc", 1, 1, 1));
    assertEquals (3, LevenshteinDistance.getDistance ("abc", "", 1, 1, 1));
    assertEquals (3, LevenshteinDistance.getDistance ("abc", null, 1, 1, 1));
    assertEquals (6, LevenshteinDistance.getDistance ("abc", null, 2, 1, 1));
    assertEquals (3, LevenshteinDistance.getDistance ("abc", null, 1, 2, 2));

    assertEquals (1, LevenshteinDistance.getDistance ("abc", "abcd"));
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "abcd", 1, 1, 1));

    assertEquals (2, LevenshteinDistance.getDistance ("abcd", "abc", 2, 1, 1));
    assertEquals (1, LevenshteinDistance.getDistance ("abcd", "abc", 1, 2, 1));
    assertEquals (1, LevenshteinDistance.getDistance ("abcd", "abc", 1, 1, 2));

    // Order matters!
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "abcd", 2, 1, 1));
    assertEquals (2, LevenshteinDistance.getDistance ("abc", "abcd", 1, 2, 1));
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "abcd", 1, 1, 2));

    // substitute
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "acc", 2, 1, 1));
    assertEquals (1, LevenshteinDistance.getDistance ("abc", "acc", 1, 2, 1));
    assertEquals (2, LevenshteinDistance.getDistance ("abc", "acc", 1, 1, 2));
  }

  @Test
  public void testCostsCharArray ()
  {
    final char [] _empty = "".toCharArray ();
    final char [] abc = "abc".toCharArray ();
    final char [] abcd = "abcd".toCharArray ();
    final char [] acc = "acc".toCharArray ();

    assertEquals (0, LevenshteinDistance.getDistance (_empty, _empty));
    assertEquals (0, LevenshteinDistance.getDistance (null, _empty));
    assertEquals (0, LevenshteinDistance.getDistance (_empty, _empty, 1, 1, 1));
    assertEquals (0, LevenshteinDistance.getDistance (null, _empty, 1, 1, 1));
    assertEquals (abc.length, LevenshteinDistance.getDistance (_empty, abc, 1, 1, 1));
    assertEquals (abc.length, LevenshteinDistance.getDistance (null, abc, 1, 1, 1));
    assertEquals (abc.length, LevenshteinDistance.getDistance (abc, _empty, 1, 1, 1));
    assertEquals (abc.length, LevenshteinDistance.getDistance (abc, null, 1, 1, 1));
    assertEquals (abc.length * 2, LevenshteinDistance.getDistance (abc, null, 2, 1, 1));
    assertEquals (abc.length, LevenshteinDistance.getDistance (abc, null, 1, 2, 2));

    assertEquals (1, LevenshteinDistance.getDistance (abc, abcd));
    assertEquals (1, LevenshteinDistance.getDistance (abc, abcd, 1, 1, 1));

    assertEquals (2, LevenshteinDistance.getDistance (abcd, abc, 2, 1, 1));
    assertEquals (1, LevenshteinDistance.getDistance (abcd, abc, 1, 2, 1));
    assertEquals (1, LevenshteinDistance.getDistance (abcd, abc, 1, 1, 2));

    // Order matters!
    assertEquals (1, LevenshteinDistance.getDistance (abc, abcd, 2, 1, 1));
    assertEquals (2, LevenshteinDistance.getDistance (abc, abcd, 1, 2, 1));
    assertEquals (1, LevenshteinDistance.getDistance (abc, abcd, 1, 1, 2));

    // substitute
    assertEquals (1, LevenshteinDistance.getDistance (abc, acc, 2, 1, 1));
    assertEquals (1, LevenshteinDistance.getDistance (abc, acc, 1, 2, 1));
    assertEquals (2, LevenshteinDistance.getDistance (abc, acc, 1, 1, 2));
  }
}
