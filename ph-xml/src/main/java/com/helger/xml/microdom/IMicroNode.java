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
package com.helger.xml.microdom;

import java.io.Serializable;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.hierarchy.IHasChildrenRecursive;
import com.helger.commons.hierarchy.IHasChildrenSorted;
import com.helger.commons.hierarchy.IHasParent;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * This is the base interface for all kind of nodes in the micro document object
 * model.
 *
 * @author Philip Helger
 */
public interface IMicroNode extends
                            ICloneable <IMicroNode>,
                            IHasChildrenSorted <IMicroNode>,
                            IHasChildrenRecursive <IMicroNode>,
                            IHasParent <IMicroNode>,
                            Serializable
{
  /**
   * @return Just an abstract name that depends on the implementing class. For
   *         {@link IMicroElement} nodes this is the same as the tag name.
   */
  @Nonnull
  @Nonempty
  String getNodeName ();

  /**
   * @return The value of this node. This depends on the concrete implementation
   *         class. It is currently implemented for {@link IMicroText},
   *         {@link IMicroComment} and {@link IMicroEntityReference}.
   */
  default String getNodeValue ()
  {
    return "";
  }

  /**
   * Get a list of all direct child nodes.
   *
   * @return May be <code>null</code> if the node has no children.
   */
  @Nullable
  ICommonsList <IMicroNode> getAllChildren ();

  /**
   * Check if any direct child matching the provided filter is contained.
   *
   * @param aFilter
   *        The filter that is applied to all child nodes. May not be
   *        <code>null</code>.
   * @return <code>true</code> if any child matching the provided filter is
   *         contained, <code>false</code> otherwise.
   */
  default boolean containsAnyChild (@Nonnull final Predicate <? super IMicroNode> aFilter)
  {
    ValueEnforcer.notNull (aFilter, "Filter");
    if (hasNoChildren ())
      return false;
    return getAllChildren ().containsAny (aFilter);
  }

  /**
   * @return The first child node of this node, or <code>null</code> if this
   *         node has no children.
   */
  @Override
  @Nullable
  IMicroNode getFirstChild ();

  /**
   * @return The last child node of this node, or <code>null</code> if this node
   *         has no children.
   */
  @Override
  @Nullable
  IMicroNode getLastChild ();

  /**
   * Recursively get all children. Micro container are contained in this list
   * (incl. their children of course)
   *
   * @return A list containing all recursively contained child elements. May be
   *         <code>null</code> if this node has no children.
   */
  @Nullable
  default ICommonsList <IMicroNode> getAllChildrenRecursive ()
  {
    if (hasNoChildren ())
      return null;

    final ICommonsList <IMicroNode> ret = new CommonsArrayList <> ();
    forAllChildrenRecursive (ret::add);
    return ret;
  }

  /**
   * @return The previous node on the same level as this node, or
   *         <code>null</code> if this node has no preceding siblings.
   */
  @Nullable
  IMicroNode getPreviousSibling ();

  /**
   * @return The next node on the same level as this node, or <code>null</code>
   *         if this node has no succeeding siblings.
   */
  @Nullable
  IMicroNode getNextSibling ();

  /**
   * @return <code>true</code> if this node has a parent node assigned,
   *         <code>false</code> otherwise.
   */
  @Override
  boolean hasParent ();

  /**
   * @return May be <code>null</code>.
   */
  @Nullable
  IMicroNode getParent ();

  /**
   * Detach this node from the parent node so it can be inserted into another
   * node without problems. Otherwise you would get an
   * {@link IllegalStateException} if adding this node again to another parent
   * since each node can only have one parent.
   *
   * @return this
   */
  @Nonnull
  IMicroNode detachFromParent ();

  @Nullable
  IMicroElement findParentElement (@Nonnull Predicate <? super IMicroElement> aFilter);

  @Nullable
  default IMicroElement getParentElementWithName (@Nullable final String sTagName)
  {
    return findParentElement (x -> x.getTagName ().equals (sTagName));
  }

  @Nullable
  default IMicroElement getParentElementWithName (@Nullable final String sNamespaceURI, @Nullable final String sTagName)
  {
    return findParentElement (x -> x.hasNamespaceURI (sNamespaceURI) && x.getTagName ().equals (sTagName));
  }

  /**
   * Append any child to the node.
   *
   * @param <NODETYPE>
   *        Parameter type == return type
   * @param aChildNode
   *        The child node to append. May be <code>null</code>.
   * @return The appended node, or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nullable
  <NODETYPE extends IMicroNode> NODETYPE appendChild (@Nullable NODETYPE aChildNode);

  /**
   * Append multiple children to the node at once.
   *
   * @param aChildren
   *        The child nodes to be appended. May be <code>null</code> and may
   *        contain <code>null</code> values.
   * @throws MicroException
   *         if this node cannot have children
   * @since 9.0.3
   */
  default void appendChildren (@Nullable final IMicroNode... aChildren)
  {
    if (aChildren != null)
      for (final IMicroNode aChild : aChildren)
        appendChild (aChild);
  }

  /**
   * Append multiple children to the node at once.
   *
   * @param aChildren
   *        The child nodes to be appended. May be <code>null</code> and may
   *        contain <code>null</code> values.
   * @throws MicroException
   *         if this node cannot have children
   * @since 9.0.3
   */
  default void appendChildren (@Nullable final Iterable <? extends IMicroNode> aChildren)
  {
    if (aChildren != null)
      for (final IMicroNode aChild : aChildren)
        appendChild (aChild);
  }

  /**
   * Insert an existing node before a certain child node of this.
   *
   * @param <NODETYPE>
   *        Parameter type == return type
   * @param aChildNode
   *        The new child node to be inserted.
   * @param aSuccessor
   *        The node before which the new node will be inserted.
   * @return The newly inserted node
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nullable
  <NODETYPE extends IMicroNode> NODETYPE insertBefore (@Nullable NODETYPE aChildNode, @Nonnull IMicroNode aSuccessor);

  /**
   * Insert an existing node after a certain child node of this.
   *
   * @param <NODETYPE>
   *        Parameter type == return type
   * @param aChildNode
   *        The new child node to be inserted.
   * @param aPredecessor
   *        The node after which the new node will be inserted.
   * @return The newly inserted node
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nullable
  <NODETYPE extends IMicroNode> NODETYPE insertAfter (@Nullable NODETYPE aChildNode, @Nonnull IMicroNode aPredecessor);

  /**
   * Insert an existing node as a child at the specified index.
   *
   * @param <NODETYPE>
   *        Parameter type == return type
   * @param nIndex
   *        The index to insert. Must be &ge; 0.
   * @param aChildNode
   *        The new child node to be inserted.
   * @return The newly inserted node
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nullable
  <NODETYPE extends IMicroNode> NODETYPE insertAtIndex (@Nonnegative int nIndex, @Nullable NODETYPE aChildNode);

  /**
   * Append a text node to this node.
   *
   * @param sText
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendText (@Nullable final CharSequence sText)
  {
    return appendChild (new MicroText (sText, false));
  }

  /**
   * Append a text node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendText (@Nonnull final char [] aChars)
  {
    return appendText (aChars, 0, aChars.length);
  }

  /**
   * Append a text node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @param nOfs
   *        Offset into the array where to start copying data. May not be &lt;
   *        0.
   * @param nLen
   *        Number of bytes to take from the array. May not be &lt; 0.
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendText (@Nonnull final char [] aChars,
                                 @Nonnegative final int nOfs,
                                 @Nonnegative final int nLen)
  {
    return appendChild (new MicroText (aChars, nOfs, nLen, false));
  }

  /**
   * Append a text node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendTextWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return appendText (sValue);
  }

  /**
   * Append a text node which is ignorable whitespace content to this node.
   *
   * @param sText
   *        The whitespace content to be added.
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendIgnorableWhitespaceText (@Nullable final CharSequence sText)
  {
    return appendChild (new MicroText (sText, true));
  }

  /**
   * Append a text node which is ignorable whitespace content to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendIgnorableWhitespaceText (@Nonnull final char [] aChars)
  {
    return appendIgnorableWhitespaceText (aChars, 0, aChars.length);
  }

  /**
   * Append a text node which is ignorable whitespace content to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @param nOfs
   *        Offset into the array where to start copying data. May not be &lt;
   *        0.
   * @param nLen
   *        Number of bytes to take from the array. May not be &lt; 0.
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroText appendIgnorableWhitespaceText (@Nonnull final char [] aChars,
                                                    @Nonnegative final int nOfs,
                                                    @Nonnegative final int nLen)
  {
    return appendChild (new MicroText (aChars, nOfs, nLen, true));
  }

  /**
   * Append a CDATA node to this node.
   *
   * @param sText
   *        CDATA text
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroCDATA appendCDATA (@Nullable final CharSequence sText)
  {
    return appendChild (new MicroCDATA (sText));
  }

  /**
   * Append a CDATA node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroCDATA appendCDATA (@Nonnull final char [] aChars)
  {
    return appendCDATA (aChars, 0, aChars.length);
  }

  /**
   * Append a CDATA node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @param nOfs
   *        Offset into the array where to start copying data. May not be &lt;
   *        0.
   * @param nLen
   *        Number of bytes to take from the array. May not be &lt; 0.
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroCDATA appendCDATA (@Nonnull final char [] aChars,
                                   @Nonnegative final int nOfs,
                                   @Nonnegative final int nLen)
  {
    return appendChild (new MicroCDATA (aChars, nOfs, nLen));
  }

  /**
   * Append a CDATA node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        CDATA to be added
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroCDATA appendCDATAWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return appendCDATA (sValue);
  }

  /**
   * Append a comment node to this node.
   *
   * @param sText
   *        comment text
   * @return The created comment.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroComment appendComment (@Nullable final CharSequence sText)
  {
    return appendChild (new MicroComment (sText));
  }

  /**
   * Append a comment node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created comment.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroComment appendComment (@Nonnull final char [] aChars)
  {
    return appendComment (aChars, 0, aChars.length);
  }

  /**
   * Append a comment node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @param nOfs
   *        Offset into the array where to start copying data. May not be &lt;
   *        0.
   * @param nLen
   *        Number of bytes to take from the array. May not be &lt; 0.
   * @return The created comment.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroComment appendComment (@Nonnull final char [] aChars,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen)
  {
    return appendChild (new MicroComment (aChars, nOfs, nLen));
  }

  /**
   * Append a comment node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        Comment to be added
   * @return The created comment node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroComment appendCommentWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return appendComment (sValue);
  }

  /**
   * Append an entity reference to this node.
   *
   * @param sName
   *        The name of the entity reference.
   * @return The created entity reference.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroEntityReference appendEntityReference (@Nonnull final String sName)
  {
    return appendChild (new MicroEntityReference (sName));
  }

  /**
   * Append an element without namespace to this node.
   *
   * @param sTagName
   *        Element name to be created. May neither be <code>null</code> nor
   *        empty.
   * @return The created element
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroElement appendElement (@Nonnull @Nonempty final String sTagName)
  {
    return appendElement (null, sTagName);
  }

  /**
   * Append an element with namespace to this node.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sTagName
   *        Element name to be created. May neither be <code>null</code> nor
   *        empty.
   * @return The created element
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroElement appendElement (@Nullable final String sNamespaceURI, @Nonnull @Nonempty final String sTagName)
  {
    return appendChild (new MicroElement (sNamespaceURI, sTagName));
  }

  /**
   * Append a processing instruction to this node.
   *
   * @param sTarget
   *        The PI target
   * @param sData
   *        The PI data
   * @return The created element
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroProcessingInstruction appendProcessingInstruction (@Nonnull @Nonempty final String sTarget,
                                                                   @Nullable final String sData)
  {
    return appendChild (new MicroProcessingInstruction (sTarget, sData));
  }

  /**
   * Append a new container to this node
   *
   * @return The created container.
   * @throws MicroException
   *         if this node cannot have children
   */
  @Nonnull
  default IMicroContainer appendContainer ()
  {
    return appendChild (new MicroContainer ());
  }

  /**
   * Remove the passed child.
   *
   * @param aChild
   *        The child to be removed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was successfully removed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeChild (@Nonnull IMicroNode aChild);

  /**
   * Remove the child not at the specified index.
   *
   * @param nIndex
   *        The 0-based index of the item to be removed.
   * @return {@link EChange#CHANGED} if the node was successfully removed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeChildAtIndex (@Nonnegative int nIndex);

  /**
   * Remove all children from this node.
   *
   * @return {@link EChange#CHANGED} if at least one child was present, and was
   *         successfully removed, {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeAllChildren ();

  /**
   * Replace the passed old child with the new child.
   *
   * @param aOldChild
   *        The child to be removed. May not be <code>null</code>.
   * @param aNewChild
   *        The child to be inserted instead. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was successfully replaced,
   *         {@link EChange#UNCHANGED} if old child and new child are identical.
   */
  @Nonnull
  default EChange replaceChild (@Nonnull final IMicroNode aOldChild, @Nonnull final IMicroNode aNewChild)
  {
    ValueEnforcer.notNull (aOldChild, "OldChild");
    ValueEnforcer.notNull (aNewChild, "NewChild");

    if (aOldChild.equals (aNewChild))
      return EChange.UNCHANGED;
    insertBefore (aNewChild, aOldChild);
    removeChild (aOldChild);
    return EChange.CHANGED;
  }

  /**
   * @return The node type. Never <code>null</code>.
   */
  @Nonnull
  EMicroNodeType getType ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroDocument}.
   */
  boolean isDocument ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroDocumentType}.
   */
  boolean isDocumentType ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroText}.
   */
  boolean isText ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroCDATA}.
   */
  boolean isCDATA ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroComment}.
   */
  boolean isComment ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroEntityReference}.
   */
  boolean isEntityReference ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroElement}.
   */
  boolean isElement ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroProcessingInstruction}.
   */
  boolean isProcessingInstruction ();

  /**
   * @return <code>true</code> if this node can safely be casted to
   *         {@link IMicroContainer}.
   */
  boolean isContainer ();

  /**
   * Register a specific MicroDOM event listener. One event listener can only be
   * attached once to an event!
   *
   * @param eEventType
   *        The event type. May not be <code>null</code>.
   * @param aTarget
   *        The event target to be added. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the event listener was registered,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange registerEventTarget (@Nonnull EMicroEvent eEventType, @Nonnull IMicroEventTarget aTarget);

  /**
   * Unregister a specific MicroDOM event listener.
   *
   * @param eEventType
   *        The event type. May not be <code>null</code>.
   * @param aTarget
   *        The event target to be added. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the event listener was unregistered,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange unregisterEventTarget (@Nonnull EMicroEvent eEventType, @Nonnull IMicroEventTarget aTarget);

  /**
   * @return A map of all registered event targets. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <EMicroEvent, CallbackList <IMicroEventTarget>> getAllEventTargets ();

  /**
   * Get all event targets for a certain event.
   *
   * @param eEvent
   *        The event to be queried. May be <code>null</code>.
   * @return A map of all registered event targets. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  CallbackList <IMicroEventTarget> getAllEventTargets (@Nullable EMicroEvent eEvent);

  /**
   * As instances of this class may not implement equals/hashCode we need a way
   * to determine, if 2 nodes are equal by content.
   *
   * @param aNode
   *        The node to compare to this.
   * @return <code>true</code> if the nodes are of the same type and the same
   *         content, <code>false</code> otherwise.
   */
  boolean isEqualContent (@Nullable IMicroNode aNode);
}
