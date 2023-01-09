/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An in-memory {@link IReadableResource} based on an {@link InputStream}.
 *
 * @author Philip Helger
 */
public class ReadableResourceInputStream extends AbstractMemoryReadableResource
{
  private final InputStream m_aIS;

  /**
   * Constructor to use.
   *
   * @param sResourceID
   *        The unique resource ID, used as the caching key.
   * @param aIS
   *        The InputStream to read from. May not be <code>null</code>. This
   *        Stream is NOT closed by this class.
   */
  public ReadableResourceInputStream (@Nullable final String sResourceID, @Nonnull @WillNotClose final InputStream aIS)
  {
    // Ensure a unique resource ID
    super (StringHelper.hasText (sResourceID) ? sResourceID : "input-stream");
    m_aIS = ValueEnforcer.notNull (aIS, "InputStream");
  }

  @Nonnull
  public final InputStream getInputStream ()
  {
    return m_aIS;
  }

  public final boolean isReadMultiple ()
  {
    return false;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("InputStream", m_aIS).getToString ();
  }
}
