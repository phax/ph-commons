/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.tree.simple;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Root class for a simple tree. The elements of the tree are not sorted by any
 * means.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class BasicTree <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> implements ITree <DATATYPE, ITEMTYPE>
{
  // Root item.
  private final ITEMTYPE m_aRoot;

  public BasicTree (@Nonnull final ITreeItemFactory <DATATYPE, ITEMTYPE> aFactory)
  {
    ValueEnforcer.notNull (aFactory, "Factory");
    m_aRoot = aFactory.createRoot ();
    if (m_aRoot == null)
      throw new IllegalStateException ("Failed to create root item!");
  }

  @Nonnull
  public final ITEMTYPE getRootItem ()
  {
    return m_aRoot;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BasicTree <?, ?> rhs = (BasicTree <?, ?>) o;
    return m_aRoot.equals (rhs.m_aRoot);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aRoot).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("root", m_aRoot).toString ();
  }
}
