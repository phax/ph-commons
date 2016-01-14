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
package com.helger.commons.typeconvert;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
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
    aRegistry.registerTypeConverter (String.class, Calendar.class, aSource -> {
      final Calendar aCal = Calendar.getInstance ();
      aCal.setTimeInMillis (StringParser.parseLong (aSource, 0));
      return aCal;
    });
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class, Calendar.class, aSource -> {
      final Calendar aCal = Calendar.getInstance ();
      aCal.setTimeInMillis (aSource.longValue ());
      return aCal;
    });

    // Destination: GregorianCalendar
    aRegistry.registerTypeConverter (OffsetDateTime.class,
                                     GregorianCalendar.class,
                                     aSource -> GregorianCalendar.from (aSource.toZonedDateTime ()));
    aRegistry.registerTypeConverter (ZonedDateTime.class, GregorianCalendar.class, GregorianCalendar::from);

    // Source: Date
    aRegistry.registerTypeConverter (Date.class, Calendar.class, aSource -> {
      final Calendar aCal = Calendar.getInstance ();
      aCal.setTime (aSource);
      return aCal;
    });
    aRegistry.registerTypeConverter (Date.class, String.class, aSource -> Long.toString (aSource.getTime ()));
    aRegistry.registerTypeConverter (Date.class, Long.class, aSource -> Long.valueOf (aSource.getTime ()));
    aRegistry.registerTypeConverter (String.class,
                                     Date.class,
                                     aSource -> new Date (StringParser.parseLong (aSource, 0)));
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Date.class,
                                                                         aSource -> new Date (aSource.longValue ()));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (Date.class, Date::toInstant);
  }
}
