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
package com.helger.typeconvert.trait;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.trait.MockGetterTraits.ByIndex;
import com.helger.typeconvert.trait.MockGetterTraits.ByKey;
import com.helger.typeconvert.trait.MockGetterTraits.Direct;

/**
 * Test class for the default methods of {@link IGetterDirectTrait}, {@link IGetterByIndexTrait} and
 * {@link IGetterByKeyTrait}.
 *
 * @author Philip Helger
 */
public final class IGetterTraitsTest
{
  private static final Direct S = new Direct ("abc");
  private static final Direct N = new Direct ("123");
  private static final Direct NULL = new Direct (null);

  @Test
  public void testDirectPresenceAndClass ()
  {
    assertEquals ("abc", S.getValue ());
    assertTrue (S.hasValue ());
    assertFalse (S.hasNoValue ());
    assertSame (String.class, S.getValueClass ());

    assertFalse (NULL.hasValue ());
    assertTrue (NULL.hasNoValue ());
    assertNull (NULL.getValueClass ());
  }

  @Test
  public void testDirectCastedValue ()
  {
    assertEquals ("abc", S.<String> getCastedValue ());
    assertEquals ("abc", S.getCastedValue ("def"));
    assertEquals ("abc", S.getCastedValue (String.class));
    assertEquals ("abc", S.getCastedValue ("def", String.class));

    assertNull (NULL.<String> getCastedValue ());
    assertEquals ("def", NULL.getCastedValue ("def"));
    assertNull (NULL.getCastedValue (String.class));
    assertEquals ("def", NULL.getCastedValue ("def", String.class));
  }

  @Test
  public void testDirectConvertedValue ()
  {
    assertEquals (Integer.valueOf (123), N.getConvertedValue (Integer.class));
    assertEquals (Integer.valueOf (123), N.getConvertedValue (Integer.valueOf (0), Integer.class));

    assertNull (NULL.getConvertedValue (Integer.class));
    assertEquals (Integer.valueOf (7), NULL.getConvertedValue (Integer.valueOf (7), Integer.class));
  }

  @Test
  public void testDirectPrimitiveGetters ()
  {
    assertTrue (new Direct ("true").getAsBoolean ());
    assertTrue (NULL.getAsBoolean (true));
    assertEquals (123, N.getAsByte ());
    assertEquals (2, NULL.getAsByte ((byte) 2));
    assertEquals ('a', new Direct ("a").getAsChar ());
    assertEquals ('b', NULL.getAsChar ('b'));
    assertEquals (123, N.getAsDouble (), 0.0001);
    assertEquals (2.5, NULL.getAsDouble (2.5), 0.0001);
    assertEquals (123, N.getAsFloat (), 0.0001f);
    assertEquals (2.5f, NULL.getAsFloat (2.5f), 0.0001f);
    assertEquals (123, N.getAsInt ());
    assertEquals (4, NULL.getAsInt (4));
    assertEquals (123, N.getAsLong ());
    assertEquals (6, NULL.getAsLong (6L));
    assertEquals (123, N.getAsShort ());
    assertEquals (8, NULL.getAsShort ((short) 8));
  }

  @Test
  public void testDirectObjectGetters ()
  {
    assertEquals (Boolean.TRUE, new Direct ("true").getAsBooleanObj ());
    assertEquals (Byte.valueOf ((byte) 123), N.getAsByteObj ());
    assertEquals (Character.valueOf ('a'), new Direct ("a").getAsCharObj ());
    assertEquals (Double.valueOf (123), N.getAsDoubleObj ());
    assertEquals (Float.valueOf (123), N.getAsFloatObj ());
    assertEquals (Integer.valueOf (123), N.getAsIntObj ());
    assertEquals (Long.valueOf (123), N.getAsLongObj ());
    assertEquals (Short.valueOf ((short) 123), N.getAsShortObj ());

    assertEquals (new BigDecimal ("123"), N.getAsBigDecimal ());
    assertEquals (new BigDecimal ("1"), NULL.getAsBigDecimal (new BigDecimal ("1")));
    assertEquals (BigInteger.valueOf (123), N.getAsBigInteger ());
    assertEquals (BigInteger.ONE, NULL.getAsBigInteger (BigInteger.ONE));

    assertEquals ("abc", S.getAsString ());
    assertEquals ("def", NULL.getAsString ("def"));
    assertArrayEquals ("abc".toCharArray (), S.getAsCharArray ());
    assertArrayEquals ("x".toCharArray (), NULL.getAsCharArray ("x".toCharArray ()));
    // String to byte[] uses Base64 decoding
    assertArrayEquals ("abc".getBytes (StandardCharsets.UTF_8), new Direct ("YWJj").getAsByteArray ());
  }

