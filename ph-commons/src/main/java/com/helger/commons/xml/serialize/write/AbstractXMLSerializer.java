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
package com.helger.commons.xml.serialize.write;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.io.stream.NonBlockingBufferedWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.XMLHelper;
import com.helger.commons.xml.namespace.IIterableNamespaceContext;

/**
 * Abstract XML serializer implementation that works with IMicroNode and
 * org.w3c.dom.Node objects.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        The DOM node type to use
 */
public abstract class AbstractXMLSerializer <NODETYPE>
{
  /**
   * The prefix to be used for created namespace prefixes :) (e.g. for "ns0" or
   * "ns1")
   */
  public static final String DEFAULT_NAMESPACE_PREFIX_PREFIX = "ns";

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractXMLSerializer.class);

  /**
   * Contains the XML namespace definitions for a single element.
   *
   * @author Philip Helger
   */
  protected static final class NamespaceLevel
  {
    @SuppressWarnings ("hiding")
    private static final Logger s_aLogger = LoggerFactory.getLogger (NamespaceLevel.class);
    private String m_sDefaultNamespaceURI;
    private Map <String, String> m_aURL2PrefixMap;

    /**
     * Ctor
     */
    public NamespaceLevel ()
    {}

    /**
     * Get the URL matching a given namespace prefix in this level.
     *
     * @param sPrefix
     *        The prefix to be searched. If it is <code>null</code> the default
     *        namespace URL is returned.
     * @return <code>null</code> if the namespace mapping is not used or the URL
     *         otherwise.
     */
    @Nullable
    public String getNamespaceURIOfPrefix (@Nullable final String sPrefix)
    {
      if (StringHelper.hasNoText (sPrefix))
        return m_sDefaultNamespaceURI;

      if (m_aURL2PrefixMap != null)
        for (final Map.Entry <String, String> aEntry : m_aURL2PrefixMap.entrySet ())
          if (aEntry.getValue ().equals (sPrefix))
            return aEntry.getKey ();
      return null;
    }

    void addPrefixNamespaceMapping (@Nullable final String sPrefix, @Nonnull final String sNamespaceURI)
    {
      if (s_aLogger.isTraceEnabled ())
        s_aLogger.trace ("Adding namespace mapping " + sPrefix + ":" + sNamespaceURI);

      // namespace prefix uniqueness check
      final String sExistingNamespaceURI = getNamespaceURIOfPrefix (sPrefix);
      if (sExistingNamespaceURI != null && !sExistingNamespaceURI.equals (sNamespaceURI))
        s_aLogger.warn ("Overwriting namespace prefix '" +
                        sPrefix +
                        "' to use URL '" +
                        sNamespaceURI +
                        "' instead of '" +
                        sExistingNamespaceURI +
                        "'");

      if (StringHelper.hasNoText (sPrefix))
      {
        if (m_sDefaultNamespaceURI != null)
          s_aLogger.warn ("Overwriting default namespace '" +
                          m_sDefaultNamespaceURI +
                          "' with namespace '" +
                          sNamespaceURI +
                          "'");
        m_sDefaultNamespaceURI = sNamespaceURI;
      }
      else
      {
        if (m_aURL2PrefixMap == null)
          m_aURL2PrefixMap = new HashMap <String, String> ();
        m_aURL2PrefixMap.put (sNamespaceURI, sPrefix);
      }
    }

    @Nullable
    public String getDefaultNamespaceURI ()
    {
      return m_sDefaultNamespaceURI;
    }

    @Nullable
    public String getPrefixOfNamespaceURI (@Nonnull final String sNamespaceURI)
    {
      // is it the default?
      if (sNamespaceURI.equals (m_sDefaultNamespaceURI))
        return null;

      // Check in the map
      return m_aURL2PrefixMap == null ? null : m_aURL2PrefixMap.get (sNamespaceURI);
    }

    @Nonnegative
    int getNamespaceCount ()
    {
      return (m_sDefaultNamespaceURI == null ? 0 : 1) + (m_aURL2PrefixMap == null ? 0 : m_aURL2PrefixMap.size ());
    }

    boolean hasAnyNamespace ()
    {
      return m_sDefaultNamespaceURI != null || (m_aURL2PrefixMap != null && !m_aURL2PrefixMap.isEmpty ());
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("defaultNSURI", m_sDefaultNamespaceURI)
                                         .append ("url2prefix", m_aURL2PrefixMap)
                                         .toString ();
    }
  }

  /**
   * Contains the hierarchy of XML namespaces within a document structure.
   * Important: null namespace URIs are different from empty namespace URIs!
   *
   * @author Philip Helger
   */
  protected static final class NamespaceStack
  {
    private final List <NamespaceLevel> m_aStack = new ArrayList <NamespaceLevel> ();
    private final NamespaceContext m_aNamespaceCtx;

    public NamespaceStack (@Nonnull final NamespaceContext aNamespaceCtx)
    {
      m_aNamespaceCtx = aNamespaceCtx;
    }

    /**
     * Start a new namespace level.
     */
    public void push ()
    {
      final NamespaceLevel aNSL = new NamespaceLevel ();
      // add at front
      m_aStack.add (0, aNSL);
    }

    /**
     * Add a new prefix-namespace URI mapping at the current stack level
     *
     * @param sPrefix
     *        Prefix to use. May be <code>null</code>.
     * @param sNamespaceURI
     *        Namespace URI to use. May neither be <code>null</code> nor empty.
     */
    public void addNamespaceMapping (@Nullable final String sPrefix, @Nonnull @Nonempty final String sNamespaceURI)
    {
      // Add the namespace to the current level
      m_aStack.get (0).addPrefixNamespaceMapping (sPrefix, sNamespaceURI);
    }

    /**
     * End the current namespace level.
     */
    public void pop ()
    {
      // remove at front
      m_aStack.remove (0);
    }

    @Nonnegative
    public int size ()
    {
      return m_aStack.size ();
    }

    /**
     * @return The namespace URI that is currently active in the stack. May be
     *         <code>null</code> for no specific namespace.
     */
    @Nullable
    private String _getDefaultNamespaceURI ()
    {
      // iterate from front to end
      for (final NamespaceLevel aNSLevel : m_aStack)
      {
        final String sDefaultNamespaceURI = aNSLevel.getDefaultNamespaceURI ();
        if (StringHelper.hasText (sDefaultNamespaceURI))
          return sDefaultNamespaceURI;
      }

      // no default namespace
      return null;
    }

    /**
     * Resolve the given namespace URI to a prefix using the known namespaces of
     * this stack.
     *
     * @param sNamespaceURI
     *        The namespace URI to resolve. May not be <code>null</code>. Pass
     *        in an empty string for an empty namespace URI!
     * @return <code>null</code> if no namespace prefix is required.
     */
    @Nullable
    private String _getUsedPrefixOfNamespace (@Nonnull final String sNamespaceURI)
    {
      ValueEnforcer.notNull (sNamespaceURI, "NamespaceURI");

      // find existing prefix (iterate current to root)
      for (final NamespaceLevel aNSLevel : m_aStack)
      {
        final String sPrefix = aNSLevel.getPrefixOfNamespaceURI (sNamespaceURI);
        if (sPrefix != null)
          return sPrefix;
      }

      // no matching prefix found
      return null;
    }

    private boolean _containsNoNamespace ()
    {
      for (final NamespaceLevel aNSLevel : m_aStack)
        if (aNSLevel.hasAnyNamespace ())
          return false;
      return true;
    }

    /**
     * Check if the whole prefix is used somewhere in the stack.
     *
     * @param sPrefix
     *        The prefix to be checked
     * @return <code>true</code> if somewhere in the stack, the specified prefix
     *         is already used
     */
    private boolean _containsNoPrefix (@Nonnull final String sPrefix)
    {
      // find existing prefix (iterate current to root)
      for (final NamespaceLevel aNSLevel : m_aStack)
        if (aNSLevel.getNamespaceURIOfPrefix (sPrefix) != null)
          return false;
      return true;
    }

    /**
     * Check if the passed namespace URI is mapped in the namespace context.
     *
     * @param sNamespaceURI
     *        The namespace URI to check. May not be <code>null</code>.
     * @return <code>null</code> if no namespace context mapping is present
     */
    @Nullable
    private String _getMappedPrefix (@Nonnull final String sNamespaceURI)
    {
      ValueEnforcer.notNull (sNamespaceURI, "NamespaceURI");

      // If a mapping is defined, it always takes precedence over the default
      // namespace
      if (m_aNamespaceCtx != null)
      {
        // Is a mapping defined?
        final String sPrefix = m_aNamespaceCtx.getPrefix (sNamespaceURI);
        if (sPrefix != null)
          return sPrefix;
      }
      return null;
    }

    /**
     * Create a new unique namespace prefix.
     *
     * @return <code>null</code> or empty if the default namespace is available,
     *         the prefix otherwise.
     */
    @Nullable
    private String _createUniquePrefix ()
    {
      // Is the default namespace available?
      if (_containsNoNamespace ())
      {
        // Use the default namespace
        return null;
      }

      // find a unique prefix
      int nCount = 0;
      do
      {
        final String sNSPrefix = DEFAULT_NAMESPACE_PREFIX_PREFIX + nCount;
        if (_containsNoPrefix (sNSPrefix))
          return sNSPrefix;
        ++nCount;
      } while (true);
    }

    @Nullable
    public String getElementNamespacePrefixToUse (@Nonnull final String sNamespaceURI,
                                                  final boolean bIsRootElement,
                                                  @Nonnull final Map <QName, String> aAttrMap)
    {
      final String sDefaultNamespaceURI = StringHelper.getNotNull (_getDefaultNamespaceURI ());
      if (sNamespaceURI.equals (sDefaultNamespaceURI))
      {
        // It's the default namespace
        return null;
      }

      String sNSPrefix = _getUsedPrefixOfNamespace (sNamespaceURI);

      // Do we need to create a prefix?
      if (sNSPrefix == null && (!bIsRootElement || sNamespaceURI.length () > 0))
      {
        // Ensure to use the correct prefix (namespace context)
        sNSPrefix = _getMappedPrefix (sNamespaceURI);

        // Do not create a prefix for the root element
        if (sNSPrefix == null && !bIsRootElement)
          sNSPrefix = _createUniquePrefix ();

        // Add and remember the attribute
        aAttrMap.put (XMLHelper.getXMLNSAttrQName (sNSPrefix), sNamespaceURI);
        addNamespaceMapping (sNSPrefix, sNamespaceURI);
      }
      return sNSPrefix;
    }

    @Nullable
    public String getAttributeNamespacePrefixToUse (@Nonnull final String sNamespaceURI,
                                                    @Nonnull final String sName,
                                                    @Nonnull final String sValue,
                                                    @Nonnull final Map <QName, String> aAttrMap)
    {
      final String sDefaultNamespaceURI = StringHelper.getNotNull (_getDefaultNamespaceURI ());
      if (sNamespaceURI.equals (sDefaultNamespaceURI))
      {
        // It's the default namespace
        return null;
      }

      String sNSPrefix = _getUsedPrefixOfNamespace (sNamespaceURI);

      // Do we need to create a prefix?
      if (sNSPrefix == null)
      {
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals (sNamespaceURI))
        {
          // It is an "xmlns:xyz" attribute
          sNSPrefix = sName;
          // Don't emit "xmlns:xmlns"
          if (!XMLConstants.XMLNS_ATTRIBUTE.equals (sName))
          {
            // Add and remember the attribute
            aAttrMap.put (XMLHelper.getXMLNSAttrQName (sNSPrefix), sValue);
          }
          addNamespaceMapping (sNSPrefix, sValue);
        }
        else
        {
          // Ensure to use the correct prefix (namespace context)
          sNSPrefix = _getMappedPrefix (sNamespaceURI);

          // Do not create a prefix for the root element
          if (sNSPrefix == null)
          {
            if (sNamespaceURI.length () == 0)
            {
              // Don't create a namespace mapping for attributes without a
              // namespace URI
              return null;
            }

            sNSPrefix = _createUniquePrefix ();
          }

          // Don't emit "xmlns:xml"
          if (!XMLConstants.XML_NS_PREFIX.equals (sNSPrefix))
          {
            // Add and remember the attribute
            aAttrMap.put (XMLHelper.getXMLNSAttrQName (sNSPrefix), sNamespaceURI);
          }
          addNamespaceMapping (sNSPrefix, sNamespaceURI);
        }
      }
      return sNSPrefix;
    }
  }

  /**
   * The serialization settings
   */
  protected final IXMLWriterSettings m_aSettings;

  /**
   * helper variable: current indentation.
   */
  protected final StringBuilder m_aIndent = new StringBuilder (32);

  /**
   * Current stack of namespaces.
   */
  protected final NamespaceStack m_aNSStack;

  public AbstractXMLSerializer (@Nonnull final IXMLWriterSettings aSettings)
  {
    m_aSettings = ValueEnforcer.notNull (aSettings, "Settings");
    m_aNSStack = new NamespaceStack (aSettings.getNamespaceContext ());
  }

  @Nonnull
  public final IXMLWriterSettings getSettings ()
  {
    return m_aSettings;
  }

  protected final void handlePutNamespaceContextPrefixInRoot (@Nonnull final Map <QName, String> aAttrMap)
  {
    if (m_aNSStack.size () == 1 &&
        m_aSettings.isPutNamespaceContextPrefixesInRoot () &&
        m_aSettings.isEmitNamespaces ())
    {
      // The only place where the namespace context prefixes are added to the
      // root element
      final NamespaceContext aNC = m_aSettings.getNamespaceContext ();
      if (aNC != null)
      {
        if (aNC instanceof IIterableNamespaceContext)
        {
          // Put all on top-level
          for (final Map.Entry <String, String> aEntry : ((IIterableNamespaceContext) aNC).getPrefixToNamespaceURIMap ()
                                                                                          .entrySet ())
          {
            final String sNSPrefix = aEntry.getKey ();
            final String sNamespaceURI = aEntry.getValue ();
            aAttrMap.put (XMLHelper.getXMLNSAttrQName (sNSPrefix), sNamespaceURI);
            m_aNSStack.addNamespaceMapping (sNSPrefix, sNamespaceURI);
          }
        }
        else
          s_aLogger.error ("XMLWriter settings has putNamespaceContextPrefixesInRoot set, but the NamespaceContext does not implement the IIterableNamespaceContext interface!");
      }
    }
  }

  protected abstract void emitNode (@Nonnull final XMLEmitter aXMLWriter,
                                    @Nullable final NODETYPE aParentNode,
                                    @Nullable final NODETYPE aPrevSibling,
                                    @Nonnull final NODETYPE aNode,
                                    @Nullable final NODETYPE aNextSibling);

  @Nonnull
  @OverrideOnDemand
  protected XMLEmitter createXMLEmitter (@Nonnull @WillNotClose final Writer aWriter,
                                         @Nonnull final IXMLWriterSettings aSettings)
  {
    return new XMLEmitter (aWriter, aSettings);
  }

  public final void write (@Nonnull final NODETYPE aNode, @Nonnull final XMLEmitter aXMLEmitter)
  {
    // No parent node
    // No previous and no next sibling
    emitNode (aXMLEmitter, null, null, aNode, null);
  }

  /**
   * Write the specified node to the specified {@link OutputStream}.
   *
   * @param aNode
   *        The node to write. May not be <code>null</code>.
   * @param aOS
   *        The stream to serialize onto. May not be <code>null</code>.
   */
  public final void write (@Nonnull final NODETYPE aNode, @Nonnull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aOS, "OutputStream");

    // Create a writer for the passed output stream
    final NonBlockingBufferedWriter aWriter = new NonBlockingBufferedWriter (StreamHelper.createWriter (aOS,
                                                                                                        m_aSettings.getCharsetObj ()));
    // Inside the other write method, the writer must be flushed!
    write (aNode, aWriter);
    // Do not close the writer!
  }

  /**
   * Write the specified node to the specified {@link Writer}.
   *
   * @param aNode
   *        The node to write. May not be <code>null</code>.
   * @param aWriter
   *        The writer to serialize onto. May not be <code>null</code>.
   */
  public final void write (@Nonnull final NODETYPE aNode, @Nonnull @WillNotClose final Writer aWriter)
  {
    final XMLEmitter aXMLWriter = createXMLEmitter (aWriter, m_aSettings);
    // No parent node
    // No previous and no next sibling
    emitNode (aXMLWriter, null, null, aNode, null);
    // Flush is important for Writer!
    StreamHelper.flush (aWriter);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("settings", m_aSettings)
                                       .append ("sIndent", m_aIndent.toString ())
                                       .append ("namespaceStack", m_aNSStack)
                                       .toString ();
  }
}
