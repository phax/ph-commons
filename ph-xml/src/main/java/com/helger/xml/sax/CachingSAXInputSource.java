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
package com.helger.xml.sax;

import java.io.InputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.InputSource;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.io.resource.IReadableResource;

/**
 * {@link org.xml.sax.InputSource} that ensures that the passed {@link InputStream} is closed. This
 * is achieved by copying the content in a {@link NonBlockingByteArrayInputStream}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CachingSAXInputSource extends InputSource
{
  @NonNull
  private static NonBlockingByteArrayInputStream _getCachedInputStream (@NonNull @WillClose final InputStream aIS)
  {
    return new NonBlockingByteArrayInputStream (StreamHelper.getAllBytes (aIS));
  }

  public CachingSAXInputSource (@NonNull final IReadableResource aRes)
  {
    this (aRes.getInputStream (), aRes.getResourceID ());
  }

  public CachingSAXInputSource (@NonNull final IHasInputStream aISP)
  {
    this (aISP.getInputStream (), null);
  }

  public CachingSAXInputSource (@NonNull final IHasInputStream aISP, @Nullable final String sSystemID)
  {
    this (aISP.getInputStream (), sSystemID);
  }

  public CachingSAXInputSource (@NonNull @WillClose final InputStream aIS)
  {
    this (aIS, null);
  }

  public CachingSAXInputSource (@NonNull @WillClose final InputStream aIS, @Nullable final String sSystemID)
  {
    super (_getCachedInputStream (aIS));
    setSystemId (sSystemID);
  }
}
