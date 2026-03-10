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
package com.helger.xml.serialize.read;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.pool.ObjectPool;
import com.helger.base.timing.StopWatch;
import com.helger.io.resource.IReadableResource;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.api.IMutableStatisticsHandlerTimer;
import com.helger.statistics.impl.StatisticsManager;
import com.helger.xml.XMLFactory;
import com.helger.xml.sax.CollectingSAXErrorHandler;
import com.helger.xml.sax.InputSourceFactory;

/**
 * Helper class to read XML documents via SAX or DOM
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class DOMReader
{
  private static final IMutableStatisticsHandlerTimer STARS_DOM_TIMER = StatisticsManager.getTimerHandler (DOMReader.class.getName () +
                                                                                                           "$DOM");
  private static final IMutableStatisticsHandlerTimer STATS_DOM_SCHEMA_TIMER = StatisticsManager.getTimerHandler (DOMReader.class.getName () +
                                                                                                                  "$DOMwithSchema");
  private static final IMutableStatisticsHandlerCounter STATS_DOM_ERROR_COUNTER = StatisticsManager.getCounterHandler (DOMReader.class.getName () +
                                                                                                                       "$DOMERRORS");

  private static final ObjectPool <DocumentBuilder> POOL = new ObjectPool <> (20, XMLFactory::createDocumentBuilder);

  @PresentForCodeCoverage
  private static final DOMReader INSTANCE = new DOMReader ();

  private DOMReader ()
  {}

  /**
   * Reinitialize the internal document builder pool by clearing all unused
   * items.
   */
  public static void reinitialize ()
  {
    POOL.clearUnusedItems ();
  }

  /**
   * Read an XML document from the given {@link InputSource} using default
   * settings.
   *
   * @param aIS
   *        The input source to read from. May not be <code>null</code>. Will
   *        be closed after reading.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@WillClose @NonNull final InputSource aIS)
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link InputStream} using default
   * settings.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>. Will
   *        be closed after reading.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull @WillClose final InputStream aIS)
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link InputStream} using custom
   * settings.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>. Will
   *        be closed after reading.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull @WillClose final InputStream aIS,
                                     @NonNull final IDOMReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    try
    {
      return readXMLDOM (InputSourceFactory.create (aIS), aSettings);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  /**
   * Read an XML document from the given {@link Reader} using default settings.
   *
   * @param aReader
   *        The reader to read from. May not be <code>null</code>. Will be
   *        closed after reading.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@WillClose @NonNull final Reader aReader)
  {
    return readXMLDOM (aReader, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link Reader} using custom settings.
   *
   * @param aReader
   *        The reader to read from. May not be <code>null</code>. Will be
   *        closed after reading.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@WillClose @NonNull final Reader aReader,
                                     @NonNull final IDOMReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aReader, "Reader");

    try
    {
      return readXMLDOM (InputSourceFactory.create (aReader), aSettings);
    }
    finally
    {
      StreamHelper.close (aReader);
    }
  }

  /**
   * Read an XML document from the given {@link URI} using default settings.
   *
   * @param aFile
   *        The URI to read from. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final URI aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link URI} using custom settings.
   *
   * @param aFile
   *        The URI to read from. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final URI aFile, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  /**
   * Read an XML document from the given {@link URL} using default settings.
   *
   * @param aFile
   *        The URL to read from. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final URL aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link URL} using custom settings.
   *
   * @param aFile
   *        The URL to read from. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final URL aFile, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  /**
   * Read an XML document from the given {@link File} using default settings.
   *
   * @param aFile
   *        The file to read from. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final File aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link File} using custom settings.
   *
   * @param aFile
   *        The file to read from. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final File aFile, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  /**
   * Read an XML document from the given {@link Path} using default settings.
   *
   * @param aPath
   *        The path to read from. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final Path aPath)
  {
    return readXMLDOM (aPath, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link Path} using custom settings.
   *
   * @param aPath
   *        The path to read from. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final Path aPath, @NonNull final IDOMReaderSettings aSettings)
  {
    final InputSource aSource = InputSourceFactory.create (aPath);
    return aSource == null ? null : readXMLDOM (aSource, aSettings);
  }

  /**
   * Read an XML document from the given {@link IReadableResource} using
   * default settings.
   *
   * @param aIIS
   *        The resource to read from. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final IReadableResource aIIS)
  {
    return readXMLDOM (aIIS, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link IReadableResource} using
   * custom settings.
   *
   * @param aIIS
   *        The resource to read from. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final IReadableResource aIIS, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aIIS), aSettings);
  }

  /**
   * Read an XML document from the given XML string using default settings.
   *
   * @param sXML
   *        The XML string to parse. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final String sXML)
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given XML string using custom settings.
   *
   * @param sXML
   *        The XML string to parse. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final String sXML, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  /**
   * Read an XML document from the given {@link CharSequence} using default
   * settings.
   *
   * @param sXML
   *        The XML char sequence to parse. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final CharSequence sXML)
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link CharSequence} using custom
   * settings.
   *
   * @param sXML
   *        The XML char sequence to parse. May not be <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final CharSequence sXML, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  /**
   * Read an XML document from the given {@link ByteBuffer} using default
   * settings.
   *
   * @param aXML
   *        The byte buffer containing the XML to parse. May not be
   *        <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final ByteBuffer aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given {@link ByteBuffer} using custom
   * settings.
   *
   * @param aXML
   *        The byte buffer containing the XML to parse. May not be
   *        <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@NonNull final ByteBuffer aXML, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read an XML document from the given byte array using default settings.
   *
   * @param aXML
   *        The byte array containing the XML to parse. May not be
   *        <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final byte @NonNull [] aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given byte array using custom settings.
   *
   * @param aXML
   *        The byte array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final byte @NonNull [] aXML, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read an XML document from a portion of the given byte array using
   * default settings.
   *
   * @param aXML
   *        The byte array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param nOfs
   *        The offset within the byte array to start reading.
   * @param nLen
   *        The number of bytes to read from the byte array.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final byte @NonNull [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from a portion of the given byte array using
   * custom settings.
   *
   * @param aXML
   *        The byte array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param nOfs
   *        The offset within the byte array to start reading.
   * @param nLen
   *        The number of bytes to read from the byte array.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final byte @NonNull [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  /**
   * Read an XML document from the given char array using default settings.
   *
   * @param aXML
   *        The char array containing the XML to parse. May not be
   *        <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final char @NonNull [] aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from the given char array using custom settings.
   *
   * @param aXML
   *        The char array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final char @NonNull [] aXML, @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  /**
   * Read an XML document from a portion of the given char array using
   * default settings.
   *
   * @param aXML
   *        The char array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param nOfs
   *        The offset within the char array to start reading.
   * @param nLen
   *        The number of chars to read from the char array.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final char @NonNull [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  /**
   * Read an XML document from a portion of the given char array using
   * custom settings.
   *
   * @param aXML
   *        The char array containing the XML to parse. May not be
   *        <code>null</code>.
   * @param nOfs
   *        The offset within the char array to start reading.
   * @param nLen
   *        The number of chars to read from the char array.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (final char @NonNull [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @NonNull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  /**
   * Read an XML document from the given {@link InputSource} using the
   * specified settings. This is the main reading method that all other
   * overloads delegate to.
   *
   * @param aInputSource
   *        The input source to read from. May not be <code>null</code>. Will
   *        be closed after reading.
   * @param aSettings
   *        The DOM reader settings to use. May not be <code>null</code>.
   * @return The read XML {@link Document} or <code>null</code> if reading
   *         fails.
   */
  @Nullable
  public static Document readXMLDOM (@WillClose @NonNull final InputSource aInputSource,
                                     @NonNull final IDOMReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aInputSource, "InputSource");
    ValueEnforcer.notNull (aSettings, "Settings");

    Document aDoc = null;
    try
    {
      final StopWatch aSW = StopWatch.createdStarted ();
      final DocumentBuilder aDocumentBuilder;
      boolean bFromPool = false;
      if (aSettings.requiresNewXMLParser ())
      {
        // We need to create a new DocumentBuilderFactory
        final DocumentBuilderFactory aDocumentBuilderFactory = XMLFactory.createDefaultDocumentBuilderFactory ();

        // Apply the settings on the DocumentBuilderFactory
        aSettings.applyToDocumentBuilderFactory (aDocumentBuilderFactory);

        // Ready to create document builder
        aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder ();
      }
      else
      {
        // Use one from the pool
        aDocumentBuilder = POOL.borrowObject ();
        bFromPool = true;
      }

      try
      {
        // Apply settings on DocumentBuilder
        aSettings.applyToDocumentBuilder (aDocumentBuilder);

        // Ensure a collecting error handler is present
        final CollectingSAXErrorHandler aCEH;
        final ErrorHandler aCustomErrorHandler = aSettings.getErrorHandler ();
        if (aCustomErrorHandler instanceof final CollectingSAXErrorHandler aCollectingHdl)
          aCEH = aCollectingHdl;
        else
        {
          aCEH = new CollectingSAXErrorHandler ();
          aDocumentBuilder.setErrorHandler (aCEH.andThen (aCustomErrorHandler));
        }

        // Main parsing
        aDoc = aDocumentBuilder.parse (aInputSource);

        // Statistics update
        if (aSettings.getSchema () == null)
          STARS_DOM_TIMER.addTime (aSW.stopAndGetMillis ());
        else
          STATS_DOM_SCHEMA_TIMER.addTime (aSW.stopAndGetMillis ());

        // By default, a document is returned, even if does not match the schema
        // (if errors occurred), so I'm handling this manually by checking for
        // collected errors
        if (aCEH.containsAtLeastOneError ())
          return null;
      }
      finally
      {
        if (bFromPool)
        {
          // Return to the pool
          POOL.returnObject (aDocumentBuilder);
        }
      }
    }
    catch (final Exception ex)
    {
      aSettings.exceptionCallbacks ().forEach (x -> x.onException (ex));
      STATS_DOM_ERROR_COUNTER.increment ();
    }
    finally
    {
      // Close both byte stream and character stream, as we don't know which one
      // was used
      StreamHelper.close (aInputSource.getByteStream ());
      StreamHelper.close (aInputSource.getCharacterStream ());
    }
    return aDoc;
  }
}