  @Test
  public void testDirectNullGetters ()
  {
    // A null value returns the default (null) without invoking a converter
    assertNull (NULL.getAsBooleanObj ());
    assertNull (NULL.getAsByteObj ());
    assertNull (NULL.getAsCharObj ());
    assertNull (NULL.getAsDoubleObj ());
    assertNull (NULL.getAsFloatObj ());
    assertNull (NULL.getAsIntObj ());
    assertNull (NULL.getAsLongObj ());
    assertNull (NULL.getAsShortObj ());
    assertNull (NULL.getAsBigDecimal ());
    assertNull (NULL.getAsBigInteger ());
    assertNull (NULL.getAsString ());
    assertNull (NULL.getAsCharArray ());
    assertNull (NULL.getAsByteArray ());
    assertNull (NULL.getAsLocalDate ());
    assertNull (NULL.getAsLocalTime ());
    assertNull (NULL.getAsLocalDateTime ());
    assertNull (NULL.getAsSqlBlob ());
    assertNull (NULL.getAsSqlClob ());
    assertNull (NULL.getAsSqlDate ());
    assertNull (NULL.getAsSqlNClob ());
    assertNull (NULL.getAsSqlRowId ());
    assertNull (NULL.getAsSqlTime ());
    assertNull (NULL.getAsSqlTimestamp ());

    // Same for the versions with an explicit default
    final LocalDate aDate = LocalDate.of (2020, 1, 1);
    assertEquals (aDate, NULL.getAsLocalDate (aDate));
    final LocalTime aTime = LocalTime.of (12, 0);
    assertEquals (aTime, NULL.getAsLocalTime (aTime));
    final LocalDateTime aDateTime = LocalDateTime.of (aDate, aTime);
    assertEquals (aDateTime, NULL.getAsLocalDateTime (aDateTime));
  }

  @Test
  public void testDirectNullConversionThrows ()
  {
    try
    {
      NULL.getAsBoolean ();
      fail ();
    }
    catch (final TypeConverterException ex)
    {
      // expected
    }
    try
    {
      NULL.getAsInt ();
      fail ();
    }
    catch (final TypeConverterException ex)
    {
      // expected
    }
  }

  @Test
  public void testByIndexPresenceAndCast ()
  {
    final ByIndex a = new ByIndex ("abc", "123", null);

    assertEquals ("abc", a.getValue (0));
    assertNull (a.getValue (-1));
    assertNull (a.getValue (99));
    assertTrue (a.hasValue (0));
    assertFalse (a.hasValue (2));
    assertTrue (a.hasNoValue (2));
    assertFalse (a.hasNoValue (0));
    assertSame (String.class, a.getValueClass (0));
    assertNull (a.getValueClass (2));

    assertEquals ("abc", a.<String> getCastedValue (0));
    assertNull (a.<String> getCastedValue (2));
    assertEquals ("abc", a.getCastedValue (0, "def"));
    assertEquals ("def", a.getCastedValue (2, "def"));
    assertEquals ("abc", a.getCastedValue (0, String.class));
    assertNull (a.getCastedValue (2, String.class));
    assertEquals ("abc", a.getCastedValue (0, "def", String.class));
    assertEquals ("def", a.getCastedValue (2, "def", String.class));

    assertEquals ("abc", a.getSafeCastedValue (0, String.class));
    // Wrong type gives the default instead of an exception
    assertNull (a.getSafeCastedValue (0, Integer.class));
    assertEquals (Integer.valueOf (1), a.getSafeCastedValue (0, Integer.valueOf (1), Integer.class));
    assertNull (a.getSafeCastedValue (2, String.class));
    // Explicitly invoke the "empty" default implementation
    a.onSafeCastError (0, Integer.class, "abc");

    assertEquals (Integer.valueOf (123), a.getConvertedValue (1, Integer.class));
    assertNull (a.getConvertedValue (2, Integer.class));
    assertEquals (Integer.valueOf (123), a.getConvertedValue (1, Integer.valueOf (9), Integer.class));
    assertEquals (Integer.valueOf (9), a.getConvertedValue (2, Integer.valueOf (9), Integer.class));
  }

