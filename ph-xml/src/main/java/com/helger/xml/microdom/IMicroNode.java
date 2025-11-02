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
package com.helger.xml.microdom;

import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.callback.CallbackList;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.hierarchy.IHasChildrenRecursive;
import com.helger.collection.hierarchy.IHasChildrenSorted;
import com.helger.collection.hierarchy.IHasParent;
import com.helger.typeconvert.impl.TypeConverter;

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
                            IHasParent <IMicroNode>
{
  /**
   * @return Just an abstract name that depends on the implementing class. For
   *         {@link IMicroElement} nodes this is the same as the tag name.
   */
  @NonNull
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
  default boolean containsAnyChild (@NonNull final Predicate <? super IMicroNode> aFilter)
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
  @NonNull
  IMicroNode detachFromParent ();

  @Nullable
  IMicroElement findParentElement (@NonNull Predicate <? super IMicroElement> aFilter);

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
   * @deprecated Use {@link #addChild(IMicroNode)} instead
   */
  @Deprecated
  @Nullable
  default <NODETYPE extends IMicroNode> NODETYPE appendChild (@Nullable final NODETYPE aChildNode)
  {
    return addChild (aChildNode);
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
  <NODETYPE extends IMicroNode> NODETYPE addChild (@Nullable NODETYPE aChildNode);

  /**
   * Append multiple children to the node at once.
   *
   * @param aChildren
   *        The child nodes to be appended. May be <code>null</code> and may
   *        contain <code>null</code> values.
   * @throws MicroException
   *         if this node cannot have children
   * @since 9.0.3
   * @deprecated Use {@link #addChildren(IMicroNode...)} instead
   */
  @Deprecated
  default void appendChildren (@Nullable final IMicroNode... aChildren)
  {
    addChildren (aChildren);
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
  default void addChildren (@Nullable final IMicroNode... aChildren)
  {
    if (aChildren != null)
      for (final IMicroNode aChild : aChildren)
        addChild (aChild);
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
   * @deprecated Use {@link #addChildren(Iterable)} instead
   */
  @Deprecated
  default void appendChildren (@Nullable final Iterable <? extends IMicroNode> aChildren)
  {
    addChildren (aChildren);
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
  default void addChildren (@Nullable final Iterable <? extends IMicroNode> aChildren)
  {
    if (aChildren != null)
      for (final IMicroNode aChild : aChildren)
        addChild (aChild);
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
  <NODETYPE extends IMicroNode> NODETYPE insertBefore (@Nullable NODETYPE aChildNode, @NonNull IMicroNode aSuccessor);

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
  <NODETYPE extends IMicroNode> NODETYPE insertAfter (@Nullable NODETYPE aChildNode, @NonNull IMicroNode aPredecessor);

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
   * @param bValue
   *        Will add <code>true</code> or <code>false</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   * @deprecated Use {@link #addText(boolean)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (final boolean bValue)
  {
    return addText (bValue);
  }

  /**
   * Append a text node to this node.
   *
   * @param bValue
   *        Will add <code>true</code> or <code>false</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   */
  @NonNull
  default IMicroText addText (final boolean bValue)
  {
    return addText (Boolean.toString (bValue));
  }

  /**
   * Append a text node to this node.
   *
   * @param nValue
   *        The number to append
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   * @deprecated Use {@link #addText(int)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (final int nValue)
  {
    return addText (nValue);
  }

  /**
   * Append a text node to this node.
   *
   * @param nValue
   *        The number to append
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   */
  @NonNull
  default IMicroText addText (final int nValue)
  {
    return addText (Integer.toString (nValue));
  }

  /**
   * Append a text node to this node.
   *
   * @param nValue
   *        The number to append
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   * @deprecated Use {@link #addText(long)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (final long nValue)
  {
    return addText (nValue);
  }

  /**
   * Append a text node to this node.
   *
   * @param nValue
   *        The number to append
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @since 11.1.11
   */
  @NonNull
  default IMicroText addText (final long nValue)
  {
    return addText (Long.toString (nValue));
  }

  /**
   * Append a text node to this node.
   *
   * @param sText
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addText(CharSequence)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (@Nullable final CharSequence sText)
  {
    return addText (sText);
  }

  /**
   * Append a text node to this node.
   *
   * @param sText
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @NonNull
  default IMicroText addText (@Nullable final CharSequence sText)
  {
    return addChild (new MicroText (sText, false));
  }

  /**
   * Append a text node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addText(char[])} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (@NonNull final char [] aChars)
  {
    return addText (aChars);
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
  @NonNull
  default IMicroText addText (@NonNull final char [] aChars)
  {
    return addText (aChars, 0, aChars.length);
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
   * @deprecated Use {@link #addText(char[],int,int)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendText (@NonNull final char [] aChars,
                                 @Nonnegative final int nOfs,
                                 @Nonnegative final int nLen)
  {
    return addText (aChars, nOfs, nLen);
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
  @NonNull
  default IMicroText addText (@NonNull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    return addChild (new MicroText (aChars, nOfs, nLen, false));
  }

  /**
   * Append a text node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addTextWithConversion(Object)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendTextWithConversion (@Nullable final Object aValue)
  {
    return addTextWithConversion (aValue);
  }

  /**
   * Append a text node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        text to be added
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @NonNull
  default IMicroText addTextWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return addText (sValue);
  }

  /**
   * Append a text node which is ignorable whitespace content to this node.
   *
   * @param sText
   *        The whitespace content to be added.
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addIgnorableWhitespaceText(CharSequence)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendIgnorableWhitespaceText (@Nullable final CharSequence sText)
  {
    return addIgnorableWhitespaceText (sText);
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
  @NonNull
  default IMicroText addIgnorableWhitespaceText (@Nullable final CharSequence sText)
  {
    return addChild (new MicroText (sText, true));
  }

  /**
   * Append a text node which is ignorable whitespace content to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created text node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addIgnorableWhitespaceText(char[])} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendIgnorableWhitespaceText (@NonNull final char [] aChars)
  {
    return addIgnorableWhitespaceText (aChars);
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
  @NonNull
  default IMicroText addIgnorableWhitespaceText (@NonNull final char [] aChars)
  {
    return addIgnorableWhitespaceText (aChars, 0, aChars.length);
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
   * @deprecated Use {@link #addIgnorableWhitespaceText(char[],int,int)} instead
   */
  @Deprecated
  @NonNull
  default IMicroText appendIgnorableWhitespaceText (@NonNull final char [] aChars,
                                                    @Nonnegative final int nOfs,
                                                    @Nonnegative final int nLen)
  {
    return addIgnorableWhitespaceText (aChars, nOfs, nLen);
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
  @NonNull
  default IMicroText addIgnorableWhitespaceText (@NonNull final char [] aChars,
                                                 @Nonnegative final int nOfs,
                                                 @Nonnegative final int nLen)
  {
    return addChild (new MicroText (aChars, nOfs, nLen, true));
  }

  /**
   * Append a CDATA node to this node.
   *
   * @param sText
   *        CDATA text
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addCDATA(CharSequence)} instead
   */
  @Deprecated
  @NonNull
  default IMicroCDATA appendCDATA (@Nullable final CharSequence sText)
  {
    return addCDATA (sText);
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
  @NonNull
  default IMicroCDATA addCDATA (@Nullable final CharSequence sText)
  {
    return addChild (new MicroCDATA (sText));
  }

  /**
   * Append a CDATA node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addCDATA(char[])} instead
   */
  @Deprecated
  @NonNull
  default IMicroCDATA appendCDATA (@NonNull final char [] aChars)
  {
    return addCDATA (aChars);
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
  @NonNull
  default IMicroCDATA addCDATA (@NonNull final char [] aChars)
  {
    return addCDATA (aChars, 0, aChars.length);
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
   * @deprecated Use {@link #addCDATA(char[],int,int)} instead
   */
  @Deprecated
  @NonNull
  default IMicroCDATA appendCDATA (@NonNull final char [] aChars,
                                   @Nonnegative final int nOfs,
                                   @Nonnegative final int nLen)
  {
    return addCDATA (aChars, nOfs, nLen);
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
  @NonNull
  default IMicroCDATA addCDATA (@NonNull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    return addChild (new MicroCDATA (aChars, nOfs, nLen));
  }

  /**
   * Append a CDATA node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        CDATA to be added
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addCDATAWithConversion(Object)} instead
   */
  @Deprecated
  @NonNull
  default IMicroCDATA appendCDATAWithConversion (@Nullable final Object aValue)
  {
    return addCDATAWithConversion (aValue);
  }

  /**
   * Append a CDATA node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        CDATA to be added
   * @return The created CDATA node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @NonNull
  default IMicroCDATA addCDATAWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return addCDATA (sValue);
  }

  /**
   * Append a comment node to this node.
   *
   * @param sText
   *        comment text
   * @return The created comment.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addComment(CharSequence)} instead
   */
  @Deprecated
  @NonNull
  default IMicroComment appendComment (@Nullable final CharSequence sText)
  {
    return addComment (sText);
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
  @NonNull
  default IMicroComment addComment (@Nullable final CharSequence sText)
  {
    return addChild (new MicroComment (sText));
  }

  /**
   * Append a comment node to this node.
   *
   * @param aChars
   *        Characters to append. May not be <code>null</code>
   * @return The created comment.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addComment(char[])} instead
   */
  @Deprecated
  @NonNull
  default IMicroComment appendComment (@NonNull final char [] aChars)
  {
    return addComment (aChars);
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
  @NonNull
  default IMicroComment addComment (@NonNull final char [] aChars)
  {
    return addComment (aChars, 0, aChars.length);
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
   * @deprecated Use {@link #addComment(char[],int,int)} instead
   */
  @Deprecated
  @NonNull
  default IMicroComment appendComment (@NonNull final char [] aChars,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen)
  {
    return addComment (aChars, nOfs, nLen);
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
  @NonNull
  default IMicroComment addComment (@NonNull final char [] aChars,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    return addChild (new MicroComment (aChars, nOfs, nLen));
  }

  /**
   * Append a comment node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        Comment to be added
   * @return The created comment node.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addCommentWithConversion(Object)} instead
   */
  @Deprecated
  @NonNull
  default IMicroComment appendCommentWithConversion (@Nullable final Object aValue)
  {
    return addCommentWithConversion (aValue);
  }

  /**
   * Append a comment node to this node. If the type of the value is not
   * {@link String}, the {@link com.helger.typeconvert.impl.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aValue
   *        Comment to be added
   * @return The created comment node.
   * @throws MicroException
   *         if this node cannot have children
   */
  @NonNull
  default IMicroComment addCommentWithConversion (@Nullable final Object aValue)
  {
    // Throws IlliegalArgumentException when no conversion is available
    final String sValue = TypeConverter.convert (aValue, String.class);
    return addComment (sValue);
  }

  /**
   * Append an entity reference to this node.
   *
   * @param sName
   *        The name of the entity reference.
   * @return The created entity reference.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addEntityReference(String)} instead
   */
  @Deprecated
  @NonNull
  default IMicroEntityReference appendEntityReference (@NonNull final String sName)
  {
    return addEntityReference (sName);
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
  @NonNull
  default IMicroEntityReference addEntityReference (@NonNull final String sName)
  {
    return addChild (new MicroEntityReference (sName));
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
   * @deprecated Use {@link #addElement(String)} instead
   */
  @Deprecated
  @NonNull
  default IMicroElement appendElement (@NonNull @Nonempty final String sTagName)
  {
    return addElement (sTagName);
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
  @NonNull
  default IMicroElement addElement (@NonNull @Nonempty final String sTagName)
  {
    return addElementNS (null, sTagName);
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
   * @deprecated Use {@link #addElementNS(String,String)} instead
   */
  @Deprecated
  @NonNull
  default IMicroElement appendElement (@Nullable final String sNamespaceURI, @NonNull @Nonempty final String sTagName)
  {
    return addElementNS (sNamespaceURI, sTagName);
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
  @NonNull
  default IMicroElement addElementNS (@Nullable final String sNamespaceURI, @NonNull @Nonempty final String sTagName)
  {
    return addChild (new MicroElement (sNamespaceURI, sTagName));
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
   * @deprecated Use {@link #addProcessingInstruction(String,String)} instead
   */
  @Deprecated
  @NonNull
  default IMicroProcessingInstruction appendProcessingInstruction (@NonNull @Nonempty final String sTarget,
                                                                   @Nullable final String sData)
  {
    return addProcessingInstruction (sTarget, sData);
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
  @NonNull
  default IMicroProcessingInstruction addProcessingInstruction (@NonNull @Nonempty final String sTarget,
                                                                @Nullable final String sData)
  {
    return addChild (new MicroProcessingInstruction (sTarget, sData));
  }

  /**
   * Append a new container to this node
   *
   * @return The created container.
   * @throws MicroException
   *         if this node cannot have children
   * @deprecated Use {@link #addContainer()} instead
   */
  @Deprecated
  @NonNull
  default IMicroContainer appendContainer ()
  {
    return addContainer ();
  }

  /**
   * Append a new container to this node
   *
   * @return The created container.
   * @throws MicroException
   *         if this node cannot have children
   */
  @NonNull
  default IMicroContainer addContainer ()
  {
    return addChild (new MicroContainer ());
  }

  /**
   * Remove the passed child.
   *
   * @param aChild
   *        The child to be removed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was successfully removed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
  EChange removeChild (@NonNull IMicroNode aChild);

  /**
   * Remove the child not at the specified index.
   *
   * @param nIndex
   *        The 0-based index of the item to be removed.
   * @return {@link EChange#CHANGED} if the node was successfully removed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
  EChange removeChildAtIndex (@Nonnegative int nIndex);

  /**
   * Remove all children from this node.
   *
   * @return {@link EChange#CHANGED} if at least one child was present, and was
   *         successfully removed, {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
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
  @NonNull
  default EChange replaceChild (@NonNull final IMicroNode aOldChild, @NonNull final IMicroNode aNewChild)
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
  @NonNull
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
  @NonNull
  EChange registerEventTarget (@NonNull EMicroEvent eEventType, @NonNull IMicroEventTarget aTarget);

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
  @NonNull
  EChange unregisterEventTarget (@NonNull EMicroEvent eEventType, @NonNull IMicroEventTarget aTarget);

  /**
   * @return A map of all registered event targets. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsMap <EMicroEvent, CallbackList <IMicroEventTarget>> getAllEventTargets ();

  /**
   * Get all event targets for a certain event.
   *
   * @param eEvent
   *        The event to be queried. May be <code>null</code>.
   * @return A map of all registered event targets. Never <code>null</code>.
   */
  @NonNull
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
