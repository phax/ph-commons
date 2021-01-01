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
package com.helger.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.string.StringHelper;

/**
 * Base interface for all objects owning a byte array.
 *
 * @author Philip Helger
 * @since 9.1.3
 */
public interface IHasByteArray extends IHasSize, IHasInputStreamAndReader
{
  default boolean isEmpty ()
  {
    return size () == 0;
  }

  @Override
  default boolean isNotEmpty ()
  {
    return size () > 0;
  }

  /**
   * @return <code>true</code> if the contained byte array was copied in the
   *         constructor or not.
   */
  boolean isCopy ();

  /**
   * @return A copy of all bytes contained. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default byte [] getAllBytes ()
  {
    return ArrayHelper.getCopy (bytes ());
  }

  /**
   * @return A reference to the contained byte array. Gives write access to the
   *         payload! Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  byte [] bytes ();

  /**
   * @return The offset into the byte array to start reading. This is always 0
   *         when copied. Must be ge; 0.
   * @see #bytes()
   * @see #size()
   */
  @Nonnegative
  int getOffset ();

  /**
   * @return <code>true</code> if an offset is present, <code>false</code> if
   *         not.
   */
  default boolean hasOffset ()
  {
    return getOffset () > 0;
  }

  @Nonnull
  default InputStream getInputStream ()
  {
    return new NonBlockingByteArrayInputStream (bytes (), getOffset (), size ());
  }

  default boolean isReadMultiple ()
  {
    return true;
  }

  /**
   * Write the relevant part of the byte array onto the provided output stream.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of a write error.
   */
  default void writeTo (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");
    aOS.write (bytes (), getOffset (), size ());
  }

  /**
   * Check if the passed byte array starts with the bytes of this object.
   *
   * @param aCmpBytes
   *        The bytes to compare to. May not be <code>null</code>.
   * @return <code>true</code> if the passed bytes start with the bytes in this
   *         object.
   */
  default boolean startsWith (@Nonnull final byte [] aCmpBytes)
  {
    return ArrayHelper.startsWith (bytes (), aCmpBytes);
  }

  /**
   * @return The hex encoded version of this string. Never <code>null</code> but
   *         maybe empty, if the underlying array length is empty.
   */
  @Nonnull
  default String getHexEncoded ()
  {
    return StringHelper.getHexEncoded (bytes (), getOffset (), size ());
  }
}
