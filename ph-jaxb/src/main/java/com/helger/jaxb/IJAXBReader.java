package com.helger.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.xml.sax.InputSourceFactory;
import com.helger.commons.xml.serialize.read.SAXReaderFactory;
import com.helger.commons.xml.serialize.read.SAXReaderSettings;
import com.helger.commons.xml.transform.TransformSourceFactory;

/**
 * Interface for reading JAXB documents from various sources.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be read
 */
public interface IJAXBReader <JAXBTYPE>
{
  /**
   * Check if secure reading is enabled. Secure reading means that documents are
   * checked for XXE and XML bombs (infinite entity expansions). By default
   * secure reading is enabled.
   *
   * @return <code>true</code> if secure reading is enabled.
   */
  boolean isReadSecure ();

  /**
   * Read a document from the specified input source. The secure reading feature
   * has affect when using this method.
   *
   * @param aInputSource
   *        The source to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  JAXBTYPE read (@Nonnull InputSource aInputSource);

  /**
   * Read a document from the specified input source using the specified SAX
   * reader settings. The secure reading must be enabled in the SAX settings.
   *
   * @param aSettings
   *        The SAX Settings to use.
   * @param aInputSource
   *        The source to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final SAXReaderSettings aSettings, @Nonnull final InputSource aInputSource)
  {
    ValueEnforcer.notNull (aSettings, "Settings");
    ValueEnforcer.notNull (aInputSource, "InputSource");

    // Create new XML reader
    final XMLReader aParser = SAXReaderFactory.createXMLReader ();
    aSettings.applyToSAXReader (aParser);
    return read (new SAXSource (aParser, aInputSource));
  }

  /**
   * Read a document from the specified file. The secure reading feature has
   * affect when using this method.
   *
   * @param aFile
   *        The file to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");
    return read (InputSourceFactory.create (aFile));
  }

  /**
   * Read a document from the specified resource. The secure reading feature has
   * affect when using this method.
   *
   * @param aResource
   *        The resource to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final IReadableResource aResource)
  {
    ValueEnforcer.notNull (aResource, "Resource");
    return read (InputSourceFactory.create (aResource));
  }

  /**
   * Read a document from the specified input stream. The secure reading feature
   * has affect when using this method.
   *
   * @param aIS
   *        The input stream to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    return read (InputSourceFactory.create (aIS));
  }

  /**
   * Read a document from the specified reader. The secure reading feature has
   * affect when using this method.
   *
   * @param aReader
   *        The reader to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final Reader aReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    return read (InputSourceFactory.create (aReader));
  }

  /**
   * Read a document from the specified byte array. The secure reading feature
   * has affect when using this method.
   *
   * @param aXML
   *        The XML bytes to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final byte [] aXML)
  {
    ValueEnforcer.notNull (aXML, "XML");
    return read (InputSourceFactory.create (aXML));
  }

  /**
   * Read a document from the specified byte buffer. The secure reading feature
   * has affect when using this method.
   *
   * @param aXML
   *        The XML bytes to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final ByteBuffer aXML)
  {
    ValueEnforcer.notNull (aXML, "XML");
    return read (InputSourceFactory.create (aXML));
  }

  /**
   * Read a document from the specified String. The secure reading feature has
   * affect when using this method.
   *
   * @param sXML
   *        The XML string to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final String sXML)
  {
    ValueEnforcer.notNull (sXML, "XML");
    return read (InputSourceFactory.create (sXML));
  }

  /**
   * Read a document from the specified source. The secure reading feature has
   * <b>NO</b> affect when using this method because the parameter type is too
   * generic.
   *
   * @param aSource
   *        The source to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  JAXBTYPE read (@Nonnull Source aSource);

  /**
   * Read a document from the specified DOM node. The secure reading feature has
   * <b>NO</b> affect when using this method because no parsing happens! To
   * ensure secure reading the Node must first be serialized to a String and be
   * parsed again!
   *
   * @param aNode
   *        The DOM node to read. May not be <code>null</code>.
   * @return <code>null</code> in case reading fails.
   */
  @Nullable
  default JAXBTYPE read (@Nonnull final Node aNode)
  {
    ValueEnforcer.notNull (aNode, "Node");
    return read (TransformSourceFactory.create (aNode));
  }
}
