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
package com.helger.commons.typeconvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.mutable.MutableByte;
import com.helger.commons.mutable.MutableDouble;
import com.helger.commons.mutable.MutableFloat;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.mutable.MutableShort;

/**
 * Test class for class {@link DateTimeTypeConverterRegistrar}.
 *
 * @author Philip Helger
 */
public final class DateTimeTypeConverterRegistrarTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DateTimeTypeConverterRegistrarTest.class);
  private static final Object [] NUMBERS = new Object [] { new AtomicInteger (17),
                                                           new AtomicLong (1234567890),
                                                           new BigDecimal ("11238712367812368712368.32123213"),
                                                           new BigInteger ("23127893819732"),
                                                           Byte.valueOf ((byte) 5),
                                                           Double.valueOf (123.234234),
                                                           Float.valueOf (123433.324f),
                                                           Integer.valueOf (567),
                                                           Long.valueOf (213687123617283L),
                                                           Short.valueOf ((short) 12345),
                                                           new MutableByte ((byte) 47),
                                                           new MutableDouble (34432.45465),
                                                           new MutableFloat (3245678.1f),
                                                           new MutableInt (4711),
                                                           new MutableLong (4567890987654l),
                                                           new MutableShort (65532) };

  @Test
  public void testDateTime ()
  {
    assertNotNull (TypeConverter.convert (PDTFactory.createCalendar (), ZonedDateTime.class));
    assertNotNull (TypeConverter.convert (PDTFactory.createGregorianCalendar (), ZonedDateTime.class));
    assertNotNull (TypeConverter.convert (new Date (), ZonedDateTime.class));
    for (final Object aNumber : NUMBERS)
      assertNotNull (TypeConverter.convert (aNumber, ZonedDateTime.class));

    // Test auto conversion to and from string
    final ZonedDateTime aNow = PDTFactory.getCurrentZonedDateTime ();
    final String sNow = TypeConverter.convert (aNow, String.class);
    final ZonedDateTime aNow2 = TypeConverter.convert (sNow, aNow.getClass ());
    assertEquals (aNow, aNow2);
  }

  @Test
  public void testLocalDateTime ()
  {
    assertNotNull (TypeConverter.convert (PDTFactory.createCalendar (), LocalDateTime.class));
    assertNotNull (TypeConverter.convert (PDTFactory.createGregorianCalendar (), LocalDateTime.class));
    assertNotNull (TypeConverter.convert (new Date (), LocalDateTime.class));
    for (final Object aNumber : NUMBERS)
      assertNotNull (TypeConverter.convert (aNumber, LocalDateTime.class));

    // Test auto conversion to and from string
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    final String sNow = TypeConverter.convert (aNow, String.class);
    final LocalDateTime aNow2 = TypeConverter.convert (sNow, aNow.getClass ());
    assertEquals (aNow, aNow2);
  }

  @Test
  public void testLocalDate ()
  {
    assertNotNull (TypeConverter.convert (PDTFactory.createCalendar (), LocalDate.class));
    assertNotNull (TypeConverter.convert (PDTFactory.createGregorianCalendar (), LocalDate.class));
    assertNotNull (TypeConverter.convert (new Date (), LocalDate.class));
    for (final Object aNumber : NUMBERS)
      assertNotNull (TypeConverter.convert (aNumber, LocalDate.class));

    // Test auto conversion to and from string
    final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
    final String sNow = TypeConverter.convert (aNow, String.class);
    final LocalDate aNow2 = TypeConverter.convert (sNow, aNow.getClass ());
    assertEquals (aNow, aNow2);
  }

  @Test
  public void testLocalTime ()
  {
    assertNotNull (TypeConverter.convert (PDTFactory.createCalendar (), LocalTime.class));
    assertNotNull (TypeConverter.convert (PDTFactory.createGregorianCalendar (), LocalTime.class));
    assertNotNull (TypeConverter.convert (new Date (), LocalTime.class));
    for (final Object aNumber : NUMBERS)
      assertNotNull (TypeConverter.convert (aNumber, LocalTime.class));

    // Test auto conversion to and from string
    final LocalTime aNowTime = PDTFactory.getCurrentLocalTime ();
    final String sNow = TypeConverter.convert (aNowTime, String.class);
    final LocalTime aNowTime2 = TypeConverter.convert (sNow, aNowTime.getClass ());
    assertEquals (aNowTime, aNowTime2);

    // Test auto conversion between default types
    for (final Class <?> aDestClass : new Class <?> [] { ZonedDateTime.class, LocalDateTime.class })
    {
      final LocalTime aNow = PDTFactory.getCurrentLocalTime ();
      final Object aDT = TypeConverter.convert (aNow, aDestClass);
      final LocalTime aNow2 = TypeConverter.convert (aDT, aNow.getClass ());
      assertEquals (aNow, aNow2);
    }
    for (final Class <?> aDestClass : new Class <?> [] { ZonedDateTime.class, LocalDateTime.class })
    {
      final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
      final Object aDT = TypeConverter.convert (aNow, aDestClass);
      final LocalDate aNow2 = TypeConverter.convert (aDT, aNow.getClass ());
      assertEquals (aNow, aNow2);
    }
  }

  @Test
  public void testConvertIntoEachOther ()
  {
    final ICommonsOrderedMap <Class <?>, Object> aValues = new CommonsLinkedHashMap <> ();
    aValues.put (Date.class, new Date ());
    aValues.put (Calendar.class, PDTFactory.createCalendar ());
    aValues.put (GregorianCalendar.class, PDTFactory.createGregorianCalendar ());
    aValues.put (ZonedDateTime.class, PDTFactory.getCurrentZonedDateTime ());
    aValues.put (OffsetDateTime.class, PDTFactory.getCurrentOffsetDateTime ());
    aValues.put (LocalDateTime.class, PDTFactory.getCurrentLocalDateTime ());
    aValues.put (LocalDate.class, PDTFactory.getCurrentLocalDate ());
    aValues.put (LocalTime.class, PDTFactory.getCurrentLocalTime ());
    aValues.put (YearMonth.class, PDTFactory.getCurrentYearMonth ());
    aValues.put (Year.class, PDTFactory.getCurrentYearObj ());
    aValues.put (Instant.class, PDTFactory.getCurrentInstant ());

    for (final Map.Entry <Class <?>, Object> aSrc : aValues.entrySet ())
    {
      // Convert to String and back
      final String s = TypeConverter.convert (aSrc.getValue (), String.class);
      assertNotNull (s);
      final Object aSrcValue2 = TypeConverter.convert (s, aSrc.getKey ());
      assertNotNull (aSrcValue2);
      assertEquals ("Difference after reading from: " + s, aSrc.getValue (), aSrcValue2);

      // COnvert to any other type
      for (final Class <?> aDst : aValues.keySet ())
        if (aSrc.getKey () != aDst)
        {
          final boolean bIsTime = aSrc.getKey () == LocalTime.class || aDst == LocalTime.class;
          if (bIsTime &&
              (aSrc.getKey () == LocalDate.class ||
               aDst == LocalDate.class ||
               aSrc.getKey () == YearMonth.class ||
               aDst == YearMonth.class ||
               aSrc.getKey () == Year.class ||
               aDst == Year.class))
          {
            // Not convertible
          }
          else
          {
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Converting from " + aSrc.getKey ().getName () + " to " + aDst.getName ());
            final Object aDstValue = TypeConverter.convert (aSrc.getValue (), aDst);
            assertNotNull (aDstValue);
          }
        }
    }
  }
}
