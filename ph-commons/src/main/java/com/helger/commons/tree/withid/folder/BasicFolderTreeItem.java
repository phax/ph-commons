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
package com.helger.commons.tree.withid.folder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.tree.withid.BasicTreeItemWithID;

/**
 * Base implementation of the {@link IFolderTreeItem} interface.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <DATATYPE>
 *        Value type
 * @param <COLLTYPE>
 *        Collection type consisting of value elements
 * @param <ITEMTYPE>
 *        the implementation item type
 */
@NotThreadSafe
public class BasicFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE extends Collection <DATATYPE>, ITEMTYPE extends BasicFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>>
                                 extends BasicTreeItemWithID <KEYTYPE, COLLTYPE, ITEMTYPE>
                                 implements IFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>
{
  // Combinator to create a global unique ID.
  private final IAggregator <KEYTYPE, KEYTYPE> m_aKeyCombinator;

  /**
   * Constructor for root object
   *
   * @param aFactory
   *        The item factory to use.
   */
  public BasicFolderTreeItem (@Nonnull final IFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE> aFactory)
  {
    super (aFactory);
    m_aKeyCombinator = aFactory.getKeyCombinator ();
  }

  /**
   * Constructor for root object
   *
   * @param aFactory
   *        The item factory to use.
   * @param aDataID
   *        The data ID of the root item.
   */
  public BasicFolderTreeItem (@Nonnull final IFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE> aFactory,
                              @Nullable final KEYTYPE aDataID)
  {
    super (aFactory, aDataID);
    m_aKeyCombinator = aFactory.getKeyCombinator ();
  }

  /**
   * Constructor for normal elements
   *
   * @param aParent
   *        Parent item. May never be <code>null</code> since only the root has
   *        no parent.
   * @param aDataID
   *        The ID of the new item. May not be <code>null</code>.
   */
  public BasicFolderTreeItem (@Nonnull final ITEMTYPE aParent, @Nonnull final KEYTYPE aDataID)
  {
    super (aParent, aDataID);
    m_aKeyCombinator = ((BasicFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>) aParent).m_aKeyCombinator;
  }

  @Nonnull
  public final KEYTYPE getGlobalUniqueDataID ()
  {
    if (m_aKeyCombinator == null)
      return getID ();

    final ITEMTYPE aParent = getParent ();
    if (aParent == null)
      return getID ();

    final List <KEYTYPE> aList = new ArrayList <KEYTYPE> ();
    aList.add (aParent.getGlobalUniqueDataID ());
    aList.add (getID ());
    return m_aKeyCombinator.aggregate (aList);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final BasicFolderTreeItem <?, ?, ?, ?> rhs = (BasicFolderTreeItem <?, ?, ?, ?>) o;
    return EqualsHelper.equals (m_aKeyCombinator, rhs.m_aKeyCombinator);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aKeyCombinator).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("keyCombinator", m_aKeyCombinator).toString ();
  }
}
