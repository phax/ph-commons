package com.helger.xml.serialize.write;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.collection.iterate.CombinedIterator;
import com.helger.xml.EXMLVersion;
import com.helger.xml.namespace.MapBasedNamespaceContext;

public class SafeXMLStreamWriter implements XMLStreamWriter
{
  private static final class ElementState
  {
    private final String m_sPrefix;
    private final String m_sLocalName;
    private final EXMLSerializeBracketMode m_eBracketMode;

    public ElementState (@Nullable final String sPrefix,
                         @Nonnull final String sLocalName,
                         @Nonnull final EXMLSerializeBracketMode eBracketMode)
    {
      m_sPrefix = sPrefix;
      m_sLocalName = sLocalName;
      m_eBracketMode = eBracketMode;
    }
  }

  private static final class MultiNamespaceContext implements NamespaceContext
  {
    private final MapBasedNamespaceContext m_aInternalContext = new MapBasedNamespaceContext ();
    private NamespaceContext m_aUserContext;

    public MultiNamespaceContext ()
    {}

    @Override
    @Nonnull
    public String getNamespaceURI (@Nonnull final String prefix)
    {
      String ret = m_aInternalContext.getNamespaceURI (prefix);
      if ((ret == null || XMLConstants.NULL_NS_URI.equals (ret)) && m_aUserContext != null)
        ret = m_aUserContext.getNamespaceURI (prefix);
      return ret;
    }

    @Override
    @Nullable
    public String getPrefix (@Nonnull final String uri)
    {
      String ret = m_aInternalContext.getPrefix (uri);
      if (ret == null && m_aUserContext != null)
        ret = m_aUserContext.getPrefix (uri);
      return ret;
    }

