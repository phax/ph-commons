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
package com.helger.commons.xml.transform;

import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.stream.StreamResult;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.IHasOutputStream;
import com.helger.commons.io.resource.IWritableResource;
import com.helger.commons.string.ToStringGenerator;

/**
 * Special {@link StreamResult} implementation that writes to
 * {@link IWritableResource} or {@link IHasOutputStream} objects. The system ID
 * of the stream source is automatically determined from the resource or can be
 * manually passed in.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ResourceStreamResult extends StreamResult
{
  private final IHasOutputStream m_aOSP;

  public ResourceStreamResult (@Nonnull final IWritableResource aResource)
  {
    this (aResource, aResource.getResourceID ());
  }

  public ResourceStreamResult (@Nonnull final IHasOutputStream aOSP, @Nullable final String sSystemID)
  {
    m_aOSP = ValueEnforcer.notNull (aOSP, "OutputStreamProvider");
    setSystemId (sSystemID);
  }

  @Override
  public OutputStream getOutputStream ()
  {
    final OutputStream aOS = m_aOSP.getOutputStream (EAppend.TRUNCATE);
    if (aOS == null)
      throw new IllegalStateException ("Failed to open output stream for " + m_aOSP);
    return aOS;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("OSP", m_aOSP).append ("systemID", getSystemId ()).toString ();
  }
}
