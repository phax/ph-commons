/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.callback.CallbackList;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;

/**
 * This is an abstract base class for the micro document object model. It implements a set of common
 * methods required for all object types. Especially for the parent/child handling, the sub class
 * AbstractMicroNodeWithChildren provides some additional features.
 *
 * @author Philip Helger
 */
public abstract class AbstractMicroNode implements IMicroNode
{
  /** The parent node of this node. */
  private AbstractMicroNodeWithChildren m_aParentNode;
  private CommonsEnumMap <EMicroEvent, CallbackList <IMicroEventTarget>> m_aEventTargets;

  /**
   * Callback that is invoked once a child is to be appended.
   *
   * @param aChildNode
   *        The appended child node.
   */
  @OverrideOnDemand
  protected void onAddChild (@NonNull final AbstractMicroNode aChildNode)
  {
    throw new MicroException ("Cannot add children in class " + getClass ().getName ());
  }

  /**
   * Callback that is invoked once a child is to be inserted before another child.
   *
   * @param aChildNode
   *        The new child node to be inserted.
   * @param aSuccessor
   *        The node before which the new node will be inserted.
   */
  @OverrideOnDemand
  protected void onInsertBefore (@NonNull final AbstractMicroNode aChildNode, @NonNull final IMicroNode aSuccessor)
  {
    throw new MicroException ("Cannot insert children in class " + getClass ().getName ());
  }

  /**
   * Callback that is invoked once a child is to be inserted after another child.
   *
   * @param aChildNode
   *        The new child node to be inserted.
   * @param aPredecessor
   *        The node after which the new node will be inserted.
   */
  @OverrideOnDemand
  protected void onInsertAfter (@NonNull final AbstractMicroNode aChildNode, @NonNull final IMicroNode aPredecessor)
  {
    throw new MicroException ("Cannot insert children in class " + getClass ().getName ());
  }

  /**
   * Callback that is invoked once a child is to be inserted at the specified index.
   *
   * @param nIndex
   *        The index where the node should be inserted.
   * @param aChildNode
   *        The new child node to be inserted.
   */
  @OverrideOnDemand
  protected void onInsertAtIndex (@Nonnegative final int nIndex, @NonNull final AbstractMicroNode aChildNode)
  {
    throw new MicroException ("Cannot insert children in class " + getClass ().getName ());
  }

  @Nullable
  public final <NODETYPE extends IMicroNode> NODETYPE addChild (@Nullable final NODETYPE aChildNode)
  {
    if (aChildNode != null)
      onAddChild ((AbstractMicroNode) aChildNode);
    return aChildNode;
  }

  @Nullable
  public final <NODETYPE extends IMicroNode> NODETYPE insertBefore (@Nullable final NODETYPE aChildNode,
                                                                    @NonNull final IMicroNode aSuccessor)
  {
    if (aChildNode != null)
      onInsertBefore ((AbstractMicroNode) aChildNode, aSuccessor);
    return aChildNode;
  }

  @Nullable
  public final <NODETYPE extends IMicroNode> NODETYPE insertAfter (@Nullable final NODETYPE aChildNode,
                                                                   @NonNull final IMicroNode aPredecessor)
  {
    if (aChildNode != null)
      onInsertAfter ((AbstractMicroNode) aChildNode, aPredecessor);
    return aChildNode;
  }

  @Nullable
  public final <NODETYPE extends IMicroNode> NODETYPE insertAtIndex (@Nonnegative final int nIndex,
                                                                     @Nullable final NODETYPE aChildNode)
  {
    if (aChildNode != null)
      onInsertAtIndex (nIndex, (AbstractMicroNode) aChildNode);
    return aChildNode;
  }

  /**
   * Callback when a child is removed.
   *
   * @param aChild
   *        The child that is removed.
   * @return {@link EChange#CHANGED} if something changed
   */
  @OverrideOnDemand
  @NonNull
  protected EChange onRemoveChild (final IMicroNode aChild)
  {
    throw new MicroException ("Cannot remove child from this node: " + getClass ().getName ());
  }

  @NonNull
  public final EChange removeChild (@NonNull final IMicroNode aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");
    return onRemoveChild (aChild);
  }

  /**
   * Remove the child not at the specified index.
   *
   * @param nIndex
   *        The 0-based index of the item to be removed.
   * @return {@link EChange#CHANGED} if the node was successfully removed, {@link EChange#UNCHANGED}
   *         otherwise.
   */
  @OverrideOnDemand
  @NonNull
  protected EChange onRemoveChildAtIndex (final int nIndex)
  {
    throw new MicroException ("Cannot remove child from this node: " + getClass ().getName ());
  }