    @Override
    @Nonnull
    public Iterator getPrefixes (@Nonnull final String uri)
    {
      final Iterator <?> aIter1 = m_aInternalContext.getPrefixes (uri);
      if (m_aUserContext == null)
        return aIter1;

      final Iterator <?> aIter2 = m_aUserContext.getPrefixes (uri);
      return new CombinedIterator <> (aIter1, aIter2);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (SafeXMLStreamWriter.class);

  private final XMLEmitter m_aEmitter;
  private final MultiNamespaceContext m_aNamespaceContext = new MultiNamespaceContext ();
  private final NonBlockingStack <ElementState> m_aElementStateStack = new NonBlockingStack <> ();
  private boolean m_bInElementStart = false;
  private final boolean m_bDebug = true;

  public SafeXMLStreamWriter (@Nonnull final XMLEmitter aEmitter)
  {
    ValueEnforcer.notNull (aEmitter, "EMitter");
    m_aEmitter = aEmitter;
  }

  private void _debug (@Nonnull final Supplier <String> aSupplier)
  {
    if (m_bDebug)
      s_aLogger.info (aSupplier.get ());
  }

  public void writeStartDocument () throws XMLStreamException
  {
    writeStartDocument (StandardCharsets.UTF_8.name (), EXMLVersion.XML_10.getVersion ());
  }

  public void writeStartDocument (@Nullable final String sVersion) throws XMLStreamException
  {
    writeStartDocument (StandardCharsets.UTF_8.name (), sVersion);
  }

  public void writeStartDocument (@Nullable final String sEncoding,
                                  @Nullable final String sVersion) throws XMLStreamException
  {
    _debug ( () -> "writeStartDocument (" + sEncoding + ", " + sVersion + ")");
    m_aEmitter.onXMLDeclaration (EXMLVersion.getFromVersionOrNull (sVersion), sEncoding, false);
  }

  public void writeDTD (@Nonnull final String sDTD) throws XMLStreamException
  {
    _debug ( () -> "writeDTD (" + sDTD + ")");
    m_aEmitter.onDTD (sDTD);
  }

  public void writeStartElement (final String sLocalName) throws XMLStreamException
  {
    writeStartElement (null, sLocalName);
  }

  public void writeStartElement (final String sNamespaceURI, final String sLocalName) throws XMLStreamException
  {
    writeStartElement (null, sLocalName, sNamespaceURI);
  }

  public void writeStartElement (final String sPrefix,
                                 final String sLocalName,
                                 final String sNamespaceURI) throws XMLStreamException
  {
    _debug ( () -> "writeStartElement (" + sPrefix + ", " + sLocalName + ", " + sNamespaceURI + ")");
    _elementStartClose ();
    m_aEmitter.elementStartOpen (sPrefix, sLocalName);
    m_aElementStateStack.push (new ElementState (sPrefix, sLocalName, EXMLSerializeBracketMode.OPEN_CLOSE));
    m_bInElementStart = true;
  }

  public void writeEmptyElement (final String sLocalName) throws XMLStreamException
  {
    writeEmptyElement (null, sLocalName);
  }

  public void writeEmptyElement (final String sNamespaceURI, final String sLocalName) throws XMLStreamException
  {
    writeStartElement (null, sLocalName, sNamespaceURI);
  }

  public void writeEmptyElement (final String sPrefix,
                                 final String sLocalName,
                                 final String sNamespaceURI) throws XMLStreamException
  {
    _debug ( () -> "writeEmptyElement (" + sPrefix + ", " + sLocalName + ", " + sNamespaceURI + ")");
    _elementStartClose ();
    m_aEmitter.elementStartOpen (sPrefix, sLocalName);
    m_aElementStateStack.push (new ElementState (sPrefix, sLocalName, EXMLSerializeBracketMode.SELF_CLOSED));
    m_bInElementStart = true;
  }

  public void writeAttribute (final String sLocalName, final String sValue) throws XMLStreamException
  {
    writeAttribute (null, sLocalName, sValue);
  }

  public void writeAttribute (final String sPrefix,
                              final String sNamespaceURI,
                              final String sLocalName,
                              final String sValue) throws XMLStreamException
  {
    _debug ( () -> "writeAttribute (" + sPrefix + ", " + sNamespaceURI + ", " + sLocalName + ", " + sValue + ")");
    if (!m_bInElementStart)
      throw new IllegalStateException ("No element open");
    m_aEmitter.elementAttr (sPrefix, sLocalName, sValue);
  }

  public void writeAttribute (final String sNamespaceURI,
                              final String sLocalName,
                              final String sValue) throws XMLStreamException
  {
    writeAttribute (null, sNamespaceURI, sLocalName, sValue);
  }

  public void writeNamespace (@Nullable final String sPrefix, final String sNamespaceURI) throws XMLStreamException
  {
    _debug ( () -> "writeAttribute (" + sPrefix + ", " + sNamespaceURI + ")");
    if (!m_bInElementStart)
      throw new IllegalStateException ("No element open");

    final boolean bIsDefault = sPrefix == null || sPrefix.equals ("") || sPrefix.equals ("xmlns");
    System.out.println ("writeNamespace (" + sPrefix + ", " + sNamespaceURI + ") - " + bIsDefault);
  }

  public void writeDefaultNamespace (final String sNamespaceURI) throws XMLStreamException
  {
    writeNamespace (null, sNamespaceURI);
  }

  private void _elementStartClose ()
  {
    if (m_bInElementStart)
    {
      m_aEmitter.elementStartClose (m_aElementStateStack.peek ().m_eBracketMode);
      m_bInElementStart = false;
    }
  }

  public void writeEndElement () throws XMLStreamException
  {
    _debug ( () -> "writeEndElement ()");
    _elementStartClose ();
    final ElementState eState = m_aElementStateStack.pop ();
    m_aEmitter.onElementEnd (eState.m_sPrefix, eState.m_sLocalName, eState.m_eBracketMode);
  }

  public void writeComment (final String sData) throws XMLStreamException
  {
    _debug ( () -> "writeComment (" + sData + ")");
    _elementStartClose ();
    m_aEmitter.onComment (sData);
  }

  public void writeCData (final String sData) throws XMLStreamException
  {
    _debug ( () -> "writeCData (" + sData + ")");
    _elementStartClose ();
    m_aEmitter.onCDATA (sData);
  }

  public void writeEntityRef (final String sName) throws XMLStreamException
  {
    _debug ( () -> "writeEntityRef (" + sName + ")");
    _elementStartClose ();
    m_aEmitter.onEntityReference (sName);
  }

  public void writeCharacters (final String sText) throws XMLStreamException
  {
    _debug ( () -> "writeCharacters (" + sText + ")");
    _elementStartClose ();
    m_aEmitter.onText (sText);
  }

  public void writeCharacters (final char [] aText, final int nStart, final int nLen) throws XMLStreamException
  {
    _debug ( () -> "writeCharacters (" + String.valueOf (aText) + ", " + nStart + ", " + nLen + ")");
    _elementStartClose ();
    m_aEmitter.onText (aText, nStart, nLen);
  }

  public void writeProcessingInstruction (@Nonnull final String sTarget) throws XMLStreamException
  {
    writeProcessingInstruction (sTarget, null);
  }

  public void writeProcessingInstruction (@Nonnull final String sTarget,
                                          @Nullable final String sData) throws XMLStreamException
  {
    _debug ( () -> "writeProcessingInstruction (" + sTarget + ", " + sData + ")");
    _elementStartClose ();
    m_aEmitter.onProcessingInstruction (sTarget, sData);
  }

  public void writeEndDocument () throws XMLStreamException
  {
    _debug ( () -> "writeEndDocument ()");
    _elementStartClose ();

    if (m_aElementStateStack.isNotEmpty ())
      throw new IllegalStateException ("Internal inconsistency - element stack is not empty: " + m_aElementStateStack);
  }

  public void flush () throws XMLStreamException
  {
    _debug ( () -> "flush ()");
    try
    {
      m_aEmitter.flush ();
    }
    catch (final IOException ex)
    {
      throw new XMLStreamException ("Error flushing XML emitter", ex);
    }
  }

  public void close () throws XMLStreamException
  {
    _debug ( () -> "close ()");
    try
    {
      m_aEmitter.close ();
    }
    catch (final IOException ex)
    {
      throw new XMLStreamException ("Error closing XML emitter", ex);
    }
  }

  public String getPrefix (@Nonnull final String sUri) throws XMLStreamException
  {
    _debug ( () -> "getPrefix (" + sUri + ")");
    return m_aNamespaceContext.getPrefix (sUri);
  }

  public void setPrefix (@Nonnull final String sPrefix, @Nonnull final String sUri) throws XMLStreamException
  {
    _debug ( () -> "setPrefix (" + sPrefix + ", " + sUri + ")");
    m_aNamespaceContext.m_aInternalContext.addMapping (sPrefix, sUri);
  }

  public void setDefaultNamespace (@Nonnull final String sUri) throws XMLStreamException
  {
    _debug ( () -> "setDefaultNamespace (" + sUri + ")");
    m_aNamespaceContext.m_aInternalContext.setDefaultNamespaceURI (sUri);
  }

  public void setNamespaceContext (@Nullable final NamespaceContext aContext) throws XMLStreamException
  {
    _debug ( () -> "setNamespaceContext (" + aContext + ")");
    m_aNamespaceContext.m_aUserContext = aContext;
  }

  @Nonnull
  public NamespaceContext getNamespaceContext ()
  {
    _debug ( () -> "getNamespaceContext ()");
    return m_aNamespaceContext;
  }

  public Object getProperty (final String sName) throws IllegalArgumentException
  {
    // TODO
    throw new UnsupportedOperationException ();
  }

  @Nonnull
  public static SafeXMLStreamWriter create (@Nonnull final Writer aWriter, @Nonnull final IXMLWriterSettings aSettings)
  {
    return new SafeXMLStreamWriter (new XMLEmitter (aWriter, aSettings));
  }
}
