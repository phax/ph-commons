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
package com.helger.commons.io.streamprovider;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.IHasOutputStreamAndWriter;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An {@link java.io.OutputStream} provider based on a byte array.
 *
 * @author Philip Helger
 */
public class ByteArrayOutputStreamProvider implements IHasOutputStreamAndWriter, Serializable
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

  @Nonnull
  public final OutputStreamWriter getWriter (@Nonnull final Charset aCharset, @Nonnull final EAppend eAppend)
  {
    return StreamHelper.createWriter (getOutputStream (eAppend), aCharset);
  }

  /**
   * @return All bytes already written
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] getBytes ()
  {
    return m_aOS.toByteArray ();
  }

  @Nonnull
  public String getAsString (@Nonnull final Charset aCharset)
  {
    return m_aOS.getAsString (aCharset);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("os", m_aOS).toString ();
  }
}
