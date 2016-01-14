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
package com.helger.commons.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.compare.ESortOrder;

/**
 * Test class for class {@link ToStringGenerator}.
 *
 * @author Philip Helger
 */
public final class ToStringGeneratorTest
{
  private void _testNullable (@Nullable final Object o)
  {
    final String s1 = new ToStringGenerator (null).append ("o", o).toString ();
    assertNotNull (s1);
    final String s2 = new ToStringGenerator (null).append ("o", o).toString ();
    assertNotNull (s2);
    final String s3 = new ToStringGenerator (null).appendIfNotNull ("o", o).toString ();
    assertNotNull (s3);
    assertEquals (s1, s2);
    assertFalse (s2.equals (s3));
  }

  private <T> void _test (@Nullable final T o)
  {
    final String s1 = new ToStringGenerator (null).append ("o", o).toString ();
    assertNotNull (s1);
    final String s2 = new ToStringGenerator (null).append ("o", o).toString ();
    assertNotNull (s2);
    final String s3 = new ToStringGenerator (null).appendIfNotNull ("o", o).toString ();
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
    _test (CollectionHelper.newList ("Hello", "World"));
    _test (CollectionHelper.newMap ("Hello", "Hallo", "World", "Welt"));
    _test (new BigDecimal ("234324.23421378091235931253769"));
    _test (new boolean [] { true, false, true });
    _test (new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE });
    _test (new char [] { Character.MIN_VALUE, 'x', 'y', 'Z', Character.MAX_VALUE });
    _test (new double [] { Double.MIN_VALUE,
                           1,
                           2,
                           3.1415,
                           Double.MAX_VALUE,
                           Double.NaN,
                           Double.POSITIVE_INFINITY,
                           Double.NEGATIVE_INFINITY });
    _test (new float [] { Float.MIN_VALUE,
                          1,
                          2,
                          3.1415f,
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
    new ToStringGenerator (this).append ("boolean", true)
                                .append ("byte", (byte) 1)
                                .append ("char", 'x')
                                .append ("double", 3.14)
                                .append ("float", 47.11f)
                                .append ("int", 4711)
                                .append ("long", 12345678901234L)
                                .append ("short", (short) 0xff)
                                .append ("enum", ESortOrder.ASCENDING)
                                .appendPassword ("pwfield")
                                .append ("boolean[]", new boolean [] { true, false, true })
                                .append ("byte[]", new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE })
                                .append ("char[]",
                                         new char [] { Character.MIN_VALUE, 'x', 'y', 'Z', Character.MAX_VALUE })
                                .append ("double[]",
                                         new double [] { Double.MIN_VALUE,
                                                         1,
                                                         2,
                                                         3.1415,
                                                         Double.MAX_VALUE,
                                                         Double.NaN,
                                                         Double.POSITIVE_INFINITY,
                                                         Double.NEGATIVE_INFINITY })
                                .append ("float[]",
                                         new float [] { Float.MIN_VALUE,
                                                        1,
                                                        2,
                                                        3.1415f,
                                                        Float.MAX_VALUE,
                                                        Float.NaN,
                                                        Float.POSITIVE_INFINITY,
                                                        Float.NEGATIVE_INFINITY })
                                .append ("int[]", new int [] { Integer.MIN_VALUE, 1, 2, 1415, Integer.MAX_VALUE })
                                .append ("long[]", new long [] { Long.MIN_VALUE, 1, 2, 1415, Long.MAX_VALUE })
                                .append ("short[]", new short [] { Short.MIN_VALUE, 1, 2, 1415, Short.MAX_VALUE })
                                .append ("String[]", new String [] { "a", "b", "c" })
                                .appendIfNotNull ("boolean[]", new boolean [] { true, false, true })
                                .appendIfNotNull ("byte[]", new byte [] { Byte.MIN_VALUE, 1, 2, 3, Byte.MAX_VALUE })
                                .appendIfNotNull ("char[]",
                                                  new char [] { Character.MIN_VALUE,
                                                                'x',
                                                                'y',
                                                                'Z',
                                                                Character.MAX_VALUE })
                                .appendIfNotNull ("double[]",
                                                  new double [] { Double.MIN_VALUE,
                                                                  1,
                                                                  2,
                                                                  3.1415,
                                                                  Double.MAX_VALUE,
                                                                  Double.NaN,
                                                                  Double.POSITIVE_INFINITY,
                                                                  Double.NEGATIVE_INFINITY })
                                .appendIfNotNull ("float[]",
                                                  new float [] { Float.MIN_VALUE,
                                                                 1,
                                                                 2,
                                                                 3.1415f,
                                                                 Float.MAX_VALUE,
                                                                 Float.NaN,
                                                                 Float.POSITIVE_INFINITY,
                                                                 Float.NEGATIVE_INFINITY })
                                .appendIfNotNull ("int[]",
                                                  new int [] { Integer.MIN_VALUE, 1, 2, 1415, Integer.MAX_VALUE })
                                .appendIfNotNull ("long[]", new long [] { Long.MIN_VALUE, 1, 2, 1415, Long.MAX_VALUE })
                                .appendIfNotNull ("short[]",
                                                  new short [] { Short.MIN_VALUE, 1, 2, 1415, Short.MAX_VALUE })
                                .appendIfNotNull ("String[]", new String [] { "a", "b", "c" })
                                .appendIfNotNull ("boolean[]", (boolean []) null)
                                .appendIfNotNull ("byte[]", (byte []) null)
                                .appendIfNotNull ("char[]", (char []) null)
                                .appendIfNotNull ("double[]", (double []) null)
                                .appendIfNotNull ("float[]", (float []) null)
                                .appendIfNotNull ("int[]", (int []) null)
                                .appendIfNotNull ("long[]", (long []) null)
                                .appendIfNotNull ("short[]", (short []) null)
                                .appendIfNotNull ("String[]", (String []) null)
                                .toString ();
  }

  @Test
  public void testCircularReference ()
  {
    final ToStringGenerator aTSG = new ToStringGenerator (this);
    aTSG.append ("anything", "else");
    aTSG.append ("meMyselfAndI", this);
    assertTrue (aTSG.toString ().endsWith (": anything=else; meMyselfAndI=this]"));
  }
}
