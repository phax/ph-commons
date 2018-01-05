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
package com.helger.commons.io.resource.inmemory;

import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An in-memory {@link IReadableResource} based on a byte array.
 *
 * @author Philip Helger
 */
public class ReadableResourceByteArray extends AbstractMemoryReadableResource implements IHasSize
{
  private final byte [] m_aBytes;

  public ReadableResourceByteArray (@Nonnull final byte [] aBytes)
  {
    this (null, aBytes);
  }

  public ReadableResourceByteArray (@Nullable final String sResourceID, @Nonnull final byte [] aBytes)
  {
    super (StringHelper.hasText (sResourceID) ? sResourceID : "byte[]");
    // Create a copy to avoid outside modifications
    m_aBytes = ArrayHelper.getCopy (ValueEnforcer.notNull (aBytes, "Bytes"));
  }

  @Nonnull
  public final InputStream getInputStream ()
  {
    return new NonBlockingByteArrayInputStream (m_aBytes);
  }

  public final boolean isReadMultiple ()
  {
    return true;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final byte [] getAllBytes ()
  {
    return ArrayHelper.getCopy (m_aBytes);
  }

  @Nonnegative
  public final int size ()
  {
    return m_aBytes.length;
  }

  public final boolean isEmpty ()
  {
    return m_aBytes.length == 0;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("byte#", m_aBytes.length).getToString ();
  }
}
