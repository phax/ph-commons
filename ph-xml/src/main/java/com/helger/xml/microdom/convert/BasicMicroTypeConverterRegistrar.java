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
package com.helger.xml.microdom.convert;

import java.awt.Color;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.IsSPIImplementation;
import com.helger.commons.datetime.OffsetDate;
import com.helger.commons.datetime.XMLOffsetDate;
import com.helger.commons.datetime.XMLOffsetDateTime;
import com.helger.commons.datetime.XMLOffsetTime;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.state.EEOI;
import com.helger.commons.state.EEnabled;
import com.helger.commons.state.EHandled;
import com.helger.commons.state.EInterrupt;
import com.helger.commons.state.ELeftRight;
import com.helger.commons.state.EMandatory;
import com.helger.commons.state.ESuccess;
import com.helger.commons.state.ETopBottom;
import com.helger.commons.state.ETriState;
import com.helger.commons.state.EValidity;

/**
 * Implementation of {@link IMicroTypeConverterRegistrarSPI} for basic types
 * like Boolean, Byte, Integer, AtomicInteger etc.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class BasicMicroTypeConverterRegistrar implements IMicroTypeConverterRegistrarSPI
{
  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry)
  {
    // String converter
    aRegistry.registerMicroElementTypeConverter (String.class, StringMicroTypeConverter.getInstance ());

    // Other base type based on the String converter
    aRegistry.registerMicroElementTypeConverter (AtomicBoolean.class,
                                                 new StringBasedMicroTypeConverter <> (AtomicBoolean.class));
    aRegistry.registerMicroElementTypeConverter (AtomicInteger.class,
                                                 new StringBasedMicroTypeConverter <> (AtomicInteger.class));
    aRegistry.registerMicroElementTypeConverter (AtomicLong.class,
                                                 new StringBasedMicroTypeConverter <> (AtomicLong.class));
    aRegistry.registerMicroElementTypeConverter (Boolean.class, new StringBasedMicroTypeConverter <> (Boolean.class));
    aRegistry.registerMicroElementTypeConverter (Byte.class, new StringBasedMicroTypeConverter <> (Byte.class));
    aRegistry.registerMicroElementTypeConverter (Character.class,
                                                 new StringBasedMicroTypeConverter <> (Character.class));
    aRegistry.registerMicroElementTypeConverter (Double.class, new StringBasedMicroTypeConverter <> (Double.class));
    aRegistry.registerMicroElementTypeConverter (Float.class, new StringBasedMicroTypeConverter <> (Float.class));
    aRegistry.registerMicroElementTypeConverter (Integer.class, new StringBasedMicroTypeConverter <> (Integer.class));
    aRegistry.registerMicroElementTypeConverter (Long.class, new StringBasedMicroTypeConverter <> (Long.class));
    aRegistry.registerMicroElementTypeConverter (Short.class, new StringBasedMicroTypeConverter <> (Short.class));
    aRegistry.registerMicroElementTypeConverter (BigDecimal.class,
                                                 new StringBasedMicroTypeConverter <> (BigDecimal.class));
    aRegistry.registerMicroElementTypeConverter (BigInteger.class,
                                                 new StringBasedMicroTypeConverter <> (BigInteger.class));
    aRegistry.registerMicroElementTypeConverter (StringBuffer.class,
                                                 new StringBasedMicroTypeConverter <> (StringBuffer.class));
    aRegistry.registerMicroElementTypeConverter (StringBuilder.class,
                                                 new StringBasedMicroTypeConverter <> (StringBuilder.class));
    aRegistry.registerMicroElementTypeConverter (byte [].class, new StringBasedMicroTypeConverter <> (byte [].class));

    // Date Time stuff
    aRegistry.registerMicroElementTypeConverter (ZonedDateTime.class,
                                                 new StringBasedMicroTypeConverter <> (ZonedDateTime.class));
    aRegistry.registerMicroElementTypeConverter (OffsetDate.class,
                                                 new StringBasedMicroTypeConverter <> (OffsetDate.class));
    aRegistry.registerMicroElementTypeConverter (OffsetDateTime.class,
                                                 new StringBasedMicroTypeConverter <> (OffsetDateTime.class));
    aRegistry.registerMicroElementTypeConverter (OffsetTime.class,
                                                 new StringBasedMicroTypeConverter <> (OffsetTime.class));
    aRegistry.registerMicroElementTypeConverter (XMLOffsetDate.class,
                                                 new StringBasedMicroTypeConverter <> (XMLOffsetDate.class));
    aRegistry.registerMicroElementTypeConverter (XMLOffsetDateTime.class,
                                                 new StringBasedMicroTypeConverter <> (XMLOffsetDateTime.class));
    aRegistry.registerMicroElementTypeConverter (XMLOffsetTime.class,
                                                 new StringBasedMicroTypeConverter <> (XMLOffsetTime.class));
    aRegistry.registerMicroElementTypeConverter (LocalDate.class,
                                                 new StringBasedMicroTypeConverter <> (LocalDate.class));
    aRegistry.registerMicroElementTypeConverter (LocalDateTime.class,
                                                 new StringBasedMicroTypeConverter <> (LocalDateTime.class));
    aRegistry.registerMicroElementTypeConverter (LocalTime.class,
                                                 new StringBasedMicroTypeConverter <> (LocalTime.class));
    aRegistry.registerMicroElementTypeConverter (Duration.class, new StringBasedMicroTypeConverter <> (Duration.class));
    aRegistry.registerMicroElementTypeConverter (Period.class, new StringBasedMicroTypeConverter <> (Period.class));
    aRegistry.registerMicroElementTypeConverter (Date.class, new StringBasedMicroTypeConverter <> (Date.class));
    aRegistry.registerMicroElementTypeConverter (GregorianCalendar.class,
                                                 new StringBasedMicroTypeConverter <> (GregorianCalendar.class));

    // State enums
    aRegistry.registerMicroElementTypeConverter (EChange.class, new StringBasedMicroTypeConverter <> (EChange.class));
    aRegistry.registerMicroElementTypeConverter (EContinue.class,
                                                 new StringBasedMicroTypeConverter <> (EContinue.class));
    aRegistry.registerMicroElementTypeConverter (EEnabled.class, new StringBasedMicroTypeConverter <> (EEnabled.class));
    aRegistry.registerMicroElementTypeConverter (EEOI.class, new StringBasedMicroTypeConverter <> (EEOI.class));
    aRegistry.registerMicroElementTypeConverter (EHandled.class, new StringBasedMicroTypeConverter <> (EHandled.class));
    aRegistry.registerMicroElementTypeConverter (EInterrupt.class,
                                                 new StringBasedMicroTypeConverter <> (EInterrupt.class));
    aRegistry.registerMicroElementTypeConverter (ELeftRight.class,
                                                 new StringBasedMicroTypeConverter <> (ELeftRight.class));
    aRegistry.registerMicroElementTypeConverter (EMandatory.class,
                                                 new StringBasedMicroTypeConverter <> (EMandatory.class));
    aRegistry.registerMicroElementTypeConverter (ESuccess.class, new StringBasedMicroTypeConverter <> (ESuccess.class));
    aRegistry.registerMicroElementTypeConverter (ETopBottom.class,
                                                 new StringBasedMicroTypeConverter <> (ETopBottom.class));
    aRegistry.registerMicroElementTypeConverter (ETriState.class,
                                                 new StringBasedMicroTypeConverter <> (ETriState.class));
    aRegistry.registerMicroElementTypeConverter (EValidity.class,
                                                 new StringBasedMicroTypeConverter <> (EValidity.class));

    // IO stuff
    aRegistry.registerMicroElementTypeConverter (File.class, new StringBasedMicroTypeConverter <> (File.class));
    aRegistry.registerMicroElementTypeConverter (Path.class, new StringBasedMicroTypeConverter <> (Path.class));
    aRegistry.registerMicroElementTypeConverter (URI.class, new StringBasedMicroTypeConverter <> (URI.class));
    aRegistry.registerMicroElementTypeConverter (URL.class, new StringBasedMicroTypeConverter <> (URL.class));

    // Special stuff
    aRegistry.registerMicroElementTypeConverter (Color.class, new ColorMicroTypeConverter ());
  }
}