  @NonNull
  public final EChange removeChildAtIndex (@Nonnegative final int nIndex)
  {
    return onRemoveChildAtIndex (nIndex);
  }

  /**
   * Remove all children from this node.
   *
   * @return {@link EChange#CHANGED} if at least one child was present, and was successfully
   *         removed, {@link EChange#UNCHANGED} otherwise.
   */
  @OverrideOnDemand
  @NonNull
  protected EChange onRemoveAllChildren ()
  {
    throw new MicroException ("Cannot remove all children from this node: " + getClass ().getName ());
  }

  @NonNull
  public final EChange removeAllChildren ()
  {
    return onRemoveAllChildren ();
  }

  @Override
  @OverrideOnDemand
  public boolean hasChildren ()
  {
    return false;
  }

  @OverrideOnDemand
  @Nullable
  public ICommonsList <IMicroNode> getAllChildren ()
  {
    return null;
  }

  @OverrideOnDemand
  @Nullable
  public ICommonsIterable <IMicroNode> getChildren ()
  {
    return null;
  }

  @Override
  public void forAllChildren (@NonNull final Consumer <? super IMicroNode> aConsumer)
  {
    // empty
  }

  @Override
  @NonNull
  public EContinue forAllChildrenBreakable (@NonNull final Function <? super IMicroNode, EContinue> aConsumer)
  {
    return EContinue.CONTINUE;
  }

  @Override
  public void forAllChildren (@NonNull final Predicate <? super IMicroNode> aFilter,
                              @NonNull final Consumer <? super IMicroNode> aConsumer)
  {
    // empty
  }

  @Override
  public <DSTTYPE> void forAllChildrenMapped (@NonNull final Predicate <? super IMicroNode> aFilter,
                                              @NonNull final Function <? super IMicroNode, ? extends DSTTYPE> aMapper,
                                              @NonNull final Consumer <? super DSTTYPE> aConsumer)
  {
    // empty
  }

  @OverrideOnDemand
  @Nullable
  public IMicroNode getChildAtIndex (@Nonnegative final int nIndex)
  {
    return null;
  }

  @OverrideOnDemand
  @Nonnegative
  public int getChildCount ()
  {
    return 0;
  }

  @OverrideOnDemand
  @Nullable
  public IMicroNode getFirstChild ()
  {
    return null;
  }

  @Override
  @Nullable
  public IMicroNode findFirstChild (@NonNull final Predicate <? super IMicroNode> aFilter)
  {
    return null;
  }

  @Override
  @Nullable
  public <DSTTYPE> DSTTYPE findFirstChildMapped (@NonNull final Predicate <? super IMicroNode> aFilter,
                                                 @NonNull final Function <? super IMicroNode, ? extends DSTTYPE> aMapper)
  {
    return null;
  }

  @OverrideOnDemand
  @Nullable
  public IMicroNode getLastChild ()
  {
    return null;
  }

  @Nullable
  public final IMicroNode getPreviousSibling ()
  {
    if (m_aParentNode == null)
      return null;
    final ICommonsList <IMicroNode> aParentChildren = m_aParentNode.directGetAllChildren ();
    final int nIndex = aParentChildren.indexOf (this);
    if (nIndex == -1)
      throw new IllegalStateException ("this is no part of it's parents children");

    // getAtIndex handles index underflow
    return aParentChildren.getAtIndex (nIndex - 1);
  }

  @Nullable
  public final IMicroNode getNextSibling ()
  {
    if (m_aParentNode == null)
      return null;
    final ICommonsList <IMicroNode> aParentChildren = m_aParentNode.directGetAllChildren ();
    final int nIndex = aParentChildren.indexOf (this);
    if (nIndex == -1)
      throw new IllegalStateException ("this is no part of it's parents children");

    // getAtIndex handles index overflow
    return aParentChildren.getAtIndex (nIndex + 1);
  }

  public final boolean hasParent ()
  {
    return m_aParentNode != null;
  }

  @Nullable
  public final IMicroNode getParent ()
  {
    return m_aParentNode;
  }

  protected final void internalResetParentNode ()
  {
    m_aParentNode = null;
  }

  protected final void internalSetParentNode (@NonNull final AbstractMicroNodeWithChildren aParentNode)
  {
    if (aParentNode == null)
      throw new MicroException ("No parent node passed!");
    if (aParentNode == this)
      throw new MicroException ("Node cannot have itself as parent: " + toString ());
    if (m_aParentNode != null)
      throw new MicroException ("Node already has a parent: " + toString ());
    m_aParentNode = aParentNode;
  }

