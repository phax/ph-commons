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
package com.helger.charset.utf7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public final class UTF7Base64HelperTest
{
  private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789+/";
  private UTF7Base64Helper tested;

  @Before
  public void setUp () throws Exception
  {
    tested = new UTF7Base64Helper (BASE64_ALPHABET);
  }

  @SuppressWarnings ("unused")
  @Test
  public void testRejectShort () throws Exception
  {
    try
    {
      new UTF7Base64Helper ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+");
      fail ("short alphabet error accepted");
    }
    catch (final IllegalArgumentException e)
    {
      // expected
    }
  }

  @SuppressWarnings ("unused")
  @Test
  public void testRejectShortLong () throws Exception
  {
    try
    {
      new UTF7Base64Helper ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/,");
      fail ("long alphabet error accepted");
    }
    catch (final IllegalArgumentException e)
    {
      // expected
    }
  }

  @Test
  public void testContains () throws Exception
  {
    assertTrue (tested.contains ('A'));
    assertTrue (tested.contains ('/'));
    assertFalse (tested.contains (','));
  }

  @Test
  public void testGetSextet () throws Exception
  {
    assertEquals (0, tested.getSextet ((byte) 'A'));
    assertEquals (63, tested.getSextet ((byte) '/'));
    assertEquals (-1, tested.getSextet ((byte) ','));
  }

  @Test
  public void testGetChar () throws Exception
  {
    assertEquals ('A', tested.getChar (0));
    assertEquals ('/', tested.getChar (63));
    assertEquals ('a', tested.getChar (26));
    assertEquals ('0', tested.getChar (52));
  }
}
