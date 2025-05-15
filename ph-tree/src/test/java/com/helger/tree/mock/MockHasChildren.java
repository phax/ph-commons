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
package com.helger.tree.mock;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hierarchy.IHasChildren;
import com.helger.commons.id.IHasID;

public final class MockHasChildren implements IHasChildren <MockHasChildren>, IHasID <String>
{
  private final String m_sID;
  private final ICommonsList <MockHasChildren> m_aList;

  public MockHasChildren (@Nonnull final String sID, @Nullable final MockHasChildren... aList)
  {
    m_sID = sID;
    m_aList = CollectionHelper.newList (aList);
  }

  public String getID ()
  {
    return m_sID;
  }

  public boolean hasChildren ()
  {
    return m_aList.isNotEmpty ();
  }

  @Nonnegative
  public int getChildCount ()
  {
    return m_aList.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsCollection <MockHasChildren> getAllChildren ()
  {
    return m_aList.getClone ();
  }

  @Nonnull
  public ICommonsIterable <MockHasChildren> getChildren ()
  {
    return m_aList;
  }

  @Nullable
  public MockHasChildren getChildWithID (final String sID)
  {
    return m_aList.findFirst (c -> EqualsHelper.equals (c.m_sID, sID));
  }
}