  @NonNull
  public final IMicroNode detachFromParent ()
  {
    if (m_aParentNode != null)
    {
      if (m_aParentNode.removeChild (this).isUnchanged ())
        throw new IllegalStateException ("Failed to remove this from parents child list");
      internalResetParentNode ();
    }
    return this;
  }

  @Nullable
  public IMicroElement findParentElement (@NonNull final Predicate <? super IMicroElement> aFilter)
  {
    IMicroNode aParent = m_aParentNode;
    while (aParent != null && aParent.isElement ())
    {
      final IMicroElement aParentElement = (IMicroElement) aParent;
      if (aFilter.test (aParentElement))
        return aParentElement;
      aParent = aParent.getParent ();
    }
    return null;
  }

  /*
   * Note: the implementations with "this instanceof IMicroXXX" is faster than doing either
   * "getType ().equals (EMicroNodeType....)" and faster than having "return false;" in here and
   * "return true;" in the respective implementation classes.
   */

  public final boolean isDocument ()
  {
    return this instanceof IMicroDocument;
  }

  public final boolean isDocumentType ()
  {
    return this instanceof IMicroDocumentType;
  }

  public final boolean isText ()
  {
    return this instanceof IMicroText;
  }

  public final boolean isCDATA ()
  {
    return this instanceof IMicroCDATA;
  }

  public final boolean isComment ()
  {
    return this instanceof IMicroComment;
  }

  public final boolean isEntityReference ()
  {
    return this instanceof IMicroEntityReference;
  }

  public final boolean isElement ()
  {
    return this instanceof IMicroElement;
  }

  public final boolean isProcessingInstruction ()
  {
    return this instanceof IMicroProcessingInstruction;
  }

  public final boolean isContainer ()
  {
    return this instanceof IMicroContainer;
  }

  protected final void internalTriggerEvent (@NonNull final EMicroEvent eEventType, @NonNull final IMicroEvent aEvent)
  {
    // Any event targets present?
    if (m_aEventTargets != null && m_aEventTargets.isNotEmpty ())
    {
      // Get all event handler
      final CallbackList <IMicroEventTarget> aTargets = m_aEventTargets.get (eEventType);
      if (aTargets != null)
        aTargets.forEach (x -> x.handleEvent (aEvent));
    }

    // Bubble to parent
    if (m_aParentNode != null)
      m_aParentNode.internalTriggerEvent (eEventType, aEvent);
  }

  protected final void onEvent (@NonNull final EMicroEvent eEventType,
                                @NonNull final IMicroNode aSourceNode,
                                @NonNull final IMicroNode aTargetNode)
  {
    // Create the event only once
    internalTriggerEvent (eEventType, new MicroEvent (eEventType, aSourceNode, aTargetNode));
  }

  @NonNull
  public EChange registerEventTarget (@NonNull final EMicroEvent eEventType, @NonNull final IMicroEventTarget aTarget)
  {
    ValueEnforcer.notNull (eEventType, "EventType");
    ValueEnforcer.notNull (aTarget, "EventTarget");

    if (m_aEventTargets == null)
      m_aEventTargets = new CommonsEnumMap <> (EMicroEvent.class);
    final CallbackList <IMicroEventTarget> aSet = m_aEventTargets.computeIfAbsent (eEventType,
                                                                                   k -> new CallbackList <> ());
    return EChange.valueOf (aSet.add (aTarget));
  }

  @NonNull
  public EChange unregisterEventTarget (@NonNull final EMicroEvent eEventType, @NonNull final IMicroEventTarget aTarget)
  {
    ValueEnforcer.notNull (eEventType, "EventType");
    ValueEnforcer.notNull (aTarget, "EventTarget");

    if (m_aEventTargets != null && m_aEventTargets.isNotEmpty ())
    {
      final CallbackList <IMicroEventTarget> aSet = m_aEventTargets.get (eEventType);
      if (aSet != null)
        return aSet.removeObject (aTarget);
    }
    return EChange.UNCHANGED;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <EMicroEvent, CallbackList <IMicroEventTarget>> getAllEventTargets ()
  {
    return new CommonsEnumMap <> (m_aEventTargets);
  }

  @NonNull
  @ReturnsMutableCopy
  public CallbackList <IMicroEventTarget> getAllEventTargets (@Nullable final EMicroEvent eEvent)
  {
    return new CallbackList <> (m_aEventTargets == null ? null : m_aEventTargets.get (eEvent));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("ParentNodeName",
                                                         m_aParentNode == null ? null : m_aParentNode.getNodeName ())
                                       .appendIfNotNull ("EventTargets", m_aEventTargets)
                                       .getToString ();
  }
}
