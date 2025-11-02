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
package com.helger.tree.xml;

import java.util.Comparator;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.id.IHasID;
import com.helger.collection.hierarchy.ChildrenProviderHasChildrenSorting;
import com.helger.collection.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.collection.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.collection.stack.NonBlockingStack;
import com.helger.tree.IBasicTree;
import com.helger.tree.ITreeItem;
import com.helger.tree.util.TreeVisitor;
import com.helger.tree.withid.BasicTreeWithID;
import com.helger.tree.withid.DefaultTreeWithID;
import com.helger.tree.withid.ITreeItemWithID;
import com.helger.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.util.ChildrenProviderElementWithName;
import com.helger.xml.microdom.util.MicroVisitor;

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
  private static final TreeXMLConverter INSTANCE = new TreeXMLConverter ();

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
  @NonNull
  public static <DATATYPE, ITEMTYPE extends ITreeItemWithID <String, DATATYPE, ITEMTYPE>> IMicroElement getTreeWithStringIDAsXML (@NonNull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                                  @NonNull final IConverterTreeItemToMicroNode <? super DATATYPE> aConverter)
  {
    return getTreeWithIDAsXML (aTree, IHasID.getComparatorID (), x -> x, aConverter);
  }

  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void fillTreeWithIDAsXML (@NonNull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                              @NonNull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                                              @NonNull final Function <? super KEYTYPE, ? extends String> aIDConverter,
                                                                                                                              @NonNull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter,
                                                                                                                              @NonNull final IMicroElement aElement)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final NonBlockingStack <IMicroElement> aParents = new NonBlockingStack <> ();
    aParents.push (aElement);
    TreeVisitor.visitTree (aTree,
                           new ChildrenProviderHasChildrenSorting <ITEMTYPE> (aItemComparator),
                           new DefaultHierarchyVisitorCallback <> ()
                           {
                             @Override
                             @NonNull
                             public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                               {
                                 // create item element
                                 final IMicroElement eItem = aParents.peek ()
                                                                     .addElementNS (sNamespaceURI, ELEMENT_ITEM);
                                 eItem.setAttribute (ATTR_ID, aIDConverter.apply (aItem.getID ()));

                                 // append data
                                 final IMicroElement eData = eItem.addElementNS (sNamespaceURI, ELEMENT_DATA);
                                 aDataConverter.appendDataValue (eData, aItem.getData ());

                                 aParents.push (eItem);
                               }
                               return EHierarchyVisitorReturn.CONTINUE;
                             }

                             @Override
                             @NonNull
                             public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                                 aParents.pop ();
                               return EHierarchyVisitorReturn.CONTINUE;
                             }
                           });
  }

  @NonNull
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> IMicroElement getTreeWithIDAsXML (@NonNull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                                      @NonNull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                                                      @NonNull final Function <? super KEYTYPE, ? extends String> aIDConverter,
                                                                                                                                      @NonNull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final IMicroElement eRoot = new MicroElement (sNamespaceURI, ELEMENT_ROOT);
    fillTreeWithIDAsXML (aTree, aItemComparator, aIDConverter, aDataConverter, eRoot);
    return eRoot;
  }

  public static <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> void fillTreeAsXML (@NonNull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @NonNull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                @NonNull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter,
                                                                                                @NonNull final IMicroElement aElement)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final NonBlockingStack <IMicroElement> aParents = new NonBlockingStack <> ();
    aParents.push (aElement);
    TreeVisitor.visitTree (aTree,
                           new ChildrenProviderHasChildrenSorting <ITEMTYPE> (aItemComparator),
                           new DefaultHierarchyVisitorCallback <> ()
                           {
                             @Override
                             @NonNull
                             public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                               {
                                 // create item element
                                 final IMicroElement eItem = aParents.peek ()
                                                                     .addElementNS (sNamespaceURI, ELEMENT_ITEM);

                                 // append data
                                 final IMicroElement eData = eItem.addElementNS (sNamespaceURI, ELEMENT_DATA);
                                 aDataConverter.appendDataValue (eData, aItem.getData ());

                                 aParents.push (eItem);
                               }
                               return EHierarchyVisitorReturn.CONTINUE;
                             }

                             @Override
                             @NonNull
                             public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final ITEMTYPE aItem)
                             {
                               if (aItem != null)
                                 aParents.pop ();
                               return EHierarchyVisitorReturn.CONTINUE;
                             }
                           });
  }

  @NonNull
  public static <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> IMicroElement getTreeAsXML (@NonNull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                        @NonNull final Comparator <? super ITEMTYPE> aItemComparator,
                                                                                                        @NonNull final IConverterTreeItemToMicroNode <? super DATATYPE> aDataConverter)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final IMicroElement eRoot = new MicroElement (sNamespaceURI, ELEMENT_ROOT);
    fillTreeAsXML (aTree, aItemComparator, aDataConverter, eRoot);
    return eRoot;
  }

  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void fillXMLAsTreeWithID (@NonNull final IMicroElement aElement,
                                                                                                                              @NonNull final Function <? super String, ? extends KEYTYPE> aIDConverter,
                                                                                                                              @NonNull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter,
                                                                                                                              @NonNull final BasicTreeWithID <KEYTYPE, DATATYPE, ITEMTYPE> aTree)
  {
    final String sNamespaceURI = aDataConverter.getNamespaceURI ();
    final NonBlockingStack <ITEMTYPE> aParents = new NonBlockingStack <> ();
    aParents.push (aTree.getRootItem ());
    MicroVisitor.visit (aElement,
                        new ChildrenProviderElementWithName (sNamespaceURI, ELEMENT_ITEM),
                        new DefaultHierarchyVisitorCallback <IMicroElement> ()
                        {
                          @Override
                          @NonNull
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
                          @NonNull
                          public EHierarchyVisitorReturn onItemAfterChildren (@Nullable final IMicroElement aItem)
                          {
                            if (aItem != null)
                              aParents.pop ();
                            return EHierarchyVisitorReturn.CONTINUE;
                          }
                        });
  }

  @NonNull
  public static <DATATYPE> DefaultTreeWithGlobalUniqueID <String, DATATYPE> getXMLAsTreeWithUniqueStringID (@NonNull final IMicroElement aElement,
                                                                                                            @NonNull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    return getXMLAsTreeWithUniqueID (aElement, x -> x, aDataConverter);
  }

  @NonNull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> getXMLAsTreeWithUniqueID (@NonNull final IMicroElement aElement,
                                                                                                                @NonNull final Function <? super String, ? extends KEYTYPE> aIDConverter,
                                                                                                                @NonNull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    final DefaultTreeWithGlobalUniqueID <KEYTYPE, DATATYPE> aTree = new DefaultTreeWithGlobalUniqueID <> ();
    fillXMLAsTreeWithID (aElement, aIDConverter, aDataConverter, aTree);
    return aTree;
  }

  @NonNull
  public static <KEYTYPE, DATATYPE> DefaultTreeWithID <KEYTYPE, DATATYPE> getXMLAsTreeWithID (@NonNull final IMicroElement aElement,
                                                                                              @NonNull final Function <? super String, ? extends KEYTYPE> aIDConverter,
                                                                                              @NonNull final IConverterMicroNodeToTreeItem <? extends DATATYPE> aDataConverter)
  {
    final DefaultTreeWithID <KEYTYPE, DATATYPE> aTree = new DefaultTreeWithID <> ();
    fillXMLAsTreeWithID (aElement, aIDConverter, aDataConverter, aTree);
    return aTree;
  }
}