  @Test
  public void testByIndexGetters ()
  {
    final ByIndex a = new ByIndex ("abc", "123", null, "true", "a", "YWJj");

    assertTrue (a.getAsBoolean (3));
    assertTrue (a.getAsBoolean (2, true));
    assertEquals (123, a.getAsByte (1));
    assertEquals (1, a.getAsByte (2, (byte) 1));
    assertEquals ('a', a.getAsChar (4));
    assertEquals ('x', a.getAsChar (2, 'x'));
    assertEquals (123, a.getAsDouble (1), 0.0001);
    assertEquals (1, a.getAsDouble (2, 1), 0.0001);
    assertEquals (123, a.getAsFloat (1), 0.0001f);
    assertEquals (1, a.getAsFloat (2, 1f), 0.0001f);
    assertEquals (123, a.getAsInt (1));
    assertEquals (4, a.getAsInt (2, 4));
    assertEquals (123, a.getAsLong (1));
    assertEquals (6, a.getAsLong (2, 6L));
    assertEquals (123, a.getAsShort (1));
    assertEquals (8, a.getAsShort (2, (short) 8));

    assertEquals (Boolean.TRUE, a.getAsBooleanObj (3));
    assertEquals (Byte.valueOf ((byte) 123), a.getAsByteObj (1));
    assertEquals (Character.valueOf ('a'), a.getAsCharObj (4));
    assertEquals (Double.valueOf (123), a.getAsDoubleObj (1));
    assertEquals (Float.valueOf (123), a.getAsFloatObj (1));
    assertEquals (Integer.valueOf (123), a.getAsIntObj (1));
    assertEquals (Long.valueOf (123), a.getAsLongObj (1));
    assertEquals (Short.valueOf ((short) 123), a.getAsShortObj (1));

    assertEquals ("abc", a.getAsString (0));
    assertNull (a.getAsString (2));
    assertEquals ("def", a.getAsString (2, "def"));
    assertEquals (new BigDecimal ("123"), a.getAsBigDecimal (1));
    assertEquals (new BigDecimal ("1"), a.getAsBigDecimal (2, new BigDecimal ("1")));
    assertEquals (BigInteger.valueOf (123), a.getAsBigInteger (1));
    assertEquals (BigInteger.ONE, a.getAsBigInteger (2, BigInteger.ONE));
    assertArrayEquals ("abc".toCharArray (), a.getAsCharArray (0));
    assertNull (a.getAsCharArray (2));
    assertArrayEquals ("x".toCharArray (), a.getAsCharArray (2, "x".toCharArray ()));
    // String to byte[] uses Base64 decoding
    assertArrayEquals ("abc".getBytes (StandardCharsets.UTF_8), a.getAsByteArray (5));

    assertNull (a.getAsLocalDate (2));
    assertNull (a.getAsLocalTime (2));
    assertNull (a.getAsLocalDateTime (2));
    final LocalDate aDate = LocalDate.of (2020, 1, 1);
    assertEquals (aDate, a.getAsLocalDate (2, aDate));
    final LocalTime aTime = LocalTime.of (12, 0);
    assertEquals (aTime, a.getAsLocalTime (2, aTime));
    final LocalDateTime aDateTime = LocalDateTime.of (aDate, aTime);
    assertEquals (aDateTime, a.getAsLocalDateTime (2, aDateTime));

    assertNull (a.getAsSqlBlob (2));
    assertNull (a.getAsSqlClob (2));
    assertNull (a.getAsSqlDate (2));
    assertNull (a.getAsSqlNClob (2));
    assertNull (a.getAsSqlRowId (2));
    assertNull (a.getAsSqlTime (2));
    assertNull (a.getAsSqlTimestamp (2));

    assertTrue (a.hasStringValue (0, "abc"));
    assertFalse (a.hasStringValue (0, "ABC"));
    assertFalse (a.hasStringValue (2, "abc"));
    assertTrue (a.hasStringValue (2, "abc", true));
    assertFalse (a.hasStringValue (2, "abc", false));
  }

