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
package com.helger.xml.sax;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Special {@link InputSource} implementation that reads from
 * {@link IReadableResource} or {@link IHasInputStream} objects. The system ID
 * of the stream source is automatically determined from the resource or can be
 * manually passed in.
 *
 * @author Philip Helger
 */
public class ReadableResourceSAXInputSource extends InputSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ReadableResourceSAXInputSource.class);

  private final IHasInputStream m_aISP;

  public ReadableResourceSAXInputSource (@Nonnull final IReadableResource aResource)
  {
    this (aResource, aResource.getResourceID ());
  }

  public ReadableResourceSAXInputSource (@Nonnull final IHasInputStream aISP, @Nullable final String sSystemID)
  {
    m_aISP = ValueEnforcer.notNull (aISP, "InputStreamProvider");
    setSystemId (sSystemID);
  }

  @Nonnull
  public final IHasInputStream getInputStreamProvider ()
  {
    return m_aISP;
  }

  @Override
  public InputStream getByteStream ()
  {
    final InputStream aIS = m_aISP.getInputStream ();
    if (aIS == null)
      LOGGER.warn ("Failed to open input stream for " + m_aISP);
    return aIS;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resource", m_aISP).append ("systemID", getSystemId ()).getToString ();
  }
}
