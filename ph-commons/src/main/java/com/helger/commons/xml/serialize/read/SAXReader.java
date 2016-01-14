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
import javax.annotation.WillClose;
import javax.annotation.concurrent.ThreadSafe;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.pool.IMutableObjectPool;
import com.helger.commons.pool.ObjectPool;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.xml.sax.InputSourceFactory;

/**
 * Helper class to read XML documents via SAX
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SAXReader
{
  private static final IMutableStatisticsHandlerTimer s_aSaxTimerHdl = StatisticsManager.getTimerHandler (SAXReader.class.getName ());
  private static final IMutableStatisticsHandlerCounter s_aSaxSuccessCounterHdl = StatisticsManager.getCounterHandler (SAXReader.class.getName () +
                                                                                                                       "$success");
  private static final IMutableStatisticsHandlerCounter s_aSaxErrorCounterHdl = StatisticsManager.getCounterHandler (SAXReader.class.getName () +
                                                                                                                     "$error");

  // In practice no more than 5 readers are required (even 3 would be enough)
  private static final IMutableObjectPool <org.xml.sax.XMLReader> s_aSAXPool = new ObjectPool <org.xml.sax.XMLReader> (5,
                                                                                                                       new SAXReaderFactory ());

  @PresentForCodeCoverage
  private static final SAXReader s_aInstance = new SAXReader ();

  private SAXReader ()
  {}

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final File aFile, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aFile), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final URI aURI, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aURI), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final URL aURL, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aURL), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final IHasInputStream aISP, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aISP), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final IReadableResource aResource,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aResource), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final CharSequence aXML, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final String sXML, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (sXML), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final char [] aXML, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final char [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final byte [] aXML, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final byte [] aXML,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML, nOfs, nLen), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull final ByteBuffer aXML, @Nonnull final ISAXReaderSettings aSettings)
  {
    return readXMLSAX (InputSourceFactory.create (aXML), aSettings);
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull @WillClose final InputStream aIS,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    try
    {
      return readXMLSAX (InputSourceFactory.create (aIS), aSettings);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  @Nonnull
  public static ESuccess readXMLSAX (@Nonnull @WillClose final Reader aReader,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aReader, "Reader");

    try
    {
      return readXMLSAX (InputSourceFactory.create (aReader), aSettings);
    }
    finally
    {
      StreamHelper.close (aReader);
    }
  }

  /**
   * Read an XML document via a SAX handler. The streams are closed after
   * reading.
   *
   * @param aIS
   *        The input source to read from. Automatically closed upon success or
   *        error. May not be <code>null</code>.
   *        {@link com.helger.commons.xml.sax.InputSourceFactory} may be used to
   *        create {@link InputSource} objects from different input types.
   * @param aSettings
   *        Reader settings. At least a content handler should be set. May be
   *        <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if reading succeeded,
   *         {@link ESuccess#FAILURE} otherwise
   */
  @Nonnull
  public static ESuccess readXMLSAX (@WillClose @Nonnull final InputSource aIS,
                                     @Nonnull final ISAXReaderSettings aSettings)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aSettings, "Settings");

    try
    {
      boolean bFromPool = false;
      org.xml.sax.XMLReader aParser;
      if (aSettings.requiresNewXMLParser ())
      {
        aParser = SAXReaderFactory.createXMLReader ();
      }
      else
      {
        // use parser from pool
        aParser = s_aSAXPool.borrowObject ();
        bFromPool = true;
      }

      try
      {
        final StopWatch aSW = StopWatch.createdStarted ();

        // Apply settings
        aSettings.applyToSAXReader (aParser);

        // Start parsing
        aParser.parse (aIS);

        // Statistics
        s_aSaxSuccessCounterHdl.increment ();
        s_aSaxTimerHdl.addTime (aSW.stopAndGetMillis ());
        return ESuccess.SUCCESS;
      }
      finally
      {
        if (bFromPool)
        {
          // Return parser to pool
          s_aSAXPool.returnObject (aParser);
        }
      }
    }
    catch (final SAXParseException ex)
    {
      boolean bHandled = false;
      if (aSettings.getErrorHandler () != null)
        try
        {
          aSettings.getErrorHandler ().fatalError (ex);
          bHandled = true;
        }
        catch (final SAXException ex2)
        {
          // fall-through
        }

      if (!bHandled)
        aSettings.getExceptionHandler ().onException (ex);
    }
    catch (final Throwable t)
    {
      aSettings.getExceptionHandler ().onException (t);
    }
    finally
    {
      // Close both byte stream and character stream, as we don't know which one
      // was used
      StreamHelper.close (aIS.getByteStream ());
      StreamHelper.close (aIS.getCharacterStream ());
    }
    s_aSaxErrorCounterHdl.increment ();
    return ESuccess.FAILURE;
  }
}
