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
package com.helger.xml.microdom.serialize;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.ESuccess;
import com.helger.commons.io.file.FileHelper;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Utility class for serializing micro document objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroWriter
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MicroWriter.class);

  @PresentForCodeCoverage
  private static final MicroWriter INSTANCE = new MicroWriter ();

  private MicroWriter ()
  {}

  /**
   * Write a Micro Node to a file using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
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
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @param aSettings
   *        The settings to be used for the creation. May not be <code>null</code>.
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
   * Write a Micro Node to a file using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aPath
   *        The file to write to. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToFile (@Nonnull final IMicroNode aNode, @Nonnull final Path aPath)
  {
    return writeToFile (aNode, aPath, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Write a Micro Node to a file.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aPath
   *        The file to write to. May not be <code>null</code>.
   * @param aSettings
   *        The settings to be used for the creation. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeToFile (@Nonnull final IMicroNode aNode,
                                      @Nonnull final Path aPath,
                                      @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aPath, "Path");
    return writeToFile (aNode, aPath.toFile (), aSettings);
  }

  /**
   * Write a Micro Node to an output stream using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. The output stream is closed
   *        anyway directly after the operation finishes (on success and on error).
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
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. The output stream is closed
   *        anyway directly after the operation finishes (on success and on error).
   * @param aSettings
   *        The settings to be used for the creation. May not be <code>null</code>.
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
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code>. The writer is closed anyway
   *        directly after the operation finishes (on success and on error).
   * @param aSettings
   *        The settings to be used for the creation. May not be <code>null</code>.
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

  /**
   * Write a Micro Node to a {@link Writer} using the default
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code>. The writer is closed anyway
   *        directly after the operation finishes (on success and on error).
   * @return {@link ESuccess}
   * @since 8.6.3
   */
  @Nonnull
  public static ESuccess writeToWriter (@Nonnull final IMicroNode aNode, @Nonnull @WillClose final Writer aWriter)
  {
    return writeToWriter (aNode, aWriter, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Convert the passed micro node to an XML string using the provided settings.
   *
   * @param aNode
   *        The node to be converted to a string. May not be <code>null</code> .
   * @param aSettings
   *        The XML writer settings to use. May not be <code>null</code>.
   * @return The string representation of the passed node.
   */
  @Nullable
  public static String getNodeAsString (@Nonnull final IMicroNode aNode, @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aSettings, "Settings");

    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (50 * CGlobal.BYTES_PER_KILOBYTE))
    {
      // start serializing
      if (writeToWriter (aNode, aWriter, aSettings).isSuccess ())
        return aWriter.getAsString ();
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error serializing MicroDOM with settings " + aSettings.toString (), ex);
    }
    return null;
  }

  /**
   * Convert the passed micro node to an XML string using
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}. This is a specialized version of
   * {@link #getNodeAsString(IMicroNode, IXMLWriterSettings)}.
   *
   * @param aNode
   *        The node to be converted to a string. May not be <code>null</code> .
   * @return The string representation of the passed node.
   * @since 8.6.3
   */
  @Nullable
  public static String getNodeAsString (@Nonnull final IMicroNode aNode)
  {
    return getNodeAsString (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Convert the passed micro node to an XML byte array using the provided settings.
   *
   * @param aNode
   *        The node to be converted to a byte array. May not be <code>null</code> .
   * @param aSettings
   *        The XML writer settings to use. May not be <code>null</code>.
   * @return The byte array representation of the passed node.
   * @since 8.6.3
   */
  @Nullable
  public static byte [] getNodeAsBytes (@Nonnull final IMicroNode aNode, @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aSettings, "Settings");

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (50 *
                                                                                              CGlobal.BYTES_PER_KILOBYTE))
    {
      // start serializing
      if (writeToStream (aNode, aBAOS, aSettings).isSuccess ())
        return aBAOS.toByteArray ();
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error serializing MicroDOM with settings " + aSettings.toString (), ex);
    }
    return null;
  }

  /**
   * Convert the passed micro node to an XML byte array using
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}. This is a specialized version of
   * {@link #getNodeAsBytes(IMicroNode, IXMLWriterSettings)}.
   *
   * @param aNode
   *        The node to be converted to a byte array. May not be <code>null</code> .
   * @return The byte array representation of the passed node.
   * @since 8.6.3
   */
  @Nullable
  public static byte [] getNodeAsBytes (@Nonnull final IMicroNode aNode)
  {
    return getNodeAsBytes (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}
