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
package com.helger.commons.microdom.serialize;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerSize;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.xml.serialize.write.IXMLWriterSettings;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * Utility class for serializing micro document objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroWriter
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroWriter.class);
  private static final IMutableStatisticsHandlerSize s_aSizeHdl = StatisticsManager.getSizeHandler (MicroWriter.class);

  @PresentForCodeCoverage
  private static final MicroWriter s_aInstance = new MicroWriter ();

  private MicroWriter ()
  {}

  /**
   * Write a Micro Node to a file using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl.
   *        documents). May not be <code>null</code>.
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToFile (@Nonnull final IMicroNode aNode, @Nonnull final File aFile)
  {
    return writeToFile (aNode, aFile, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Write a Micro Node to a file.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl.
   *        documents). May not be <code>null</code>.
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @param aSettings
   *        The settings to be used for the creation. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToFile (@Nonnull final IMicroNode aNode,
                                      @Nonnull final File aFile,
                                      @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aFile, "File");

    final OutputStream aOS = FileHelper.getOutputStream (aFile);
    if (aOS == null)
      return ESuccess.FAILURE;

    // No need to wrap the OS in a BufferedOutputStream as inside, it is later
    // on wrapped in a BufferedWriter
    return writeToStream (aNode, aOS, aSettings);
  }

  /**
   * Write a Micro Node to an output stream using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl.
   *        documents). May not be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. The
   *        output stream is closed anyway directly after the operation finishes
   *        (on success and on error).
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToStream (@Nonnull final IMicroNode aNode, @Nonnull @WillClose final OutputStream aOS)
  {
    return writeToStream (aNode, aOS, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Write a Micro Node to an {@link OutputStream}.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl.
   *        documents). May not be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. The
   *        output stream is closed anyway directly after the operation finishes
   *        (on success and on error).
   * @param aSettings
   *        The settings to be used for the creation. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToStream (@Nonnull final IMicroNode aNode,
                                        @Nonnull @WillClose final OutputStream aOS,
                                        @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aOS, "OutputStream");
    ValueEnforcer.notNull (aSettings, "Settings");

    try
    {
      final MicroSerializer aSerializer = new MicroSerializer (aSettings);
      aSerializer.write (aNode, aOS);
      return ESuccess.SUCCESS;
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }

  /**
   * Write a Micro Node to a {@link Writer}.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl.
   *        documents). May not be <code>null</code>.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code>. The writer is
   *        closed anyway directly after the operation finishes (on success and
   *        on error).
   * @param aSettings
   *        The settings to be used for the creation. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToWriter (@Nonnull final IMicroNode aNode,
                                        @Nonnull @WillClose final Writer aWriter,
                                        @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aWriter, "Writer");
    ValueEnforcer.notNull (aSettings, "Settings");

    try
    {
      final MicroSerializer aSerializer = new MicroSerializer (aSettings);
      aSerializer.write (aNode, aWriter);
      return ESuccess.SUCCESS;
    }
    finally
    {
      StreamHelper.close (aWriter);
    }
  }

  @Nullable
  public static String getNodeAsString (@Nonnull final IMicroNode aNode, @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aSettings, "Settings");

    NonBlockingStringWriter aWriter = null;
    try
    {
      // start serializing
      aWriter = new NonBlockingStringWriter (50 * CGlobal.BYTES_PER_KILOBYTE);
      if (writeToWriter (aNode, aWriter, aSettings).isSuccess ())
      {
        s_aSizeHdl.addSize (aWriter.getSize ());
        return aWriter.getAsString ();
      }
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Error serializing MicroDOM with settings " + aSettings.toString (), t);
    }
    finally
    {
      StreamHelper.close (aWriter);
    }
    return null;
  }

  /**
   * Convert the passed micro node to an XML string using
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}. This is a specialized
   * version of {@link #getNodeAsString(IMicroNode, IXMLWriterSettings)}.
   *
   * @param aNode
   *        The node to be converted to a string. May not be <code>null</code> .
   * @return The string representation of the passed node.
   */
  @Nullable
  public static String getXMLString (@Nonnull final IMicroNode aNode)
  {
    return getNodeAsString (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}
