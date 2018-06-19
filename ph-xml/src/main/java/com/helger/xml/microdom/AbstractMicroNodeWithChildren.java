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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Basic implementation class for the micro document object model. It overrides
 * all methods required for correct parent/child handling.
 *
 * @author Philip Helger
 */
public abstract class AbstractMicroNodeWithChildren extends AbstractMicroNode implements IMicroNodeWithChildren
{
  /** The list of child elements. May be <code>null</code>. */
  private ICommonsList <IMicroNode> m_aChildren;

  /**
   * @return The writable list of all child nodes - handle with care. May be
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("efficient access")
  protected final ICommonsList <IMicroNode> directGetAllChildren ()
  {
    return m_aChildren;
  }

  private void _afterInsertAsChildOfThis (@Nonnull final AbstractMicroNode aChildNode)
  {
    aChildNode.internalSetParentNode (this);
    onEvent (EMicroEvent.NODE_INSERTED, this, aChildNode);
  }

  @Override
  protected void onAppendChild (@Nonnull final AbstractMicroNode aChildNode)
  {
    if (aChildNode.isDocument ())
      throw new MicroException ("Cannot add document to documents");
    if (m_aChildren == null)
      m_aChildren = new CommonsArrayList <> ();
    m_aChildren.add (aChildNode);
    _afterInsertAsChildOfThis (aChildNode);
  }

  @Override
  protected final void onInsertBefore (@Nonnull final AbstractMicroNode aChildNode,
                                       @Nonnull final IMicroNode aSuccessor)
  {
    if (aChildNode.isDocument ())
      throw new MicroException ("Cannot add document to nodes");
    if (aSuccessor == null || m_aChildren == null)
      throw new MicroException ("Cannot add before element which is not contained!");
    final int nIndex = m_aChildren.lastIndexOf (aSuccessor);
    if (nIndex == -1)
      throw new MicroException ("Cannot add before element which is not contained!");
    m_aChildren.add (nIndex, aChildNode);
    _afterInsertAsChildOfThis (aChildNode);
  }

  @Override
  protected final void onInsertAfter (@Nonnull final AbstractMicroNode aChildNode,
                                      @Nonnull final IMicroNode aPredecessor)
  {
    if (aChildNode.isDocument ())
      throw new MicroException ("Cannot add document to nodes");
    if (aPredecessor == null || m_aChildren == null)
      throw new MicroException ("Cannot add after element which is not contained!");
    final int nIndex = m_aChildren.lastIndexOf (aPredecessor);
    if (nIndex == -1)
      throw new MicroException ("Cannot add after element which is not contained!");
    m_aChildren.add (nIndex + 1, aChildNode);
    _afterInsertAsChildOfThis (aChildNode);
  }

  @Override
  protected final void onInsertAtIndex (@Nonnegative final int nIndex, @Nonnull final AbstractMicroNode aChildNode)
  {
    if (nIndex < 0)
      throw new MicroException ("Cannot insert element at index " + nIndex + "!");
    if (aChildNode.isDocument ())
      throw new MicroException ("Cannot add document to nodes");
    if (m_aChildren == null)
      m_aChildren = new CommonsArrayList <> ();
    m_aChildren.add (Math.min (nIndex, m_aChildren.size ()), aChildNode);
    _afterInsertAsChildOfThis (aChildNode);
  }

  private void _afterRemoveChildOfThis (@Nonnull final IMicroNode aChildNode)
  {
    if (m_aChildren.contains (aChildNode))
      throw new IllegalStateException ("Child " + aChildNode + " is contained more than once in it's parents list");

    if (m_aChildren.isEmpty ())
      m_aChildren = null;
    ((AbstractMicroNode) aChildNode).internalResetParentNode ();
    onEvent (EMicroEvent.NODE_REMOVED, this, aChildNode);
  }

  @Override
  @Nonnull
  protected final EChange onRemoveChild (@Nonnull final IMicroNode aChildNode)
  {
    if (!aChildNode.hasParent ())
      throw new MicroException ("The passed child node to be removed has no parent!");

    if (m_aChildren == null || !m_aChildren.remove (aChildNode))
      return EChange.UNCHANGED;

    _afterRemoveChildOfThis (aChildNode);
    return EChange.CHANGED;
  }

  @Override
  @Nonnull
  protected final EChange onRemoveChildAtIndex (@Nonnegative final int nIndex)
  {
    // Resolve index - may be invalid
    final IMicroNode aChildNode = getChildAtIndex (nIndex);
    if (aChildNode == null)
      return EChange.UNCHANGED;

    if (!aChildNode.hasParent ())
      throw new MicroException ("Internal inconsistency: the passed child node to be removed has no parent!");

    // Main removal
    if (m_aChildren.remove (nIndex) != aChildNode)
      throw new MicroException ("Internal inconsistency: remove resulted in an illegal object!");

    _afterRemoveChildOfThis (aChildNode);
    return EChange.CHANGED;
  }

  @Override
  @Nonnull
  protected final EChange onRemoveAllChildren ()
  {
    if (m_aChildren == null || m_aChildren.isEmpty ())
      return EChange.UNCHANGED;

    // Trigger the method manually so that all events etc. are fired
    // Don't access m_aChildren directly because it is reset to null in
    // removeChildAtIndex
    while (hasChildren ())
      removeChildAtIndex (0);

    return EChange.CHANGED;
  }

  @Override
  public final boolean hasChildren ()
  {
    return m_aChildren != null && m_aChildren.isNotEmpty ();
  }

  @Override
  @Nullable
  @ReturnsMutableCopy
  public final ICommonsList <IMicroNode> getAllChildren ()
  {
    return m_aChildren == null ? null : m_aChildren.getClone ();
  }

  @Override
  @Nullable
  public final ICommonsIterable <IMicroNode> getChildren ()
  {
    return m_aChildren;
  }

  @Override
  public final void forAllChildren (@Nonnull final Consumer <? super IMicroNode> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.forEach (aConsumer);
  }

  @Override
  @Nonnull
  public EContinue forAllChildrenBreakable (@Nonnull final Function <? super IMicroNode, EContinue> aConsumer)
  {
    if (m_aChildren != null)
      return m_aChildren.forEachBreakable (aConsumer);
    return EContinue.CONTINUE;
  }

  @Override
  public final void forAllChildren (@Nonnull final Predicate <? super IMicroNode> aFilter,
                                    @Nonnull final Consumer <? super IMicroNode> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAll (aFilter, aConsumer);
  }

  @Override
  public final <DSTTYPE> void forAllChildrenMapped (@Nonnull final Predicate <? super IMicroNode> aFilter,
                                                    @Nonnull final Function <? super IMicroNode, ? extends DSTTYPE> aMapper,
                                                    @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAllMapped (aFilter, aMapper, aConsumer);
  }

  @Override
  public boolean containsAnyChild (@Nonnull final Predicate <? super IMicroNode> aFilter)
  {
    ValueEnforcer.notNull (aFilter, "Filter");
    if (m_aChildren == null)
      return false;
    return m_aChildren.containsAny (aFilter);
  }

  @Override
  @Nullable
  public final IMicroNode getChildAtIndex (@Nonnegative final int nIndex)
  {
    return m_aChildren == null ? null : m_aChildren.getAtIndex (nIndex);
  }

  @Override
  public final int getChildCount ()
  {
    return m_aChildren == null ? 0 : m_aChildren.size ();
  }

  @Override
  @Nullable
  public final IMicroNode getFirstChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getFirst ();
  }

  @Override
  @Nullable
  public final IMicroNode findFirstChild (@Nonnull final Predicate <? super IMicroNode> aFilter)
  {
    return m_aChildren == null ? null : m_aChildren.findFirst (aFilter);
  }

  @Override
  @Nullable
  public final <DSTTYPE> DSTTYPE findFirstChildMapped (@Nonnull final Predicate <? super IMicroNode> aFilter,
                                                       @Nonnull final Function <? super IMicroNode, ? extends DSTTYPE> aMapper)
  {
    return m_aChildren == null ? null : m_aChildren.findFirstMapped (aFilter, aMapper);
  }

  @Override
  @Nullable
  public final IMicroNode getLastChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getLast ();
  }

  @Nullable
  public String getTextContent ()
  {
    if (hasNoChildren ())
      return null;

    final StringBuilder aSB = new StringBuilder ();
    for (final IMicroNode aChild : m_aChildren)
    {
      final EMicroNodeType eType = aChild.getType ();
      if (eType.isText ())
      {
        final IMicroText aText = (IMicroText) aChild;
        // ignore whitespace-only content
        if (!((IMicroText) aChild).isElementContentWhitespace ())
          aSB.append (aText.getData ());
      }
      else
        if (eType.isCDATA ())
        {
          final IMicroCDATA aCDATA = (IMicroCDATA) aChild;
          aSB.append (aCDATA.getNodeValue ());
        }
        else
          if (aChild instanceof IMicroNodeWithChildren)
          {
            // Recursive call
            final String sTextContent = ((IMicroNodeWithChildren) aChild).getTextContent ();
            if (StringHelper.hasText (sTextContent))
              aSB.append (sTextContent);
          }
    }
    return aSB.toString ();
  }

  @Nullable
  public <DSTTYPE> DSTTYPE getTextContentWithConversion (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    // Get the regular content
    final String sTextContent = getTextContent ();

    // Avoid having a conversion issue with empty strings!
    if (StringHelper.hasNoText (sTextContent))
      return null;

    return TypeConverter.convert (sTextContent, aDstClass);
  }

  @OverridingMethodsMustInvokeSuper
  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractMicroNodeWithChildren rhs = (AbstractMicroNodeWithChildren) o;
    if (m_aChildren == null && rhs.m_aChildren == null)
      return true;
    if (m_aChildren == null || rhs.m_aChildren == null)
      return false;
    final int nMax = m_aChildren.size ();
    final int nMaxRhs = rhs.m_aChildren.size ();
    if (nMax != nMaxRhs)
      return false;
    for (int i = 0; i < nMax; ++i)
    {
      final IMicroNode aChild1 = m_aChildren.get (i);
      final IMicroNode aChild2 = rhs.m_aChildren.get (i);
      if (!aChild1.isEqualContent (aChild2))
        return false;
    }
    return true;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("childrenCount", m_aChildren == null ? 0 : m_aChildren.size ())
                            .getToString ();
  }
}
