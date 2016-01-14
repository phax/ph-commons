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
package com.helger.commons.xml.sax;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.xml.sax.InputSource;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.stream.ByteBufferInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.url.URLHelper;

/**
 * Factory class to create the correct {@link InputSource} objects for different
 * input types.
 *
 * @author Philip Helger
 */
@Immutable
public final class InputSourceFactory
{
  @PresentForCodeCoverage
  private static final InputSourceFactory s_aInstance = new InputSourceFactory ();

  private InputSourceFactory ()
  {}

  @Nullable
  public static InputSource create (@Nonnull final File aFile)
  {
    return create (new FileSystemResource (aFile));
  }

  @Nonnull
  public static InputSource create (@Nonnull final URI aURI)
  {
    return create (URLHelper.getAsURL (aURI));
  }

  @Nonnull
  public static InputSource create (@Nonnull final URL aURL)
  {
    return create (new URLResource (aURL));
  }

  @Nullable
  public static InputSource create (@Nonnull final IHasInputStream aISP)
  {
    if (aISP instanceof IReadableResource)
      return create ((IReadableResource) aISP);
    return create (aISP.getInputStream ());
  }

  @Nullable
  public static InputSource create (@Nonnull final IReadableResource aResource)
  {
    if (aResource instanceof FileSystemResource)
    {
      final File aFile = aResource.getAsFile ();
      if (aFile != null)
      {
        // Potentially use memory mapped files
        return create (FileHelper.getInputStream (aFile));
      }
    }
    return new ReadableResourceSAXInputSource (aResource);
  }

  @Nonnull
  public static InputSource create (@Nonnull final CharSequence aXML)
  {
    return new StringSAXInputSource (aXML);
  }

  @Nonnull
  public static InputSource create (@Nonnull final String sXML)
  {
    return new StringSAXInputSource (sXML);
  }

  @Nonnull
  public static InputSource create (@Nonnull final char [] aXML)
  {
    return new StringSAXInputSource (aXML);
  }

  @Nonnull
  public static InputSource create (@Nonnull final char [] aXML,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    return new StringSAXInputSource (aXML, nOfs, nLen);
  }

  @Nonnull
  public static InputSource create (@Nonnull final byte [] aXML)
  {
    return create (new NonBlockingByteArrayInputStream (aXML));
  }

  @Nonnull
  public static InputSource create (@Nonnull final byte [] aXML,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    return create (new NonBlockingByteArrayInputStream (aXML, nOfs, nLen));
  }

  @Nonnull
  public static InputSource create (@Nonnull final ByteBuffer aXML)
  {
    return create (new ByteBufferInputStream (aXML));
  }

  @Nullable
  public static InputSource create (@Nullable final InputStream aIS)
  {
    return aIS == null ? null : new InputSource (aIS);
  }

  @Nullable
  public static InputSource create (@Nullable final Reader aReader)
  {
    return aReader == null ? null : new InputSource (aReader);
  }
}
