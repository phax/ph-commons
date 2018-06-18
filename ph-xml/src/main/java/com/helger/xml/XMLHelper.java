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
package com.helger.xml;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.functional.IPredicate;
import com.helger.commons.string.StringHelper;

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
  public static IPredicate <? super Node> filterNodeIsElement ()
  {
    return x -> x != null && x.getNodeType () == Node.ELEMENT_NODE;
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithNamespace ()
  {
    return x -> x != null && StringHelper.hasText (x.getNamespaceURI ());
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithoutNamespace ()
  {
    return x -> x != null && hasNoNamespaceURI (x);
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithNamespace (@Nullable final String sNamespaceURI)
  {
    return x -> x != null && hasNamespaceURI (x, sNamespaceURI);
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithNamespaceAndLocalName (@Nullable final String sNamespaceURI,
                                                                                     @Nonnull @Nonempty final String sLocalName)
  {
    ValueEnforcer.notEmpty (sLocalName, "LocalName");
    return x -> x != null && hasNamespaceURI (x, sNamespaceURI) && x.getLocalName ().equals (sLocalName);
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithTagName (@Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");
    return x -> EqualsHelper.equals (getElementName (x), sTagName);
  }

  @Nonnull
  public static IPredicate <? super Element> filterElementWithTagNameNoNS (@Nonnull @Nonempty final String sTagName)
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
    return NodeListIterator.createChildNodeIterator (aStartNode).findFirstMapped (filterNodeIsElement (),
                                                                                  x -> (Element) x);
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
          if (EqualsHelper.identityEqual (x, aCurNode))
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

      // Attribute nodes don't have a parent node, so it is not possible to
      // construct the path
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
          if (EqualsHelper.identityEqual (x, aCurNode))
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
    if (sNSPrefix != null && sNSPrefix.contains (CXML.XML_PREFIX_NAMESPACE_SEP_STR))
      throw new IllegalArgumentException ("prefix is invalid: " + sNSPrefix);
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
}
