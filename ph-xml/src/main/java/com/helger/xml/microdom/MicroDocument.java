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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroDocument} interface.
 *
 * @author Philip Helger
 */
public final class MicroDocument extends AbstractMicroNodeWithChildren implements IMicroDocument
{
  public static final ETriState DEFAULT_STANDALONE = ETriState.UNDEFINED;

  private ETriState m_eStandalone = DEFAULT_STANDALONE;

  public MicroDocument ()
  {}

  public MicroDocument (@Nullable final IMicroDocumentType aDocType)
  {
    if (aDocType != null)
      appendChild (aDocType);
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.DOCUMENT;
  }

  public String getNodeName ()
  {
    return "#document";
  }

  private static boolean _canBeAppendedToDocumentRoot (@Nonnull final IMicroNode aNode)
  {
    return aNode.isDocumentType () || aNode.isProcessingInstruction () || aNode.isComment () || aNode.isElement ();
  }

  @Override
  protected void onAppendChild (@Nonnull final AbstractMicroNode aChildNode)
  {
    if (!_canBeAppendedToDocumentRoot (aChildNode))
      throw new MicroException ("Cannot add nodes of type " + aChildNode + " to a document");

    // Ensure that only one element is appended to the document root
    if (aChildNode.isElement ())
    {
      final ICommonsList <IMicroNode> aChildren = directGetAllChildren ();
      if (aChildren != null && aChildren.isNotEmpty ())
        for (final IMicroNode aCurChild : aChildren)
          if (aCurChild.isElement ())
            throw new MicroException ("A document can only have one document element! Already has " +
                                      aCurChild +
                                      " and wants to add " +
                                      aChildNode);
    }
    super.onAppendChild (aChildNode);
  }

  @Nonnull
  public ETriState getStandalone ()
  {
    return m_eStandalone;
  }

  public void setStandalone (@Nonnull final ETriState eStandalone)
  {
    ValueEnforcer.notNull (eStandalone, "Standalone");
    m_eStandalone = eStandalone;
  }

  @Nullable
  public IMicroDocumentType getDocType ()
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isDocumentType ())
          return (IMicroDocumentType) aChild;
    return null;
  }

  @Nullable
  public IMicroElement getDocumentElement ()
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
          return (IMicroElement) aChild;
    return null;
  }

  @Nonnull
  public IMicroDocument getClone ()
  {
    final MicroDocument ret = new MicroDocument ();
    ret.setStandalone (m_eStandalone);
    forAllChildren (aChildNode -> ret.appendChild (aChildNode.getClone ()));
    return ret;
  }

  @Override
  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (!super.isEqualContent (o))
      return false;
    final MicroDocument rhs = (MicroDocument) o;
    return m_eStandalone == rhs.m_eStandalone;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Standalone", m_eStandalone).getToString ();
  }
}
