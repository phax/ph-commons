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
package com.helger.commons.serialize.convert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.typeconvert.TypeConverterException;
import com.helger.base.typeconvert.TypeConverterException.EReason;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A utility class for converting objects from and to serializable format.<br>
 * All converters are registered in the {@link SerializationConverterRegistry}.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SerializationConverter
{
  @PresentForCodeCoverage
  private static final SerializationConverter INSTANCE = new SerializationConverter ();

  private SerializationConverter ()
  {}

  public static <T> void writeConvertedObject (@Nullable final T aObject, @Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    ValueEnforcer.notNull (aOOS, "ObjectOutputStream");

    // Write boolean flag indicating null or not
    aOOS.writeBoolean (aObject == null);
    if (aObject != null)
    {
      // Lookup converter
      final Class <T> aSrcClass = GenericReflection.uncheckedCast (aObject.getClass ());
      final ISerializationConverter <T> aConverter = SerializationConverterRegistry.getInstance ().getConverter (aSrcClass);
      if (aConverter == null)
        throw new TypeConverterException (aSrcClass, EReason.NO_CONVERTER_FOUND_SINGLE);

      // Perform conversion
      aConverter.writeConvertedObject (aObject, aOOS);
    }
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE readConvertedObject (@Nonnull final ObjectInputStream aOIS,
                                                       @Nonnull final Class <DSTTYPE> aDstClass) throws IOException
  {
    ValueEnforcer.notNull (aOIS, "ObjectInputStream");
    ValueEnforcer.notNull (aDstClass, "DestinationClass");

    // Was the object null?
    final boolean bIsNull = aOIS.readBoolean ();
    if (bIsNull)
    {
      // Don't read anything
      return null;
    }

    // Lookup converter
    final ISerializationConverter <DSTTYPE> aConverter = SerializationConverterRegistry.getInstance ().getConverter (aDstClass);
    if (aConverter == null)
      throw new TypeConverterException (aDstClass, EReason.NO_CONVERTER_FOUND_SINGLE);

    // Convert
    return GenericReflection.uncheckedCast (aConverter.readConvertedObject (aOIS));
  }
}
