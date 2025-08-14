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
package com.helger.commons.typeconvert;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.lang.ClassHelper;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableBoolean;
import com.helger.base.numeric.mutable.MutableByte;
import com.helger.base.numeric.mutable.MutableChar;
import com.helger.base.numeric.mutable.MutableDouble;
import com.helger.base.numeric.mutable.MutableFloat;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.numeric.mutable.MutableLong;
import com.helger.base.numeric.mutable.MutableShort;
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.state.EEnabled;
import com.helger.base.state.EInterrupt;
import com.helger.base.state.ELeftRight;
import com.helger.base.state.EMandatory;
import com.helger.base.state.ESuccess;
import com.helger.base.state.ETopBottom;
import com.helger.base.state.EValidity;
import com.helger.base.typeconvert.TypeConverterException;
import com.helger.base.typeconvert.TypeConverterException.EReason;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.locale.ELocaleName;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.StringHelper;
import com.helger.commons.text.MultilingualText;

import jakarta.annotation.Nonnull;

/**
 * Test class for class {@link TypeConverter}.
 *
 * @author Philip Helger
 */
public final class TypeConverterTest
{
  // All classes for which type converters from and to each other are defined
  private static final Class <?> [] CONVERTIBLE_CLASSES = { Boolean.class,
                                                            Byte.class,
                                                            Character.class,
                                                            Double.class,
                                                            Float.class,
                                                            Integer.class,
                                                            Long.class,
                                                            Short.class,
                                                            String.class,
                                                            StringBuilder.class,
                                                            StringBuffer.class,
                                                            BigDecimal.class,
                                                            BigInteger.class,
                                                            AtomicBoolean.class,
                                                            AtomicInteger.class,
                                                            AtomicLong.class,
                                                            MutableBoolean.class,
                                                            MutableByte.class,
                                                            MutableChar.class,
                                                            MutableDouble.class,
                                                            MutableFloat.class,
                                                            MutableInt.class,
                                                            MutableLong.class,
                                                            MutableShort.class };

  @Nonnull
  private static Object _instantiate (@Nonnull final Class <?> aClass) throws Exception
  {
    if (aClass == AtomicBoolean.class)
      return new AtomicBoolean ((aClass.hashCode () % 2) == 0);
    if (aClass == AtomicInteger.class)
      return new AtomicInteger (aClass.hashCode ());
    if (aClass == AtomicLong.class)
      return new AtomicLong (aClass.hashCode () * 4711L);
    if (aClass == Boolean.class)
      return Boolean.valueOf ((aClass.getName ().hashCode () % 3) == 1);
    if (aClass == Character.class)
      return Character.valueOf (StringHelper.getLastChar (aClass.getName ()));
    if (aClass == MutableBoolean.class)
      return new MutableBoolean ((aClass.getName ().hashCode () % 3) == 2);
    if (aClass == MutableByte.class)
      return new MutableByte ((byte) 6);
    if (aClass == MutableChar.class)
      return new MutableChar ('a');
    if (aClass == MutableDouble.class)
      return new MutableDouble (3.4567);
    if (aClass == MutableFloat.class)
      return new MutableFloat (321313.53324f);
    if (aClass == MutableInt.class)
      return new MutableInt (aClass.hashCode () * 2);
    if (aClass == MutableLong.class)
      return new MutableLong (aClass.hashCode () * 2343L);
    if (aClass == MutableShort.class)
      return new MutableShort ((short) aClass.hashCode ());
    // One character numeric string!
    return aClass.getConstructor (String.class).newInstance (Integer.toString (aClass.getName ().length () % 10));
  }

  @Test
  public void testConvertAllToAll () throws Exception
  {
    // Try convert all types into each other
    for (final Class <?> aSrcClass : CONVERTIBLE_CLASSES)
    {
      final Object aSrcValue = _instantiate (aSrcClass);

      // Convert into all other types
      for (final Class <?> aDstClass : CONVERTIBLE_CLASSES)
        assertNotNull (TypeConverter.convert (aSrcValue, aDstClass));

      // Convert to primitive types
      for (final Class <?> aDstClass : ClassHelper.getAllPrimitiveClasses ())
        assertNotNull (TypeConverter.convert (aSrcValue, aDstClass));

      assertNotNull (TypeConverter.convert (aSrcValue, List.class));
      assertNotNull (TypeConverter.convert (aSrcValue, Set.class));
    }
  }

