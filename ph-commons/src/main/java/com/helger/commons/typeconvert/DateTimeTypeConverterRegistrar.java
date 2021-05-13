/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.datetime.OffsetDate;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.XMLOffsetDate;
import com.helger.commons.datetime.XMLOffsetDateTime;
import com.helger.commons.datetime.XMLOffsetTime;
import com.helger.commons.string.StringParser;

/**
 * Register the date and time specific type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class DateTimeTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // Source: Calendar
    aRegistry.registerTypeConverter (Calendar.class,
                                     String.class,
                                     aSource -> Long.toString (aSource.getTimeInMillis ()));
    aRegistry.registerTypeConverter (Calendar.class, Long.class, aSource -> Long.valueOf (aSource.getTimeInMillis ()));
    aRegistry.registerTypeConverter (Calendar.class, Date.class, Calendar::getTime);
    aRegistry.registerTypeConverter (Calendar.class, Instant.class, Calendar::toInstant);

    // Source: GregorianCalendar (required!)
    aRegistry.registerTypeConverter (GregorianCalendar.class,
                                     String.class,
                                     aSource -> Long.toString (aSource.getTimeInMillis ()));
    aRegistry.registerTypeConverter (GregorianCalendar.class,
                                     Long.class,
                                     aSource -> Long.valueOf (aSource.getTimeInMillis ()));
    aRegistry.registerTypeConverter (GregorianCalendar.class, Date.class, Calendar::getTime);
    aRegistry.registerTypeConverter (GregorianCalendar.class, Instant.class, Calendar::toInstant);

    // Destination: GregorianCalendar
    aRegistry.registerTypeConverter (String.class, GregorianCalendar.class, aSource -> {
      final GregorianCalendar aCal = new GregorianCalendar (TimeZone.getDefault (),
                                                            Locale.getDefault (Locale.Category.FORMAT));
      aCal.setTimeInMillis (StringParser.parseLong (aSource, 0));
      return aCal;
    });
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         GregorianCalendar.class,
                                                                         aSource -> {
                                                                           final GregorianCalendar aCal = new GregorianCalendar (TimeZone.getDefault (),
                                                                                                                                 Locale.getDefault (Locale.Category.FORMAT));
                                                                           aCal.setTimeInMillis (aSource.longValue ());
                                                                           return aCal;
                                                                         });
    aRegistry.registerTypeConverter (ZonedDateTime.class, GregorianCalendar.class, GregorianCalendar::from);
    aRegistry.registerTypeConverter (OffsetDateTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (aSource.toZonedDateTime ()));
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (aSource.toZonedDateTime ()));

    aRegistry.registerTypeConverter (OffsetDate.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (XMLOffsetDate.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));

    aRegistry.registerTypeConverter (OffsetTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (XMLOffsetTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));

    aRegistry.registerTypeConverter (LocalDateTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (LocalDate.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (LocalTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));

    aRegistry.registerTypeConverter (YearMonth.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (Year.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));
    aRegistry.registerTypeConverter (Instant.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (PDTFactory.createZonedDateTime (aSource)));

    // Source: Date
    aRegistry.registerTypeConverter (Date.class, Calendar.class, aSource -> {
      final Calendar aCal = Calendar.getInstance (TimeZone.getDefault (), Locale.getDefault (Locale.Category.FORMAT));
      aCal.setTime (aSource);
      return aCal;
    });
    aRegistry.registerTypeConverter (Date.class, String.class, aSource -> Long.toString (aSource.getTime ()));
    aRegistry.registerTypeConverter (Date.class, Long.class, aSource -> Long.valueOf (aSource.getTime ()));
    aRegistry.registerTypeConverter (String.class,
                                     Date.class,
                                     aSource -> new Date (StringParser.parseLong (aSource, 0)));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (Date.class, Date::toInstant);

    // Source: Month
    aRegistry.registerTypeConverter (Month.class, Integer.class, aSource -> Integer.valueOf (aSource.getValue ()));

    // Source: DayOfWeek
    aRegistry.registerTypeConverter (DayOfWeek.class, Integer.class, aSource -> Integer.valueOf (aSource.getValue ()));

    // Destination: Date
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Date.class,
                                                                         aSource -> new Date (aSource.longValue ()));

    // Destination: Instant
    final Function <? super Number, ? extends Instant> fToInstant = aSource -> Instant.ofEpochMilli (aSource.longValue ());
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class, Instant.class, fToInstant);
    aRegistry.registerTypeConverter (String.class, Instant.class, Instant::parse);
    aRegistry.registerTypeConverter (Date.class, Instant.class, Date::toInstant);

    aRegistry.registerTypeConverter (ZonedDateTime.class, Instant.class, ZonedDateTime::toInstant);
    aRegistry.registerTypeConverter (OffsetDateTime.class, Instant.class, OffsetDateTime::toInstant);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, Instant.class, XMLOffsetDateTime::toInstant);

    aRegistry.registerTypeConverter (OffsetDate.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    aRegistry.registerTypeConverter (XMLOffsetDate.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());

    aRegistry.registerTypeConverter (OffsetTime.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    aRegistry.registerTypeConverter (XMLOffsetTime.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());

    aRegistry.registerTypeConverter (LocalDateTime.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    aRegistry.registerTypeConverter (LocalDate.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    aRegistry.registerTypeConverter (LocalTime.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());

    aRegistry.registerTypeConverter (YearMonth.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    aRegistry.registerTypeConverter (Year.class,
                                     Instant.class,
                                     aSource -> PDTFactory.createZonedDateTime (aSource).toInstant ());
    // Not possible for MonthDay!

    // Destination: ZonedDateTime
    aRegistry.registerTypeConverter (GregorianCalendar.class, ZonedDateTime.class, GregorianCalendar::toZonedDateTime);
    aRegistry.registerTypeConverter (String.class, ZonedDateTime.class, ZonedDateTime::parse);

    aRegistry.registerTypeConverter (OffsetDateTime.class, ZonedDateTime.class, OffsetDateTime::toZonedDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, ZonedDateTime.class, XMLOffsetDateTime::toZonedDateTime);

    aRegistry.registerTypeConverter (OffsetDate.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDate.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);

    aRegistry.registerTypeConverter (OffsetTime.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (XMLOffsetTime.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (LocalDate.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (LocalTime.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);

    aRegistry.registerTypeConverter (YearMonth.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (Year.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (Instant.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (Date.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class, ZonedDateTime.class, PDTFactory::createZonedDateTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         ZonedDateTime.class,
                                                                         PDTFactory::createZonedDateTime);

    // Destination: OffsetDateTime
    aRegistry.registerTypeConverter (GregorianCalendar.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (String.class, OffsetDateTime.class, OffsetDateTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, OffsetDateTime.class, ZonedDateTime::toOffsetDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class,
                                     OffsetDateTime.class,
                                     XMLOffsetDateTime::toOffsetDateTime);

    aRegistry.registerTypeConverter (OffsetDate.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDate.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);

    aRegistry.registerTypeConverter (OffsetTime.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (XMLOffsetTime.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (LocalDate.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (LocalTime.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);

    aRegistry.registerTypeConverter (YearMonth.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (Year.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (Instant.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (Date.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class, OffsetDateTime.class, PDTFactory::createOffsetDateTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         OffsetDateTime.class,
                                                                         PDTFactory::createOffsetDateTime);

    // Destination: XMLOffsetDateTime
    aRegistry.registerTypeConverter (GregorianCalendar.class,
                                     XMLOffsetDateTime.class,
                                     PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (String.class, XMLOffsetDateTime.class, XMLOffsetDateTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (OffsetDateTime.class, XMLOffsetDateTime.class, XMLOffsetDateTime::of);

    aRegistry.registerTypeConverter (OffsetDate.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDate.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);

    aRegistry.registerTypeConverter (OffsetTime.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (XMLOffsetTime.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (LocalDate.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (LocalTime.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);

    aRegistry.registerTypeConverter (YearMonth.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (Year.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (Instant.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (Date.class, XMLOffsetDateTime.class, PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class,
                                     XMLOffsetDateTime.class,
                                     PDTFactory::createXMLOffsetDateTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         XMLOffsetDateTime.class,
                                                                         PDTFactory::createXMLOffsetDateTime);

    // Destination: LocalDateTime
    aRegistry.registerTypeConverter (GregorianCalendar.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (String.class, LocalDateTime.class, LocalDateTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, LocalDateTime.class, ZonedDateTime::toLocalDateTime);
    aRegistry.registerTypeConverter (OffsetDateTime.class, LocalDateTime.class, OffsetDateTime::toLocalDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, LocalDateTime.class, XMLOffsetDateTime::toLocalDateTime);

    aRegistry.registerTypeConverter (OffsetDate.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (XMLOffsetDate.class, LocalDateTime.class, PDTFactory::createLocalDateTime);

    aRegistry.registerTypeConverter (OffsetTime.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (XMLOffsetTime.class, LocalDateTime.class, PDTFactory::createLocalDateTime);

    aRegistry.registerTypeConverter (LocalDate.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (LocalTime.class, LocalDateTime.class, PDTFactory::createLocalDateTime);

    aRegistry.registerTypeConverter (YearMonth.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (Year.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (Instant.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (Date.class, LocalDateTime.class, PDTFactory::createLocalDateTime);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class,
                                     LocalDateTime.class,
                                     java.sql.Timestamp::toLocalDateTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         LocalDateTime.class,
                                                                         PDTFactory::createLocalDateTime);

    // Destination: OffsetDate
    aRegistry.registerTypeConverter (GregorianCalendar.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (String.class, OffsetDate.class, OffsetDate::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (OffsetDateTime.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, OffsetDate.class, XMLOffsetDateTime::toOffsetDate);

    aRegistry.registerTypeConverter (XMLOffsetDate.class, OffsetDate.class, XMLOffsetDate::toOffsetDate);

    aRegistry.registerTypeConverter (LocalDateTime.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (LocalDate.class, OffsetDate.class, PDTFactory::createOffsetDate);

    aRegistry.registerTypeConverter (YearMonth.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (Year.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (Instant.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (Date.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class, OffsetDate.class, PDTFactory::createOffsetDate);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         OffsetDate.class,
                                                                         PDTFactory::createOffsetDate);

    // Destination: XMLOffsetDate
    aRegistry.registerTypeConverter (GregorianCalendar.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (String.class, XMLOffsetDate.class, XMLOffsetDate::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (OffsetDateTime.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, XMLOffsetDate.class, XMLOffsetDateTime::toXMLOffsetDate);

    aRegistry.registerTypeConverter (OffsetDate.class, XMLOffsetDate.class, OffsetDate::toXMLOffsetDate);

    aRegistry.registerTypeConverter (LocalDateTime.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (LocalDate.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);

    aRegistry.registerTypeConverter (YearMonth.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (Year.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (Instant.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (Date.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverter (java.sql.Timestamp.class, XMLOffsetDate.class, PDTFactory::createXMLOffsetDate);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         XMLOffsetDate.class,
                                                                         PDTFactory::createXMLOffsetDate);

    // Destination: LocalDate
    aRegistry.registerTypeConverter (GregorianCalendar.class,
                                     LocalDate.class,
                                     aSource -> aSource.toZonedDateTime ().toLocalDate ());
    aRegistry.registerTypeConverter (String.class, LocalDate.class, LocalDate::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, LocalDate.class, ZonedDateTime::toLocalDate);
    aRegistry.registerTypeConverter (OffsetDateTime.class, LocalDate.class, OffsetDateTime::toLocalDate);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, LocalDate.class, XMLOffsetDateTime::toLocalDate);

    aRegistry.registerTypeConverter (OffsetDate.class, LocalDate.class, OffsetDate::toLocalDate);
    aRegistry.registerTypeConverter (XMLOffsetDate.class, LocalDate.class, XMLOffsetDate::toLocalDate);

    aRegistry.registerTypeConverter (LocalDateTime.class, LocalDate.class, LocalDateTime::toLocalDate);

    aRegistry.registerTypeConverter (Instant.class, LocalDate.class, PDTFactory::createLocalDate);
    aRegistry.registerTypeConverter (Date.class, LocalDate.class, PDTFactory::createLocalDate);
    aRegistry.registerTypeConverter (java.sql.Date.class, LocalDate.class, java.sql.Date::toLocalDate);
    aRegistry.registerTypeConverter (YearMonth.class, LocalDate.class, PDTFactory::createLocalDate);
    aRegistry.registerTypeConverter (Year.class, LocalDate.class, PDTFactory::createLocalDate);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         LocalDate.class,
                                                                         aSource -> PDTFactory.createLocalDate (fToInstant.apply (aSource)));

    // Destination: OffsetTime
    aRegistry.registerTypeConverter (GregorianCalendar.class, OffsetTime.class, PDTFactory::createOffsetTime);
    aRegistry.registerTypeConverter (String.class, OffsetTime.class, OffsetTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, OffsetTime.class, PDTFactory::createOffsetTime);
    aRegistry.registerTypeConverter (OffsetDateTime.class, OffsetTime.class, OffsetDateTime::toOffsetTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, OffsetTime.class, XMLOffsetDateTime::toOffsetTime);

    aRegistry.registerTypeConverter (XMLOffsetTime.class, OffsetTime.class, XMLOffsetTime::toOffsetTime);
    aRegistry.registerTypeConverter (LocalTime.class, OffsetTime.class, PDTFactory::createOffsetTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, OffsetTime.class, PDTFactory::createOffsetTime);

    aRegistry.registerTypeConverter (Instant.class, OffsetTime.class, PDTFactory::createOffsetTime);
    aRegistry.registerTypeConverter (Date.class, OffsetTime.class, PDTFactory::createOffsetTime);
    aRegistry.registerTypeConverter (java.sql.Time.class, OffsetTime.class, PDTFactory::createOffsetTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         OffsetTime.class,
                                                                         aSource -> PDTFactory.createOffsetTime (fToInstant.apply (aSource)));

    // Destination: XMLOffsetTime
    aRegistry.registerTypeConverter (GregorianCalendar.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverter (String.class, XMLOffsetTime.class, XMLOffsetTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverter (OffsetDateTime.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, XMLOffsetTime.class, XMLOffsetDateTime::toXMLOffsetTime);

    aRegistry.registerTypeConverter (OffsetTime.class, XMLOffsetTime.class, XMLOffsetTime::of);
    aRegistry.registerTypeConverter (LocalTime.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);

    aRegistry.registerTypeConverter (Instant.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverter (Date.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverter (java.sql.Time.class, XMLOffsetTime.class, PDTFactory::createXMLOffsetTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         XMLOffsetTime.class,
                                                                         aSource -> PDTFactory.createXMLOffsetTime (fToInstant.apply (aSource)));

    // Destination: LocalTime
    aRegistry.registerTypeConverter (GregorianCalendar.class,
                                     LocalTime.class,
                                     aSource -> aSource.toZonedDateTime ().toLocalTime ());
    aRegistry.registerTypeConverter (String.class, LocalTime.class, LocalTime::parse);

    aRegistry.registerTypeConverter (ZonedDateTime.class, LocalTime.class, ZonedDateTime::toLocalTime);
    aRegistry.registerTypeConverter (OffsetDateTime.class, LocalTime.class, OffsetDateTime::toLocalTime);
    aRegistry.registerTypeConverter (XMLOffsetDateTime.class, LocalTime.class, XMLOffsetDateTime::toLocalTime);

    aRegistry.registerTypeConverter (OffsetTime.class, LocalTime.class, OffsetTime::toLocalTime);
    aRegistry.registerTypeConverter (XMLOffsetTime.class, LocalTime.class, XMLOffsetTime::toLocalTime);

    aRegistry.registerTypeConverter (LocalDateTime.class, LocalTime.class, LocalDateTime::toLocalTime);

    aRegistry.registerTypeConverter (Instant.class, LocalTime.class, PDTFactory::createLocalTime);
    aRegistry.registerTypeConverter (Date.class, LocalTime.class, PDTFactory::createLocalTime);
    aRegistry.registerTypeConverter (java.sql.Time.class, LocalTime.class, java.sql.Time::toLocalTime);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         LocalTime.class,
                                                                         aSource -> PDTFactory.createLocalTime (fToInstant.apply (aSource)));

    // Destination: Date
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Date.class,
                                                                  aSource -> Date.from (TypeConverter.convert (aSource,
                                                                                                               Instant.class)));

    // Destination: MonthDay
    aRegistry.registerTypeConverter (String.class, MonthDay.class, MonthDay::parse);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MonthDay.class,
                                                                  aSource -> MonthDay.from (TypeConverter.convert (aSource,
                                                                                                                   LocalDate.class)));
    // Destination: YearMonth
    aRegistry.registerTypeConverter (String.class, YearMonth.class, YearMonth::parse);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (YearMonth.class,
                                                                  aSource -> YearMonth.from (TypeConverter.convert (aSource,
                                                                                                                    LocalDate.class)));
    // Destination: Year
    aRegistry.registerTypeConverter (String.class, Year.class, Year::parse);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Year.class,
                                                                  aSource -> Year.from (TypeConverter.convert (aSource,
                                                                                                               LocalDate.class)));

    // Destination: Duration
    aRegistry.registerTypeConverter (String.class, Duration.class, Duration::parse);

    // Destination: Period
    aRegistry.registerTypeConverter (String.class, Period.class, Period::parse);

    // Destination: Month
    aRegistry.registerTypeConverter (Integer.class, Month.class, aSource -> Month.of (aSource.intValue ()));

    // Destination: DayOfWeek
    aRegistry.registerTypeConverter (Integer.class, DayOfWeek.class, aSource -> DayOfWeek.of (aSource.intValue ()));
  }
}
