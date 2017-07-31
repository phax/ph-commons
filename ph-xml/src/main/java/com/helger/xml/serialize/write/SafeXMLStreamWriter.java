package com.helger.xml.serialize.write;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.helger.commons.collection.NonBlockingStack;
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

  private final IXMLWriterSettings m_aSettings;
  private final XMLEmitter m_aEmitter;
  private final MapBasedNamespaceContext m_aNamespaceContext = new MapBasedNamespaceContext ();
  private final NonBlockingStack <ElementState> m_aElementStateStack = new NonBlockingStack <> ();
  private boolean m_bInElementStart = false;

  public SafeXMLStreamWriter (@Nonnull final IXMLWriterSettings aSettings, final XMLEmitter aEmitter)
  {
    m_aSettings = aSettings;
    m_aEmitter = aEmitter;
  }

  public void writeStartDocument () throws XMLStreamException
  {
    writeStartDocument (StandardCharsets.UTF_8.name (), EXMLVersion.XML_10.getVersion ());
  }

  public void writeStartDocument (@Nullable final String sVersion) throws XMLStreamException
  {
    writeStartDocument (StandardCharsets.UTF_8.name (), sVersion);
  }

  public void writeStartDocument (final String sEncoding, final String sVersion) throws XMLStreamException
  {
    m_aEmitter.onXMLDeclaration (EXMLVersion.getFromVersionOrNull (sVersion), sEncoding, false);
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
    m_aEmitter.elementStartOpen (sPrefix, sLocalName);
    m_aElementStateStack.push (new ElementState (sPrefix, sLocalName, EXMLSerializeBracketMode.OPEN_ONLY));
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

  private void _elementStartClose ()
  {
    m_aEmitter.elementStartClose (m_aElementStateStack.peek ().m_eBracketMode);
  }

  public void writeEndElement () throws XMLStreamException
  {
    if (m_bInElementStart)
      _elementStartClose ();

    final ElementState eState = m_aElementStateStack.pop ();
    m_aEmitter.onElementEnd (eState.m_sPrefix, eState.m_sLocalName, eState.m_eBracketMode);
  }

  public void writeComment (final String sData) throws XMLStreamException
  {
    m_aEmitter.onComment (sData);
  }

  public void writeCData (final String sData) throws XMLStreamException
  {
    m_aEmitter.onCDATA (sData);
  }

  public void writeCharacters (final String sText) throws XMLStreamException
  {
    m_aEmitter.onText (sText);
  }

  public void writeCharacters (final char [] aText, final int nStart, final int nLen) throws XMLStreamException
  {
    m_aEmitter.onText (aText, nStart, nLen);
  }

  public void writeEndDocument () throws XMLStreamException
  {}

  public void close () throws XMLStreamException
  {}

  public void flush () throws XMLStreamException
  {}

  public void writeNamespace (final String sPrefix, final String sNamespaceURI) throws XMLStreamException
  {}

  public void writeDefaultNamespace (final String sNamespaceURI) throws XMLStreamException
  {}

  public void writeProcessingInstruction (final String sTarget) throws XMLStreamException
  {
    writeProcessingInstruction (sTarget, null);
  }

  public void writeProcessingInstruction (final String sTarget, final String sData) throws XMLStreamException
  {}

  public void writeDTD (final String sDtd) throws XMLStreamException
  {}

  public void writeEntityRef (final String sName) throws XMLStreamException
  {}

  public String getPrefix (final String sUri) throws XMLStreamException
  {
    return m_aNamespaceContext.getPrefix (sUri);
  }

  public void setPrefix (final String sPrefix, final String sUri) throws XMLStreamException
  {
    // TODO
  }

  public void setDefaultNamespace (final String sUri) throws XMLStreamException
  {}

  public void setNamespaceContext (final NamespaceContext aContext) throws XMLStreamException
  {}

  public NamespaceContext getNamespaceContext ()
  {
    return m_aNamespaceContext;
  }

  public Object getProperty (final String sName) throws IllegalArgumentException
  {
    return null;
  }
}
