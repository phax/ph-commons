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
package com.helger.security.bcrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Copyright (c) 2006 Damien Miller <djm@mindrot.org>
//
//Permission to use, copy, modify, and distribute this software for any
//purpose with or without fee is hereby granted, provided that the above
//copyright notice and this permission notice appear in all copies.
//
//THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
//WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
//MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
//ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
//WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
//ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
//OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

/**
 * JUnit unit tests for {@link BCrypt} routines
 *
 * @author Damien Miller
 * @version 0.2
 */
public final class BCryptTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (BCryptTest.class);

  private static final String test_vectors[][] = { { "",
                                                     "$2a$06$DCq7YPn5Rq63x1Lad4cll.",
                                                     "$2a$06$DCq7YPn5Rq63x1Lad4cll.TV4S6ytwfsfvkgY8jIucDrjc8deX1s." },
                                                   { "",
                                                     "$2a$08$HqWuK6/Ng6sg9gQzbLrgb.",
                                                     "$2a$08$HqWuK6/Ng6sg9gQzbLrgb.Tl.ZHfXLhvt/SgVyWhQqgqcZ7ZuUtye" },
                                                   { "",
                                                     "$2a$10$k1wbIrmNyFAPwPVPSVa/ze",
                                                     "$2a$10$k1wbIrmNyFAPwPVPSVa/zecw2BCEnBwVS2GbrmgzxFUOqW9dk4TCW" },
                                                   { "",
                                                     "$2a$12$k42ZFHFWqBp3vWli.nIn8u",
                                                     "$2a$12$k42ZFHFWqBp3vWli.nIn8uYyIkbvYRvodzbfbK18SSsY.CsIQPlxO" },
                                                   { "a",
                                                     "$2a$06$m0CrhHm10qJ3lXRY.5zDGO",
                                                     "$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe" },
                                                   { "a",
                                                     "$2a$08$cfcvVd2aQ8CMvoMpP2EBfe",
                                                     "$2a$08$cfcvVd2aQ8CMvoMpP2EBfeodLEkkFJ9umNEfPD18.hUF62qqlC/V." },
                                                   { "a",
                                                     "$2a$10$k87L/MF28Q673VKh8/cPi.",
                                                     "$2a$10$k87L/MF28Q673VKh8/cPi.SUl7MU/rWuSiIDDFayrKk/1tBsSQu4u" },
                                                   { "a",
                                                     "$2a$12$8NJH3LsPrANStV6XtBakCe",
                                                     "$2a$12$8NJH3LsPrANStV6XtBakCez0cKHXVxmvxIlcz785vxAIZrihHZpeS" },
                                                   { "abc",
                                                     "$2a$06$If6bvum7DFjUnE9p2uDeDu",
                                                     "$2a$06$If6bvum7DFjUnE9p2uDeDu0YHzrHM6tf.iqN8.yx.jNN1ILEf7h0i" },
                                                   { "abc",
                                                     "$2a$08$Ro0CUfOqk6cXEKf3dyaM7O",
                                                     "$2a$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm" },
                                                   { "abc",
                                                     "$2a$10$WvvTPHKwdBJ3uk0Z37EMR.",
                                                     "$2a$10$WvvTPHKwdBJ3uk0Z37EMR.hLA2W6N9AEBhEgrAOljy2Ae5MtaSIUi" },
                                                   { "abc",
                                                     "$2a$12$EXRkfkdmXn2gzds2SSitu.",
                                                     "$2a$12$EXRkfkdmXn2gzds2SSitu.MW9.gAVqa9eLS1//RYtYCmB1eLHg.9q" },
                                                   { "abcdefghijklmnopqrstuvwxyz",
                                                     "$2a$06$.rCVZVOThsIa97pEDOxvGu",
                                                     "$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC" },
                                                   { "abcdefghijklmnopqrstuvwxyz",
                                                     "$2a$08$aTsUwsyowQuzRrDqFflhge",
                                                     "$2a$08$aTsUwsyowQuzRrDqFflhgekJ8d9/7Z3GV3UcgvzQW3J5zMyrTvlz." },
                                                   { "abcdefghijklmnopqrstuvwxyz",
                                                     "$2a$10$fVH8e28OQRj9tqiDXs1e1u",
                                                     "$2a$10$fVH8e28OQRj9tqiDXs1e1uxpsjN0c7II7YPKXua2NAKYvM6iQk7dq" },
                                                   { "abcdefghijklmnopqrstuvwxyz",
                                                     "$2a$12$D4G5f18o7aMMfwasBL7Gpu",
                                                     "$2a$12$D4G5f18o7aMMfwasBL7GpuQWuP3pkrZrOAnqP.bmezbMng.QwJ/pG" },
                                                   { "~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
                                                     "$2a$06$fPIsBO8qRqkjj273rfaOI.",
                                                     "$2a$06$fPIsBO8qRqkjj273rfaOI.HtSV9jLDpTbZn782DC6/t7qT67P6FfO" },
                                                   { "~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
                                                     "$2a$08$Eq2r4G/76Wv39MzSX262hu",
                                                     "$2a$08$Eq2r4G/76Wv39MzSX262huzPz612MZiYHVUJe/OcOql2jo4.9UxTW" },
                                                   { "~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
                                                     "$2a$10$LgfYWkbzEvQ4JakH7rOvHe",
                                                     "$2a$10$LgfYWkbzEvQ4JakH7rOvHe0y8pHKF9OaFgwUZ2q7W2FFZmZzJYlfS" },
                                                   { "~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
                                                     "$2a$12$WApznUOJfkEGSmYRfnkrPO",
                                                     "$2a$12$WApznUOJfkEGSmYRfnkrPOr466oFDCaj4b6HY3EXGvfxm43seyhgC" }, };

  /**
   * Test method for 'BCrypt.hashpw(String, String)'
   */
  @Test
  public void testHashpw ()
  {
    s_aLogger.info ("BCrypt.hashpw(): ");
    for (final String [] test_vector : test_vectors)
    {
      final String plain = test_vector[0];
      final String salt = test_vector[1];
      final String expected = test_vector[2];
      final String hashed = BCrypt.hashpw (plain, salt);
      assertEquals (hashed, expected);
      s_aLogger.info (".");
    }
    s_aLogger.info ("");
  }

  /**
   * Test method for 'BCrypt.gensalt(int)'
   */
  @Test
  public void testGensaltInt ()
  {
    s_aLogger.info ("BCrypt.gensalt(log_rounds):");
    for (int i = 4; i <= 12; i++)
    {
      s_aLogger.info (" " + Integer.toString (i) + ":");
      for (int j = 0; j < test_vectors.length; j += 4)
      {
        final String plain = test_vectors[j][0];
        final String salt = BCrypt.gensalt (i);
        final String hashed1 = BCrypt.hashpw (plain, salt);
        final String hashed2 = BCrypt.hashpw (plain, hashed1);
        assertEquals (hashed1, hashed2);
        s_aLogger.info (".");
      }
    }
    s_aLogger.info ("");
  }

  /**
   * Test method for 'BCrypt.gensalt()'
   */
  @Test
  public void testGensalt ()
  {
    s_aLogger.info ("BCrypt.gensalt(): ");
    for (int i = 0; i < test_vectors.length; i += 4)
    {
      final String plain = test_vectors[i][0];
      final String salt = BCrypt.gensalt ();
      final String hashed1 = BCrypt.hashpw (plain, salt);
      final String hashed2 = BCrypt.hashpw (plain, hashed1);
      assertEquals (hashed1, hashed2);
      s_aLogger.info (".");
    }
    s_aLogger.info ("");
  }

  /**
   * Test method for 'BCrypt.checkpw(String, String)' expecting success
   */
  @Test
  public void testCheckpw_success ()
  {
    s_aLogger.info ("BCrypt.checkpw w/ good passwords: ");
    for (final String [] test_vector : test_vectors)
    {
      final String plain = test_vector[0];
      final String expected = test_vector[2];
      assertTrue (BCrypt.checkpw (plain, expected));
      s_aLogger.info (".");
    }
    s_aLogger.info ("");
  }

  /**
   * Test method for 'BCrypt.checkpw(String, String)' expecting failure
   */
  @Test
  public void testCheckpw_failure ()
  {
    s_aLogger.info ("BCrypt.checkpw w/ bad passwords: ");
    for (int i = 0; i < test_vectors.length; i++)
    {
      final int broken_index = (i + 4) % test_vectors.length;
      final String plain = test_vectors[i][0];
      final String expected = test_vectors[broken_index][2];
      assertFalse (BCrypt.checkpw (plain, expected));
      s_aLogger.info (".");
    }
    s_aLogger.info ("");
  }

  /**
   * Test for correct hashing of non-US-ASCII passwords
   */
  @Test
  public void testInternationalChars ()
  {
    s_aLogger.info ("BCrypt.hashpw w/ international chars: ");
    final String pw1 = "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605";
    final String pw2 = "????????";

    final String h1 = BCrypt.hashpw (pw1, BCrypt.gensalt ());
    assertFalse (BCrypt.checkpw (pw2, h1));
    s_aLogger.info (".");

    final String h2 = BCrypt.hashpw (pw2, BCrypt.gensalt ());
    assertFalse (BCrypt.checkpw (pw1, h2));
    s_aLogger.info (".");
    s_aLogger.info ("");
  }

  @Test
  public void testSimple ()
  {
    final String sSalt = BCrypt.gensalt ();
    final String h1 = BCrypt.hashpw ("password", sSalt);
    assertTrue (BCrypt.checkpw ("password", h1));
    final String h2 = BCrypt.hashpw ("password", sSalt);
    assertEquals (h1, h2);
  }
}