  @Test
  public void testFromBoolean ()
  {
    for (int i = 0; i < 2; ++i)
    {
      final Boolean aSrcValue = Boolean.valueOf (i == 0);

      for (final Class <?> aDstClass : CONVERTIBLE_CLASSES)
        assertNotNull (TypeConverter.convert (aSrcValue, aDstClass));

      // Convert to primitive types
      for (final Class <?> aDstClass : ClassHelper.getAllPrimitiveClasses ())
        assertNotNull (TypeConverter.convert (aSrcValue, aDstClass));
    }
  }

  @Test
  public void testFromNonNumericString ()
  {
    // Test non numeric string
    for (final Class <?> aDstClass : ArrayHelper.getConcatenated (CONVERTIBLE_CLASSES, new Class <?> [] { Enum.class }))
      try
      {
        TypeConverter.convert ("not a number", aDstClass);
      }
      catch (final TypeConverterException ex)
      {
        assertEquals (EReason.CONVERSION_FAILED, ex.getReason ());
      }
  }

  @Test
  public void testToString ()
  {
    // Check conversion with explicit converters defined
    final Object [] aDefinedObjs = { BigDecimal.ONE,
                                     BigHelper.toBigDecimal (Double.MAX_VALUE),
                                     new BigDecimal ("123446712345678765456547865789762131.9999123446712345678765456547865789762131"),
                                     BigInteger.ZERO,
                                     new BigInteger ("123446712345678765456547865789762131"),
                                     Byte.valueOf ((byte) 5),
                                     Boolean.TRUE,
                                     Character.valueOf ('c'),
                                     Double.valueOf (1245.3433),
                                     Float.valueOf (31.451f),
                                     Integer.valueOf (17),
                                     Long.valueOf (Long.MAX_VALUE),
                                     Short.valueOf (Short.MIN_VALUE),
                                     EChange.CHANGED,
                                     EContinue.BREAK,
                                     EEnabled.DISABLED,
                                     EInterrupt.INTERRUPTED,
                                     ELeftRight.RIGHT,
                                     EMandatory.MANDATORY,
                                     ESuccess.FAILURE,
                                     ETopBottom.BOTTOM,
                                     ETriState.UNDEFINED,
                                     EValidity.VALID,
                                     "Jägalä".getBytes (StandardCharsets.ISO_8859_1) };
    for (final Object aSrcValue : aDefinedObjs)
    {
      final String sValue = TypeConverter.convert (aSrcValue, String.class);
      final Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertTrue (EqualsHelper.equals (aSrcValue, aObj2));
    }

    // Test conversion if no explicit converter available for source class, but
    // for super class
    final Object [] aUpCastObjs = { ELocaleName.ID_LANGUAGE_ALL };
    for (final Object aSrcValue : aUpCastObjs)
    {
      final String sValue = TypeConverter.convert (aSrcValue, String.class);
      final Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue, aObj2);
    }

    // Check if conversion works for special objects not overwriting
    // equals/hashcode
    for (final Object aSrcValue : new Object [] { new AtomicBoolean (true),
                                                  new AtomicInteger (177),
                                                  new AtomicLong (12374893127489L),
                                                  new StringBuilder (),
                                                  new StringBuilder ("Das StringBuilder kein equals implementiert ist doof"),
                                                  new StringBuffer (),
                                                  new StringBuffer ("Das gilt auch für StringBuffer") })
    {
      String sValue = TypeConverter.convert (aSrcValue, String.class);
      Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue.toString (), aObj2.toString ());

