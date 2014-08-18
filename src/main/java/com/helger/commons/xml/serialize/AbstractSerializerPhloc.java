/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

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
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.io.streams.NonBlockingBufferedWriter;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.IXMLIterationHandler;
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
public abstract class AbstractSerializerPhloc <NODETYPE> implements IXMLSerializer <NODETYPE>
{
  /**
   * The prefix to be used for created namespace prefixes :) (e.g. for "ns0" or
   * "ns1")
   */
  public static final String DEFAULT_NAMESPACE_PREFIX_PREFIX = "ns";

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSerializerPhloc.class);

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
     * Extract all attribute starting with "xmlns" and create the appropriate
     * prefix mapping.
     * 
     * @param aAttrs
     *        Attribute map to check. May be <code>null</code>.
     */
    public NamespaceLevel (@Nullable final Map <String, String> aAttrs)
    {
      // check all attributes
      if (aAttrs != null)
        for (final Map.Entry <String, String> aEntry : aAttrs.entrySet ())
        {
          final String sAttrName = aEntry.getKey ();
          if (sAttrName.equals (CXML.XML_ATTR_XMLNS))
          {
            // set default namespace (xmlns)
            final String sNamespaceURI = aEntry.getValue ();
            addPrefixNamespaceMapping (null, sNamespaceURI);

            // Happens quite often when using regular DOM serialization
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Found default namespace '" + sNamespaceURI + "' in attribute!");
          }
          else
            if (sAttrName.startsWith (CXML.XML_ATTR_XMLNS_WITH_SEP))
            {
              // prefixed namespace (xmlns:...)
              final String sPrefix = sAttrName.substring (CXML.XML_ATTR_XMLNS_WITH_SEP.length ());
              final String sNamespaceURI = aEntry.getValue ();
              addPrefixNamespaceMapping (sPrefix, sNamespaceURI);

              // Happens quite often when using regular DOM serialization
              if (s_aLogger.isDebugEnabled ())
                s_aLogger.debug ("Found namespace prefix '" +
                                 sPrefix +
                                 "' (with URL '" +
                                 sNamespaceURI +
                                 "') in attribute!");
            }
        }
    }

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

    void addPrefixNamespaceMapping (@Nullable final String sPrefix, @Nonnull @Nonempty final String sNamespaceURI)
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

    public NamespaceStack (@Nullable final NamespaceContext aNamespaceCtx)
    {
      m_aNamespaceCtx = aNamespaceCtx;
    }

    /**
     * Start a new namespace level.
     * 
     * @param aAttrs
     *        Existing attributes from which XML namespace prefix mappings
     *        should be extracted. May be <code>null</code>.
     */
    public void push (@Nullable final Map <String, String> aAttrs)
    {
      final NamespaceLevel aNSL = new NamespaceLevel (aAttrs);
      // add at front
      m_aStack.add (0, aNSL);
    }

    public void addNamespaceMapping (@Nullable final String sPrefix, @Nonnull @Nonempty final String sNamespaceURI)
    {
      // Add the namespace to the current level
      m_aStack.get (0).addPrefixNamespaceMapping (sPrefix, sNamespaceURI);
    }

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
    public String getDefaultNamespaceURI ()
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
     * Resolve the given namespace URI to a prefix.
     * 
     * @param sNamespaceURI
     *        The namespace URI to resolve. May not be <code>null</code>. Pass
     *        in an empty string for an empty namespace URI!
     * @return <code>null</code> if no namespace prefix is required.
     */
    @Nullable
    public String getUsedPrefixOfNamespace (@Nonnull final String sNamespaceURI)
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
    public String getMappedPrefix (@Nonnull final String sNamespaceURI)
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
    public String createUniquePrefix ()
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

  public AbstractSerializerPhloc (@Nonnull final IXMLWriterSettings aSettings)
  {
    m_aSettings = ValueEnforcer.notNull (aSettings, "Settings");
    m_aNSStack = new NamespaceStack (aSettings.getNamespaceContext ());
  }

  @Nonnull
  public final IXMLWriterSettings getSettings ()
  {
    return m_aSettings;
  }

  protected final void handlePutNamespaceContextPrefixInRoot (@Nonnull final Map <String, String> aAttrMap)
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
          for (final Map.Entry <String, String> aEntry : ((IIterableNamespaceContext) aNC).getPrefixToNamespaceURIMap ()
                                                                                          .entrySet ())
          {
            final String sNSPrefix = aEntry.getKey ();
            final String sNamespaceURI = aEntry.getValue ();
            aAttrMap.put (XMLHelper.getXMLNSAttrName (sNSPrefix), sNamespaceURI);
            m_aNSStack.addNamespaceMapping (sNSPrefix, sNamespaceURI);
          }
        }
        else
          s_aLogger.error ("XMLWriter settings has putNamespaceContextPrefixesInRoot set, but the NamespaceContext does not implement the IIterableNamespaceContext interface!");
      }
    }
  }

  protected abstract void emitNode (@Nonnull final IXMLIterationHandler aXMLWriter,
                                    @Nullable final NODETYPE aPrevSibling,
                                    @Nonnull final NODETYPE aNode,
                                    @Nullable final NODETYPE aNextSibling);

  @Nonnull
  @OverrideOnDemand
  protected XMLEmitterPhloc createXMLEmitter (@Nonnull @WillNotClose final Writer aWriter,
                                              @Nonnull final IXMLWriterSettings aSettings)
  {
    return new XMLEmitterPhloc (aWriter, aSettings);
  }

  public final void write (@Nonnull final NODETYPE aNode, @Nonnull @WillNotClose final Writer aWriter)
  {
    final XMLEmitterPhloc aXMLWriter = createXMLEmitter (aWriter, m_aSettings);
    // No previous and no next sibling
    emitNode (aXMLWriter, null, aNode, null);
    // Flush is important for Writer!
    StreamUtils.flush (aWriter);
  }

  public final void write (@Nonnull final NODETYPE aNode, @Nonnull final IXMLIterationHandler aXMLEmitter)
  {
    // No previous and no next sibling
    emitNode (aXMLEmitter, null, aNode, null);
  }

  public final void write (@Nonnull final NODETYPE aNode, @Nonnull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aOS, "OutputStream");

    // Create a writer for the passed output stream
    final NonBlockingBufferedWriter aWriter = new NonBlockingBufferedWriter (StreamUtils.createWriter (aOS,
                                                                                                       m_aSettings.getCharsetObj ()));
    // Inside the other write method, the writer must be flushed!
    write (aNode, aWriter);
    // Do not close the writer!
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
