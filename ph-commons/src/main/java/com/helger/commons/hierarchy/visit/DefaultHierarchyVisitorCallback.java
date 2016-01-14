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
package com.helger.commons.hierarchy.visit;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * The default implementation of the {@link IHierarchyVisitorCallback} interface
 * doing nothing except counting levels.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object in the hierarchy to be iterated
 */
public class DefaultHierarchyVisitorCallback <DATATYPE> implements IHierarchyVisitorCallback <DATATYPE>
{
  private int m_nLevel;

  public DefaultHierarchyVisitorCallback ()
  {
    this (0);
  }

  public DefaultHierarchyVisitorCallback (final int nInitialLevel)
  {
    m_nLevel = nInitialLevel;
  }

  @Nonnegative
  public int getLevel ()
  {
    return m_nLevel;
  }

  @OverridingMethodsMustInvokeSuper
  public void onLevelDown ()
  {
    ++m_nLevel;
  }

  @OverridingMethodsMustInvokeSuper
  public void onLevelUp ()
  {
    --m_nLevel;
  }

  @Nonnull
  public EHierarchyVisitorReturn onItemBeforeChildren (final DATATYPE aItem)
  {
    // Always continue
    return EHierarchyVisitorReturn.CONTINUE;
  }

  @Nonnull
  public EHierarchyVisitorReturn onItemAfterChildren (final DATATYPE aItem)
  {
    // Always continue
    return EHierarchyVisitorReturn.CONTINUE;
  }
}
