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

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.IHasInputStreamAndReader;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An {@link java.io.InputStream} provider based on a byte array.
 *
 * @author Philip Helger
 */
public class ByteArrayInputStreamProvider implements IHasInputStreamAndReader, Serializable
{
  private final byte [] m_aData;
  private final int m_nOfs;
  private final int m_nLen;

  @SuppressFBWarnings ({ "EI_EXPOSE_REP2" })
  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData)
  {
    ValueEnforcer.notNull (aData, "Data");
    m_aData = aData;
    m_nOfs = 0;
    m_nLen = aData.length;
  }

  @SuppressFBWarnings ({ "EI_EXPOSE_REP2" })
  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aData, nOfs, nLen);
    m_aData = aData;
    m_nOfs = nOfs;
    m_nLen = nLen;
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getData ()
  {
    return ArrayHelper.getCopy (m_aData);
  }

  @Nonnegative
  public int getOffset ()
  {
    return m_nOfs;
  }

  @Nonnegative
  public int getLength ()
  {
    return m_nLen;
  }

  @Nonnull
  public final InputStream getInputStream ()
  {
    return new NonBlockingByteArrayInputStream (m_aData, m_nOfs, m_nLen);
  }

  @Nonnull
  public final Reader getReader (@Nonnull final Charset aCharset)
  {
    return StreamHelper.createReader (getInputStream (), aCharset);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("byteArray[]", m_aData.length + " bytes")
                                       .append ("ofs", m_nOfs)
                                       .append ("len", m_nLen)
                                       .toString ();
  }
}
