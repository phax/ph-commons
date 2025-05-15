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

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.NotThreadSafe;

import org.xml.sax.InputSource;

import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.StreamHelper;

/**
 * {@link org.xml.sax.InputSource} that ensures that the passed
 * {@link InputStream} is closed. This is achieved by copying the content in a
 * {@link NonBlockingByteArrayInputStream}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CachingSAXInputSource extends InputSource
{
  @Nonnull
  private static NonBlockingByteArrayInputStream _getCachedInputStream (@Nonnull @WillClose final InputStream aIS)
  {
    return new NonBlockingByteArrayInputStream (StreamHelper.getAllBytes (aIS));
  }

  public CachingSAXInputSource (@Nonnull final IReadableResource aRes)
  {
    this (aRes.getInputStream (), aRes.getResourceID ());
  }

  public CachingSAXInputSource (@Nonnull final IHasInputStream aISP)
  {
    this (aISP.getInputStream (), null);
  }

  public CachingSAXInputSource (@Nonnull final IHasInputStream aISP, @Nullable final String sSystemID)
  {
    this (aISP.getInputStream (), sSystemID);
  }

  public CachingSAXInputSource (@Nonnull @WillClose final InputStream aIS)
  {
    this (aIS, null);
  }

  public CachingSAXInputSource (@Nonnull @WillClose final InputStream aIS, @Nullable final String sSystemID)
  {
    super (_getCachedInputStream (aIS));
    setSystemId (sSystemID);
  }
}
