/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.jaxb;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.resource.IWritableResource;
import com.helger.commons.io.stream.ByteBufferOutputStream;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.xml.XMLFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroSAXHandler;
import com.helger.xml.namespace.INamespaceContext;
import com.helger.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.SafeXMLStreamWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;
import com.helger.xml.transform.TransformResultFactory;

/**
 * Interface for writing JAXB documents to various destinations.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be written
 */
public interface IJAXBWriter <JAXBTYPE>
{
  /**
   * Use the {@link SafeXMLStreamWriter} where applicable to ensure valid XML is
   * created? This is a work around for
   * https://github.com/javaee/jaxb-v2/issues/614 and
   * https://github.com/javaee/jaxb-v2/issues/960
   */
  boolean USE_JAXB_CHARSET_FIX = true;

  /**
   * @return The special JAXB namespace context to be used. May be
   *         <code>null</code>.
   * @since 8.5.3 in this interface
   */
  @Nullable
  INamespaceContext getNamespaceContext ();

  /**
   * @return <code>true</code> if the JAXB output should be formatted. Default
   *         is <code>false</code>.
   * @since 8.5.3 in this interface
   */
  boolean isFormattedOutput ();

  /**
   * @return The special JAXB Charset to be used for writing. <code>null</code>
   *         by default.
   * @since 8.5.3 in this interface
   */
  @Nullable
  Charset getCharset ();

  default boolean hasCharset ()
  {
    return getCharset () != null;
  }

  /**
   * @return The JAXB indentation string to be used for writing.
   *         <code>null</code> by default. Only used when formatted output is
   *         used.
   * @since 8.5.3 in this interface
   */
  @Nullable
  String getIndentString ();

  default boolean hasIndentString ()
  {
    return StringHelper.hasText (getIndentString ());
  }

  /**
   * @return The schema location to be used for writing. <code>null</code> by
   *         default.
   * @since 8.6.0
   */
  @Nullable
  String getSchemaLocation ();

  default boolean hasSchemaLocation ()
  {
    return StringHelper.hasText (getSchemaLocation ());
  }

  /**
   * @return The no namespace schema location to be used for writing.
   *         <code>null</code> by default.
   * @since 9.0.0
   */
  @Nullable
  String getNoNamespaceSchemaLocation ();

  default boolean hasNoNamespaceSchemaLocation ()
  {
    return StringHelper.hasText (getNoNamespaceSchemaLocation ());
  }

