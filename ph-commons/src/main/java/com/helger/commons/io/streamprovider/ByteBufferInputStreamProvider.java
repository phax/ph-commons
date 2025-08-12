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
package com.helger.commons.io.streamprovider;

import java.nio.ByteBuffer;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStreamAndReader;
import com.helger.commons.io.stream.ByteBufferInputStream;
import com.helger.commons.string.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * An {@link java.io.InputStream} provider based on a
 * {@link java.nio.ByteBuffer}.
 *
 * @author Philip Helger
 */
public class ByteBufferInputStreamProvider implements IHasInputStreamAndReader
{
  private final ByteBuffer m_aBuffer;

  public ByteBufferInputStreamProvider (@Nonnull final ByteBuffer aBuffer)
  {
    m_aBuffer = ValueEnforcer.notNull (aBuffer, "Buffer");
  }

  @Nonnull
  @ReturnsMutableObject
  public final ByteBuffer getByteBuffer ()
  {
    return m_aBuffer;
  }

  @Nonnull
  public final ByteBufferInputStream getInputStream ()
  {
    return new ByteBufferInputStream (m_aBuffer);
  }

  public final boolean isReadMultiple ()
  {
    return true;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("buffer", m_aBuffer).getToString ();
  }
}
