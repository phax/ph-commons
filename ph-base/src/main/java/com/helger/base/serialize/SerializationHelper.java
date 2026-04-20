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
package com.helger.base.serialize;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.reflection.GenericReflection;

/**
 * Global serialization helper method.
 *
 * @author Philip Helger
 * @since 8.5.5
 */
@Immutable
public final class SerializationHelper
{
  private SerializationHelper ()
  {}

  /**
   * Convert the passed Serializable object to a serialized byte array.
   *
   * @param aData
   *        Source object. May not be <code>null</code>.
   * @return A non-<code>null</code> byte array.
   * @throws IllegalStateException
   *         If serialization failed
   */
  public static byte @NonNull [] getSerializedByteArray (@NonNull final Serializable aData)
  {
    ValueEnforcer.notNull (aData, "Data");

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      // Convert to byte array
      try (final ObjectOutputStream aOOS = new ObjectOutputStream (aBAOS))
      {
        aOOS.writeObject (aData);
      }

      // Main sending
      return aBAOS.toByteArray ();
    }
    catch (final NotSerializableException ex)
    {
      throw new IllegalArgumentException ("Not serializable: " + ex.getMessage (), ex);
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Failed to write serializable object " + aData + " of type " + aData.getClass ().getName (), ex);
    }
  }

  /**
   * Convert the passed byte array to an object using deserialization.
   *
   * @param aData
   *        The source serialized byte array. Must contain a single object only. May not be
   *        <code>null</code>.
   * @param aFilter
   *        An optional {@link ObjectInputFilter} to restrict deserialization. May be
   *        <code>null</code>.
   * @return The deserialized object. Never <code>null</code>.
   * @throws IllegalStateException
   *         If deserialization failed
   * @param <T>
   *        The type of the deserialized object
   * @since 12.2.1
   */
  @NonNull
  public static <T> T getDeserializedObject (final byte @NonNull [] aData,
                                             @Nullable final ObjectInputFilter aFilter)
  {
    ValueEnforcer.notNull (aData, "Data");

    // Read new object from byte array
    try (final ObjectInputStream aOIS = new ObjectInputStream (new NonBlockingByteArrayInputStream (aData)))
    {
      if (aFilter != null)
        aOIS.setObjectInputFilter (aFilter);
      return GenericReflection.uncheckedCast (aOIS.readObject ());
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException ("Failed to read serializable object", ex);
    }
  }

  /**
   * Convert the passed byte array to an object using deserialization. This method does not apply
   * any {@link ObjectInputFilter} and should only be used with trusted data.
   *
   * @param aData
   *        The source serialized byte array. Must contain a single object only. May not be
   *        <code>null</code>.
   * @return The deserialized object. Never <code>null</code>.
   * @throws IllegalStateException
   *         If deserialization failed
   * @param <T>
   *        The type of the deserialized object
   * @deprecated Use {@link #getDeserializedObject(byte[], ObjectInputFilter)} with an appropriate
   *             filter instead.
   */
  @Deprecated (since = "12.2.1", forRemoval = false)
  @NonNull
  public static <T> T getDeserializedObject (final byte @NonNull [] aData)
  {
    return getDeserializedObject (aData, null);
  }
}
