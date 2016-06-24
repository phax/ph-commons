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
package com.helger.jaxb;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;

import org.w3c.dom.Document;

import com.helger.commons.io.resource.IWritableResource;
import com.helger.commons.io.stream.ByteBufferOutputStream;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.xml.XMLFactory;
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
   * A special bi-consumer that additionally can throw a {@link JAXBException}
   *
   * @author Philip Helger
   * @param <JAXBTYPE>
   *        The JAXB type to be written
   */
  @FunctionalInterface
  public interface IJAXBMarshaller <JAXBTYPE>
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
    return write (aObject, TransformResultFactory.create (aResultFile));
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
   * Convert the passed object to XML.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param aResult
   *        The result object holder. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final Result aResult)
  {
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
  default ESuccess write (@Nonnull final JAXBTYPE aObject, @Nonnull final javax.xml.stream.XMLStreamWriter aWriter)
  {
    return write (aObject, (m, e) -> m.marshal (e, aWriter));
  }

  /**
   * Convert the passed object to a new DOM document.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if converting the document failed.
   */
  @Nullable
  default Document getAsDocument (@Nonnull final JAXBTYPE aObject)
  {
    final Document aDoc = XMLFactory.newDocument ();
    return write (aObject, TransformResultFactory.create (aDoc)).isSuccess () ? aDoc : null;
  }

  /**
   * Utility method to directly convert the passed domain object to an XML
   * string.
   *
   * @param aObject
   *        The domain object to be converted. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default String getAsString (@Nonnull final JAXBTYPE aObject)
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    return write (aObject, TransformResultFactory.create (aSW)).isSuccess () ? aSW.getAsString () : null;
  }

  /**
   * Write the passed object to a {@link ByteBuffer} and return it.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default ByteBuffer getAsByteBuffer (@Nonnull final JAXBTYPE aObject)
  {
    final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ();
    return write (aObject, aBBOS).isFailure () ? null : aBBOS.getBuffer ();
  }

  /**
   * Write the passed object to a byte array and return the created byte array.
   *
   * @param aObject
   *        The object to be written. May not be <code>null</code>.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  default byte [] getAsBytes (@Nonnull final JAXBTYPE aObject)
  {
    final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ();
    return write (aObject, aBBOS).isFailure () ? null : aBBOS.getAsByteArray ();
  }
}
