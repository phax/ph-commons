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
package com.helger.xml;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.misc.PresentForCodeCoverage;
import com.helger.annotation.misc.ReturnsMutableCopy;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringHelper;

/**
 * This class contains multiple XML utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLHelper
{
  @PresentForCodeCoverage
  private static final XMLHelper INSTANCE = new XMLHelper ();

  private XMLHelper ()
  {}

  /**
   * Get the owner document of the passed node. If the node itself is a
   * document, only a cast is performed.
   *
   * @param aNode
   *        The node to get the document from. May be <code>null</code>.
   * @return <code>null</code> if the passed node was <code>null</code>.
   */
  @Nullable
  public static Document getOwnerDocument (@Nullable final Node aNode)
  {
    if (aNode == null)
      return null;
    if (aNode instanceof Document)
      return (Document) aNode;
    return aNode.getOwnerDocument ();
  }

  @Nullable
  public static Element getDocumentElement (@Nullable final Node aNode)
  {
    final Document aDoc = getOwnerDocument (aNode);
    return aDoc == null ? null : aDoc.getDocumentElement ();
  }

  @Nullable
  public static String getNamespaceURI (@Nullable final Node aNode)
  {
    if (aNode instanceof Document)
    {
      // Recurse into document element
      return getNamespaceURI (((Document) aNode).getDocumentElement ());
    }
    if (aNode != null)
      return aNode.getNamespaceURI ();
    return null;
  }

  @Nonnull
  public static String getLocalNameOrTagName (@Nonnull final Element aElement)
  {
    String ret = aElement.getLocalName ();
    if (ret == null)
      ret = aElement.getTagName ();
    return ret;
  }

  @Nonnull
  public static String getLocalNameOrName (@Nonnull final Attr aAttr)
  {
    String ret = aAttr.getLocalName ();
    if (ret == null)
      ret = aAttr.getName ();
    return ret;
  }

  @Nullable
  public static String getElementName (@Nullable final Node aNode)
  {
    if (aNode instanceof Document)
    {
      // Recurse into document element
      return getElementName (((Document) aNode).getDocumentElement ());
    }
    if (aNode instanceof Element)
    {
      return getLocalNameOrTagName ((Element) aNode);
    }
    return null;
  }

  public static boolean hasNoNamespaceURI (@Nonnull final Node aNode)
  {
    return StringHelper.hasNoText (aNode.getNamespaceURI ());
  }

  public static boolean hasNamespaceURI (@Nullable final Node aNode, @Nullable final String sNamespaceURI)
  {
    final String sNSURI = aNode == null ? null : aNode.getNamespaceURI ();
    return sNSURI != null && sNSURI.equals (sNamespaceURI);
  }

  /**
   * Check if the passed node is a text node. This includes all nodes derived
   * from {@link Text} (Text and CData) or {@link EntityReference} nodes.
   *
   * @param aNode
   *        The node to be checked.
   * @return <code>true</code> if the passed node is a text node,
   *         <code>false</code> otherwise.
   */
  public static boolean isInlineNode (@Nullable final Node aNode)
  {
    return aNode instanceof Text || aNode instanceof EntityReference;
  }

  @Nonnegative
  public static int getLength (@Nullable final NodeList aNL)
  {
    return aNL == null ? 0 : aNL.getLength ();
  }

  public static boolean isEmpty (@Nullable final NodeList aNL)
  {
    return aNL == null || aNL.getLength () == 0;
  }

  @Nonnull
  public static Predicate <? super Node> filterNodeIsElement ()
  {
    return x -> x != null && x.getNodeType () == Node.ELEMENT_NODE;
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithNamespace ()
  {
    return x -> x != null && StringHelper.hasText (x.getNamespaceURI ());
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithoutNamespace ()
  {
    return x -> x != null && hasNoNamespaceURI (x);
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithNamespace (@Nullable final String sNamespaceURI)
  {
    return x -> x != null && hasNamespaceURI (x, sNamespaceURI);
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithNamespaceAndLocalName (@Nullable final String sNamespaceURI,
                                                                                    @Nonnull @Nonempty final String sLocalName)
  {
    ValueEnforcer.notEmpty (sLocalName, "LocalName");
    return x -> x != null && hasNamespaceURI (x, sNamespaceURI) && x.getLocalName ().equals (sLocalName);
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithTagName (@Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");
    return x -> EqualsHelper.equals (getElementName (x), sTagName);
  }

  @Nonnull
  public static Predicate <? super Element> filterElementWithTagNameNoNS (@Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");
    return x -> hasNoNamespaceURI (x) && x.getTagName ().equals (sTagName);
  }

  /**
   * Get the first direct child element of the passed element.
   *
   * @param aStartNode
   *        The element to start searching. May be <code>null</code>.
   * @return <code>null</code> if the passed element does not have any direct
   *         child element.
   */
  @Nullable
  public static Element getFirstChildElement (@Nullable final Node aStartNode)
  {
    if (aStartNode == null)
      return null;
    return NodeListIterator.createChildNodeIterator (aStartNode)
                           .findFirstMapped (filterNodeIsElement (), Element.class::cast);
  }

  /**
   * Check if the passed node has at least one direct child element or not.
   *
   * @param aStartNode
   *        The parent element to be searched. May be <code>null</code>.
   * @return <code>true</code> if the passed node has at least one child
   *         element, <code>false</code> otherwise.
   */
  public static boolean hasChildElementNodes (@Nullable final Node aStartNode)
  {
    if (aStartNode == null)
      return false;
    return NodeListIterator.createChildNodeIterator (aStartNode).containsAny (filterNodeIsElement ());
  }

  /**
   * Search all child nodes of the given for the first element that has the
   * specified tag name.
   *
   * @param aStartNode
   *        The parent element to be searched. May be <code>null</code>.
   * @param sTagName
   *        The tag name to search.
   * @return <code>null</code> if the parent element has no such child element.
   */
  @Nullable
  public static Element getFirstChildElementOfName (@Nullable final Node aStartNode,
                                                    @Nonnull @Nonempty final String sTagName)
  {
    if (aStartNode == null)
      return null;
    return new ChildElementIterator (aStartNode).findFirst (filterElementWithTagName (sTagName));
  }

  /**
   * Search all child nodes of the given for the first element that has the
   * specified tag name.
   *
   * @param aStartNode
   *        The parent element to be searched. May be <code>null</code>.
   * @param sNamespaceURI
   *        Namespace URI to search. May be <code>null</code>.
   * @param sLocalName
   *        The tag name to search.
   * @return <code>null</code> if the parent element has no such child element.
   */
  @Nullable
  public static Element getFirstChildElementOfName (@Nullable final Node aStartNode,
                                                    @Nullable final String sNamespaceURI,
                                                    @Nonnull @Nonempty final String sLocalName)
  {
    if (aStartNode == null)
      return null;
    return new ChildElementIterator (aStartNode).findFirst (filterElementWithNamespaceAndLocalName (sNamespaceURI,
                                                                                                    sLocalName));
  }

  /**
   * Find a direct child using multiple levels, starting from a given start
   * element.
   *
   * @param aStartElement
   *        The element to start from. May be <code>null</code>.
   * @param aTagNames
   *        The child elements to be found in order. May neither be
   *        <code>null</code> nor empty and may not contain <code>null</code>
   *        elements.
   * @return <code>null</code> if no such child element was found, of if the
   *         start element was <code>null</code>.
   * @see #getFirstChildElementOfName(Node, String)
   * @since 10.1.2
   */
  @Nullable
  public static Element getChildElementOfNames (@Nullable final Element aStartElement,
                                                @Nonnull final String... aTagNames)
  {
    ValueEnforcer.notEmptyNoNullValue (aTagNames, "TagNames");

    Element aCurElement = aStartElement;
    if (aCurElement != null)
      for (final String sTagName : aTagNames)
      {
        aCurElement = getFirstChildElementOfName (aCurElement, sTagName);
        if (aCurElement == null)
          return null;
      }
    return aCurElement;
  }

  @Nonnull
  public static Node append (@Nonnull final Node aParentNode, @Nullable final Object aChild)
  {
    ValueEnforcer.notNull (aParentNode, "ParentNode");

    if (aChild != null)
      if (aChild instanceof Document)
      {
        // Special handling for Document comes first, as this is a special case
        // of "Node"

        // Cannot add complete documents!
        append (aParentNode, ((Document) aChild).getDocumentElement ());
      }
      else
        if (aChild instanceof Node)
        {
          // directly append Node
          final Node aChildNode = (Node) aChild;
          final Document aParentDoc = getOwnerDocument (aParentNode);
          if (getOwnerDocument (aChildNode).equals (aParentDoc))
          {
            // Nodes have the same parent
            aParentNode.appendChild (aChildNode);
          }
          else
          {
            // Node to be added belongs to a different document
            aParentNode.appendChild (aParentDoc.adoptNode (aChildNode.cloneNode (true)));
          }
        }
        else
          if (aChild instanceof String)
          {
            // append a string node
            aParentNode.appendChild (getOwnerDocument (aParentNode).createTextNode ((String) aChild));
          }
          else
            if (aChild instanceof Iterable <?>)
            {
              // it's a nested collection -> recursion
              for (final Object aSubChild : (Iterable <?>) aChild)
                append (aParentNode, aSubChild);
            }
            else
              if (ArrayHelper.isArray (aChild))
              {
                // it's a nested collection -> recursion
                for (final Object aSubChild : (Object []) aChild)
                  append (aParentNode, aSubChild);
              }
              else
              {
                // unsupported type
                throw new IllegalArgumentException ("Passed object cannot be appended to a DOMNode (type=" +
                                                    aChild.getClass ().getName () +
                                                    ".");
              }
    return aParentNode;
  }

  public static void append (@Nonnull final Node aParentNode, @Nonnull final Iterable <?> aNodesToAppend)
  {
    ValueEnforcer.notNull (aParentNode, "ParentNode");
    for (final Object aNode : aNodesToAppend)
      append (aParentNode, aNode);
  }

  @Nonnegative
  public static int getDirectChildElementCount (@Nullable final Element aParent)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIterator (aParent));
  }

  @Nonnegative
  public static int getDirectChildElementCountNoNS (@Nullable final Element aParent)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIteratorNoNS (aParent));
  }

  @Nonnegative
  public static int getDirectChildElementCount (@Nullable final Element aParent,
                                                @Nonnull @Nonempty final String sTagName)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIterator (aParent, sTagName));
  }

  @Nonnegative
  public static int getDirectChildElementCountNoNS (@Nullable final Element aParent,
                                                    @Nonnull @Nonempty final String sTagName)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIteratorNoNS (aParent, sTagName));
  }

  @Nonnegative
  public static int getDirectChildElementCountNS (@Nullable final Element aParent, @Nullable final String sNamespaceURI)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIteratorNS (aParent, sNamespaceURI));
  }

  @Nonnegative
  public static int getDirectChildElementCountNS (@Nullable final Element aParent,
                                                  @Nullable final String sNamespaceURI,
                                                  @Nonnull @Nonempty final String sLocalName)
  {
    return aParent == null ? 0
                           : CollectionHelper.getSize (getChildElementIteratorNS (aParent, sNamespaceURI, sLocalName));
  }

  /**
   * Get an iterator over all child elements.
   *
   * @param aStartNode
   *        the parent element
   * @return a non-null Iterator
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIterator (@Nullable final Node aStartNode)
  {
    return new ChildElementIterator (aStartNode);
  }

  /**
   * Get an iterator over all child elements that have no namespace.
   *
   * @param aStartNode
   *        the parent element
   * @return a non-null Iterator
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nullable final Node aStartNode)
  {
    return new ChildElementIterator (aStartNode).withFilter (filterElementWithoutNamespace ());
  }

  /**
   * Get an iterator over all child elements that have no namespace and the
   * desired tag name.
   *
   * @param aStartNode
   *        the parent element
   * @param sTagName
   *        the name of the tag that is desired
   * @return a non-null Iterator
   * @throws IllegalArgumentException
   *         if the passed tag name is null or empty
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nullable final Node aStartNode,
                                                                         @Nonnull @Nonempty final String sTagName)
  {
    return new ChildElementIterator (aStartNode).withFilter (filterElementWithTagNameNoNS (sTagName));
  }

  /**
   * Get an iterator over all child elements that have the desired tag name (but
   * potentially a namespace URI).
   *
   * @param aStartNode
   *        the parent element
   * @param sTagName
   *        the name of the tag that is desired
   * @return a non-null Iterator
   * @throws IllegalArgumentException
   *         if the passed tag name is null or empty
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIterator (@Nullable final Node aStartNode,
                                                                     @Nonnull @Nonempty final String sTagName)
  {
    return new ChildElementIterator (aStartNode).withFilter (filterElementWithTagName (sTagName));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nullable final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI)
  {
    return new ChildElementIterator (aStartNode).withFilter (filterElementWithNamespace (sNamespaceURI));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nullable final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI,
                                                                       @Nonnull @Nonempty final String sLocalName)
  {
    return new ChildElementIterator (aStartNode).withFilter (filterElementWithNamespaceAndLocalName (sNamespaceURI,
                                                                                                     sLocalName));
  }

  public static boolean hasSameElementName (@Nonnull final Element aFirst, @Nonnull final Element aSecond)
  {
    final String sFirstNS = aFirst.getNamespaceURI ();
    final String sSecondNS = aSecond.getNamespaceURI ();
    if (StringHelper.hasText (sFirstNS))
    {
      // NS + local name
      return sFirstNS.equals (sSecondNS) && aFirst.getLocalName ().equals (aSecond.getLocalName ());
    }
    // No NS + tag name
    return StringHelper.hasNoText (sSecondNS) && aFirst.getTagName ().equals (aSecond.getTagName ());
  }

  @Nonnull
  private static String _getPathToNode (@Nonnull final Node aNode,
                                        @Nonnull final String sSep,
                                        final boolean bExcludeDocumentNode,
                                        final boolean bZeroBasedIndex,
                                        final boolean bForceUseIndex,
                                        final boolean bTrailingSeparator,
                                        final boolean bCompareIncludingNamespaceURI,
                                        @Nullable final NamespaceContext aNamespaceCtx)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (sSep, "Separator");

    final Function <String, String> funGetNSPrefix = aNamespaceCtx == null ? ns -> "" : ns -> {
      if (StringHelper.hasText (ns))
      {
        final String sPrefix = aNamespaceCtx.getPrefix (ns);
        if (StringHelper.hasText (sPrefix))
          return sPrefix + ":";
      }
      return "";
    };

    final StringBuilder aRet = new StringBuilder (128);
    Node aCurNode = aNode;
    while (aCurNode != null)
    {
      final short nNodeType = aCurNode.getNodeType ();
      if (bExcludeDocumentNode && nNodeType == Node.DOCUMENT_NODE && aRet.length () > 0)
      {
        // Avoid printing the content of the document node, if something else
        // is already present

        // Add leading separator
        aRet.insert (0, sSep);
        break;
      }

      final String sNamespaceURI = aCurNode.getNamespaceURI ();

      final StringBuilder aName = new StringBuilder ();
      if (nNodeType == Node.ATTRIBUTE_NODE)
        aName.append ('@');
      if (StringHelper.hasText (sNamespaceURI))
      {
        aName.append (funGetNSPrefix.apply (sNamespaceURI));
        aName.append (aCurNode.getLocalName ());
      }
      else
        aName.append (aCurNode.getNodeName ());

      final Node aParentNode;
      if (nNodeType == Node.ATTRIBUTE_NODE)
        aParentNode = ((Attr) aCurNode).getOwnerElement ();
      else
        aParentNode = aCurNode.getParentNode ();

      // Is there a parent node to work on?
      if (aParentNode != null)
      {
        // Attribute nodes don't have a parent node, so it is not possible to
        // construct the path
        if (nNodeType == Node.ELEMENT_NODE)
        {
          // Differentiate between root element and nested child element
          if (aParentNode.getNodeType () == Node.ELEMENT_NODE)
          {
            // get index of current element in parent element
            final Element aCurElement = (Element) aCurNode;
            int nParentChildCountWithName = 0;
            int nMatchingIndex = -1;
            for (final Element aCurParentChild : new ChildElementIterator (aParentNode))
            {
              if (EqualsHelper.identityEqual (aCurParentChild, aCurNode))
              {
                // 0-based index
                nMatchingIndex = nParentChildCountWithName;
              }

              final boolean bMatches;
              if (bCompareIncludingNamespaceURI)
                bMatches = hasSameElementName (aCurParentChild, aCurElement);
              else
                bMatches = aCurParentChild.getTagName ().equals (aCurElement.getTagName ());

              if (bMatches)
                ++nParentChildCountWithName;
            }
            if (nMatchingIndex < 0)
              throw new IllegalStateException ("Failed to find Node with name '" +
                                               aCurElement.getTagName () +
                                               "' at parent");

            if (nParentChildCountWithName > 1 || bForceUseIndex)
            {
              // Append index only, if more than one element is present
              aName.append ('[').append (bZeroBasedIndex ? nMatchingIndex : nMatchingIndex + 1).append (']');
            }
          }
          else
          {
            // This is the root element - special case
            if (bForceUseIndex)
            {
              // Append index only, if more than one element is present
              aName.append ('[').append (bZeroBasedIndex ? 0 : 1).append (']');
            }
          }
        }
      }

      if (aRet.length () > 0)
      {
        // Avoid trailing separator
        aRet.insert (0, sSep);
      }
      aRet.insert (0, aName);

      // goto parent
      aCurNode = aParentNode;
    }
    if (bTrailingSeparator && aRet.length () > 0)
      aRet.append (sSep);
    return aRet.toString ();
  }

  /**
   * Builder class for the different possibilities to get the path of a node
   *
   * @author Philip Helger
   * @since 10.2.2
   */
  @NotThreadSafe
  public static class PathToNodeBuilder implements IBuilder <String>
  {
    private Node m_aNode;
    private String m_sSeperator;
    private boolean m_bExcludeDocumentNode;
    private boolean m_bZeroBasedIndex;
    private boolean m_bForceUseIndex;
    private boolean m_bTrailingSeparator;
    private boolean m_bCompareIncludingNamespaceURI;
    private NamespaceContext m_aNamespaceCtx;

    public PathToNodeBuilder ()
    {
      separator ("/");
      excludeDocumentNode (true);
      zeroBasedIndex (false);
      forceUseIndex (false);
      trailingSeparator (false);
      compareIncludingNamespaceURI (true);
    }

    /**
     * The node to get the path from.
     *
     * @param a
     *        Node to be used. Should not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder node (@Nullable final Node a)
    {
      m_aNode = a;
      return this;
    }

    /**
     * Set the separator to be used.
     *
     * @param c
     *        The separator char
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder separator (final char c)
    {
      return separator (Character.toString (c));
    }

    /**
     * Set the separator to be used.
     *
     * @param s
     *        The separator string. Should not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder separator (@Nullable final String s)
    {
      m_sSeperator = s;
      return this;
    }

    /**
     * Determine to include or exclude the document node in the resulting path.
     *
     * @param b
     *        <code>true</code> to exclude it, <code>false</code> to include it
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder excludeDocumentNode (final boolean b)
    {
      m_bExcludeDocumentNode = b;
      return this;
    }

    /**
     * Shortcut to exclude the document Node from the resulting output
     *
     * @return this for chaining
     * @see #excludeDocumentNode(boolean)
     */
    @Nonnull
    public PathToNodeBuilder excludeDocumentNode ()
    {
      return excludeDocumentNode (true);
    }

    /**
     * Shortcut to include the document Node in the resulting output
     *
     * @return this for chaining
     * @see #excludeDocumentNode(boolean)
     */
    @Nonnull
    public PathToNodeBuilder includeDocumentNode ()
    {
      return excludeDocumentNode (false);
    }

    /**
     * Determine whether a 0-based index or a 1-based index should be used. For
     * XPath usage etc. a 1-based index should be used. For a 0-based index the
     * first element uses <code>[0]</code> and for a 1-based index this is
     * <code>[1]</code>.
     *
     * @param b
     *        <code>true</code> to use a 0-based index, <code>false</code>
     * @return this for chaining.
     */
    @Nonnull
    public PathToNodeBuilder zeroBasedIndex (final boolean b)
    {
      m_bZeroBasedIndex = b;
      return this;
    }

    /**
     * Shortcut to enable the usage of a zero based index.
     *
     * @return this for chaining
     * @see #zeroBasedIndex(boolean)
     */
    @Nonnull
    public PathToNodeBuilder zeroBasedIndex ()
    {
      return zeroBasedIndex (true);
    }

    /**
     * Shortcut to enable the usage of a one based index.
     *
     * @return this for chaining
     * @see #zeroBasedIndex(boolean)
     */
    @Nonnull
    public PathToNodeBuilder oneBasedIndex ()
    {
      return zeroBasedIndex (false);
    }

    /**
     * Enable or disable the force of an index. If the index is forced, the
     * <code>[0]</code> or <code>[1]</code>, depending on
     * {@link #zeroBasedIndex(boolean)} is always emitted, even if only one
     * element exists. If this is disabled and only element exists, the index is
     * not emitted.
     *
     * @param b
     *        <code>true</code> to force the index, <code>false</code> to omit
     *        it if possible.
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder forceUseIndex (final boolean b)
    {
      m_bForceUseIndex = b;
      return this;
    }

    /**
     * Enable or disable the usage of a trailing separator. If enabled, the
     * output is e.g. <code>element/</code> compared to the output
     * <code>element</code> if the trailing separator is disabled.
     *
     * @param b
     *        <code>true</code> to use a trailing separator, <code>false</code>
     *        to omit it.
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder trailingSeparator (final boolean b)
    {
      m_bTrailingSeparator = b;
      return this;
    }

    /**
     * Compare with namespace URI and local name, or just with the tag name.
     *
     * @param b
     *        <code>true</code> to compare with namespace URI,
     *        <code>false</code> to compare without namespace URI
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder compareIncludingNamespaceURI (final boolean b)
    {
      m_bCompareIncludingNamespaceURI = b;
      return this;
    }

    /**
     * Set the optional namespace context to be used for emitting prefixes. This
     * is optional.
     *
     * @param a
     *        The namespace context to be used. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public PathToNodeBuilder namespaceContext (@Nullable final NamespaceContext a)
    {
      m_aNamespaceCtx = a;
      return this;
    }

    @Nonnull
    public String build ()
    {
      if (m_aNode == null)
        throw new IllegalStateException ("A source Node need to be provided");

      // Empty separator makes not much sense, but who knows....
      if (m_sSeperator == null)
        throw new IllegalStateException ("A non-null separator needs to be provided");

      return _getPathToNode (m_aNode,
                             m_sSeperator,
                             m_bExcludeDocumentNode,
                             m_bZeroBasedIndex,
                             m_bForceUseIndex,
                             m_bTrailingSeparator,
                             m_bCompareIncludingNamespaceURI,
                             m_aNamespaceCtx);
    }
  }

  @Nonnull
  public static PathToNodeBuilder pathToNodeBuilder ()
  {
    return new PathToNodeBuilder ();
  }

  /**
   * Shortcut for {@link #getPathToNode(Node, String)} using "/" as the
   * separator.
   *
   * @param aNode
   *        The node to check.
   * @return A non-<code>null</code> path.
   */
  @Nonnull
  public static String getPathToNode (@Nonnull final Node aNode)
  {
    return getPathToNode (aNode, "/");
  }

  /**
   * Get the path from root node to the passed node. This includes all nodes up
   * to the document node!
   *
   * @param aNode
   *        The node to start. May not be <code>null</code>.
   * @param sSep
   *        The separator string to use. May not be <code>null</code>.
   * @return The path to the node.
   */
  @Nonnull
  public static String getPathToNode (@Nonnull final Node aNode, @Nonnull final String sSep)
  {
    return pathToNodeBuilder ().node (aNode)
                               .separator (sSep)
                               .includeDocumentNode ()
                               .zeroBasedIndex ()
                               .forceUseIndex (true)
                               .trailingSeparator (true)
                               .compareIncludingNamespaceURI (false)
                               .build ();
  }

  /**
   * Shortcut for {@link #getPathToNode2(Node,String)} using "/" as the
   * separator.
   *
   * @param aNode
   *        The node to check.
   * @return A non-<code>null</code> path.
   */
  @Nonnull
  public static String getPathToNode2 (@Nonnull final Node aNode)
  {
    return getPathToNode2 (aNode, "/");
  }

  /**
   * Get the path from root node to the passed node. This includes all nodes but
   * excluding the document node!
   *
   * @param aNode
   *        The node to start. May not be <code>null</code>.
   * @param sSep
   *        The separator string to use. May not be <code>null</code>.
   * @return The path to the node.
   */
  @Nonnull
  public static String getPathToNode2 (@Nonnull final Node aNode, @Nonnull final String sSep)
  {
    return pathToNodeBuilder ().node (aNode)
                               .separator (sSep)
                               .excludeDocumentNode ()
                               .zeroBasedIndex ()
                               .forceUseIndex (false)
                               .trailingSeparator (false)
                               .compareIncludingNamespaceURI (false)
                               .build ();
  }

  /**
   * Remove all child nodes of the given node.
   *
   * @param aElement
   *        The element whose children are to be removed.
   */
  public static void removeAllChildElements (@Nonnull final Element aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");

    while (aElement.getChildNodes ().getLength () > 0)
      aElement.removeChild (aElement.getChildNodes ().item (0));
  }

  /**
   * Get the content of the first Text child element of the passed element.
   *
   * @param aStartNode
   *        the element to scan for a TextNode child
   * @return <code>null</code> if the element contains no text node as child
   */
  @Nullable
  public static String getFirstChildText (@Nullable final Node aStartNode)
  {
    return NodeListIterator.createChildNodeIterator (aStartNode)
                           .findFirstMapped (x -> x instanceof Text && !((Text) x).isElementContentWhitespace (),
                                             x -> ((Text) x).getData ());
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns null for non existing attributes.
   *
   * @param aElement
   *        the source element to get the attribute from
   * @param sAttrName
   *        the name of the attribute to query
   * @return <code>null</code> if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValue (@Nonnull final Element aElement, @Nonnull final String sAttrName)
  {
    return getAttributeValue (aElement, sAttrName, null);
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns a default value for non existing
   * attributes.
   *
   * @param aElement
   *        the source element to get the attribute from. May not be
   *        <code>null</code>.
   * @param sAttrName
   *        the name of the attribute to query. May not be <code>null</code>.
   * @param sDefault
   *        the value to be returned if the attribute is not present.
   * @return the default value if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValue (@Nonnull final Element aElement,
                                          @Nonnull final String sAttrName,
                                          @Nullable final String sDefault)
  {
    final Attr aAttr = aElement.getAttributeNode (sAttrName);
    return aAttr == null ? sDefault : aAttr.getValue ();
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns null for non existing attributes.
   *
   * @param aElement
   *        the source element to get the attribute from
   * @param sNamespaceURI
   *        The namespace URI of the attribute to retrieve. May be
   *        <code>null</code>.
   * @param sAttrName
   *        the name of the attribute to query
   * @return <code>null</code> if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValueNS (@Nonnull final Element aElement,
                                            @Nullable final String sNamespaceURI,
                                            @Nonnull final String sAttrName)
  {
    return getAttributeValueNS (aElement, sNamespaceURI, sAttrName, null);
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns a default value for non existing
   * attributes.
   *
   * @param aElement
   *        the source element to get the attribute from. May not be
   *        <code>null</code>.
   * @param sNamespaceURI
   *        The namespace URI of the attribute to retrieve. May be
   *        <code>null</code>.
   * @param sAttrName
   *        the name of the attribute to query. May not be <code>null</code>.
   * @param sDefault
   *        the value to be returned if the attribute is not present.
   * @return the default value if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValueNS (@Nonnull final Element aElement,
                                            @Nullable final String sNamespaceURI,
                                            @Nonnull final String sAttrName,
                                            @Nullable final String sDefault)
  {
    final Attr aAttr = aElement.getAttributeNodeNS (sNamespaceURI, sAttrName);
    return aAttr == null ? sDefault : aAttr.getValue ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <Attr> getAllAttributesAsList (@Nullable final Element aSrcNode)
  {
    final ICommonsList <Attr> ret = new CommonsArrayList <> ();
    NamedNodeMapIterator.createAttributeIterator (aSrcNode).forEach (x -> ret.add ((Attr) x));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, String> getAllAttributesAsMap (@Nullable final Element aSrcNode)
  {
    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    // Cast needed for Oracle JDK 8
    forAllAttributes (aSrcNode, (BiConsumer <? super String, ? super String>) ret::put);
    return ret;
  }

  public static void forAllAttributes (@Nullable final Element aSrcNode,
                                       @Nonnull final Consumer <? super Attr> aConsumer)
  {
    NamedNodeMapIterator.createAttributeIterator (aSrcNode).forEach (x -> aConsumer.accept ((Attr) x));
  }

  public static void forAllAttributes (@Nullable final Element aSrcNode,
                                       @Nonnull final BiConsumer <? super String, ? super String> aConsumer)
  {
    forAllAttributes (aSrcNode, x -> aConsumer.accept (x.getName (), x.getValue ()));
  }

  /**
   * Get the full qualified attribute name to use for the given namespace
   * prefix. The result will e.g. be <code>xmlns</code> or
   * <code>{http://www.w3.org/2000/xmlns/}xmlns:foo</code>.
   *
   * @param sNSPrefix
   *        The namespace prefix to build the attribute name from. May be
   *        <code>null</code> or empty.
   * @return If the namespace prefix is empty (if it equals
   *         {@link XMLConstants#DEFAULT_NS_PREFIX} or <code>null</code>) than
   *         "xmlns" is returned, else "xmlns:<i>prefix</i>" is returned.
   */
  @Nonnull
  public static QName getXMLNSAttrQName (@Nullable final String sNSPrefix)
  {
    if (sNSPrefix != null)
      ValueEnforcer.isFalse (sNSPrefix.contains (CXML.XML_PREFIX_NAMESPACE_SEP_STR),
                             () -> "prefix is invalid: " + sNSPrefix);

    if (sNSPrefix == null || sNSPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
    {
      // Default (empty) namespace prefix
      return new QName (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
    }
    // Named XML namespace prefix
    return new QName (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, sNSPrefix, XMLConstants.XMLNS_ATTRIBUTE);
  }

  /**
   * Get the namespace prefix of the passed element in a safe way.
   *
   * @param aElement
   *        The element to be queried. May be <code>null</code>.
   * @return {@link XMLConstants#DEFAULT_NS_PREFIX} or the provided prefix.
   *         Never <code>null</code>.
   * @since 8.4.1
   */
  @Nonnull
  public static String getPrefix (@Nullable final Element aElement)
  {
    final String sPrefix = aElement == null ? null : aElement.getPrefix ();
    return sPrefix == null ? XMLConstants.DEFAULT_NS_PREFIX : sPrefix;
  }

  /**
   * Get the QName of the passed element. If the passed element has no namespace
   * URI, only the tag name is used. Otherwise namespace URI, local name and
   * prefix are used.
   *
   * @param aElement
   *        The element to be used. May not be <code>null</code>.
   * @return The created {@link QName}.
   * @since 8.4.1
   */
  @Nonnull
  public static QName getQName (@Nonnull final Element aElement)
  {
    final String sNamespaceURI = aElement.getNamespaceURI ();
    if (sNamespaceURI == null)
      return new QName (aElement.getTagName ());
    return new QName (sNamespaceURI, aElement.getLocalName (), getPrefix (aElement));
  }

  /**
   * Iterate all child nodes of the provided element NOT recursive. The provided
   * consumer is invoked for every child node. Please note: the Consumer is not
   * invoked for the parent element itself.
   *
   * @param aParent
   *        The parent node to start from. May not be <code>null</code>.
   * @param aConsumer
   *        The Consumer to be invoked for every node. May not be
   *        <code>null</code>.
   * @since 10.1.7
   */
  public static void iterateChildren (@Nonnull final Node aParent, @Nonnull final Consumer <? super Node> aConsumer)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    final NodeList aNodeList = aParent.getChildNodes ();
    if (aNodeList != null)
    {
      final int nChildCount = aNodeList.getLength ();
      for (int i = 0; i < nChildCount; ++i)
      {
        final Node aCurrent = aNodeList.item (i);
        aConsumer.accept (aCurrent);
      }
    }
  }

  private static void _recursiveIterateChildren (@Nonnull final Node aParent,
                                                 @Nonnull final Consumer <? super Node> aConsumer)
  {
    final NodeList aNodeList = aParent.getChildNodes ();
    if (aNodeList != null)
    {
      final int nChildCount = aNodeList.getLength ();
      for (int i = 0; i < nChildCount; ++i)
      {
        final Node aCurrent = aNodeList.item (i);
        aConsumer.accept (aCurrent);

        _recursiveIterateChildren (aCurrent, aConsumer);
      }
    }
  }

  /**
   * Recursively iterate all children of the provided element. The provided
   * consumer is invoked for every child node. Please note: the Consumer is not
   * invoked for the parent element itself.
   *
   * @param aParent
   *        The parent node to start from. May not be <code>null</code>.
   * @param aConsumer
   *        The Consumer to be invoked for every node. May not be
   *        <code>null</code>.
   * @since 10.1.7
   */
  public static void recursiveIterateChildren (@Nonnull final Node aParent,
                                               @Nonnull final Consumer <? super Node> aConsumer)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    // Use a private method to avoid the ValueEnforcer is called over and over
    // again
    _recursiveIterateChildren (aParent, aConsumer);
  }
}
