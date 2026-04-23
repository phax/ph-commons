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
package com.helger.xml.microdom.serialize;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import org.jspecify.annotations.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.ext.EntityResolver2;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.io.resource.IReadableResource;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.serialize.read.ISAXReaderSettings;
import com.helger.xml.serialize.read.SAXReader;
import com.helger.xml.serialize.read.SAXReaderSettings;

/**
 * Utility class to read an XML stream into an {@link IMicroDocument}.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroReader
{
  @PresentForCodeCoverage
  private static final MicroReader INSTANCE = new MicroReader ();

  private MicroReader ()
  {}

  /**
   * Read the passed input source as MicroXML using the default settings.
   *
   * @param aInputSource
   *        The input source to use. May be <code>null</code> in which case <code>null</code> is
   *        directly returned.
   * @return <code>null</code> if either the input source is <code>null</code> or if the input was
   *         invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputSource aInputSource)
  {
    return readMicroXML (aInputSource, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed input source as MicroXML.
   *
   * @param aInputSource
   *        The input source to use. May be <code>null</code> in which case <code>null</code> is
   *        directly returned.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if either the input source is <code>null</code> or if the input was
   *         invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputSource aInputSource,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aInputSource == null)
      return null;

    final EntityResolver aEntityResolver = aSettings == null ? null : aSettings.getEntityResolver ();
    final MicroSAXHandler aMicroHandler = new MicroSAXHandler (false, aEntityResolver, true);
    if (aSettings instanceof final IMicroReaderSettings aMRS)
      aMicroHandler.setSaveNamespaceDeclarations (aMRS.isSaveNamespaceDeclarations ());

    // Copy and modify settings
    final SAXReaderSettings aRealSettings = SAXReaderSettings.createCloneOnDemand (aSettings);
    aRealSettings.setEntityResolver (aMicroHandler)
                 .setDTDHandler (aMicroHandler)
                 .setContentHandler (aMicroHandler)
                 .setLexicalHandler (aMicroHandler);
    if (aRealSettings.getErrorHandler () == null)
    {
      // Use MicroHandler as default error handler if none is specified
      aRealSettings.setErrorHandler (aMicroHandler);
    }
    if (aEntityResolver instanceof EntityResolver2)
    {
      // Ensure to use the new aEntityResolver2 APIs if available
      aRealSettings.setFeatureValue (EXMLParserFeature.USE_ENTITY_RESOLVER2, true);
    }

    if (SAXReader.readXMLSAX (aInputSource, aRealSettings).isFailure ())
      return null;
    return aMicroHandler.getDocument ();
  }

  /**
   * Read the passed input stream as MicroXML using the default settings.
   *
   * @param aIS
   *        The input stream to read from. Will be closed. May be <code>null</code>.
   * @return <code>null</code> if the input stream is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final InputStream aIS)
  {
    return readMicroXML (aIS, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed input stream as MicroXML.
   *
   * @param aIS
   *        The input stream to read from. Will be closed. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the input stream is <code>null</code> or if the input was invalid
   *         XML.
   */
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
      StreamHelper.close (aIS);
    }
  }

  /**
   * Read the passed file as MicroXML using the default settings.
   *
   * @param aFile
   *        The file to read from. May be <code>null</code>.
   * @return <code>null</code> if the file is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final File aFile)
  {
    return readMicroXML (aFile, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed file as MicroXML.
   *
   * @param aFile
   *        The file to read from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the file is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final File aFile, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aFile == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aFile), aSettings);
  }

  /**
   * Read the passed path as MicroXML using the default settings.
   *
   * @param aPath
   *        The path to read from. May be <code>null</code>.
   * @return <code>null</code> if the path is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final Path aPath)
  {
    return readMicroXML (aPath, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed path as MicroXML.
   *
   * @param aPath
   *        The path to read from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the path is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final Path aPath, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aPath == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aPath), aSettings);
  }

  /**
   * Read the passed readable resource as MicroXML using the default settings.
   *
   * @param aRes
   *        The readable resource to read from. May be <code>null</code>.
   * @return <code>null</code> if the resource is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IReadableResource aRes)
  {
    return readMicroXML (aRes, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed readable resource as MicroXML.
   *
   * @param aRes
   *        The readable resource to read from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the resource is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IReadableResource aRes,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aRes == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aRes), aSettings);
  }

  /**
   * Read the passed input stream provider as MicroXML using the default settings.
   *
   * @param aISP
   *        The input stream provider to read from. May be <code>null</code>.
   * @return <code>null</code> if the input stream provider is <code>null</code> or if the input was
   *         invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IHasInputStream aISP)
  {
    return readMicroXML (aISP, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed input stream provider as MicroXML.
   *
   * @param aISP
   *        The input stream provider to read from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the input stream provider is <code>null</code> or if the input was
   *         invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final IHasInputStream aISP,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aISP == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aISP), aSettings);
  }

  /**
   * Read the passed reader as MicroXML using the default settings.
   *
   * @param aReader
   *        The reader to read from. Will be closed. May be <code>null</code>.
   * @return <code>null</code> if the reader is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@WillClose @Nullable final Reader aReader)
  {
    return readMicroXML (aReader, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed reader as MicroXML.
   *
   * @param aReader
   *        The reader to read from. Will be closed. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the reader is <code>null</code> or if the input was invalid XML.
   */
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
      StreamHelper.close (aReader);
    }
  }

  /**
   * Read the passed XML string as MicroXML using the default settings.
   *
   * @param sXML
   *        The XML string to parse. May be <code>null</code>.
   * @return <code>null</code> if the string is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final String sXML)
  {
    return readMicroXML (sXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed XML string as MicroXML.
   *
   * @param sXML
   *        The XML string to parse. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the string is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final String sXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (sXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (sXML), aSettings);
  }

  /**
   * Read the passed char sequence as MicroXML using the default settings.
   *
   * @param sXML
   *        The char sequence containing XML to parse. May be <code>null</code>.
   * @return <code>null</code> if the char sequence is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final CharSequence sXML)
  {
    return readMicroXML (sXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed char sequence as MicroXML.
   *
   * @param sXML
   *        The char sequence containing XML to parse. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the char sequence is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final CharSequence sXML,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (sXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (sXML), aSettings);
  }

  /**
   * Read the passed byte array as MicroXML using the default settings.
   *
   * @param aXML
   *        The byte array containing XML to parse. May be <code>null</code>.
   * @return <code>null</code> if the byte array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final byte @Nullable [] aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed byte array as MicroXML.
   *
   * @param aXML
   *        The byte array containing XML to parse. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the byte array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final byte @Nullable [] aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read a portion of the passed byte array as MicroXML using the default settings.
   *
   * @param aXML
   *        The byte array containing XML to parse. May be <code>null</code>.
   * @param nOfs
   *        The offset into the byte array to start reading from.
   * @param nLen
   *        The number of bytes to read.
   * @return <code>null</code> if the byte array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final byte @Nullable [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen)
  {
    return readMicroXML (aXML, nOfs, nLen, (ISAXReaderSettings) null);
  }

  /**
   * Read a portion of the passed byte array as MicroXML.
   *
   * @param aXML
   *        The byte array containing XML to parse. May be <code>null</code>.
   * @param nOfs
   *        The offset into the byte array to start reading from.
   * @param nLen
   *        The number of bytes to read.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the byte array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final byte @Nullable [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  /**
   * Read the passed char array as MicroXML using the default settings.
   *
   * @param aXML
   *        The char array containing XML to parse. May be <code>null</code>.
   * @return <code>null</code> if the char array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final char @Nullable [] aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed char array as MicroXML.
   *
   * @param aXML
   *        The char array containing XML to parse. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the char array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final char @Nullable [] aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read a portion of the passed char array as MicroXML using the default settings.
   *
   * @param aXML
   *        The char array containing XML to parse. May be <code>null</code>.
   * @param nOfs
   *        The offset into the char array to start reading from.
   * @param nLen
   *        The number of chars to read.
   * @return <code>null</code> if the char array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final char @Nullable [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen)
  {
    return readMicroXML (aXML, nOfs, nLen, (ISAXReaderSettings) null);
  }

  /**
   * Read a portion of the passed char array as MicroXML.
   *
   * @param aXML
   *        The char array containing XML to parse. May be <code>null</code>.
   * @param nOfs
   *        The offset into the char array to start reading from.
   * @param nLen
   *        The number of chars to read.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the char array is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (final char @Nullable [] aXML,
                                             @Nonnegative final int nOfs,
                                             @Nonnegative final int nLen,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  /**
   * Read the passed byte buffer as MicroXML using the default settings.
   *
   * @param aXML
   *        The byte buffer containing XML to parse. May be <code>null</code>.
   * @return <code>null</code> if the byte buffer is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final ByteBuffer aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the passed byte buffer as MicroXML.
   *
   * @param aXML
   *        The byte buffer containing XML to parse. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the byte buffer is <code>null</code> or if the input was invalid
   *         XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final ByteBuffer aXML,
                                             @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read the XML at the passed URI as MicroXML using the default settings.
   *
   * @param aXML
   *        The URI to read XML from. May be <code>null</code>.
   * @return <code>null</code> if the URI is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URI aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the XML at the passed URI as MicroXML.
   *
   * @param aXML
   *        The URI to read XML from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the URI is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URI aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read the XML at the passed URL as MicroXML using the default settings.
   *
   * @param aXML
   *        The URL to read XML from. May be <code>null</code>.
   * @return <code>null</code> if the URL is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URL aXML)
  {
    return readMicroXML (aXML, (ISAXReaderSettings) null);
  }

  /**
   * Read the XML at the passed URL as MicroXML.
   *
   * @param aXML
   *        The URL to read XML from. May be <code>null</code>.
   * @param aSettings
   *        The settings to use. If <code>null</code> the default settings will be used.
   * @return <code>null</code> if the URL is <code>null</code> or if the input was invalid XML.
   */
  @Nullable
  public static IMicroDocument readMicroXML (@Nullable final URL aXML, @Nullable final ISAXReaderSettings aSettings)
  {
    if (aXML == null)
      return null;

    return readMicroXML (InputSourceFactory.create (aXML), aSettings);
  }
}
