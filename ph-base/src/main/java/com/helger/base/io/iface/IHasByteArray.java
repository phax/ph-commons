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
package com.helger.base.io.iface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.array.ArrayHelper;
import com.helger.base.iface.IHasSize;
import com.helger.base.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.string.StringHex;

import jakarta.annotation.Nonnull;

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
   * @return <code>true</code> if the contained byte array was copied in the constructor or not.
   */
  boolean isCopy ();

  /**
   * @return A copy of all bytes contained, from {@link #getOffset()} for {@link #size()} bytes.
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default byte [] getAllBytes ()
  {
    return ArrayHelper.getCopy (bytes (), getOffset (), size ());
  }

  /**
   * @return A reference to the contained byte array. Gives write access to the payload! Don't
   *         forget to apply {@link #getOffset()} and {@link #size()}. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  byte [] bytes ();

  /**
   * @return <code>true</code> if this object deals with a partial array that either has an offset
   *         or which is not completely used. Based on this method, some performance optimizations
   *         might be used to avoid copying data.
   * @since 11.1.5
   */
  default boolean isPartialArray ()
  {
    return hasOffset () || bytes ().length != size ();
  }

  /**
   * @return The offset into the byte array to start reading. This is always 0 when copied. Must be
   *         ge; 0.
   * @see #bytes()
   * @see #size()
   */
  @Nonnegative
  int getOffset ();

  /**
   * @return <code>true</code> if an offset is present, <code>false</code> if not.
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
    aOS.write (bytes (), getOffset (), size ());
  }

  /**
   * Check if the passed byte array starts with the bytes of this object.
   *
   * @param aCmpBytes
   *        The bytes to compare to. May not be <code>null</code>.
   * @return <code>true</code> if the passed bytes start with the bytes in this object.
   */
  default boolean startsWith (@Nonnull final byte [] aCmpBytes)
  {
    return ArrayHelper.startsWith (bytes (), getOffset (), size (), aCmpBytes, 0, aCmpBytes.length);
  }

  /**
   * @return The hex encoded version of this string. Never <code>null</code> but maybe empty, if the
   *         underlying array length is empty.
   */
  @Nonnull
  default String getHexEncoded ()
  {
    return StringHex.getHexEncoded (bytes (), getOffset (), size ());
  }

  /**
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return The byte array converted to a String, honoring {@link #getOffset()} and
   *         {@link #size()}.
   * @since 10.1.3
   */
  @Nonnull
  default String getBytesAsString (@Nonnull final Charset aCharset)
  {
    return new String (bytes (), getOffset (), size (), aCharset);
  }
}
