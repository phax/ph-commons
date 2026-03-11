/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.resource.IReadableResource;

/**
 * {@link javax.xml.transform.Source} that ensures that the passed {@link InputStream} is copied.
 * This is achieved by copying the content in a {@link NonBlockingByteArrayInputStream}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CachingTransformStreamSource extends StreamSource
{
  /**
   * Constructor using a readable resource.
   *
   * @param aResource
   *        The readable resource. May not be <code>null</code>.
   */
  public CachingTransformStreamSource (@NonNull final IReadableResource aResource)
  {
    this (aResource.getInputStream (), aResource.getResourceID ());
  }

  /**
   * Constructor using an input stream provider.
   *
   * @param aIIS
   *        The input stream provider. May not be <code>null</code>.
   */
  public CachingTransformStreamSource (@NonNull final IHasInputStream aIIS)
  {
    this (aIIS, null);
  }

  /**
   * Constructor using an input stream provider and a system ID.
   *
   * @param aIIS
   *        The input stream provider. May not be <code>null</code>.
   * @param sSystemID
   *        The system ID. May be <code>null</code>.
   */
  public CachingTransformStreamSource (@NonNull final IHasInputStream aIIS, @Nullable final String sSystemID)
  {
    this (aIIS.getInputStream (), sSystemID);
  }

  /**
   * Constructor using an input stream.
   *
   * @param aIS
   *        The input stream. May be <code>null</code>. Will be closed.
   */
  public CachingTransformStreamSource (@Nullable @WillClose final InputStream aIS)
  {
    this (aIS, null);
  }

  /**
   * Constructor using an input stream and a system ID.
   *
   * @param aIS
   *        The input stream. May be <code>null</code>. Will be closed.
   * @param sSystemID
   *        The system ID. May be <code>null</code>.
   */
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
