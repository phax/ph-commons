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
package com.helger.json.valueserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.mutable.MutableBigDecimal;
import com.helger.commons.mutable.MutableBigInteger;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.mutable.MutableByte;
import com.helger.commons.mutable.MutableChar;
import com.helger.commons.mutable.MutableDouble;
import com.helger.commons.mutable.MutableFloat;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.mutable.MutableShort;

/**
 * Implementation of {@link IJsonValueSerializerRegistrarSPI} for basic types
 * like Boolean, Byte, Integer, AtomicInteger etc.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class DefaultJsonValueSerializerRegistrarSPI implements IJsonValueSerializerRegistrarSPI
{
  public void registerJsonValueSerializer (@Nonnull final IJsonValueSerializerRegistry aRegistry)
  {
    aRegistry.registerJsonValueSerializer (AtomicBoolean.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (AtomicInteger.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (AtomicLong.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Boolean.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Byte.class, JsonValueSerializerToString.getInstance ());
    // For Character the default "escaped" version is used
    aRegistry.registerJsonValueSerializer (Double.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Float.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Integer.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Long.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (Short.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (BigDecimal.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (BigInteger.class, JsonValueSerializerToString.getInstance ());
    aRegistry.registerJsonValueSerializer (String.class, JsonValueSerializerEscaped.getInstance ());

    // Special versions for Mutable objects
    aRegistry.registerJsonValueSerializer (MutableBigDecimal.class,
                                           JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableBigInteger.class,
                                           JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableBoolean.class,
                                           JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableByte.class, JsonValueSerializerTypeConverterToString.getInstance ());
    // Handle as String:
    aRegistry.registerJsonValueSerializer (MutableChar.class,
                                           JsonValueSerializerTypeConverterToStringEscaped.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableDouble.class,
                                           JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableFloat.class, JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableInt.class, JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableLong.class, JsonValueSerializerTypeConverterToString.getInstance ());
    aRegistry.registerJsonValueSerializer (MutableShort.class, JsonValueSerializerTypeConverterToString.getInstance ());
  }
}
