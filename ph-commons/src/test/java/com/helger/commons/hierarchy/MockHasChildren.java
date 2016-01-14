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
package com.helger.commons.hierarchy;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.id.IHasID;

public final class MockHasChildren implements IHasChildren <MockHasChildren>, IHasID <String>
{
  private final String m_sID;
  private final List <MockHasChildren> m_aList;

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
    return !m_aList.isEmpty ();
  }

  @Nonnegative
  public int getChildCount ()
  {
    return m_aList.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends MockHasChildren> getAllChildren ()
  {
    return CollectionHelper.newList (m_aList);
  }

  @Nullable
  public MockHasChildren getChildWithID (final String sID)
  {
    for (final MockHasChildren aChild : m_aList)
      if (EqualsHelper.equals (aChild.m_sID, sID))
        return aChild;
    return null;
  }
}
