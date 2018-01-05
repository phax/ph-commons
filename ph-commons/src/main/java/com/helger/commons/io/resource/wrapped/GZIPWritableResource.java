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
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.resource.IWritableResource;

/**
 * A writable resource that is GZIP compressed.
 *
 * @author Philip Helger
 */
public class GZIPWritableResource extends AbstractWrappedWritableResource
{
  public GZIPWritableResource (@Nonnull final IWritableResource aBaseResource)
  {
    super (aBaseResource);
  }

  @Nullable
  public OutputStream getOutputStream (@Nonnull final EAppend eAppend)
  {
    final OutputStream aIS = getWrappedWritableResource ().getOutputStream (eAppend);
    if (aIS == null)
      return null;
    try
    {
      return new GZIPOutputStream (aIS);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to open GZIP OutputStream", ex);
    }
  }

  @Nonnull
  public GZIPWritableResource getWritableCloneForPath (@Nonnull final String sPath)
  {
    return new GZIPWritableResource (getWrappedWritableResource ().getWritableCloneForPath (sPath));
  }
}