  /**
   * @return The XML writer settings to be used based on this writer settings.
   *         Never <code>null</code>.
   */
  @Nonnull
  default IXMLWriterSettings getXMLWriterSettings ()
  {
    final XMLWriterSettings ret = new XMLWriterSettings ().setNamespaceContext (getNamespaceContext ())
                                                          .setIndent (isFormattedOutput () ? EXMLSerializeIndent.INDENT_AND_ALIGN
                                                                                           : EXMLSerializeIndent.NONE);
    if (hasIndentString ())
      ret.setIndentationString (getIndentString ());
    if (hasCharset ())
      ret.setCharset (getCharset ());
    return ret.setNewLineMode (ENewLineMode.DEFAULT)
              .setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING);
  }

  /**
   * A special bi-consumer that additionally can throw a {@link JAXBException}
   *
   * @author Philip Helger
   * @param <JAXBTYPE>
   *        The JAXB type to be written
   */
  @FunctionalInterface
  interface IJAXBMarshaller <JAXBTYPE>
  {
    void doMarshal (@Nonnull Marshaller aMarshaller, @Nonnull JAXBElement <JAXBTYPE> aJAXBElement) throws JAXBException;
  }

  /**
   * Write the passed object to a {@link File}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aResultFile
   *        The result file to be written to. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final File aResultFile)
  {
    if (USE_JAXB_CHARSET_FIX)
    {
      final OutputStream aOS = FileHelper.getBufferedOutputStream (aResultFile);
      if (aOS == null)
        return ESuccess.FAILURE;
      return write (aObject, aOS);
    }
    return write (aObject, TransformResultFactory.create (aResultFile));
  }

  /**
   * Write the passed object to a {@link Path}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aResultPath
   *        The result path to be written to. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final Path aResultPath)
  {
    if (USE_JAXB_CHARSET_FIX)
      return write (aObject, aResultPath.toFile ());
    return write (aObject, TransformResultFactory.create (aResultPath));
  }

  /**
   * Write the passed object to an {@link OutputStream}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write to. Will always be closed. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull @WillClose final OutputStream aOS)
  {
    try
    {
      if (USE_JAXB_CHARSET_FIX)
      {
        return write (aObject, SafeXMLStreamWriter.create (aOS, getXMLWriterSettings ()));
      }
      return write (aObject, TransformResultFactory.create (aOS));
    }
    finally
    {
      // Needs to be manually closed
      StreamHelper.close (aOS);
    }
  }

  /**
   * Write the passed object to a {@link Writer}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aWriter
   *        The writer to write to. Will always be closed. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull @WillClose final Writer aWriter)
  {
    try
    {
      if (USE_JAXB_CHARSET_FIX)
      {
        return write (aObject, SafeXMLStreamWriter.create (aWriter, getXMLWriterSettings ()));
      }
      return write (aObject, TransformResultFactory.create (aWriter));
    }
    finally
    {
      // Needs to be manually closed
      StreamHelper.close (aWriter);
    }
  }

  /**
   * Write the passed object to a {@link ByteBuffer}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aBuffer
   *        The byte buffer to write to. If the buffer is too small, it is
   *        automatically extended. May not be <code>null</code>.
   * @return {@link ESuccess}
   * @throws BufferOverflowException
   *         If the ByteBuffer is too small
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final ByteBuffer aBuffer)
  {
    return write (aObject, new ByteBufferOutputStream (aBuffer, false));
  }

  /**
   * Write the passed object to an {@link IWritableResource}.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @param aResource
   *        The result resource to be written to. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final IWritableResource aResource)
  {
    if (USE_JAXB_CHARSET_FIX)
    {
      final OutputStream aOS = aResource.getOutputStream (EAppend.TRUNCATE);
      if (aOS == null)
        return ESuccess.FAILURE;
      return write (aObject, aOS);
    }
    return write (aObject, TransformResultFactory.create (aResource));
  }

  /**
   * Convert the passed object to XML.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param aMarshallerFunc
   *        The marshalling function. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  ESuccess write (@Nonnull JAXBTYPE aObject, @Nonnull IJAXBMarshaller <JAXBTYPE> aMarshallerFunc);

  /**
   * Convert the passed object to XML. This method is potentially dangerous,
   * when using StreamResult because it may create invalid XML. Only when using
   * the {@link SafeXMLStreamWriter} it is ensured that only valid XML is
   * created!
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param aResult
   *        The result object holder. May not be <code>null</code>. Usually
   *        SAXResult, DOMResult and StreamResult are supported.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final Result aResult)
  {
    if (USE_JAXB_CHARSET_FIX && aResult instanceof StreamResult)
    {
      LoggerFactory.getLogger (IJAXBWriter.class)
                   .warn ("Potentially invalid XML is created by using StreamResult object: {}", aResult);
    }

    return write (aObject, (m, e) -> m.marshal (e, aResult));
  }

  /**
   * Convert the passed object to XML.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param aHandler
   *        XML will be sent to this handler as SAX2 events. May not be
   *        <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final org.xml.sax.ContentHandler aHandler)
  {
    // No need for charset fix, because it is up to the ContentHandler, if it is
    // converting to a byte[] or not.
    return write (aObject, (m, e) -> m.marshal (e, aHandler));
  }

  /**
   * Convert the passed object to XML.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param aWriter
   *        XML will be sent to this writer. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject,
                          @Nonnull @WillClose final javax.xml.stream.XMLStreamWriter aWriter)
  {
    // No need for charset fix, because it is up to the XMLStreamWriter, if it
    // is converting to a byte[] or not.
    final ESuccess ret = write (aObject, (m, e) -> m.marshal (e, aWriter));
    // Needs to be manually flushed and closed
    try
    {
      aWriter.flush ();
    }
    catch (final XMLStreamException ex)
    {
      throw new IllegalStateException ("Failed to flush XMLStreamWriter", ex);
    }
    try
    {
      aWriter.close ();
    }
    catch (final XMLStreamException ex)
    {
      throw new IllegalStateException ("Failed to close XMLStreamWriter", ex);
    }
    return ret;
  }

  /**
   * Convert the passed object to a new DOM document (write).
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if converting the document failed.
   */
  @Nullable
  default Document getAsDocument (@Nonnull final JAXBTYPE aObject)
  {
    // No need for charset fix, because the document is returned in an internal
    // representation with String content
    final Document aDoc = XMLFactory.newDocument ();
    return write (aObject, TransformResultFactory.create (aDoc)).isSuccess () ? aDoc : null;
  }

  /**
   * Convert the passed object to a new micro document (write).
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if converting the document failed.
   */
  @Nullable
  default IMicroDocument getAsMicroDocument (@Nonnull final JAXBTYPE aObject)
  {
    // No need for charset fix, because the document is returned in an internal
    // representation with String content
    final MicroSAXHandler aHandler = new MicroSAXHandler (false, null, true);
    return write (aObject, aHandler).isSuccess () ? aHandler.getDocument () : null;
  }

  /**
   * Convert the passed object to a new micro document and return only the root
   * element (write).
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if converting the document failed.
   */
  @Nullable
  default IMicroElement getAsMicroElement (@Nonnull final JAXBTYPE aObject)
  {
    final IMicroDocument aDoc = getAsMicroDocument (aObject);
    if (aDoc == null)
      return null;

    final IMicroElement ret = aDoc.getDocumentElement ();
    // Important to detach from document - otherwise the element cannot be
    // re-added somewhere else
    ret.detachFromParent ();
    return ret;
  }

  /**
   * Utility method to directly convert the passed domain object to an XML
   * string (write).
   *
   * @param aObject
   *        The domain object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default String getAsString (@Nonnull final JAXBTYPE aObject)
  {
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      if (USE_JAXB_CHARSET_FIX)
      {
        final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW, getXMLWriterSettings ());
        return write (aObject, aXSW).isSuccess () ? aSW.getAsString () : null;
      }
      return write (aObject, TransformResultFactory.create (aSW)).isSuccess () ? aSW.getAsString () : null;
    }
  }

  /**
   * Write the passed object to a {@link ByteBuffer} and return it (write).
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default ByteBuffer getAsByteBuffer (@Nonnull final JAXBTYPE aObject)
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      if (USE_JAXB_CHARSET_FIX)
      {
        return write (aObject,
                      SafeXMLStreamWriter.create (aBBOS, getXMLWriterSettings ())).isFailure () ? null
                                                                                                : aBBOS.getBuffer ();
      }
      return write (aObject, aBBOS).isFailure () ? null : aBBOS.getBuffer ();
    }
  }

  /**
   * Write the passed object to a byte array and return the created byte array
   * (write).
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default byte [] getAsBytes (@Nonnull final JAXBTYPE aObject)
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      if (USE_JAXB_CHARSET_FIX)
      {
        return write (aObject,
                      SafeXMLStreamWriter.create (aBBOS, getXMLWriterSettings ())).isFailure () ? null
                                                                                                : aBBOS.getAsByteArray ();
      }
      return write (aObject, aBBOS).isFailure () ? null : aBBOS.getAsByteArray ();
    }
  }
}