      sValue = TypeConverter.convert (TypeConverterProviderRuleBased.getInstance (), aSrcValue, String.class);
      aObj2 = TypeConverter.convert (TypeConverterProviderRuleBased.getInstance (), sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue.toString (), aObj2.toString ());
    }
  }

  @Test
  public void testLocale ()
  {
    // Check if conversion works for all locales
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      final String sLocale = TypeConverter.convert (aLocale, String.class);
      assertNotNull (aLocale.toString (), sLocale);
      final Locale aLocale2 = TypeConverter.convert (sLocale, Locale.class);
      assertTrue (LocaleHelper.equalLocales (aLocale, aLocale2));
    }
  }

  @Test
  public void testArray ()
  {
    // Check if conversion works for char[]
    final char [] aValue1 = "xyz".toCharArray ();
    String sValue = TypeConverter.convert (aValue1, String.class);
    final char [] aValue2 = TypeConverter.convert (sValue, char [].class);
    assertArrayEquals (aValue1, aValue2);

    // Check if conversion works for byte[]
    final byte [] aValue3 = new byte [256];
    for (int i = 0; i < aValue3.length; ++i)
      aValue3[i] = (byte) i;
    sValue = TypeConverter.convert (aValue3, String.class);
    final byte [] aValue4 = TypeConverter.convert (sValue, byte [].class);
    assertArrayEquals (aValue3, aValue4);
  }

  @Test
  public void testBigX ()
  {
    BigInteger aBI = BigInteger.valueOf (1234);
    BigDecimal aBD = TypeConverter.convert (aBI, BigDecimal.class);
    assertEquals (1234, aBD.intValue ());
    assertEquals (aBI, aBD.toBigInteger ());

    aBD = new BigDecimal ("4567.891");
    aBI = TypeConverter.convert (aBD, BigInteger.class);
    assertEquals (4567, aBI.intValue ());
    assertEquals ("4567", aBI.toString ());
  }

  @Test
  public void testConvertToObject ()
  {
    final Object aObj = TypeConverter.convert (this, Object.class);
    assertNotNull (aObj);
    assertSame (this, aObj);
  }

  @Test
  public void testNoConverterButAssignable ()
  {
    // No type converter present, but objects are assignable
    final MockImplementation aImpl = new MockImplementation ();
    assertSame (aImpl, TypeConverter.convert (aImpl, IMockInterface.class));

    final MockSubImplementation aSubImpl = new MockSubImplementation ();
    assertSame (aSubImpl, TypeConverter.convert (aSubImpl, IMockInterface.class));
    assertSame (aSubImpl, TypeConverter.convert (aSubImpl, MockImplementation.class));
  }

  @Test
  public void testNoConverterNoConversion ()
  {
    try
    {
      final MultilingualText m = TypeConverter.convert ("1234", MultilingualText.class);
      assertNotNull (m);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }

  @Test
  public void testBooleanArray ()
  {
    final boolean [] aBooleans = { true, false, true };
    assertFalse (TypeConverter.convert (aBooleans, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBooleans, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBooleans, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBooleans, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBooleans, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aBooleans, String [].class).length);
  }

  @Test
  public void testByteArray ()
  {
    final byte [] aBytes = { 5, 6, 7 };
    assertFalse (TypeConverter.convert (aBytes, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBytes, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBytes, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBytes, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aBytes, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aBytes, String [].class).length);
  }

  @Test
  public void testCharArray ()
  {
    final char [] aChars = { 'a', 'b', 'c' };
    assertFalse (TypeConverter.convert (aChars, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aChars, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aChars, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aChars, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aChars, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aChars, String [].class).length);
  }

  @Test
  public void testDoubleArray ()
  {
    final double [] aDoubles = { 7, 3.14, 47.11 };
    assertFalse (TypeConverter.convert (aDoubles, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aDoubles, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aDoubles, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aDoubles, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aDoubles, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aDoubles, String [].class).length);
  }

  @Test
  public void testFloatArray ()
  {
    final float [] aFloats = { 5, 1.1f, 12234.5f };
    assertFalse (TypeConverter.convert (aFloats, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aFloats, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aFloats, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aFloats, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aFloats, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aFloats, String [].class).length);
  }

  @Test
  public void testIntArray ()
  {
    final int [] aInts = { 6, 8, 110 };
    assertFalse (TypeConverter.convert (aInts, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aInts, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aInts, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aInts, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aInts, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aInts, String [].class).length);
  }

  @Test
  public void testLongArray ()
  {
    final long [] aLongs = { 10, 111, 1212 };
    assertFalse (TypeConverter.convert (aLongs, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aLongs, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aLongs, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aLongs, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aLongs, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aLongs, String [].class).length);
  }

  @Test
  public void testShortArray ()
  {
    final short [] aShorts = { 4, 5, 4 };
    assertFalse (TypeConverter.convert (aShorts, List.class).isEmpty ());
    assertFalse (TypeConverter.convert (aShorts, CommonsArrayList.class).isEmpty ());
    assertFalse (TypeConverter.convert (aShorts, Set.class).isEmpty ());
    assertFalse (TypeConverter.convert (aShorts, CommonsHashSet.class).isEmpty ());
    assertFalse (TypeConverter.convert (aShorts, CommonsLinkedHashSet.class).isEmpty ());
    assertEquals (3, TypeConverter.convert (aShorts, String [].class).length);
  }

  @Test
  public void testPrimitives ()
  {
    // boolean
    assertEquals ("true", TypeConverter.convert (true, String.class));
    assertEquals ("false", TypeConverter.convert (false, String.class));

    // byte
    assertEquals ("5", TypeConverter.convert ((byte) 5, String.class));
    assertEquals ("-6", TypeConverter.convert ((byte) -6, String.class));

    // byte
    assertEquals ("a", TypeConverter.convert ('a', String.class));
    assertEquals ("@", TypeConverter.convert ('@', String.class));

    // double
    assertEquals ("3.1", TypeConverter.convert (3.1, String.class));
    assertEquals ("-6.9", TypeConverter.convert (-6.9, String.class));

    // float
    assertEquals ("3.1", TypeConverter.convert (3.1f, String.class));
    assertEquals ("-6.9", TypeConverter.convert (-6.9f, String.class));

    // int
    assertEquals ("5", TypeConverter.convert (5, String.class));
    assertEquals ("-6", TypeConverter.convert (-6, String.class));

    // long
    assertEquals ("5", TypeConverter.convert (5L, String.class));
    assertEquals ("-6", TypeConverter.convert (-6L, String.class));

    // short
    assertEquals ("5", TypeConverter.convert ((short) 5, String.class));
    assertEquals ("-6", TypeConverter.convert ((short) -6, String.class));
  }

  @Test
  public void testSpecials ()
  {
    // Test 9.2.2 changes
    assertEquals (Byte.valueOf ((byte) 1), TypeConverter.convert ("1", Byte.class));
    assertEquals (Byte.valueOf ((byte) 1), TypeConverter.convert ("00000000000000001", Byte.class));
    assertEquals (Byte.valueOf ((byte) -1), TypeConverter.convert ("-00000000000000001", Byte.class));

    assertEquals (Integer.valueOf (1), TypeConverter.convert ("1", Integer.class));
    assertEquals (Integer.valueOf (1), TypeConverter.convert ("00000000000000001", Integer.class));
    assertEquals (Integer.valueOf (-1), TypeConverter.convert ("-00000000000000001", Integer.class));

    assertEquals (Long.valueOf (1), TypeConverter.convert ("1", Long.class));
    assertEquals (Long.valueOf (1), TypeConverter.convert ("00000000000000001", Long.class));
    assertEquals (Long.valueOf (-1), TypeConverter.convert ("-00000000000000001", Long.class));

    assertEquals (Short.valueOf ((short) 1), TypeConverter.convert ("1", Short.class));
    assertEquals (Short.valueOf ((short) 1), TypeConverter.convert ("00000000000000001", Short.class));
    assertEquals (Short.valueOf ((short) -1), TypeConverter.convert ("-00000000000000001", Short.class));

    for (final Class <?> aDstClass : new Class <?> [] { Byte.class, Integer.class, Long.class, Short.class })
    {
      assertNull (TypeConverter.convert ("a", aDstClass, null));
      assertNull (TypeConverter.convert ("a1", aDstClass, null));
      assertNull (TypeConverter.convert ("1a", aDstClass, null));

      try
      {
        TypeConverter.convert ("a", aDstClass);
        fail ();
      }
      catch (final TypeConverterException ex)
      {
        // Expected
      }
      try
      {
        TypeConverter.convert ("a1", aDstClass);
        fail ();
      }
      catch (final TypeConverterException ex)
      {
        // Expected
      }
      try
      {
        TypeConverter.convert ("1a", aDstClass);
        fail ();
      }
      catch (final TypeConverterException ex)
      {
        // Expected
      }
    }
  }
}
