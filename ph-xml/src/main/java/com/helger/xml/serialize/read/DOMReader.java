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

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.ValueEnforcer;
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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

  public static void reinitialize ()
  {
    POOL.clearUnusedItems ();
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final InputSource aIS)
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull @WillClose final InputStream aIS)
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull @WillClose final InputStream aIS,
                                     @Nonnull final IDOMReaderSettings aSettings)
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

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final Reader aReader)
  {
    return readXMLDOM (aReader, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final Reader aReader,
                                     @Nonnull final IDOMReaderSettings aSettings)
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

  @Nullable
  public static Document readXMLDOM (@Nonnull final URI aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URI aFile, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URL aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URL aFile, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final File aFile)
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final File aFile, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final Path aPath)
  {
    return readXMLDOM (aPath, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final Path aPath, @Nonnull final IDOMReaderSettings aSettings)
  {
    final InputSource aSource = InputSourceFactory.create (aPath);
    return aSource == null ? null : readXMLDOM (aSource, aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final IReadableResource aIIS)
  {
    return readXMLDOM (aIIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final IReadableResource aIIS, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aIIS), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final String sXML)
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final String sXML, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final CharSequence sXML)
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final CharSequence sXML, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final ByteBuffer aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final ByteBuffer aXML, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML)
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML, @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final IDOMReaderSettings aSettings)
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final InputSource aInputSource,
                                     @Nonnull final IDOMReaderSettings aSettings)
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
