/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.microdom.serialize;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import org.xml.sax.InputSource;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.io.IInputStreamProvider;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.xml.sax.InputSourceFactory;
import com.helger.commons.xml.serialize.ISAXReaderSettings;
import com.helger.commons.xml.serialize.SAXReader;
import com.helger.commons.xml.serialize.SAXReaderSettings;

/**
 * Utility class to read an XML stream into an {@link IMicroDocument}.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroReader
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final MicroReader s_aInstance = new MicroReader ();

  private MicroReader ()
  {}

  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputSource aInputSource)
  {
    return readMicroXML (aInputSource, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed input source as MicroXML.
   *
   * @param aInputSource
   *        The input source to use. May be <code>null</code> in which case
   *        <code>null</code> is directly returned.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will
   *        be used.
   * @return <code>null</code> if either the input source is <code>null</code>
   *         or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputSource aInputSource,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aInputSource == null)
      return null;

    final MicroSAXHandler aMicroHandler = new MicroSAXHandler (false, aSettings == null ? null
                                                                                       : aSettings.getEntityResolver ());

    // Copy and modify settings
    final SAXReaderSettings aRealSettings = SAXReaderSettings.createCloneOnDemand (aSettings);
    aRealSettings.setEntityResolver (aMicroHandler)
                 .setDTDHandler (aMicroHandler)
                 .setContentHandler (aMicroHandler)
                 .setLexicalHandler (aMicroHandler);
    if (aRealSettings.getErrorHandler () == null)
      aRealSettings.setErrorHandler (aMicroHandler);

    if (SAXReader.readXMLSAX (aInputSource, aRealSettings).isFailure ())
      return null;
    return aMicroHandler.getDocument ();
  }

  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputStream aIS)
  {
    return readMicroXML (aIS, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputStream aIS,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aIS == null)
      return null;

    try
    {
      return readMicroXML (InputSourceFactory.create (aIS), aSettings);
    }
    finally
    {
      StreamUtils.close (aIS);
    }
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final File aFile)
  {
    return readMicroXML (aFile, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final File aFile, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aFile == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IReadableResource aRes)
  {
    return readMicroXML (aRes, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IReadableResource aRes,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aRes == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aRes), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IInputStreamProvider aISP)
  {
    return readMicroXML (aISP, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IInputStreamProvider aISP,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aISP == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aISP), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final Reader aReader)
  {
    return readMicroXML (aReader, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final Reader aReader,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aReader == null)
      return null;

    try
    {
      return readMicroXML (InputSourceFactory.create (aReader), aSettings);
    }
    finally
    {
      StreamUtils.close (aReader);
    }
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final String sXML)
  {
    return readMicroXML (sXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final String sXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (sXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final CharSequence sXML)
  {
    return readMicroXML (sXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final CharSequence sXML,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (sXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final byte [] aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final byte [] aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final byte [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen)
  {
    return readMicroXML (aXML, nOfs, nLen, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final byte [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final char [] aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final char [] aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final char [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen)
  {
    return readMicroXML (aXML, nOfs, nLen, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final char [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final ByteBuffer aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final ByteBuffer aXML,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URI aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URI aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URL aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URL aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }
}
