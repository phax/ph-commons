/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import com.helger.commons.annotations.DevelopersNote;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.IOutputStreamAndWriterProvider;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.ToStringGenerator;

/**
 * An {@link java.io.OutputStream} provider based on a byte array.
 * 
 * @author Philip Helger
 */
public class ByteArrayOutputStreamProvider implements IOutputStreamAndWriterProvider, Serializable
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
  @Deprecated
  public final OutputStreamWriter getWriter (@Nonnull final String sCharset, @Nonnull final EAppend eAppend)
  {
    return StreamUtils.createWriter (getOutputStream (eAppend), sCharset);
  }

  @Nonnull
  public final OutputStreamWriter getWriter (@Nonnull final Charset aCharset, @Nonnull final EAppend eAppend)
  {
    return StreamUtils.createWriter (getOutputStream (eAppend), aCharset);
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
  @Deprecated
  public String getAsString (@Nonnull final String sCharset)
  {
    return m_aOS.getAsString (sCharset);
  }

  @Nonnull
  public String getAsString (@Nonnull final Charset aCharset)
  {
    return m_aOS.getAsString (aCharset);
  }

  @Override
  @Deprecated
  @DevelopersNote ("Most probably you wanted to call getAsString")
  public String toString ()
  {
    return new ToStringGenerator (this).append ("os", m_aOS).toString ();
  }
}
