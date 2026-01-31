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
package com.helger.xml.serialize.write;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.ESuccess;
import com.helger.statistics.api.IMutableStatisticsHandlerSize;
import com.helger.statistics.impl.StatisticsManager;
import com.helger.xml.EXMLVersion;

/**
 * This is a helper class to serialize DOM nodes to a String.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLWriter
{
  /** The default XML version to be used */
  public static final EXMLVersion DEFAULT_XML_VERSION = EXMLVersion.XML_10;
  /** By default no XML namespace map is present */
  public static final NamespaceContext DEFAULT_NAMESPACE_CTX = null;

  private static final Logger LOGGER = LoggerFactory.getLogger (XMLWriter.class);
  private static final IMutableStatisticsHandlerSize STATS_SIZE = StatisticsManager.getSizeHandler (XMLWriter.class);

  @PresentForCodeCoverage
  private static final XMLWriter INSTANCE = new XMLWriter ();

  private XMLWriter ()
  {}

  /**
   * Write a node to an {@link OutputStream} using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aOS
   *        The {@link OutputStream} to write to. May not be <code>null</code>. The
   *        {@link OutputStream} is closed anyway directly after the operation finishes (on success
   *        and on error).
   * @return {@link ESuccess}
   */
  @NonNull
  public static ESuccess writeToStream (@NonNull final Node aNode, @NonNull @WillClose final OutputStream aOS)
  {
    return writeToStream (aNode, aOS, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Write a node to an {@link OutputStream} using custom settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aOS
   *        The {@link OutputStream} to write to. May not be <code>null</code>. The
   *        {@link OutputStream} is closed anyway directly after the operation finishes (on success
   *        and on error).
   * @param aSettings
   *        The serialization settings to be used. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @NonNull
  public static ESuccess writeToStream (@NonNull final Node aNode,
                                        @NonNull @WillClose final OutputStream aOS,
                                        @NonNull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aOS, "OutputStream");
    ValueEnforcer.notNull (aSettings, "Settings");

    try
    {
      final XMLSerializer aSerializer = new XMLSerializer (aSettings);
      aSerializer.write (aNode, aOS);
      return ESuccess.SUCCESS;
    }
    catch (final RuntimeException ex)
    {
      LOGGER.error ("Error in XML serialization", ex);
      throw ex;
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error in XML serialization", ex);
    }
    finally
    {
      StreamHelper.close (aOS);
    }
    return ESuccess.FAILURE;
  }

  /**
   * Write a node to a {@link Writer} using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aWriter
   *        The {@link Writer} to write to. May not be <code>null</code>. The {@link Writer} is
   *        closed anyway directly after the operation finishes (on success and on error).
   * @return {@link ESuccess}
   */
  @NonNull
  public static ESuccess writeToWriter (@NonNull final Node aNode, @NonNull @WillClose final Writer aWriter)
  {
    return writeToWriter (aNode, aWriter, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Write a node to a {@link Writer} using the default settings.
   *
   * @param aNode
   *        The node to be serialized. May be any kind of node (incl. documents). May not be
   *        <code>null</code>.
   * @param aWriter
   *        The {@link Writer} to write to. May not be <code>null</code>. The {@link Writer} is
   *        closed anyway directly after the operation finishes (on success and on error).
   * @param aSettings
   *        The serialization settings to be used. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @NonNull
  public static ESuccess writeToWriter (@NonNull final Node aNode,
                                        @NonNull @WillClose final Writer aWriter,
                                        @NonNull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aWriter, "Writer");
    ValueEnforcer.notNull (aSettings, "Settings");

    try
    {
      final XMLSerializer aSerializer = new XMLSerializer (aSettings);
      aSerializer.write (aNode, aWriter);
      return ESuccess.SUCCESS;
    }
    catch (final RuntimeException ex)
    {
      LOGGER.error ("Error in XML serialization", ex);
      throw ex;
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error in XML serialization", ex);
    }
    finally
    {
      StreamHelper.close (aWriter);
    }
    return ESuccess.FAILURE;
  }

  /**
   * Convert the passed DOM node to an XML string using the provided XML writer settings.
   *
   * @param aNode
   *        The node to be converted to a string. May not be <code>null</code> .
   * @param aSettings
   *        The XML writer settings to be used. May not be <code>null</code>.
   * @return The string representation of the passed node.
   * @since 8.6.3
   */
  @Nullable
  public static String getNodeAsString (@NonNull final Node aNode, @NonNull final IXMLWriterSettings aSettings)
  {
    // start serializing
    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (50 * CGlobal.BYTES_PER_KILOBYTE))
    {
      if (writeToWriter (aNode, aWriter, aSettings).isSuccess ())
      {
        STATS_SIZE.addSize (aWriter.size ());
        return aWriter.getAsString ();
      }
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error serializing DOM node with settings " + aSettings.toString (), ex);
    }
    return null;
  }

  /**
   * Convert the passed DOM node to an XML string using
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}. This is a specialized version of
   * {@link #getNodeAsString(Node, IXMLWriterSettings)}.
   *
   * @param aNode
   *        The node to be converted to a string. May not be <code>null</code> .
   * @return The string representation of the passed node.
   * @since 8.6.3
   */
  @Nullable
  public static String getNodeAsString (@NonNull final Node aNode)
  {
    return getNodeAsString (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Convert the passed DOM node to an XML byte array using the provided XML writer settings.
   *
   * @param aNode
   *        The node to be converted to a byte array. May not be <code>null</code>.
   * @param aSettings
   *        The XML writer settings to be used. May not be <code>null</code>.
   * @return The byte array representation of the passed node.
   * @since 8.6.3
   */
  public static byte @Nullable [] getNodeAsBytes (@NonNull final Node aNode, @NonNull final IXMLWriterSettings aSettings)
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
      LOGGER.error ("Error serializing DOM node with settings " + aSettings.toString (), ex);
    }
    return null;
  }

  /**
   * Convert the passed micro node to an XML byte array using
   * {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}. This is a specialized version of
   * {@link #getNodeAsBytes(Node, IXMLWriterSettings)}.
   *
   * @param aNode
   *        The node to be converted to a byte array. May not be <code>null</code> .
   * @return The byte array representation of the passed node.
   * @since 8.6.3
   */
  public static byte @Nullable [] getNodeAsBytes (@NonNull final Node aNode)
  {
    return getNodeAsBytes (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}
