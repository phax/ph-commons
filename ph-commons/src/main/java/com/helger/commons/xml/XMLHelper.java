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
package com.helger.commons.xml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.IIterableIterator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class contains multiple XML utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLHelper
{
  @PresentForCodeCoverage
  private static final XMLHelper s_aInstance = new XMLHelper ();

  private XMLHelper ()
  {}

  /**
   * Get the first direct child element of the passed element.
   *
   * @param aStartNode
   *        The element to start searching.
   * @return <code>null</code> if the passed element does not have any direct
   *         child element.
   */
  @Nullable
  public static Element getFirstChildElement (@Nonnull final Node aStartNode)
  {
    final NodeList aNodeList = aStartNode.getChildNodes ();
    final int nLen = aNodeList.getLength ();
    for (int i = 0; i < nLen; ++i)
    {
      final Node aNode = aNodeList.item (i);
      if (aNode.getNodeType () == Node.ELEMENT_NODE)
        return (Element) aNode;
    }
    return null;
  }

  /**
   * Check if the passed node has at least one direct child element or not.
   *
   * @param aStartNode
   *        The parent element to be searched. May not be <code>null</code>.
   * @return <code>true</code> if the passed node has at least one child
   *         element, <code>false</code> otherwise.
   */
  public static boolean hasChildElementNodes (@Nonnull final Node aStartNode)
  {
    return getFirstChildElement (aStartNode) != null;
  }

  /**
   * Search all child nodes of the given for the first element that has the
   * specified tag name.
   *
   * @param aStartNode
   *        The parent element to be searched. May not be <code>null</code>.
   * @param sName
   *        The tag name to search.
   * @return <code>null</code> if the parent element has no such child element.
   */
  @Nullable
  public static Element getFirstChildElementOfName (@Nonnull final Node aStartNode, @Nullable final String sName)
  {
    final NodeList aNodeList = aStartNode.getChildNodes ();
    final int nLen = aNodeList.getLength ();
    for (int i = 0; i < nLen; ++i)
    {
      final Node aNode = aNodeList.item (i);
      if (aNode.getNodeType () == Node.ELEMENT_NODE)
      {
        final Element aElement = (Element) aNode;
        if (aElement.getTagName ().equals (sName))
          return aElement;
      }
    }
    return null;
  }

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
    return aNode == null ? null : aNode instanceof Document ? (Document) aNode : aNode.getOwnerDocument ();
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

  public static void append (@Nonnull final Node aSrcNode, @Nonnull final Collection <?> aNodesToAppend)
  {
    for (final Object aNode : aNodesToAppend)
      append (aSrcNode, aNode);
  }

  @Nonnegative
  public static int getDirectChildElementCountNoNS (@Nullable final Element aParent)
  {
    return aParent == null ? 0 : CollectionHelper.getSize (getChildElementIteratorNoNS (aParent));
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
   * Get an iterator over all child elements that have no namespace.
   *
   * @param aStartNode
   *        the parent element
   * @return a non-null Iterator
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nonnull final Node aStartNode)
  {
    return new ChildElementIterator (aStartNode, new FilterElementWithoutNamespace ());
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
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nonnull final Node aStartNode,
                                                                         @Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");

    return new ChildElementIterator (aStartNode, new FilterElementWithTagName (sTagName));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nonnull final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI)
  {
    return new ChildElementIterator (aStartNode, new FilterElementWithNamespace (sNamespaceURI));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nonnull final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI,
                                                                       @Nonnull @Nonempty final String sLocalName)
  {
    ValueEnforcer.notEmpty (sLocalName, "LocalName");

    return new ChildElementIterator (aStartNode,
                                     new FilterElementWithNamespaceAndLocalName (sNamespaceURI, sLocalName));
  }

  public static boolean hasNamespaceURI (@Nullable final Node aNode, @Nullable final String sNamespaceURI)
  {
    final String sNSURI = aNode == null ? null : aNode.getNamespaceURI ();
    return sNSURI != null && sNSURI.equals (sNamespaceURI);
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
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public static String getPathToNode (@Nonnull final Node aNode, @Nonnull final String sSep)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (sSep, "Separator");

    final StringBuilder aRet = new StringBuilder ();
    Node aCurNode = aNode;
    while (aCurNode != null)
    {
      final StringBuilder aName = new StringBuilder (aCurNode.getNodeName ());
      if (aCurNode.getNodeType () == Node.ELEMENT_NODE && aCurNode.getParentNode () != null)
      {
        // get index of my current element
        final Element aCurElement = (Element) aCurNode;
        int nIndex = 0;
        // For all elements of the parent node
        for (final Element x : new ChildElementIterator (aCurNode.getParentNode ()))
        {
          if (x == aCurNode)
            break;
          if (x.getTagName ().equals (aCurElement.getTagName ()))
            ++nIndex;
        }
        aName.append ('[').append (nIndex).append (']');
      }

      aRet.insert (0, sSep).insert (0, aName);

      // goto parent
      aCurNode = aCurNode.getParentNode ();
    }
    return aRet.toString ();
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
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (sSep, "Separator");

    final StringBuilder aRet = new StringBuilder ();
    Node aCurNode = aNode;
    while (aCurNode != null)
    {
      if (aCurNode.getNodeType () == Node.DOCUMENT_NODE && aRet.length () > 0)
      {
        // Avoid printing the content of the document node, if something else is
        // already present

        // Add leading separator
        aRet.insert (0, sSep);
        break;
      }

      final StringBuilder aName = new StringBuilder (aCurNode.getNodeName ());

      if (aCurNode.getNodeType () == Node.ELEMENT_NODE &&
          aCurNode.getParentNode () != null &&
          aCurNode.getParentNode ().getNodeType () == Node.ELEMENT_NODE)
      {
        // get index of current element in parent element
        final Element aCurElement = (Element) aCurNode;
        int nIndex = 0;
        int nMatchingIndex = -1;
        for (final Element x : new ChildElementIterator (aCurNode.getParentNode ()))
        {
          if (x == aCurNode)
            nMatchingIndex = nIndex;

          if (x.getTagName ().equals (aCurElement.getTagName ()))
            ++nIndex;
        }
        if (nMatchingIndex < 0)
          throw new IllegalStateException ("Failed to find Node at parent");
        if (nIndex > 1)
        {
          // Append index only, if more than one element is present
          aName.append ('[').append (nMatchingIndex).append (']');
        }
      }

      if (aRet.length () > 0)
      {
        // Avoid trailing separator
        aRet.insert (0, sSep);
      }
      aRet.insert (0, aName);

      // goto parent
      aCurNode = aCurNode.getParentNode ();
    }
    return aRet.toString ();
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
    if (aStartNode != null)
    {
      final NodeList aNodeList = aStartNode.getChildNodes ();
      final int nLen = aNodeList.getLength ();
      for (int i = 0; i < nLen; ++i)
      {
        final Node aNode = aNodeList.item (i);
        if (aNode instanceof Text)
        {
          final Text aText = (Text) aNode;

          // ignore whitespace-only content
          if (!aText.isElementContentWhitespace ())
            return aText.getData ();
        }
      }
    }
    return null;
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

  @Nullable
  @ReturnsMutableCopy
  public static Map <String, String> getAllAttributesAsMap (@Nullable final Element aSrcNode)
  {
    if (aSrcNode != null)
    {
      final NamedNodeMap aNNM = aSrcNode.getAttributes ();
      if (aNNM != null)
      {
        final Map <String, String> aMap = new LinkedHashMap <String, String> (aNNM.getLength ());
        final int nMax = aNNM.getLength ();
        for (int i = 0; i < nMax; ++i)
        {
          final Attr aAttr = (Attr) aNNM.item (i);
          aMap.put (aAttr.getName (), aAttr.getValue ());
        }
        return aMap;
      }
    }
    return null;
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
    if (sNSPrefix != null && sNSPrefix.contains (CXML.XML_PREFIX_NAMESPACE_SEP_STR))
      throw new IllegalArgumentException ("prefix is invalid: " + sNSPrefix);
    if (sNSPrefix == null || sNSPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
    {
      // Default (empty) namespace prefix
      return new QName (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, CXML.XML_ATTR_XMLNS);
    }
    // Named XML namespace prefix
    return new QName (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, sNSPrefix, CXML.XML_ATTR_XMLNS);
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
      String ret = aNode.getLocalName ();
      if (ret == null)
        ret = ((Element) aNode).getTagName ();
      return ret;
    }
    return null;
  }

  @Nonnegative
  public static int getLength (@Nullable final NodeList aNL)
  {
    return aNL == null ? 0 : aNL.getLength ();
  }

  public static boolean isEmpty (@Nullable final NodeList aNL)
  {
    return aNL == null ? true : aNL.getLength () == 0;
  }
}
