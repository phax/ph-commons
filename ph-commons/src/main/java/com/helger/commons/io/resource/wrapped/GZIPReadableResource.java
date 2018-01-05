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
package com.helger.commons.io.resource.wrapped;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.io.resource.IReadableResource;

/**
 * A readable resource that is GZIP compressed.
 *
 * @author Philip Helger
 */
public class GZIPReadableResource extends AbstractWrappedReadableResource
{
  public GZIPReadableResource (@Nonnull final IReadableResource aBaseResource)
  {
    super (aBaseResource);
  }

  @Nullable
  public InputStream getInputStream ()
  {
    final InputStream aIS = getWrappedReadableResource ().getInputStream ();
    if (aIS == null)
      return null;
    try
    {
      return new GZIPInputStream (aIS);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to open GZIP InputStream", ex);
    }
  }

  @Nonnull
  public GZIPReadableResource getReadableCloneForPath (@Nonnull final String sPath)
  {
    return new GZIPReadableResource (getWrappedReadableResource ().getReadableCloneForPath (sPath));
  }
}
