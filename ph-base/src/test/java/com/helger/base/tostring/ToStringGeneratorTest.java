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
package com.helger.base.tostring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.junit.Test;

import com.helger.base.compare.ESortOrder;

/**
 * Test class for class {@link ToStringGenerator}.
 *
 * @author Philip Helger
 */
public final class ToStringGeneratorTest
{
  private void _testNullable (@Nullable final Object o)
  {
    final String s1 = new ToStringGenerator (null).append ("O", o).getToString ();
    assertNotNull (s1);
    final String s2 = new ToStringGenerator (null).append ("O", o).getToString ();
    assertNotNull (s2);
    final String s3 = new ToStringGenerator (null).appendIfNotNull ("O", o).getToString ();
    assertNotNull (s3);
    assertEquals (s1, s2);
    assertFalse (s2.equals (s3));
  }

  private <T> void _test (@Nullable final T o)
  {
    final String s1 = new ToStringGenerator (null).append ("O", o).getToString ();
    assertNotNull (s1);
    final String s2 = new ToStringGenerator (null).append ("O", o).getToString ();
    assertNotNull (s2);
    final String s3 = new ToStringGenerator (null).appendIfNotNull ("O", o).getToString ();
    assertNotNull (s3);
    assertEquals (s1, s2);
    assertEquals (s2, s3);
  }

  @Test
  public void testSimple ()
  {
    _testNullable (null);
    _test ("Hallo");
    _test (Long.valueOf (123456789));
    _test (Arrays.asList ("Hello", "World"));
    Map <String, String> aMap = new HashMap <> ();
    aMap.put ("Hello", "Hallo");
    aMap.put ("World", "Welt");
    _test (aMap);
    _test (new BigDecimal ("234324.23421378091235931253769"));
    _test (new boolean [] { true, false, true });
    _test (new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE });
    _test (new char [] { Character.MIN_VALUE, 'x', 'y', 'Z', Character.MAX_VALUE });
    _test (new double [] { Double.MIN_VALUE,
                           1,
                           2,
                           314.15,
                           Double.MAX_VALUE,
                           Double.NaN,
                           Double.POSITIVE_INFINITY,
                           Double.NEGATIVE_INFINITY });
    _test (new float [] { Float.MIN_VALUE,
                          1,
                          2,
                          31.415f,
                          Float.MAX_VALUE,
                          Float.NaN,
                          Float.POSITIVE_INFINITY,
                          Float.NEGATIVE_INFINITY });
    _test (new int [] { Integer.MIN_VALUE, 1, 2, 1415, Integer.MAX_VALUE });
    _test (new long [] { Long.MIN_VALUE, 1, 2, 1415, Long.MAX_VALUE });
    _test (new short [] { Short.MIN_VALUE, 1, 2, 1415, Short.MAX_VALUE });
    _test (new String [] { "a", "b", "c" });
  }

  @Test
  public void testCommon ()
  {
    new ToStringGenerator (this).append ("Boolean", true)
                                .append ("Byte", (byte) 1)
                                .append ("Char", 'x')
                                .append ("Double", 31.4)
                                .append ("Float", 47.11f)
                                .append ("Int", 4711)
                                .append ("Long", 12345678901234L)
                                .append ("Short", (short) 0xff)
                                .append ("Enum", ESortOrder.ASCENDING)
                                .appendPassword ("Pwfield")
                                .append ("Boolean[]", new boolean [] { true, false, true })
                                .append ("Byte[]", new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE })
                                .append ("Char[]",
                                         new char [] { Character.MIN_VALUE, 'x', 'y', 'Z', Character.MAX_VALUE })
                                .append ("Double[]",
                                         new double [] { Double.MIN_VALUE,
                                                         1,
                                                         2,
                                                         314.15,
                                                         Double.MAX_VALUE,
                                                         Double.NaN,
                                                         Double.POSITIVE_INFINITY,
                                                         Double.NEGATIVE_INFINITY })
                                .append ("Float[]",
                                         new float [] { Float.MIN_VALUE,
                                                        1,
                                                        2,
                                                        31.415f,
                                                        Float.MAX_VALUE,
                                                        Float.NaN,
                                                        Float.POSITIVE_INFINITY,
                                                        Float.NEGATIVE_INFINITY })
                                .append ("Int[]", new int [] { Integer.MIN_VALUE, 1, 2, 1415, Integer.MAX_VALUE })
                                .append ("Long[]", new long [] { Long.MIN_VALUE, 1, 2, 1415, Long.MAX_VALUE })
                                .append ("Short[]", new short [] { Short.MIN_VALUE, 1, 2, 1415, Short.MAX_VALUE })
                                .append ("String[]", new String [] { "a", "b", "c" })
                                .appendIfNotNull ("Boolean[]", new boolean [] { true, false, true })
                                .appendIfNotNull ("Byte[]", new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE })
                                .appendIfNotNull ("Char[]",
                                                  new char [] { Character.MIN_VALUE,
                                                                'x',
                                                                'y',
                                                                'Z',
                                                                Character.MAX_VALUE })
                                .appendIfNotNull ("Double[]",
                                                  new double [] { Double.MIN_VALUE,
                                                                  1,
                                                                  2,
                                                                  314.15,
                                                                  Double.MAX_VALUE,
                                                                  Double.NaN,
                                                                  Double.POSITIVE_INFINITY,
                                                                  Double.NEGATIVE_INFINITY })
                                .appendIfNotNull ("Float[]",
                                                  new float [] { Float.MIN_VALUE,
                                                                 1,
                                                                 2,
                                                                 314.15f,
                                                                 Float.MAX_VALUE,
                                                                 Float.NaN,
                                                                 Float.POSITIVE_INFINITY,
                                                                 Float.NEGATIVE_INFINITY })
                                .appendIfNotNull ("Int[]",
                                                  new int [] { Integer.MIN_VALUE, 1, 2, 1415, Integer.MAX_VALUE })
                                .appendIfNotNull ("Long[]", new long [] { Long.MIN_VALUE, 1, 2, 1415, Long.MAX_VALUE })
                                .appendIfNotNull ("Short[]",
                                                  new short [] { Short.MIN_VALUE, 1, 2, 1415, Short.MAX_VALUE })
                                .appendIfNotNull ("String[]", new String [] { "a", "b", "c" })
                                .appendIfNotNull ("Boolean[]", (boolean []) null)
                                .appendIfNotNull ("Byte[]", (byte []) null)
                                .appendIfNotNull ("Char[]", (char []) null)
                                .appendIfNotNull ("Double[]", (double []) null)
                                .appendIfNotNull ("Float[]", (float []) null)
                                .appendIfNotNull ("Int[]", (int []) null)
                                .appendIfNotNull ("Long[]", (long []) null)
                                .appendIfNotNull ("Short[]", (short []) null)
                                .appendIfNotNull ("String[]", (String []) null)
                                .getToString ();
  }

  @Test
  public void testCircularReference ()
  {
    final ToStringGenerator aTSG = new ToStringGenerator (this);
    aTSG.append ("Anything", "else");
    aTSG.append ("MeMyselfAndI", this);
    assertTrue (aTSG.getToString ().endsWith (": Anything=else; MeMyselfAndI=this]"));
  }
}
