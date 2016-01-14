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
package com.helger.commons.io.resource.inmemory;

import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.lang.IHasSize;
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
    // Create a copy to avoid outside modifications
    m_aBytes = ArrayHelper.getCopy (ValueEnforcer.notNull (aBytes, "Schematron"));
  }

  @Nonnull
  @Nonempty
  public String getResourceID ()
  {
    return "byte[]";
  }

  @Nonnull
  public InputStream getInputStream ()
  {
    return new NonBlockingByteArrayInputStream (m_aBytes);
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllBytes ()
  {
    return ArrayHelper.getCopy (m_aBytes);
  }

  @Nonnegative
  public int getSize ()
  {
    return m_aBytes.length;
  }

  public boolean isEmpty ()
  {
    return m_aBytes.length == 0;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("byte#", m_aBytes.length).toString ();
  }
}
