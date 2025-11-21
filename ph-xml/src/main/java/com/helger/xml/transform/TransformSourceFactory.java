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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.ByteBufferInputStream;
import com.helger.base.url.URLHelper;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.URLResource;

/**
 * Factory class to create the correct {@link javax.xml.transform.Source} objects for different
 * input types.
 *
 * @author Philip Helger
 */
@Immutable
public final class TransformSourceFactory
{
  @PresentForCodeCoverage
  private static final TransformSourceFactory INSTANCE = new TransformSourceFactory ();

  private TransformSourceFactory ()
  {}

  @NonNull
  public static StreamSource create (@NonNull final File aFile)
  {
    return new StreamSource (aFile);
  }

  @NonNull
  public static StreamSource create (@NonNull final Path aPath)
  {
    return new StreamSource (aPath.toFile ());
  }

  @NonNull
  public static StreamSource create (@NonNull final URI aURI)
  {
    return create (URLHelper.getAsURL (aURI));
  }

  @NonNull
  public static StreamSource create (@NonNull final URL aURL)
  {
    return create (new URLResource (aURL));
  }

  @NonNull
  public static StreamSource create (@NonNull final IHasInputStream aISP)
  {
    if (aISP instanceof final IReadableResource aRes)
      return create (aRes);
    return create (aISP.getInputStream ());
  }

  @NonNull
  public static StreamSource create (@NonNull final IReadableResource aResource)
  {
    // Read into memory
    return new CachingTransformStreamSource (aResource);
  }

  @NonNull
  public static StringStreamSource create (@NonNull final CharSequence aXML)
  {
    return new StringStreamSource (aXML);
  }

  @NonNull
  public static StringStreamSource create (@NonNull final String sXML)
  {
    return new StringStreamSource (sXML);
  }

  @NonNull
  public static StringStreamSource create (final char @NonNull [] aXML)
  {
    return new StringStreamSource (aXML);
  }

  @NonNull
  public static StringStreamSource create (final char @NonNull [] aXML,
                                           @Nonnegative final int nOfs,
                                           @Nonnegative final int nLength)
  {
    return new StringStreamSource (aXML, nOfs, nLength);
  }

  @NonNull
  public static StreamSource create (final byte @NonNull [] aXML)
  {
    return create (new NonBlockingByteArrayInputStream (aXML));
  }

  @NonNull
  public static StreamSource create (final byte @NonNull [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLength)
  {
    return create (new NonBlockingByteArrayInputStream (aXML, nOfs, nLength));
  }

  @NonNull
  public static StreamSource create (@NonNull final ByteBuffer aXML)
  {
    return create (new ByteBufferInputStream (aXML));
  }

  @NonNull
  public static StreamSource create (@Nullable final InputStream aIS)
  {
    return new StreamSource (aIS);
  }

  @NonNull
  public static StreamSource create (@Nullable final Reader aReader)
  {
    return new StreamSource (aReader);
  }

  @NonNull
  public static DOMSource create (@Nullable final Node aNode)
  {
    return new DOMSource (aNode);
  }
}