  @Test
  public void testByIndexStringCollections ()
  {
    final ByIndex a = new ByIndex ("abc", new String [] { "a", "b" }, null, Integer.valueOf (5));

    assertEquals (new CommonsArrayList <> ("abc"), a.getAsStringList (0));
    assertEquals (new CommonsArrayList <> ("a", "b"), a.getAsStringList (1));
    assertNull (a.getAsStringList (2));
    assertNull (a.getAsStringList (3));
    final ICommonsList <String> aDefaultList = new CommonsArrayList <> ("x");
    assertSame (aDefaultList, a.getAsStringList (2, aDefaultList));

    assertEquals (new CommonsLinkedHashSet <> ("abc"), a.getAsStringSet (0));
    assertEquals (new CommonsLinkedHashSet <> ("a", "b"), a.getAsStringSet (1));
    assertNull (a.getAsStringSet (2));
    assertNull (a.getAsStringSet (3));
    final ICommonsOrderedSet <String> aDefaultSet = new CommonsLinkedHashSet <> ("x");
    assertSame (aDefaultSet, a.getAsStringSet (2, aDefaultSet));
  }

  @Test
  public void testByKeyPresenceAndCast ()
  {
    final ByKey a = new ByKey ().put ("s", "abc").put ("n", "123").put ("null", null);

    assertEquals ("abc", a.getValue ("s"));
    assertNull (a.getValue (null));
    assertNull (a.getValue ("does-not-exist"));
    assertTrue (a.containsNonNullValue ("s"));
    assertFalse (a.containsNonNullValue ("null"));
    assertTrue (a.containsNullValue ("null"));
    assertTrue (a.containsNullValue ("does-not-exist"));
    assertFalse (a.containsNullValue ("s"));
    assertSame (String.class, a.getValueClass ("s"));
    assertNull (a.getValueClass ("null"));

    assertEquals ("abc", a.<String> getCastedValue ("s"));
    assertNull (a.<String> getCastedValue ("null"));
    assertEquals ("abc", a.getCastedValue ("s", "def"));
    assertEquals ("def", a.getCastedValue ("null", "def"));
    assertEquals ("abc", a.getCastedValue ("s", String.class));
    assertNull (a.getCastedValue ("null", String.class));
    assertEquals ("abc", a.getCastedValue ("s", "def", String.class));
    assertEquals ("def", a.getCastedValue ("null", "def", String.class));

    assertEquals ("abc", a.getSafeCastedValue ("s", String.class));
    assertNull (a.getSafeCastedValue ("s", Integer.class));
    assertEquals (Integer.valueOf (1), a.getSafeCastedValue ("s", Integer.valueOf (1), Integer.class));
    assertNull (a.getSafeCastedValue ("null", String.class));
    // Explicitly invoke the "empty" default implementation
    a.onSafeCastError ("s", Integer.class, "abc");

    assertEquals (Integer.valueOf (123), a.getConvertedValue ("n", Integer.class));
    assertNull (a.getConvertedValue ("null", Integer.class));
    assertEquals (Integer.valueOf (123), a.getConvertedValue ("n", Integer.valueOf (9), Integer.class));
    assertEquals (Integer.valueOf (9), a.getConvertedValue ("null", Integer.valueOf (9), Integer.class));
  }

