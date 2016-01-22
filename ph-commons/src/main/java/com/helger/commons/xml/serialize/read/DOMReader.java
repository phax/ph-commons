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
package com.helger.commons.xml.serialize.read;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.pool.ObjectPool;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.sax.CollectingSAXErrorHandler;
import com.helger.commons.xml.sax.InputSourceFactory;

/**
 * Helper class to read XML documents via SAX or DOM
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class DOMReader
{
  private static final IMutableStatisticsHandlerTimer s_aDomTimerHdl = StatisticsManager.getTimerHandler (DOMReader.class.getName () +
                                                                                                          "$DOM");
  private static final IMutableStatisticsHandlerTimer s_aDomSchemaTimerHdl = StatisticsManager.getTimerHandler (DOMReader.class.getName () +
                                                                                                                "$DOMwithSchema");
  private static final IMutableStatisticsHandlerCounter s_aDomErrorCounterHdl = StatisticsManager.getCounterHandler (DOMReader.class.getName () +
                                                                                                                     "$DOMERRORS");

  // In practice no more than 5 readers are required (even 3 would be enough)
  private static final ObjectPool <DocumentBuilder> s_aDOMPool = new ObjectPool <> (5,
                                                                                    () -> XMLFactory.createDocumentBuilder ());

  @PresentForCodeCoverage
  private static final DOMReader s_aInstance = new DOMReader ();

  private DOMReader ()
  {}

  public static void reinitialize ()
  {
    s_aDOMPool.clearUnusedItems ();
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final InputSource aIS) throws SAXException
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull @WillClose final InputStream aIS) throws SAXException
  {
    return readXMLDOM (aIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull @WillClose final InputStream aIS,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
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
  public static Document readXMLDOM (@WillClose @Nonnull final Reader aReader) throws SAXException
  {
    return readXMLDOM (aReader, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final Reader aReader,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
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
  public static Document readXMLDOM (@Nonnull final URI aFile) throws SAXException
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URI aFile,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URL aFile) throws SAXException
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final URL aFile,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final File aFile) throws SAXException
  {
    return readXMLDOM (aFile, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final File aFile,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aFile), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final IReadableResource aIIS) throws SAXException
  {
    return readXMLDOM (aIIS, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final IReadableResource aIIS,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aIIS), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final String sXML) throws SAXException
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final String sXML,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final CharSequence sXML) throws SAXException
  {
    return readXMLDOM (sXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final CharSequence sXML,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (sXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final ByteBuffer aXML) throws SAXException
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final ByteBuffer aXML,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML) throws SAXException
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen) throws SAXException
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final byte [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML) throws SAXException
  {
    return readXMLDOM (aXML, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aXML), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen) throws SAXException
  {
    return readXMLDOM (aXML, nOfs, nLen, new DOMReaderSettings ());
  }

  @Nullable
  public static Document readXMLDOM (@Nonnull final char [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    return readXMLDOM (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nullable
  public static Document readXMLDOM (@WillClose @Nonnull final InputSource aInputSource,
                                     @Nonnull final IDOMReaderSettings aSettings) throws SAXException
  {
    ValueEnforcer.notNull (aInputSource, "InputSource");
    ValueEnforcer.notNull (aSettings, "Settings");

    Document aDoc = null;
    try
    {
      final StopWatch aSW = StopWatch.createdStarted ();
      DocumentBuilder aDocumentBuilder;
      boolean bFromPool = false;
      if (aSettings.requiresNewXMLParser ())
      {
        // We need to create a new DocumentBuilderFactory
        final DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance ();

        // Apply the settings on the DocumentBuilderFactory
        aSettings.applyToDocumentBuilderFactory (aDocumentBuilderFactory);

        // Ready to create document builder
        aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder ();
      }
      else
      {
        // Use one from the pool
        aDocumentBuilder = s_aDOMPool.borrowObject ();
        bFromPool = true;
      }

      try
      {
        // Apply settings on DocumentBuilder
        aSettings.applyToDocumentBuilder (aDocumentBuilder);

        // Ensure a collecting error handler is present
        CollectingSAXErrorHandler aCEH;
        final ErrorHandler aCustomErrorHandler = aSettings.getErrorHandler ();
        if (aCustomErrorHandler instanceof CollectingSAXErrorHandler)
          aCEH = (CollectingSAXErrorHandler) aCustomErrorHandler;
        else
        {
          aCEH = new CollectingSAXErrorHandler (aCustomErrorHandler);
          aDocumentBuilder.setErrorHandler (aCEH);
        }

        // Main parsing
        aDoc = aDocumentBuilder.parse (aInputSource);

        // Statistics update
        if (aSettings.getSchema () == null)
          s_aDomTimerHdl.addTime (aSW.stopAndGetMillis ());
        else
          s_aDomSchemaTimerHdl.addTime (aSW.stopAndGetMillis ());

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
          s_aDOMPool.returnObject (aDocumentBuilder);
        }
      }
    }
    catch (final Throwable t)
    {
      aSettings.getExceptionHandler ().onException (t);
      s_aDomErrorCounterHdl.increment ();

      if (t instanceof SAXException)
        throw (SAXException) t;
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
