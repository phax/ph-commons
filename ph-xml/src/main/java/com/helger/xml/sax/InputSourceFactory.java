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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.InputSource;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.ByteBufferInputStream;
import com.helger.base.url.URLHelper;
import com.helger.io.file.FileHelper;
import com.helger.io.resource.FileSystemResource;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.URLResource;

/**
 * Factory class to create the correct {@link InputSource} objects for different input types.
 *
 * @author Philip Helger
 */
@Immutable
public final class InputSourceFactory
{
  @PresentForCodeCoverage
  private static final InputSourceFactory INSTANCE = new InputSourceFactory ();

  private InputSourceFactory ()
  {}

  /**
   * Create an {@link InputSource} from a {@link File}.
   *
   * @param aFile
   *        The file to read from. May not be <code>null</code>.
   * @return <code>null</code> if the file does not exist.
   */
  @Nullable
  public static InputSource create (@NonNull final File aFile)
  {
    return create (new FileSystemResource (aFile));
  }

  /**
   * Create an {@link InputSource} from a {@link Path}.
   *
   * @param aPath
   *        The path to read from. May not be <code>null</code>.
   * @return <code>null</code> if the path does not exist.
   */
  @Nullable
  public static InputSource create (@NonNull final Path aPath)
  {
    return create (new FileSystemResource (aPath));
  }

  /**
   * Create an {@link InputSource} from a {@link URI}.
   *
   * @param aURI
   *        The URI to read from. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (@NonNull final URI aURI)
  {
    return create (URLHelper.getAsURL (aURI));
  }

  /**
   * Create an {@link InputSource} from a {@link URL}.
   *
   * @param aURL
   *        The URL to read from. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (@NonNull final URL aURL)
  {
    return create (new URLResource (aURL));
  }

  /**
   * Create an {@link InputSource} from an {@link IHasInputStream}.
   *
   * @param aISP
   *        The input stream provider. May not be <code>null</code>.
   * @return <code>null</code> if the input stream could not be opened.
   */
  @Nullable
  public static InputSource create (@NonNull final IHasInputStream aISP)
  {
    if (aISP instanceof final IReadableResource aRes)
      return create (aRes);
    return create (aISP.getInputStream ());
  }

  /**
   * Create an {@link InputSource} from an {@link IReadableResource}.
   *
   * @param aResource
   *        The readable resource. May not be <code>null</code>.
   * @return <code>null</code> if the resource could not be opened.
   */
  @Nullable
  public static InputSource create (@NonNull final IReadableResource aResource)
  {
    if (aResource instanceof FileSystemResource)
    {
      final File aFile = aResource.getAsFile ();
      if (aFile != null)
      {
        // Use a buffered stream
        final InputSource ret = create (FileHelper.getBufferedInputStream (aFile));
        if (ret != null)
        {
          // Ensure system ID is present - may be helpful for resource
          // resolution
          final URL aURL = aResource.getAsURL ();
          if (aURL != null)
            ret.setSystemId (aURL.toExternalForm ());
        }
        return ret;
      }
    }
    return new ReadableResourceSAXInputSource (aResource);
  }

  /**
   * Create an {@link InputSource} from a {@link CharSequence}.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (@NonNull final CharSequence aXML)
  {
    return new StringSAXInputSource (aXML);
  }

  /**
   * Create an {@link InputSource} from a {@link String}.
   *
   * @param sXML
   *        The XML content. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (@NonNull final String sXML)
  {
    return new StringSAXInputSource (sXML);
  }

  /**
   * Create an {@link InputSource} from a char array.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (final char @NonNull [] aXML)
  {
    return new StringSAXInputSource (aXML);
  }

  /**
   * Create an {@link InputSource} from a char array with offset and length.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the array. Must be &ge; 0.
   * @param nLen
   *        The number of characters. Must be &ge; 0.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (final char @NonNull [] aXML,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    return new StringSAXInputSource (aXML, nOfs, nLen);
  }

  /**
   * Create an {@link InputSource} from a byte array.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (final byte @NonNull [] aXML)
  {
    return create (new NonBlockingByteArrayInputStream (aXML));
  }

  /**
   * Create an {@link InputSource} from a byte array with offset and length.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the array. Must be &ge; 0.
   * @param nLen
   *        The number of bytes. Must be &ge; 0.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (final byte @NonNull [] aXML,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    return create (new NonBlockingByteArrayInputStream (aXML, nOfs, nLen));
  }

  /**
   * Create an {@link InputSource} from a {@link ByteBuffer}.
   *
   * @param aXML
   *        The XML content. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static InputSource create (@NonNull final ByteBuffer aXML)
  {
    return create (new ByteBufferInputStream (aXML));
  }

  /**
   * Create an {@link InputSource} from an {@link InputStream}.
   *
   * @param aIS
   *        The input stream. May be <code>null</code>.
   * @return <code>null</code> if the input stream is <code>null</code>.
   */
  @Nullable
  public static InputSource create (@Nullable final InputStream aIS)
  {
    return aIS == null ? null : new InputSource (aIS);
  }

  /**
   * Create an {@link InputSource} from a {@link Reader}.
   *
   * @param aReader
   *        The reader. May be <code>null</code>.
   * @return <code>null</code> if the reader is <code>null</code>.
   */
  @Nullable
  public static InputSource create (@Nullable final Reader aReader)
  {
    return aReader == null ? null : new InputSource (aReader);
  }
}