  @Test
  public void testByKeyGetters ()
  {
    final ByKey a = new ByKey ().put ("s", "abc")
                                .put ("n", "123")
                                .put ("null", null)
                                .put ("b", "true")
                                .put ("c", "a")
                                .put ("b64", "YWJj");

    assertTrue (a.getAsBoolean ("b"));
    assertTrue (a.getAsBoolean ("null", true));
    assertEquals (123, a.getAsByte ("n"));
    assertEquals (1, a.getAsByte ("null", (byte) 1));
    assertEquals ('a', a.getAsChar ("c"));
    assertEquals ('x', a.getAsChar ("null", 'x'));
    assertEquals (123, a.getAsDouble ("n"), 0.0001);
    assertEquals (1, a.getAsDouble ("null", 1), 0.0001);
    assertEquals (123, a.getAsFloat ("n"), 0.0001f);
    assertEquals (1, a.getAsFloat ("null", 1f), 0.0001f);
    assertEquals (123, a.getAsInt ("n"));
    assertEquals (4, a.getAsInt ("null", 4));
    assertEquals (123, a.getAsLong ("n"));
    assertEquals (6, a.getAsLong ("null", 6L));
    assertEquals (123, a.getAsShort ("n"));
    assertEquals (8, a.getAsShort ("null", (short) 8));

    assertEquals (Boolean.TRUE, a.getAsBooleanObj ("b"));
    assertEquals (Byte.valueOf ((byte) 123), a.getAsByteObj ("n"));
    assertEquals (Character.valueOf ('a'), a.getAsCharObj ("c"));
    assertEquals (Double.valueOf (123), a.getAsDoubleObj ("n"));
    assertEquals (Float.valueOf (123), a.getAsFloatObj ("n"));
    assertEquals (Integer.valueOf (123), a.getAsIntObj ("n"));
    assertEquals (Long.valueOf (123), a.getAsLongObj ("n"));
    assertEquals (Short.valueOf ((short) 123), a.getAsShortObj ("n"));

    assertEquals ("abc", a.getAsString ("s"));
    assertNull (a.getAsString ("null"));
    assertEquals ("def", a.getAsString ("null", "def"));
    assertEquals (new BigDecimal ("123"), a.getAsBigDecimal ("n"));
    assertEquals (new BigDecimal ("1"), a.getAsBigDecimal ("null", new BigDecimal ("1")));
    assertEquals (BigInteger.valueOf (123), a.getAsBigInteger ("n"));
    assertEquals (BigInteger.ONE, a.getAsBigInteger ("null", BigInteger.ONE));
    assertArrayEquals ("abc".toCharArray (), a.getAsCharArray ("s"));
    assertNull (a.getAsCharArray ("null"));
    assertArrayEquals ("x".toCharArray (), a.getAsCharArray ("null", "x".toCharArray ()));
    // String to byte[] uses Base64 decoding
    assertArrayEquals ("abc".getBytes (StandardCharsets.UTF_8), a.getAsByteArray ("b64"));

    assertNull (a.getAsLocalDate ("null"));
    assertNull (a.getAsLocalTime ("null"));
    assertNull (a.getAsLocalDateTime ("null"));
    final LocalDate aDate = LocalDate.of (2020, 1, 1);
    assertEquals (aDate, a.getAsLocalDate ("null", aDate));
    final LocalTime aTime = LocalTime.of (12, 0);
    assertEquals (aTime, a.getAsLocalTime ("null", aTime));
    final LocalDateTime aDateTime = LocalDateTime.of (aDate, aTime);
    assertEquals (aDateTime, a.getAsLocalDateTime ("null", aDateTime));

    assertNull (a.getAsSqlBlob ("null"));
    assertNull (a.getAsSqlClob ("null"));
    assertNull (a.getAsSqlDate ("null"));
    assertNull (a.getAsSqlNClob ("null"));
    assertNull (a.getAsSqlRowId ("null"));
    assertNull (a.getAsSqlTime ("null"));
    assertNull (a.getAsSqlTimestamp ("null"));

    assertTrue (a.hasStringValue ("s", "abc"));
    assertFalse (a.hasStringValue ("s", "ABC"));
    assertFalse (a.hasStringValue ("null", "abc"));
    assertTrue (a.hasStringValue ("null", "abc", true));
    assertFalse (a.hasStringValue ("null", "abc", false));
  }

  @Test
  public void testByKeyStringCollections ()
  {
    final ByKey a = new ByKey ().put ("s", "abc")
                                .put ("array", new String [] { "a", "b" })
                                .put ("null", null)
                                .put ("other", Integer.valueOf (5));

    assertEquals (new CommonsArrayList <> ("abc"), a.getAsStringList ("s"));
    assertEquals (new CommonsArrayList <> ("a", "b"), a.getAsStringList ("array"));
    assertNull (a.getAsStringList ("null"));
    assertNull (a.getAsStringList ("other"));
    final ICommonsList <String> aDefaultList = new CommonsArrayList <> ("x");
    assertSame (aDefaultList, a.getAsStringList ("null", aDefaultList));

    assertEquals (new CommonsLinkedHashSet <> ("abc"), a.getAsStringSet ("s"));
    assertEquals (new CommonsLinkedHashSet <> ("a", "b"), a.getAsStringSet ("array"));
    assertNull (a.getAsStringSet ("null"));
    assertNull (a.getAsStringSet ("other"));
    final ICommonsOrderedSet <String> aDefaultSet = new CommonsLinkedHashSet <> ("x");
    assertSame (aDefaultSet, a.getAsStringSet ("null", aDefaultSet));
  }
}
