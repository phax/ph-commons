/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.streamprovider;

import java.nio.charset.Charset;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.io.EAppend;
import com.helger.base.io.iface.IHasOutputStreamAndWriter;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * An {@link java.io.OutputStream} provider based on a byte array.
 *
 * @author Philip Helger
 */
public class ByteArrayOutputStreamProvider implements IHasOutputStreamAndWriter
{
  private final NonBlockingByteArrayOutputStream m_aOS = new NonBlockingByteArrayOutputStream ();

  public ByteArrayOutputStreamProvider ()
  {}

  @Nonnull
  public final NonBlockingByteArrayOutputStream getOutputStream (@Nonnull final EAppend eAppend)
  {
    if (eAppend.isTruncate ())
      m_aOS.reset ();
    return m_aOS;
  }

  public final boolean isWriteMultiple ()
  {
    return true;
  }

  /**
   * @return All bytes already written
   */
  @Nonnull
  @ReturnsMutableCopy
  public final byte [] getBytes ()
  {
    return m_aOS.toByteArray ();
  }

  @Nonnull
  public final String getAsString (@Nonnull final Charset aCharset)
  {
    return m_aOS.getAsString (aCharset);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("os", m_aOS).getToString ();
  }
}
