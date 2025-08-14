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
package com.helger.xml.serialize.write;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillCloseWhenClosed;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.log.IHasConditionalLogger;
import com.helger.base.string.Strings;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.iterator.CombinedIterator;
import com.helger.collection.stack.NonBlockingStack;
import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.state.ETriState;
import com.helger.xml.EXMLVersion;
import com.helger.xml.namespace.MapBasedNamespaceContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A special stream writer, that ensures that special XML characters are handled correctly.<br>
 * See https://github.com/javaee/jaxb-v2/issues/614<br>
 * See https://github.com/javaee/jaxb-v2/issues/960
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class SafeXMLStreamWriter implements XMLStreamWriter, AutoCloseable, IHasConditionalLogger
{
  private static final class ElementState
  {
    private final String m_sPrefix;
    private final String m_sLocalName;
    private final EXMLSerializeBracketMode m_eBracketMode;
    private final MapBasedNamespaceContext m_aNamespaceContext;
    // Status vars
    private int m_nTextBasedContentCount = 0;
    private int m_nElementBasedContentCount = 0;
    private boolean m_bOnAfterEndNewLine = false;

    public ElementState (@Nullable final String sPrefix,
                         @Nonnull final String sLocalName,
                         @Nonnull final EXMLSerializeBracketMode eBracketMode,
                         @Nonnull final MapBasedNamespaceContext aNamespaceContext)
    {
      m_sPrefix = sPrefix;
      m_sLocalName = sLocalName;
      m_eBracketMode = eBracketMode;
      m_aNamespaceContext = aNamespaceContext;
    }

    @Nonnegative
    public int getContentCount ()
    {
      return m_nTextBasedContentCount + m_nElementBasedContentCount;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (null).append ("Prefix", m_sPrefix)
                                         .append ("LocalName", m_sLocalName)
                                         .append ("BracketMode", m_eBracketMode)
                                         .append ("NsCtx", m_aNamespaceContext.getPrefixToNamespaceURIMap ().keySet ())
                                         .append ("TBC", m_nTextBasedContentCount)
                                         .append ("EBC", m_nElementBasedContentCount)
                                         .append ("OnAfterEndNL", m_bOnAfterEndNewLine)
                                         .getToString ();
    }
  }

  private static final class MultiNamespaceContext implements NamespaceContext
  {
    private MapBasedNamespaceContext m_aInternalContext = new MapBasedNamespaceContext ();
    private NamespaceContext m_aUserContext;

    public MultiNamespaceContext ()
    {}

    @Override
    @Nonnull
    public String getNamespaceURI (@Nonnull final String sPrefix)
    {
      String ret = m_aInternalContext.getNamespaceURI (sPrefix);
      if (XMLConstants.NULL_NS_URI.equals (ret) && m_aUserContext != null)
        ret = m_aUserContext.getNamespaceURI (sPrefix);
      return ret;
    }

    @Override
    @Nullable
    public String getPrefix (@Nonnull final String sUri)
    {
      String ret = m_aInternalContext.getPrefix (sUri);
      if (ret == null && m_aUserContext != null)
        ret = m_aUserContext.getPrefix (sUri);
      return ret;
    }

    @Override
    @Nonnull
    public Iterator <String> getPrefixes (@Nonnull final String uri)
    {
      final Iterator <String> aIter1 = m_aInternalContext.getPrefixes (uri);
      if (m_aUserContext == null)
        return aIter1;

      final Iterator <String> aIter2 = m_aUserContext.getPrefixes (uri);
      return new CombinedIterator <> (aIter1, aIter2);
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (null).append ("InternalContext", m_aInternalContext)
                                         .append ("UserContext", m_aUserContext)
                                         .getToString ();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (SafeXMLStreamWriter.class);
  private static final AtomicBoolean DEFAULT_DEBUG_MODE = new AtomicBoolean (ConditionalLogger.DEFAULT_ENABLED);

  public static boolean isDefaultDebugMode ()
  {
    return DEFAULT_DEBUG_MODE.get ();
  }

  public static void setDefaultDebugMode (final boolean bDefaultDebugMode)
  {
    DEFAULT_DEBUG_MODE.set (bDefaultDebugMode);
  }

  private final XMLEmitter m_aEmitter;
  private final boolean m_bIndent;
  private final String m_sIndent;
  private final MultiNamespaceContext m_aNamespaceContext = new MultiNamespaceContext ();
  private final NonBlockingStack <ElementState> m_aElementStateStack = new NonBlockingStack <> ()
  {
    @Override
    public ElementState push (final ElementState o)
    {
      final int nOld = size ();
      final ElementState ret = super.push (o);
      debug ( () -> "Pushed[" + nOld + "->" + (nOld + 1) + "] " + o);
      return ret;
    }

    @Override
    public ElementState pop ()
    {
      final int nOld = size ();
      final ElementState ret = super.pop ();
      debug ( () -> "Popped[" + nOld + "->" + (nOld - 1) + "] " + ret);
      return ret;
    }
  };
  private boolean m_bInElementStart = false;
  private final ConditionalLogger m_aCondLog = new ConditionalLogger (LOGGER, DEFAULT_DEBUG_MODE.get ());

  public SafeXMLStreamWriter (@Nonnull final XMLEmitter aEmitter)
  {
    ValueEnforcer.notNull (aEmitter, "Emitter");
    m_aEmitter = aEmitter;
    m_bIndent = aEmitter.getXMLWriterSettings ().getIndent ().isIndent ();
    m_sIndent = m_aEmitter.getXMLWriterSettings ().getIndentationString ();
  }

  /**
   * @return <code>true</code> if debug mode is enabled, <code>false</code> if it is disabled. By
   *         default it is disabled.
   * @see #setDebugMode(boolean)
   */
  public final boolean isDebugMode ()
  {
    return m_aCondLog.isEnabled ();
  }

  /**
   * Enable or disable debug mode
   *
   * @param bDebugMode
   *        <code>true</code> to enable debug mode, <code>false</code> to disable it.
   * @return this for chaining
   * @see #isDebugMode()
   */
  @Nonnull
  public final SafeXMLStreamWriter setDebugMode (final boolean bDebugMode)
  {
    m_aCondLog.setEnabled (bDebugMode);
    return this;
  }

  private void _indent ()
  {
    // Indentation enabled?
    if (m_bIndent)
    {
      // Current level?
      final int nLevel = m_aElementStateStack.size ();
      if (nLevel > 0)
      {
        final ElementState aState = m_aElementStateStack.peek ();
        if (aState.m_nTextBasedContentCount == 0)
        {
          debug ( () -> "indent[" + nLevel + "]");
          m_aEmitter.onContentElementWhitespace (Strings.getRepeated (m_sIndent, nLevel));
        }
      }
    }
  }

  private void _newLineBeforeStartElement ()
  {
    final int nLevel = m_aElementStateStack.size ();
    if (nLevel > 0)
    {
      final ElementState aState = m_aElementStateStack.peek ();
      if (aState.m_nTextBasedContentCount == 0 && !aState.m_bOnAfterEndNewLine)
      {
        if (false)
          m_aEmitter.onContentElementWhitespace ("[BS]");

        debug ( () -> "newLineBeforeStartElement");
        m_aEmitter.newLine ();
      }
    }
  }

  private boolean _newLineAfterEndElement ()
  {
    final int nLevel = m_aElementStateStack.size ();
    if (nLevel > 0)
    {
      final ElementState aState = m_aElementStateStack.peek ();

      // Avoid newline in <p>bla<code>foo</code>bar</p>
      if (aState.m_nTextBasedContentCount == 0)
      {
        if (false)
          m_aEmitter.onContentElementWhitespace ("[AE]");

        debug ( () -> "newLineAfterEndElement");
        m_aEmitter.newLine ();
        aState.m_bOnAfterEndNewLine = true;
        return true;
      }
    }
    return false;
  }

  private void _setTextBasedContent ()
  {
    final ElementState aState = m_aElementStateStack.peek ();
    aState.m_nTextBasedContentCount++;
  }

  private void _setElementBasedContent ()
  {
    if (m_aElementStateStack.isNotEmpty ())
    {
      final ElementState aState = m_aElementStateStack.peek ();
      aState.m_nElementBasedContentCount++;
    }
  }

  @OverrideOnDemand
  protected void debug (@Nonnull final Supplier <String> aSupplier)
  {
    m_aCondLog.info (aSupplier);
  }

  @Nonnull
  private IXMLWriterSettings _getSettings ()
  {
    return m_aEmitter.getXMLWriterSettings ();
  }

  public void writeStartDocument () throws XMLStreamException
  {
    writeStartDocument (_getSettings ().getCharset (), _getSettings ().getXMLVersion ());
  }

  public void writeStartDocument (@Nullable final String sVersion) throws XMLStreamException
  {
    writeStartDocument (_getSettings ().getCharset (), EXMLVersion.getFromVersionOrNull (sVersion));
  }

  public void writeStartDocument (@Nonnull final String sEncoding, @Nullable final String sVersion)
                                                                                                    throws XMLStreamException
  {
    writeStartDocument (CharsetHelper.getCharsetFromName (sEncoding), EXMLVersion.getFromVersionOrNull (sVersion));
  }

  public void writeStartDocument (@Nonnull final Charset aEncoding, @Nonnull final EXMLVersion eVersion)
  {
    debug ( () -> "writeStartDocument (" + aEncoding + ", " + eVersion + ")");
    m_aEmitter.onXMLDeclaration (eVersion, aEncoding.name (), ETriState.UNDEFINED, true);
  }

  public void writeDTD (@Nonnull final String sDTD) throws XMLStreamException
  {
    debug ( () -> "writeDTD (" + sDTD + ")");
    m_aEmitter.onDTD (sDTD);
  }

  public void writeStartElement (final String sLocalName) throws XMLStreamException
  {
    writeStartElement (null, sLocalName);
  }

  public void writeStartElement (@Nullable final String sNamespaceURI, final String sLocalName)
                                                                                                throws XMLStreamException
  {
    writeStartElement (null, sLocalName, sNamespaceURI);
  }

  public void writeStartElement (@Nullable final String sPrefix,
                                 final String sLocalName,
                                 @Nullable final String sNamespaceURI) throws XMLStreamException
  {
    debug ( () -> "writeStartElement (" + sPrefix + ", " + sLocalName + ", " + sNamespaceURI + ")");
    _elementStartClose ();
    _setElementBasedContent ();
    _newLineBeforeStartElement ();
    _indent ();
    m_aEmitter.elementStartOpen (sPrefix, sLocalName);
    m_aElementStateStack.push (new ElementState (sPrefix,
                                                 sLocalName,
                                                 EXMLSerializeBracketMode.OPEN_CLOSE,
                                                 m_aNamespaceContext.m_aInternalContext.getClone ()));
    m_bInElementStart = true;
  }

  public void writeEmptyElement (final String sLocalName) throws XMLStreamException
  {
    writeEmptyElement (null, sLocalName);
  }

  public void writeEmptyElement (@Nullable final String sNamespaceURI, final String sLocalName)
                                                                                                throws XMLStreamException
  {
    writeStartElement (null, sLocalName, sNamespaceURI);
  }

  public void writeEmptyElement (@Nullable final String sPrefix,
                                 final String sLocalName,
                                 @Nullable final String sNamespaceURI) throws XMLStreamException
  {
    debug ( () -> "writeEmptyElement (" + sPrefix + ", " + sLocalName + ", " + sNamespaceURI + ")");
    _elementStartClose ();
    _setElementBasedContent ();
    _newLineBeforeStartElement ();
    _indent ();
    m_aEmitter.elementStartOpen (sPrefix, sLocalName);
    m_aElementStateStack.push (new ElementState (sPrefix,
                                                 sLocalName,
                                                 EXMLSerializeBracketMode.SELF_CLOSED,
                                                 m_aNamespaceContext.m_aInternalContext.getClone ()));
    m_bInElementStart = true;
  }

  public void writeAttribute (final String sLocalName, final String sValue) throws XMLStreamException
  {
    writeAttribute (null, sLocalName, sValue);
  }

  public void writeAttribute (@Nullable final String sNamespaceURI, final String sLocalName, final String sValue)
                                                                                                                  throws XMLStreamException
  {
    writeAttribute (null, sNamespaceURI, sLocalName, sValue);
  }

  public void writeAttribute (@Nullable final String sPrefix,
                              @Nullable final String sNamespaceURI,
                              final String sLocalName,
                              final String sValue) throws XMLStreamException
  {
    debug ( () -> "writeAttribute (" + sPrefix + ", " + sNamespaceURI + ", " + sLocalName + ", " + sValue + ")");
    if (!m_bInElementStart)
      throw new IllegalStateException ("No element open");
    m_aEmitter.elementAttr (sPrefix, sLocalName, sValue);
  }

  public void writeNamespace (@Nullable final String sPrefix, @Nonnull final String sNamespaceURI)
                                                                                                   throws XMLStreamException
  {
    debug ( () -> "writeNamespace (" + sPrefix + ", " + sNamespaceURI + ")");
    if (!m_bInElementStart)
      throw new IllegalStateException ("No element open");

    // Avoid double mapping
    final boolean bIsDefault = sPrefix == null ||
                               sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX) ||
                               sPrefix.equals (XMLConstants.XMLNS_ATTRIBUTE);
    if (bIsDefault)
      m_aNamespaceContext.m_aInternalContext.setDefaultNamespaceURI (sNamespaceURI);
    else
      m_aNamespaceContext.m_aInternalContext.setMapping (sPrefix, sNamespaceURI);

    if (bIsDefault)
      writeAttribute (null, null, XMLConstants.XMLNS_ATTRIBUTE, sNamespaceURI);
    else
      writeAttribute (XMLConstants.XMLNS_ATTRIBUTE, null, sPrefix, sNamespaceURI);
  }

  public void writeDefaultNamespace (@Nonnull final String sNamespaceURI) throws XMLStreamException
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
    // Pop anyway
    final ElementState aState = m_aElementStateStack.pop ();

    debug ( () -> "writeEndElement (" + aState.m_sPrefix + ", " + aState.m_sLocalName + ")");

    // Special _elementStartClose () with check if empty
    boolean bWriteElementEnd = true;
    if (m_bInElementStart)
    {
      // If the element is empty, use self-closed mode!
      final EXMLSerializeBracketMode eBracketMode;

      // Limit to open close to avoid closing the "open only" elements
      if (aState.getContentCount () == 0 && aState.m_eBracketMode == EXMLSerializeBracketMode.OPEN_CLOSE)
      {
        eBracketMode = EXMLSerializeBracketMode.SELF_CLOSED;
        bWriteElementEnd = false;
      }
      else
        eBracketMode = aState.m_eBracketMode;
      // Close opened element
      m_aEmitter.elementStartClose (eBracketMode);
      m_bInElementStart = false;
    }
    if (bWriteElementEnd)
    {
      if (aState.m_nTextBasedContentCount == 0)
        _indent ();
      m_aEmitter.onElementEnd (aState.m_sPrefix, aState.m_sLocalName, aState.m_eBracketMode);
    }
    // Restore namespace context from state
    m_aNamespaceContext.m_aInternalContext = aState.m_aNamespaceContext;
    _newLineAfterEndElement ();
  }

  public void writeComment (final String sData) throws XMLStreamException
  {
    debug ( () -> "writeComment (" + sData + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onComment (sData);
  }

  public void writeCData (final String sData) throws XMLStreamException
  {
    debug ( () -> "writeCData (" + sData + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onCDATA (sData);
  }

  public void writeEntityRef (final String sName) throws XMLStreamException
  {
    debug ( () -> "writeEntityRef (" + sName + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onEntityReference (sName);
  }

  public void writeCharacters (final String sText) throws XMLStreamException
  {
    debug ( () -> "writeCharacters (" + sText + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onText (sText);
  }

  public void writeCharacters (final char [] aText, final int nStart, final int nLen) throws XMLStreamException
  {
    debug ( () -> "writeCharacters (" + String.valueOf (aText, nStart, nLen) + ", " + nStart + ", " + nLen + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onText (aText, nStart, nLen);
  }

  public void writeProcessingInstruction (@Nonnull final String sTarget) throws XMLStreamException
  {
    writeProcessingInstruction (sTarget, null);
  }

  public void writeProcessingInstruction (@Nonnull final String sTarget, @Nullable final String sData)
                                                                                                       throws XMLStreamException
  {
    debug ( () -> "writeProcessingInstruction (" + sTarget + ", " + sData + ")");
    _elementStartClose ();
    _setTextBasedContent ();
    m_aEmitter.onProcessingInstruction (sTarget, sData);
  }

  public void writeEndDocument () throws XMLStreamException
  {
    debug ( () -> "writeEndDocument ()");
    _elementStartClose ();

    if (m_aElementStateStack.isNotEmpty ())
      throw new IllegalStateException ("Internal inconsistency - element stack is not empty: " + m_aElementStateStack);
  }

  public void flush () throws XMLStreamException
  {
    debug ( () -> "flush ()");
    StreamHelper.flush (m_aEmitter);
  }

  public void close () throws XMLStreamException
  {
    debug ( () -> "close ()");
    StreamHelper.close (m_aEmitter);
  }

  public String getPrefix (@Nonnull final String sUri) throws XMLStreamException
  {
    debug ( () -> "getPrefix (" + sUri + ")");
    return m_aNamespaceContext.getPrefix (sUri);
  }

  public void setPrefix (@Nonnull final String sPrefix, @Nonnull final String sUri) throws XMLStreamException
  {
    debug ( () -> "setPrefix (" + sPrefix + ", " + sUri + ")");
    m_aNamespaceContext.m_aInternalContext.addMapping (sPrefix, sUri);
  }

  public void setDefaultNamespace (@Nonnull final String sUri) throws XMLStreamException
  {
    debug ( () -> "setDefaultNamespace (" + sUri + ")");
    m_aNamespaceContext.m_aInternalContext.addDefaultNamespaceURI (sUri);
  }

  public void setNamespaceContext (@Nullable final NamespaceContext aContext) throws XMLStreamException
  {
    debug ( () -> "setNamespaceContext (" + aContext + ")");
    m_aNamespaceContext.m_aUserContext = aContext;
  }

  @Nonnull
  public NamespaceContext getNamespaceContext ()
  {
    debug ( () -> "getNamespaceContext ()");
    return m_aNamespaceContext;
  }

  @Nullable
  public Object getProperty (final String sName)
  {
    debug ( () -> "getProperty (" + sName + ") - UNSUPPORTED");
    return null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Emitter", m_aEmitter)
                                       .append ("NamespaceContext", m_aNamespaceContext)
                                       .append ("ElementStateStack", m_aElementStateStack)
                                       .append ("InElementStart", m_bInElementStart)
                                       .append ("ConditionalLogger", m_aCondLog)
                                       .getToString ();
  }

  @Nonnull
  public static SafeXMLStreamWriter create (@Nonnull @WillCloseWhenClosed final Writer aWriter,
                                            @Nonnull final IXMLWriterSettings aSettings)
  {
    return new SafeXMLStreamWriter (new XMLEmitter (aWriter, aSettings));
  }

  @Nonnull
  public static SafeXMLStreamWriter create (@Nonnull @WillCloseWhenClosed final OutputStream aOS,
                                            @Nonnull final IXMLWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");
    return create (new OutputStreamWriter (aOS, aSettings.getCharset ()), aSettings);
  }
}
