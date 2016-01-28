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
package com.helger.commons.tree.xml;

import java.util.Comparator;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.impl.NonBlockingStack;
import com.helger.commons.hierarchy.ChildrenProviderHasChildrenSorting;
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.id.IHasID;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.microdom.util.ChildrenProviderElementWithName;
import com.helger.commons.microdom.util.MicroVisitor;
import com.helger.commons.tree.IBasicTree;
import com.helger.commons.tree.ITreeItem;
import com.helger.commons.tree.util.TreeVisitor;
import com.helger.commons.tree.withid.BasicTreeWithID;
import com.helger.commons.tree.withid.DefaultTreeWithID;
import com.helger.commons.tree.withid.ITreeItemWithID;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;

/**
 * Convert a tree to XML
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeXMLConverter
{
  public static final String ELEMENT_ROOT = "root";
  public static final String ELEMENT_ITEM = "item";
  public static final String ATTR_ID = "id";
  public static final String ELEMENT_DATA = "data";

  @PresentForCodeCoverage
  private static final TreeXMLConverter s_aInstance = new TreeXMLConverter ();

  private TreeXMLConverter ()
  {}

  /**
   * Specialized conversion method for converting a tree with ID to a
   * standardized XML tree.
   *
   * @param <DATATYPE>
   *        tree item value type
   * @param <ITEMTYPE>
   *        tree item type
   * @param aTree
   *        The tree to be converted
   * @param aConverter
   *        The main data converter that converts the tree item values into XML
   * @return The created document.
   */
  @Nonnull
  public static <DATATYPE, ITEMTYPE extends ITreeItemWithID <String, DATATYPE, ITEMTYPE>> IMicroElement getTreeWithStringIDAsXML (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                                  @Nonnull final IConverterTreeItemToMicroNode <? super DATATYPE> aConverter)
  {
    return getTreeWithIDAsXML (aTree, IHasID.getComparatorID (), aID -> aID, aConverter);
  }

  @Nonnull
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> IMicroElement getTreeWithIDAsXML (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                                      @Nonnull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                                                      @Nonnull final Function <KEYTYPE, String> aIDConverter,
                                                                                                                                      @Nonnull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter)
  {
    final IMicroElement eRoot = new MicroElement (ELEMENT_ROOT);
    final NonBlockingStack <IMicroElement> aParents = new NonBlockingStack <IMicroElement> ();
    aParents.push (eRoot);
    TreeVisitor.visitTree (aTree,
                           new ChildrenProviderHasChildrenSorting <ITEMTYPE> (aItemComparator),
                           new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
                           {
                             @Override
                             @Nonnull
                             public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                               {
                                 // create item element
                                 final IMicroElement eItem = aParents.peek ().appendElement (ELEMENT_ITEM);
                                 eItem.setAttribute (ATTR_ID, aIDConverter.apply (aItem.getID ()));

                                 // append data
                                 final IMicroElement eData = eItem.appendElement (ELEMENT_DATA);
                                 aDataConverter.appendDataValue (eData, aItem.getData ());

                                 aParents.push (eItem);
                               }
                               return EHierarchyVisitorReturn.CONTINUE;
                             }

                             @Override
                             @Nonnull
                             public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                                 aParents.pop ();
                               return EHierarchyVisitorReturn.CONTINUE;
                             }
                           });
    return eRoot;
  }

  @Nonnull
  public static <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> IMicroElement getTreeAsXML (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                        @Nonnull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                        @Nonnull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final IMicroElement eRoot = new MicroElement (sNamespaceURI, ELEMENT_ROOT);
    final NonBlockingStack <IMicroElement> aParents = new NonBlockingStack <IMicroElement> ();
    aParents.push (eRoot);
    TreeVisitor.visitTree (aTree,
                           new ChildrenProviderHasChildrenSorting <ITEMTYPE> (aItemComparator),
                           new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
                           {
                             @Override
                             @Nonnull
                             public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                               {
                                 // create item element
                                 final IMicroElement eItem = aParents.peek ().appendElement (sNamespaceURI,
                                                                                             ELEMENT_ITEM);

                                 // append data
                                 final IMicroElement eData = eItem.appendElement (sNamespaceURI, ELEMENT_DATA);
                                 aDataConverter.appendDataValue (eData, aItem.getData ());

                                 aParents.push (eItem);
                               }
                               return EHierarchyVisitorReturn.CONTINUE;
                             }

                             @Override
                             @Nonnull
                             public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                                 aParents.pop ();
                               return EHierarchyVisitorReturn.CONTINUE;
                             }
                           });
    return eRoot;
  }

  private static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void _getXMLAsTreeWithID (@Nonnull final IMicroElement aElement,
                                                                                                                               @Nonnull final Function <String, KEYTYPE> aIDConverter,
                                                                                                                               @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter,
                                                                                                                               @Nonnull final BasicTreeWithID <KEYTYPE, DATATYPE, ITEMTYPE> aTree)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final NonBlockingStack <ITEMTYPE> aParents = new NonBlockingStack <ITEMTYPE> ();
    aParents.push (aTree.getRootItem ());
    MicroVisitor.visit (aElement,
                        new ChildrenProviderElementWithName (sNamespaceURI, ELEMENT_ITEM),
                        new DefaultHierarchyVisitorCallback <IMicroElement> ()
                        {
                          @Override
                          @Nonnull
                          public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final IMicroElement eItem)
                          {
                            if (eItem != null)
                            {
                              final KEYTYPE aTreeItemID = aIDConverter.apply (eItem.getAttributeValue (ATTR_ID));

                              final IMicroElement eData = eItem.getFirstChildElement (sNamespaceURI, ELEMENT_DATA);
                              final DATATYPE aTreeItemValue = aDataConverter.getAsDataValue (eData);

                              final ITEMTYPE aTreeItem = aParents.peek ().createChildItem (aTreeItemID, aTreeItemValue);
                              aParents.push (aTreeItem);
                            }
                            return EHierarchyVisitorReturn.CONTINUE;
                          }

                          @Override
                          @Nonnull
                          public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final IMicroElement aItem)
                          {
                            if (aItem != null)
                              aParents.pop ();
                            return EHierarchyVisitorReturn.CONTINUE;
                          }
                        });
  }

  @Nonnull
  public static <DATATYPE> DefaultTreeWithGlobalUniqueID <String, DATATYPE> getXMLAsTreeWithUniqueStringID (@Nonnull final IMicroDocument aDoc,
                                                                                                            @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    return getXMLAsTreeWithUniqueStringID (aDoc.getDocumentElement (), aDataConverter);
  }

  @Nonnull
  public static <DATATYPE> DefaultTreeWithGlobalUniqueID <String, DATATYPE> getXMLAsTreeWithUniqueStringID (@Nonnull final IMicroElement aElement,
                                                                                                            @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    return TreeXMLConverter.<String, DATATYPE> getXMLAsTreeWithUniqueID (aElement, aID -> aID, aDataConverter);
  }

  @Nonnull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> getXMLAsTreeWithUniqueID (@Nonnull final IMicroDocument aDoc,
                                                                                                                @Nonnull final Function <String, KEYTYPE> aIDConverter,
                                                                                                                @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    return getXMLAsTreeWithUniqueID (aDoc.getDocumentElement (), aIDConverter, aDataConverter);
  }

  @Nonnull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> getXMLAsTreeWithUniqueID (@Nonnull final IMicroElement aElement,
                                                                                                                @Nonnull final Function <String, KEYTYPE> aIDConverter,
                                                                                                                @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    final DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> aTree = new DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> ();
    _getXMLAsTreeWithID (aElement, aIDConverter, aDataConverter, aTree);
    return aTree;
  }

  @Nonnull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithID <KEYTYPE, DATATYPE> getXMLAsTreeWithID (@Nonnull final IMicroDocument aDoc,
                                                                                              @Nonnull final Function <String, KEYTYPE> aIDConverter,
                                                                                              @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    return getXMLAsTreeWithID (aDoc.getDocumentElement (), aIDConverter, aDataConverter);
  }

  @Nonnull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithID <KEYTYPE, DATATYPE> getXMLAsTreeWithID (@Nonnull final IMicroElement aElement,
                                                                                              @Nonnull final Function <String, KEYTYPE> aIDConverter,
                                                                                              @Nonnull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    final DefaultTreeWithID <KEYTYPE, DATATYPE> aTree = new DefaultTreeWithID <KEYTYPE, DATATYPE> ();
    _getXMLAsTreeWithID (aElement, aIDConverter, aDataConverter, aTree);
    return aTree;
  }
}
