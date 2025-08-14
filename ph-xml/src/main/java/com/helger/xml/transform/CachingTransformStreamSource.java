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
package com.helger.xml.transform;

import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.commons.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * {@link javax.xml.transform.Source} that ensures that the passed {@link InputStream} is copied.
 * This is achieved by copying the content in a {@link NonBlockingByteArrayInputStream}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CachingTransformStreamSource extends StreamSource
{
  public CachingTransformStreamSource (@Nonnull final IReadableResource aResource)
  {
    this (aResource.getInputStream (), aResource.getResourceID ());
  }

  public CachingTransformStreamSource (@Nonnull final IHasInputStream aIIS)
  {
    this (aIIS, null);
  }

  public CachingTransformStreamSource (@Nonnull final IHasInputStream aIIS, @Nullable final String sSystemID)
  {
    this (aIIS.getInputStream (), sSystemID);
  }

  public CachingTransformStreamSource (@Nullable @WillClose final InputStream aIS)
  {
    this (aIS, null);
  }

  public CachingTransformStreamSource (@Nullable @WillClose final InputStream aIS, @Nullable final String sSystemID)
  {
    super (aIS == null ? null : new NonBlockingByteArrayInputStream (StreamHelper.getAllBytes (aIS)), sSystemID);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("SystemID", getSystemId ()).getToString ();
  }
}
