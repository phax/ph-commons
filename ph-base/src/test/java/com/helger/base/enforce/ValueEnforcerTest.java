/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.enforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Test;

/**
 * Test class for class {@link ValueEnforcer}.
 *
 * @author Philip Helger
 */
public final class ValueEnforcerTest
{
  @Test (expected = NullPointerException.class)
  public void testNotNull ()
  {
    ValueEnforcer.notNull (null, "null");
  }

  @Test (expected = NullPointerException.class)
  public void testNotEmpty1 ()
  {
    ValueEnforcer.notEmpty ((String) null, "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty2 ()
  {
    ValueEnforcer.notEmpty ("", "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty3 ()
  {
    ValueEnforcer.notEmpty (new StringBuilder (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty4 ()
  {
    ValueEnforcer.notEmpty (new String [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty5 ()
  {
    ValueEnforcer.notEmpty (new char [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty6 ()
  {
    ValueEnforcer.notEmpty (new short [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty7 ()
  {
    ValueEnforcer.notEmpty (new ArrayList <> (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty8 ()
  {
    ValueEnforcer.notEmpty (new Vector <> (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty9 ()
  {
    ValueEnforcer.notEmpty (new HashMap <> (), "null");
  }

  // Test enabled/disabled functionality
  @Test
  public void testEnabledDisabled ()
  {
    // Save original state
    final boolean bOriginal = ValueEnforcer.isEnabled ();

    try
    {
      // Test enabled state
      ValueEnforcer.setEnabled (true);
      assertTrue (ValueEnforcer.isEnabled ());

      // Test disabled state
      ValueEnforcer.setEnabled (false);
      assertFalse (ValueEnforcer.isEnabled ());

      // When disabled, validations should not throw exceptions
      // Should not throw
      ValueEnforcer.notNull (null, "test");
      // Should not throw
      ValueEnforcer.isTrue (false, "test");
    }
    finally
    {
      // Restore original state
      ValueEnforcer.setEnabled (bOriginal);
    }
  }

  // Test isTrue methods
  @Test
  public void testIsTrue ()
  {
    // Test successful cases
    ValueEnforcer.isTrue (true, "test");
    ValueEnforcer.isTrue (true, () -> "test");
    ValueEnforcer.isTrue ( () -> true, "test");
    ValueEnforcer.isTrue ( () -> true, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsTrueFail1 ()
  {
    ValueEnforcer.isTrue (false, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsTrueFail2 ()
  {
    ValueEnforcer.isTrue (false, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsTrueFail3 ()
  {
    ValueEnforcer.isTrue ( () -> false, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsTrueFail4 ()
  {
    ValueEnforcer.isTrue ( () -> false, () -> "test");
  }

  // Test isFalse methods
  @Test
  public void testIsFalse ()
  {
    // Test successful cases
    ValueEnforcer.isFalse (false, "test");
    ValueEnforcer.isFalse (false, () -> "test");
    ValueEnforcer.isFalse ( () -> false, "test");
    ValueEnforcer.isFalse ( () -> false, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsFalseFail1 ()
  {
    ValueEnforcer.isFalse (true, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsFalseFail2 ()
  {
    ValueEnforcer.isFalse (true, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsFalseFail3 ()
  {
    ValueEnforcer.isFalse ( () -> true, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsFalseFail4 ()
  {
    ValueEnforcer.isFalse ( () -> true, () -> "test");
  }

  // Test isInstanceOf methods
  @Test
  public void testIsInstanceOf ()
  {
    final String sTest = "test";
    ValueEnforcer.isInstanceOf (sTest, String.class, "test");
    ValueEnforcer.isInstanceOf (sTest, String.class, () -> "test");
    ValueEnforcer.isInstanceOf (null, String.class, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsInstanceOfFail1 ()
  {
    ValueEnforcer.isInstanceOf ("test", Integer.class, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsInstanceOfFail2 ()
  {
    ValueEnforcer.isInstanceOf ("test", Integer.class, () -> "test");
  }

  // Test notNull success cases
  @Test
  public void testNotNullSuccess ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.notNull (sTest, "test"));
    assertSame (sTest, ValueEnforcer.notNull (sTest, () -> "test"));
  }

  // Test isNull methods
  @Test
  public void testIsNull ()
  {
    ValueEnforcer.isNull (null, "test");
    ValueEnforcer.isNull (null, () -> "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNullFail1 ()
  {
    ValueEnforcer.isNull ("test", "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNullFail2 ()
  {
    ValueEnforcer.isNull ("test", () -> "test");
  }

  // Test notEmpty success cases
  @Test
  public void testNotEmptySuccess ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.notEmpty (sTest, "test"));
    assertSame (sTest, ValueEnforcer.notEmpty (sTest, () -> "test"));

    final String [] aTest = { "a", "b" };
    assertSame (aTest, ValueEnforcer.notEmpty (aTest, "test"));
    assertSame (aTest, ValueEnforcer.notEmpty (aTest, () -> "test"));

    final ArrayList <String> aList = new ArrayList <> ();
    aList.add ("test");
    assertSame (aList, ValueEnforcer.notEmpty (aList, "test"));
    assertSame (aList, ValueEnforcer.notEmpty (aList, () -> "test"));
  }

  // Test primitive array notEmpty
  @Test
  public void testNotEmptyPrimitiveArrays ()
  {
    // Test boolean arrays
    final boolean [] aBooleans = { true, false };
    assertSame (aBooleans, ValueEnforcer.notEmpty (aBooleans, "test"));
    assertSame (aBooleans, ValueEnforcer.notEmpty (aBooleans, () -> "test"));

    // Test byte arrays
    final byte [] aBytes = { 1, 2, 3 };
    assertSame (aBytes, ValueEnforcer.notEmpty (aBytes, "test"));
    assertSame (aBytes, ValueEnforcer.notEmpty (aBytes, () -> "test"));

    // Test char arrays
    final char [] aChars = { 'a', 'b' };
    assertSame (aChars, ValueEnforcer.notEmpty (aChars, "test"));
    assertSame (aChars, ValueEnforcer.notEmpty (aChars, () -> "test"));

    // Test double arrays
    final double [] aDoubles = { 1.0, 2.0 };
    assertSame (aDoubles, ValueEnforcer.notEmpty (aDoubles, "test"));
    assertSame (aDoubles, ValueEnforcer.notEmpty (aDoubles, () -> "test"));

    // Test float arrays
    final float [] aFloats = { 1.0f, 2.0f };
    assertSame (aFloats, ValueEnforcer.notEmpty (aFloats, "test"));
    assertSame (aFloats, ValueEnforcer.notEmpty (aFloats, () -> "test"));

    // Test int arrays
    final int [] aInts = { 1, 2, 3 };
    assertSame (aInts, ValueEnforcer.notEmpty (aInts, "test"));
    assertSame (aInts, ValueEnforcer.notEmpty (aInts, () -> "test"));

    // Test long arrays
    final long [] aLongs = { 1L, 2L };
    assertSame (aLongs, ValueEnforcer.notEmpty (aLongs, "test"));
    assertSame (aLongs, ValueEnforcer.notEmpty (aLongs, () -> "test"));

    // Test short arrays
    final short [] aShorts = { 1, 2 };
    assertSame (aShorts, ValueEnforcer.notEmpty (aShorts, "test"));
    assertSame (aShorts, ValueEnforcer.notEmpty (aShorts, () -> "test"));
  }

  // Test Maps and Iterables
  @Test
  public void testNotEmptyMapsAndIterables ()
  {
    final HashMap <String, String> aMap = new HashMap <> ();
    aMap.put ("key", "value");
    assertSame (aMap, ValueEnforcer.notEmpty (aMap, "test"));
    assertSame (aMap, ValueEnforcer.notEmpty (aMap, () -> "test"));

    final Vector <String> aVector = new Vector <> ();
    aVector.add ("test");
    assertSame (aVector, ValueEnforcer.notEmpty (aVector, "test"));
    assertSame (aVector, ValueEnforcer.notEmpty (aVector, () -> "test"));
  }

  // Test noNullValue methods
  @Test
  public void testNoNullValue ()
  {
    final String [] aTest = { "a", "b", "c" };
    assertSame (aTest, ValueEnforcer.noNullValue (aTest, "test"));
    assertSame (aTest, ValueEnforcer.noNullValue (aTest, () -> "test"));

    final ArrayList <String> aList = new ArrayList <> ();
    aList.add ("test1");
    aList.add ("test2");
    assertSame (aList, ValueEnforcer.noNullValue (aList, "test"));
    assertSame (aList, ValueEnforcer.noNullValue (aList, () -> "test"));

    final HashMap <String, String> aMap = new HashMap <> ();
    aMap.put ("key1", "value1");
    aMap.put ("key2", "value2");
    assertSame (aMap, ValueEnforcer.noNullValue (aMap, "test"));
    assertSame (aMap, ValueEnforcer.noNullValue (aMap, () -> "test"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNoNullValueArrayFail ()
  {
    final String [] aTest = { "a", null, "c" };
    ValueEnforcer.noNullValue (aTest, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNoNullValueIterableFail ()
  {
    final ArrayList <String> aList = new ArrayList <> ();
    aList.add ("test1");
    aList.add (null);
    ValueEnforcer.noNullValue (aList, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNoNullValueMapFail ()
  {
    final HashMap <String, String> aMap = new HashMap <> ();
    aMap.put ("key1", "value1");
    aMap.put ("key2", null);
    ValueEnforcer.noNullValue (aMap, "test");
  }

  // Test combined notNullNoNullValue methods
  @Test
  public void testNotNullNoNullValue ()
  {
    final String [] aTest = { "a", "b" };
    assertSame (aTest, ValueEnforcer.notNullNoNullValue (aTest, "test"));
    assertSame (aTest, ValueEnforcer.notNullNoNullValue (aTest, () -> "test"));

    final ArrayList <String> aList = new ArrayList <> ();
    aList.add ("test");
    assertSame (aList, ValueEnforcer.notNullNoNullValue (aList, "test"));
    assertSame (aList, ValueEnforcer.notNullNoNullValue (aList, () -> "test"));

    final HashMap <String, String> aMap = new HashMap <> ();
    aMap.put ("key", "value");
    assertSame (aMap, ValueEnforcer.notNullNoNullValue (aMap, "test"));
    assertSame (aMap, ValueEnforcer.notNullNoNullValue (aMap, () -> "test"));
  }

  @Test (expected = NullPointerException.class)
  public void testNotNullNoNullValueNullArray ()
  {
    ValueEnforcer.notNullNoNullValue ((String []) null, "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotNullNoNullValueNullInArray ()
  {
    final String [] aTest = { "a", null };
    ValueEnforcer.notNullNoNullValue (aTest, "test");
  }

  // Test notEmptyNoNullValue methods
  @Test
  public void testNotEmptyNoNullValue ()
  {
    final String [] aTest = { "a", "b" };
    assertSame (aTest, ValueEnforcer.notEmptyNoNullValue (aTest, "test"));
    assertSame (aTest, ValueEnforcer.notEmptyNoNullValue (aTest, () -> "test"));

    final ArrayList <String> aList = new ArrayList <> ();
    aList.add ("test");
    assertSame (aList, ValueEnforcer.notEmptyNoNullValue (aList, "test"));
    assertSame (aList, ValueEnforcer.notEmptyNoNullValue (aList, () -> "test"));

    final HashMap <String, String> aMap = new HashMap <> ();
    aMap.put ("key", "value");
    assertSame (aMap, ValueEnforcer.notEmptyNoNullValue (aMap, "test"));
    assertSame (aMap, ValueEnforcer.notEmptyNoNullValue (aMap, () -> "test"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmptyNoNullValueEmptyArray ()
  {
    ValueEnforcer.notEmptyNoNullValue (new String [0], "test");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmptyNoNullValueNullInArray ()
  {
    final String [] aTest = { "a", null };
    ValueEnforcer.notEmptyNoNullValue (aTest, "test");
  }

  // Test notNullNotEquals methods
  @Test
  public void testNotNullNotEquals ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.notNullNotEquals (sTest, "name", "different"));
    assertSame (sTest, ValueEnforcer.notNullNotEquals (sTest, () -> "name", "different"));
  }

  @Test (expected = NullPointerException.class)
  public void testNotNullNotEqualsNull ()
  {
    ValueEnforcer.notNullNotEquals (null, "name", "unexpected");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotNullNotEqualsEqual ()
  {
    ValueEnforcer.notNullNotEquals ("test", "name", "test");
  }

  // Test notNullAndEquals methods
  @Test
  public void testNotNullAndEquals ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.notNullAndEquals (sTest, "name", "test"));
    assertSame (sTest, ValueEnforcer.notNullAndEquals (sTest, () -> "name", "test"));
  }

  @Test (expected = NullPointerException.class)
  public void testNotNullAndEqualsNull ()
  {
    ValueEnforcer.notNullAndEquals (null, "name", "expected");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotNullAndEqualsNotEqual ()
  {
    ValueEnforcer.notNullAndEquals ("test", "name", "different");
  }

  // Test isSame methods
  @Test
  public void testIsSame ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.isSame (sTest, "name", sTest));
    assertSame (sTest, ValueEnforcer.isSame (sTest, () -> "name", sTest));

    // Test with null values
    assertSame (null, ValueEnforcer.isSame (null, "name", null));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsSameNotSame ()
  {
    final char [] aTest1 = "test".toCharArray ();
    final char [] aTest2 = "test".toCharArray ();
    ValueEnforcer.isSame (aTest1, "name", aTest2);
  }

  // Test isEqual methods
  @Test
  public void testIsEqual ()
  {
    final String sTest = "test";
    assertSame (sTest, ValueEnforcer.isEqual (sTest, "test", "name"));
    assertSame (sTest, ValueEnforcer.isEqual (sTest, "test", () -> "name"));

    // Test null values
    assertSame (null, ValueEnforcer.isEqual (null, null, "name"));

    // Test primitive equality
    ValueEnforcer.isEqual (42, 42, "name");
    ValueEnforcer.isEqual (42, 42, () -> "name");
    ValueEnforcer.isEqual (42L, 42L, "name");
    ValueEnforcer.isEqual (42L, 42L, () -> "name");
    ValueEnforcer.isEqual (3.14, 3.14, "name");
    ValueEnforcer.isEqual (3.14, 3.14, () -> "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsEqualNotEqual ()
  {
    ValueEnforcer.isEqual ("test", "different", "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsEqualIntNotEqual ()
  {
    ValueEnforcer.isEqual (42, 43, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsEqualLongNotEqual ()
  {
    ValueEnforcer.isEqual (42L, 43L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsEqualDoubleNotEqual ()
  {
    ValueEnforcer.isEqual (3.14, 3.15, "name");
  }

  // Test isGE0 (greater than or equal to 0) methods
  @Test
  public void testIsGE0 ()
  {
    // Test int
    assertEquals (0, ValueEnforcer.isGE0 (0, "name"));
    assertEquals (42, ValueEnforcer.isGE0 (42, "name"));
    assertEquals (42, ValueEnforcer.isGE0 (42, () -> "name"));

    // Test long
    assertEquals (0L, ValueEnforcer.isGE0 (0L, "name"));
    assertEquals (42L, ValueEnforcer.isGE0 (42L, "name"));
    assertEquals (42L, ValueEnforcer.isGE0 (42L, () -> "name"));

    // Test short
    assertEquals ((short) 0, ValueEnforcer.isGE0 ((short) 0, "name"));
    assertEquals ((short) 42, ValueEnforcer.isGE0 ((short) 42, "name"));
    assertEquals ((short) 42, ValueEnforcer.isGE0 ((short) 42, () -> "name"));

    // Test double
    assertEquals (0.0, ValueEnforcer.isGE0 (0.0, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isGE0 (3.14, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isGE0 (3.14, () -> "name"), 0.0);

    // Test float
    assertEquals (0.0f, ValueEnforcer.isGE0 (0.0f, "name"), 0.0f);
    assertEquals (3.14f, ValueEnforcer.isGE0 (3.14f, "name"), 0.0f);
    assertEquals (3.14f, ValueEnforcer.isGE0 (3.14f, () -> "name"), 0.0f);

    // Test BigDecimal
    final BigDecimal bd = new BigDecimal ("42.5");
    assertSame (bd, ValueEnforcer.isGE0 (bd, "name"));
    assertSame (bd, ValueEnforcer.isGE0 (bd, () -> "name"));
    assertSame (BigDecimal.ZERO, ValueEnforcer.isGE0 (BigDecimal.ZERO, "name"));

    // Test BigInteger
    final BigInteger bi = new BigInteger ("42");
    assertSame (bi, ValueEnforcer.isGE0 (bi, "name"));
    assertSame (bi, ValueEnforcer.isGE0 (bi, () -> "name"));
    assertSame (BigInteger.ZERO, ValueEnforcer.isGE0 (BigInteger.ZERO, "name"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0IntNegative ()
  {
    ValueEnforcer.isGE0 (-1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0LongNegative ()
  {
    ValueEnforcer.isGE0 (-1L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0ShortNegative ()
  {
    ValueEnforcer.isGE0 ((short) -1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0DoubleNegative ()
  {
    ValueEnforcer.isGE0 (-0.1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0FloatNegative ()
  {
    ValueEnforcer.isGE0 (-0.1f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0BigDecimalNegative ()
  {
    ValueEnforcer.isGE0 (new BigDecimal ("-0.1"), "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGE0BigIntegerNegative ()
  {
    ValueEnforcer.isGE0 (new BigInteger ("-1"), "name");
  }

  // Test isGT0 (greater than 0) methods
  @Test
  public void testIsGT0 ()
  {
    // Test int
    assertEquals (1, ValueEnforcer.isGT0 (1, "name"));
    assertEquals (42, ValueEnforcer.isGT0 (42, "name"));
    assertEquals (42, ValueEnforcer.isGT0 (42, () -> "name"));

    // Test long
    assertEquals (1L, ValueEnforcer.isGT0 (1L, "name"));
    assertEquals (42L, ValueEnforcer.isGT0 (42L, "name"));
    assertEquals (42L, ValueEnforcer.isGT0 (42L, () -> "name"));

    // Test short
    assertEquals ((short) 1, ValueEnforcer.isGT0 ((short) 1, "name"));
    assertEquals ((short) 42, ValueEnforcer.isGT0 ((short) 42, "name"));
    assertEquals ((short) 42, ValueEnforcer.isGT0 ((short) 42, () -> "name"));

    // Test double
    assertEquals (0.1, ValueEnforcer.isGT0 (0.1, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isGT0 (3.14, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isGT0 (3.14, () -> "name"), 0.0);

    // Test float
    assertEquals (0.1f, ValueEnforcer.isGT0 (0.1f, "name"), 0.0f);
    assertEquals (3.14f, ValueEnforcer.isGT0 (3.14f, "name"), 0.0f);
    assertEquals (3.14f, ValueEnforcer.isGT0 (3.14f, () -> "name"), 0.0f);

    // Test BigDecimal
    final BigDecimal bd = new BigDecimal ("42.5");
    assertSame (bd, ValueEnforcer.isGT0 (bd, "name"));
    assertSame (bd, ValueEnforcer.isGT0 (bd, () -> "name"));

    // Test BigInteger
    final BigInteger bi = new BigInteger ("42");
    assertSame (bi, ValueEnforcer.isGT0 (bi, "name"));
    assertSame (bi, ValueEnforcer.isGT0 (bi, () -> "name"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0IntZero ()
  {
    ValueEnforcer.isGT0 (0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0IntNegative ()
  {
    ValueEnforcer.isGT0 (-1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0LongZero ()
  {
    ValueEnforcer.isGT0 (0L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0LongNegative ()
  {
    ValueEnforcer.isGT0 (-1L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0ShortZero ()
  {
    ValueEnforcer.isGT0 ((short) 0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0ShortNegative ()
  {
    ValueEnforcer.isGT0 ((short) -1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0DoubleZero ()
  {
    ValueEnforcer.isGT0 (0.0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0DoubleNegative ()
  {
    ValueEnforcer.isGT0 (-0.1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0FloatZero ()
  {
    ValueEnforcer.isGT0 (0.0f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0FloatNegative ()
  {
    ValueEnforcer.isGT0 (-0.1f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0BigDecimalZero ()
  {
    ValueEnforcer.isGT0 (BigDecimal.ZERO, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0BigDecimalNegative ()
  {
    ValueEnforcer.isGT0 (new BigDecimal ("-0.1"), "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0BigIntegerZero ()
  {
    ValueEnforcer.isGT0 (BigInteger.ZERO, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsGT0BigIntegerNegative ()
  {
    ValueEnforcer.isGT0 (new BigInteger ("-1"), "name");
  }

  // Test isNE0 (not equal to 0) methods
  @Test
  public void testIsNE0 ()
  {
    // Test int
    assertEquals (1, ValueEnforcer.isNE0 (1, "name"));
    assertEquals (-1, ValueEnforcer.isNE0 (-1, "name"));
    assertEquals (42, ValueEnforcer.isNE0 (42, "name"));
    assertEquals (42, ValueEnforcer.isNE0 (42, () -> "name"));

    // Test long
    assertEquals (1L, ValueEnforcer.isNE0 (1L, "name"));
    assertEquals (-1L, ValueEnforcer.isNE0 (-1L, "name"));
    assertEquals (42L, ValueEnforcer.isNE0 (42L, "name"));
    assertEquals (42L, ValueEnforcer.isNE0 (42L, () -> "name"));

    // Test double
    assertEquals (0.1, ValueEnforcer.isNE0 (0.1, "name"), 0.0);
    assertEquals (-0.1, ValueEnforcer.isNE0 (-0.1, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isNE0 (3.14, "name"), 0.0);
    assertEquals (3.14, ValueEnforcer.isNE0 (3.14, () -> "name"), 0.0);

    // Test BigDecimal
    final BigDecimal bd = new BigDecimal ("42.5");
    assertSame (bd, ValueEnforcer.isNE0 (bd, "name"));
    assertSame (bd, ValueEnforcer.isNE0 (bd, () -> "name"));
    final BigDecimal bdNeg = new BigDecimal ("-42.5");
    assertSame (bdNeg, ValueEnforcer.isNE0 (bdNeg, "name"));

    // Test BigInteger
    final BigInteger bi = new BigInteger ("42");
    assertSame (bi, ValueEnforcer.isNE0 (bi, "name"));
    assertSame (bi, ValueEnforcer.isNE0 (bi, () -> "name"));
    final BigInteger biNeg = new BigInteger ("-42");
    assertSame (biNeg, ValueEnforcer.isNE0 (biNeg, "name"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNE0IntZero ()
  {
    ValueEnforcer.isNE0 (0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNE0LongZero ()
  {
    ValueEnforcer.isNE0 (0L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNE0DoubleZero ()
  {
    ValueEnforcer.isNE0 (0.0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNE0BigDecimalZero ()
  {
    ValueEnforcer.isNE0 (BigDecimal.ZERO, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsNE0BigIntegerZero ()
  {
    ValueEnforcer.isNE0 (BigInteger.ZERO, "name");
  }

  // Test isLE0 (less than or equal to 0) methods
  @Test
  public void testIsLE0 ()
  {
    // Test int
    assertEquals (0, ValueEnforcer.isLE0 (0, "name"));
    assertEquals (-1, ValueEnforcer.isLE0 (-1, "name"));
    assertEquals (-42, ValueEnforcer.isLE0 (-42, "name"));
    assertEquals (-42, ValueEnforcer.isLE0 (-42, () -> "name"));

    // Test long
    assertEquals (0L, ValueEnforcer.isLE0 (0L, "name"));
    assertEquals (-1L, ValueEnforcer.isLE0 (-1L, "name"));
    assertEquals (-42L, ValueEnforcer.isLE0 (-42L, "name"));
    assertEquals (-42L, ValueEnforcer.isLE0 (-42L, () -> "name"));

    // Test short
    assertEquals ((short) 0, ValueEnforcer.isLE0 ((short) 0, "name"));
    assertEquals ((short) -1, ValueEnforcer.isLE0 ((short) -1, "name"));
    assertEquals ((short) -42, ValueEnforcer.isLE0 ((short) -42, "name"));
    assertEquals ((short) -42, ValueEnforcer.isLE0 ((short) -42, () -> "name"));

    // Test double
    assertEquals (0.0, ValueEnforcer.isLE0 (0.0, "name"), 0.0);
    assertEquals (-0.1, ValueEnforcer.isLE0 (-0.1, "name"), 0.0);
    assertEquals (-3.14, ValueEnforcer.isLE0 (-3.14, "name"), 0.0);
    assertEquals (-3.14, ValueEnforcer.isLE0 (-3.14, () -> "name"), 0.0);

    // Test float
    assertEquals (0.0f, ValueEnforcer.isLE0 (0.0f, "name"), 0.0f);
    assertEquals (-0.1f, ValueEnforcer.isLE0 (-0.1f, "name"), 0.0f);
    assertEquals (-3.14f, ValueEnforcer.isLE0 (-3.14f, "name"), 0.0f);
    assertEquals (-3.14f, ValueEnforcer.isLE0 (-3.14f, () -> "name"), 0.0f);

    // Test BigDecimal
    assertSame (BigDecimal.ZERO, ValueEnforcer.isLE0 (BigDecimal.ZERO, "name"));
    assertSame (BigDecimal.ZERO, ValueEnforcer.isLE0 (BigDecimal.ZERO, () -> "name"));
    final BigDecimal bdNeg = new BigDecimal ("-42.5");
    assertSame (bdNeg, ValueEnforcer.isLE0 (bdNeg, "name"));

    // Test BigInteger
    assertSame (BigInteger.ZERO, ValueEnforcer.isLE0 (BigInteger.ZERO, "name"));
    assertSame (BigInteger.ZERO, ValueEnforcer.isLE0 (BigInteger.ZERO, () -> "name"));
    final BigInteger biNeg = new BigInteger ("-42");
    assertSame (biNeg, ValueEnforcer.isLE0 (biNeg, "name"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0IntPositive ()
  {
    ValueEnforcer.isLE0 (1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0LongPositive ()
  {
    ValueEnforcer.isLE0 (1L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0ShortPositive ()
  {
    ValueEnforcer.isLE0 ((short) 1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0DoublePositive ()
  {
    ValueEnforcer.isLE0 (0.1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0FloatPositive ()
  {
    ValueEnforcer.isLE0 (0.1f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0BigDecimalPositive ()
  {
    ValueEnforcer.isLE0 (new BigDecimal ("0.1"), "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLE0BigIntegerPositive ()
  {
    ValueEnforcer.isLE0 (new BigInteger ("1"), "name");
  }

  // Test isLT0 (less than 0) methods
  @Test
  public void testIsLT0 ()
  {
    // Test int
    assertEquals (-1, ValueEnforcer.isLT0 (-1, "name"));
    assertEquals (-42, ValueEnforcer.isLT0 (-42, "name"));
    assertEquals (-42, ValueEnforcer.isLT0 (-42, () -> "name"));

    // Test long
    assertEquals (-1L, ValueEnforcer.isLT0 (-1L, "name"));
    assertEquals (-42L, ValueEnforcer.isLT0 (-42L, "name"));
    assertEquals (-42L, ValueEnforcer.isLT0 (-42L, () -> "name"));

    // Test short
    assertEquals ((short) -1, ValueEnforcer.isLT0 ((short) -1, "name"));
    assertEquals ((short) -42, ValueEnforcer.isLT0 ((short) -42, "name"));
    assertEquals ((short) -42, ValueEnforcer.isLT0 ((short) -42, () -> "name"));

    // Test double
    assertEquals (-0.1, ValueEnforcer.isLT0 (-0.1, "name"), 0.0);
    assertEquals (-3.14, ValueEnforcer.isLT0 (-3.14, "name"), 0.0);
    assertEquals (-3.14, ValueEnforcer.isLT0 (-3.14, () -> "name"), 0.0);

    // Test float
    assertEquals (-0.1f, ValueEnforcer.isLT0 (-0.1f, "name"), 0.0f);
    assertEquals (-3.14f, ValueEnforcer.isLT0 (-3.14f, "name"), 0.0f);
    assertEquals (-3.14f, ValueEnforcer.isLT0 (-3.14f, () -> "name"), 0.0f);

    // Test BigDecimal
    final BigDecimal bdNeg = new BigDecimal ("-42.5");
    assertSame (bdNeg, ValueEnforcer.isLT0 (bdNeg, "name"));
    assertSame (bdNeg, ValueEnforcer.isLT0 (bdNeg, () -> "name"));

    // Test BigInteger
    final BigInteger biNeg = new BigInteger ("-42");
    assertSame (biNeg, ValueEnforcer.isLT0 (biNeg, "name"));
    assertSame (biNeg, ValueEnforcer.isLT0 (biNeg, () -> "name"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0IntZero ()
  {
    ValueEnforcer.isLT0 (0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0IntPositive ()
  {
    ValueEnforcer.isLT0 (1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0LongZero ()
  {
    ValueEnforcer.isLT0 (0L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0LongPositive ()
  {
    ValueEnforcer.isLT0 (1L, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0ShortZero ()
  {
    ValueEnforcer.isLT0 ((short) 0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0ShortPositive ()
  {
    ValueEnforcer.isLT0 ((short) 1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0DoubleZero ()
  {
    ValueEnforcer.isLT0 (0.0, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0DoublePositive ()
  {
    ValueEnforcer.isLT0 (0.1, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0FloatZero ()
  {
    ValueEnforcer.isLT0 (0.0f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0FloatPositive ()
  {
    ValueEnforcer.isLT0 (0.1f, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0BigDecimalZero ()
  {
    ValueEnforcer.isLT0 (BigDecimal.ZERO, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0BigDecimalPositive ()
  {
    ValueEnforcer.isLT0 (new BigDecimal ("0.1"), "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0BigIntegerZero ()
  {
    ValueEnforcer.isLT0 (BigInteger.ZERO, "name");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsLT0BigIntegerPositive ()
  {
    ValueEnforcer.isLT0 (new BigInteger ("1"), "name");
  }

  // Test isBetweenInclusive methods
  @Test
  public void testIsBetweenInclusive ()
  {
    // Test int
    assertEquals (5, ValueEnforcer.isBetweenInclusive (5, "name", 1, 10));
    // boundary
    assertEquals (1, ValueEnforcer.isBetweenInclusive (1, "name", 1, 10));
    // boundary
    assertEquals (10, ValueEnforcer.isBetweenInclusive (10, "name", 1, 10));
    assertEquals (5, ValueEnforcer.isBetweenInclusive (5, () -> "name", 1, 10));

    // Test long
    assertEquals (5L, ValueEnforcer.isBetweenInclusive (5L, "name", 1L, 10L));
    assertEquals (1L, ValueEnforcer.isBetweenInclusive (1L, "name", 1L, 10L));
    assertEquals (10L, ValueEnforcer.isBetweenInclusive (10L, "name", 1L, 10L));
    assertEquals (5L, ValueEnforcer.isBetweenInclusive (5L, () -> "name", 1L, 10L));

    // Test short
    assertEquals ((short) 5, ValueEnforcer.isBetweenInclusive ((short) 5, "name", (short) 1, (short) 10));
    assertEquals ((short) 1, ValueEnforcer.isBetweenInclusive ((short) 1, "name", (short) 1, (short) 10));
    assertEquals ((short) 10, ValueEnforcer.isBetweenInclusive ((short) 10, "name", (short) 1, (short) 10));
    assertEquals ((short) 5, ValueEnforcer.isBetweenInclusive ((short) 5, () -> "name", (short) 1, (short) 10));

    // Test double
    assertEquals (5.5, ValueEnforcer.isBetweenInclusive (5.5, "name", 1.0, 10.0), 0.0);
    assertEquals (1.0, ValueEnforcer.isBetweenInclusive (1.0, "name", 1.0, 10.0), 0.0);
    assertEquals (10.0, ValueEnforcer.isBetweenInclusive (10.0, "name", 1.0, 10.0), 0.0);
    assertEquals (5.5, ValueEnforcer.isBetweenInclusive (5.5, () -> "name", 1.0, 10.0), 0.0);

    // Test float
    assertEquals (5.5f, ValueEnforcer.isBetweenInclusive (5.5f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (1.0f, ValueEnforcer.isBetweenInclusive (1.0f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (10.0f, ValueEnforcer.isBetweenInclusive (10.0f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (5.5f, ValueEnforcer.isBetweenInclusive (5.5f, () -> "name", 1.0f, 10.0f), 0.0f);

    // Test BigDecimal
    final BigDecimal bd5 = new BigDecimal ("5.5");
    final BigDecimal bd1 = new BigDecimal ("1.0");
    final BigDecimal bd10 = new BigDecimal ("10.0");
    assertSame (bd5, ValueEnforcer.isBetweenInclusive (bd5, "name", bd1, bd10));
    assertSame (bd1, ValueEnforcer.isBetweenInclusive (bd1, "name", bd1, bd10));
    assertSame (bd10, ValueEnforcer.isBetweenInclusive (bd10, "name", bd1, bd10));
    assertSame (bd5, ValueEnforcer.isBetweenInclusive (bd5, () -> "name", bd1, bd10));

    // Test BigInteger
    final BigInteger bi5 = new BigInteger ("5");
    final BigInteger bi1 = new BigInteger ("1");
    final BigInteger bi10 = new BigInteger ("10");
    assertSame (bi5, ValueEnforcer.isBetweenInclusive (bi5, "name", bi1, bi10));
    assertSame (bi1, ValueEnforcer.isBetweenInclusive (bi1, "name", bi1, bi10));
    assertSame (bi10, ValueEnforcer.isBetweenInclusive (bi10, "name", bi1, bi10));
    assertSame (bi5, ValueEnforcer.isBetweenInclusive (bi5, () -> "name", bi1, bi10));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveIntTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (0, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveIntTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (11, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveLongTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (0L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveLongTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (11L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveShortTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive ((short) 0, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveShortTooBig ()
  {
    ValueEnforcer.isBetweenInclusive ((short) 11, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveDoubleTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (0.5, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveDoubleTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (10.5, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveFloatTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (0.5f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveFloatTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (10.5f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveBigDecimalTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (new BigDecimal ("0.5"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveBigDecimalTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (new BigDecimal ("10.5"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveBigIntegerTooSmall ()
  {
    ValueEnforcer.isBetweenInclusive (new BigInteger ("0"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenInclusiveBigIntegerTooBig ()
  {
    ValueEnforcer.isBetweenInclusive (new BigInteger ("11"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  // Test isBetweenExclusive methods
  @Test
  public void testIsBetweenExclusive ()
  {
    // Test int
    assertEquals (5, ValueEnforcer.isBetweenExclusive (5, "name", 1, 10));
    assertEquals (2, ValueEnforcer.isBetweenExclusive (2, "name", 1, 10));
    assertEquals (9, ValueEnforcer.isBetweenExclusive (9, "name", 1, 10));
    assertEquals (5, ValueEnforcer.isBetweenExclusive (5, () -> "name", 1, 10));

    // Test long
    assertEquals (5L, ValueEnforcer.isBetweenExclusive (5L, "name", 1L, 10L));
    assertEquals (2L, ValueEnforcer.isBetweenExclusive (2L, "name", 1L, 10L));
    assertEquals (9L, ValueEnforcer.isBetweenExclusive (9L, "name", 1L, 10L));
    assertEquals (5L, ValueEnforcer.isBetweenExclusive (5L, () -> "name", 1L, 10L));

    // Test short
    assertEquals ((short) 5, ValueEnforcer.isBetweenExclusive ((short) 5, "name", (short) 1, (short) 10));
    assertEquals ((short) 2, ValueEnforcer.isBetweenExclusive ((short) 2, "name", (short) 1, (short) 10));
    assertEquals ((short) 9, ValueEnforcer.isBetweenExclusive ((short) 9, "name", (short) 1, (short) 10));
    assertEquals ((short) 5, ValueEnforcer.isBetweenExclusive ((short) 5, () -> "name", (short) 1, (short) 10));

    // Test double
    assertEquals (5.5, ValueEnforcer.isBetweenExclusive (5.5, "name", 1.0, 10.0), 0.0);
    assertEquals (1.1, ValueEnforcer.isBetweenExclusive (1.1, "name", 1.0, 10.0), 0.0);
    assertEquals (9.9, ValueEnforcer.isBetweenExclusive (9.9, "name", 1.0, 10.0), 0.0);
    assertEquals (5.5, ValueEnforcer.isBetweenExclusive (5.5, () -> "name", 1.0, 10.0), 0.0);

    // Test float
    assertEquals (5.5f, ValueEnforcer.isBetweenExclusive (5.5f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (1.1f, ValueEnforcer.isBetweenExclusive (1.1f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (9.9f, ValueEnforcer.isBetweenExclusive (9.9f, "name", 1.0f, 10.0f), 0.0f);
    assertEquals (5.5f, ValueEnforcer.isBetweenExclusive (5.5f, () -> "name", 1.0f, 10.0f), 0.0f);

    // Test BigDecimal
    final BigDecimal bd5 = new BigDecimal ("5.5");
    final BigDecimal bd1 = new BigDecimal ("1.0");
    final BigDecimal bd10 = new BigDecimal ("10.0");
    final BigDecimal bd11 = new BigDecimal ("1.1");
    final BigDecimal bd99 = new BigDecimal ("9.9");
    assertSame (bd5, ValueEnforcer.isBetweenExclusive (bd5, "name", bd1, bd10));
    assertSame (bd11, ValueEnforcer.isBetweenExclusive (bd11, "name", bd1, bd10));
    assertSame (bd99, ValueEnforcer.isBetweenExclusive (bd99, "name", bd1, bd10));
    assertSame (bd5, ValueEnforcer.isBetweenExclusive (bd5, () -> "name", bd1, bd10));

    // Test BigInteger
    final BigInteger bi5 = new BigInteger ("5");
    final BigInteger bi1 = new BigInteger ("1");
    final BigInteger bi10 = new BigInteger ("10");
    final BigInteger bi2 = new BigInteger ("2");
    final BigInteger bi9 = new BigInteger ("9");
    assertSame (bi5, ValueEnforcer.isBetweenExclusive (bi5, "name", bi1, bi10));
    assertSame (bi2, ValueEnforcer.isBetweenExclusive (bi2, "name", bi1, bi10));
    assertSame (bi9, ValueEnforcer.isBetweenExclusive (bi9, "name", bi1, bi10));
    assertSame (bi5, ValueEnforcer.isBetweenExclusive (bi5, () -> "name", bi1, bi10));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveIntTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (0, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveIntLowerBoundary ()
  {
    // boundary values excluded
    ValueEnforcer.isBetweenExclusive (1, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveIntUpperBoundary ()
  {
    // boundary values excluded
    ValueEnforcer.isBetweenExclusive (10, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveIntTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (11, "name", 1, 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveLongTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (0L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveLongLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (1L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveLongUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (10L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveLongTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (11L, "name", 1L, 10L);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveShortTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive ((short) 0, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveShortLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive ((short) 1, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveShortUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive ((short) 10, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveShortTooBig ()
  {
    ValueEnforcer.isBetweenExclusive ((short) 11, "name", (short) 1, (short) 10);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveDoubleTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (0.5, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveDoubleLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (1.0, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveDoubleUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (10.0, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveDoubleTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (10.5, "name", 1.0, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveFloatTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (0.5f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveFloatLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (1.0f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveFloatUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (10.0f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveFloatTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (10.5f, "name", 1.0f, 10.0f);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigDecimalTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (new BigDecimal ("0.5"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigDecimalLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (new BigDecimal ("1.0"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigDecimalUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (new BigDecimal ("10.0"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigDecimalTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (new BigDecimal ("10.5"), "name", new BigDecimal ("1.0"), new BigDecimal ("10.0"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigIntegerTooSmall ()
  {
    ValueEnforcer.isBetweenExclusive (new BigInteger ("0"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigIntegerLowerBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (new BigInteger ("1"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigIntegerUpperBoundary ()
  {
    ValueEnforcer.isBetweenExclusive (new BigInteger ("10"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsBetweenExclusiveBigIntegerTooBig ()
  {
    ValueEnforcer.isBetweenExclusive (new BigInteger ("11"), "name", new BigInteger ("1"), new BigInteger ("10"));
  }

  // Test isArrayOfsLen methods
  @Test
  public void testIsArrayOfsLen ()
  {
    // Test Object array - valid cases
    final String[] aStringArray = {"a", "b", "c", "d", "e"};
    // full array
    ValueEnforcer.isArrayOfsLen (aStringArray, 0, 5);
    // beginning subset
    ValueEnforcer.isArrayOfsLen (aStringArray, 0, 3);
    // middle subset
    ValueEnforcer.isArrayOfsLen (aStringArray, 2, 3);
    // single element
    ValueEnforcer.isArrayOfsLen (aStringArray, 4, 1);
    // zero length at end
    ValueEnforcer.isArrayOfsLen (aStringArray, 5, 0);
    // zero length at start
    ValueEnforcer.isArrayOfsLen (aStringArray, 0, 0);

    // Test boolean array - valid cases
    final boolean[] aBoolArray = {true, false, true, false};
    // full array
    ValueEnforcer.isArrayOfsLen (aBoolArray, 0, 4);
    // middle subset
    ValueEnforcer.isArrayOfsLen (aBoolArray, 1, 2);
    // zero length at end
    ValueEnforcer.isArrayOfsLen (aBoolArray, 4, 0);

    // Test byte array - valid cases
    final byte[] aByteArray = {1, 2, 3, 4, 5, 6};
    // full array
    ValueEnforcer.isArrayOfsLen (aByteArray, 0, 6);
    // middle subset
    ValueEnforcer.isArrayOfsLen (aByteArray, 2, 3);
    // single element
    ValueEnforcer.isArrayOfsLen (aByteArray, 5, 1);

    // Test char array - valid cases
    final char[] aCharArray = {'a', 'b', 'c'};
    // full array
    ValueEnforcer.isArrayOfsLen (aCharArray, 0, 3);
    // single element
    ValueEnforcer.isArrayOfsLen (aCharArray, 1, 1);
    // zero length at end
    ValueEnforcer.isArrayOfsLen (aCharArray, 3, 0);

    // Test double array - valid cases
    final double[] aDoubleArray = {1.0, 2.0, 3.0, 4.0};
    // full array
    ValueEnforcer.isArrayOfsLen (aDoubleArray, 0, 4);
    // middle subset
    ValueEnforcer.isArrayOfsLen (aDoubleArray, 1, 2);

    // Test float array - valid cases
    final float[] aFloatArray = {1.0f, 2.0f, 3.0f};
    // full array
    ValueEnforcer.isArrayOfsLen (aFloatArray, 0, 3);
    // beginning subset
    ValueEnforcer.isArrayOfsLen (aFloatArray, 0, 2);

    // Test int array - valid cases
    final int[] aIntArray = {10, 20, 30, 40, 50};
    // full array
    ValueEnforcer.isArrayOfsLen (aIntArray, 0, 5);
    // middle subset
    ValueEnforcer.isArrayOfsLen (aIntArray, 2, 2);

    // Test long array - valid cases
    final long[] aLongArray = {100L, 200L, 300L};
    // full array
    ValueEnforcer.isArrayOfsLen (aLongArray, 0, 3);
    // single element
    ValueEnforcer.isArrayOfsLen (aLongArray, 1, 1);

    // Test short array - valid cases
    final short[] aShortArray = {(short) 1, (short) 2, (short) 3, (short) 4};
    // full array
    ValueEnforcer.isArrayOfsLen (aShortArray, 0, 4);
    // single element at end
    ValueEnforcer.isArrayOfsLen (aShortArray, 3, 1);
  }

  // Test null array cases
  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenObjectArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((Object []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenBooleanArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((boolean []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenByteArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((byte []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenCharArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((char []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenDoubleArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((double []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenFloatArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((float []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenIntArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((int []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenLongArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((long []) null, 0, 1);
  }

  @Test (expected = NullPointerException.class)
  public void testIsArrayOfsLenShortArrayNull ()
  {
    ValueEnforcer.isArrayOfsLen ((short []) null, 0, 1);
  }

  // Test negative offset cases
  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenObjectArrayNegativeOffset ()
  {
    final String[] aArray = {"a", "b", "c"};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenBooleanArrayNegativeOffset ()
  {
    final boolean[] aArray = {true, false};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenByteArrayNegativeOffset ()
  {
    final byte[] aArray = {1, 2, 3};
    ValueEnforcer.isArrayOfsLen (aArray, -2, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenCharArrayNegativeOffset ()
  {
    final char[] aArray = {'a', 'b'};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenDoubleArrayNegativeOffset ()
  {
    final double[] aArray = {1.0, 2.0};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenFloatArrayNegativeOffset ()
  {
    final float[] aArray = {1.0f, 2.0f};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenIntArrayNegativeOffset ()
  {
    final int[] aArray = {10, 20};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenLongArrayNegativeOffset ()
  {
    final long[] aArray = {100L, 200L};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenShortArrayNegativeOffset ()
  {
    final short[] aArray = {(short) 1, (short) 2};
    ValueEnforcer.isArrayOfsLen (aArray, -1, 1);
  }

  // Test negative length cases
  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenObjectArrayNegativeLength ()
  {
    final String[] aArray = {"a", "b", "c"};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenBooleanArrayNegativeLength ()
  {
    final boolean[] aArray = {true, false};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenByteArrayNegativeLength ()
  {
    final byte[] aArray = {1, 2, 3};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -2);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenCharArrayNegativeLength ()
  {
    final char[] aArray = {'a', 'b'};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenDoubleArrayNegativeLength ()
  {
    final double[] aArray = {1.0, 2.0};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenFloatArrayNegativeLength ()
  {
    final float[] aArray = {1.0f, 2.0f};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenIntArrayNegativeLength ()
  {
    final int[] aArray = {10, 20};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenLongArrayNegativeLength ()
  {
    final long[] aArray = {100L, 200L};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenShortArrayNegativeLength ()
  {
    final short[] aArray = {(short) 1, (short) 2};
    ValueEnforcer.isArrayOfsLen (aArray, 0, -1);
  }

  // Test offset + length exceeds array bounds cases
  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenObjectArrayExceedsBounds ()
  {
    final String[] aArray = {"a", "b", "c"};
    // 2 + 2 = 4 > 3
    ValueEnforcer.isArrayOfsLen (aArray, 2, 2);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenBooleanArrayExceedsBounds ()
  {
    final boolean[] aArray = {true, false};
    // 1 + 2 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 1, 2);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenByteArrayExceedsBounds ()
  {
    final byte[] aArray = {1, 2, 3};
    // 3 + 1 = 4 > 3
    ValueEnforcer.isArrayOfsLen (aArray, 3, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenCharArrayExceedsBounds ()
  {
    final char[] aArray = {'a', 'b'};
    // 1 + 2 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 1, 2);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenDoubleArrayExceedsBounds ()
  {
    final double[] aArray = {1.0, 2.0};
    // 0 + 3 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 0, 3);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenFloatArrayExceedsBounds ()
  {
    final float[] aArray = {1.0f, 2.0f};
    // 2 + 1 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 2, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenIntArrayExceedsBounds ()
  {
    final int[] aArray = {10, 20};
    // 1 + 2 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 1, 2);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenLongArrayExceedsBounds ()
  {
    final long[] aArray = {100L, 200L};
    // 2 + 1 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 2, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIsArrayOfsLenShortArrayExceedsBounds ()
  {
    final short[] aArray = {(short) 1, (short) 2};
    // 0 + 3 = 3 > 2
    ValueEnforcer.isArrayOfsLen (aArray, 0, 3);
  }
}
