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
package com.helger.graph.impl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.hashcode.IHashCodeGenerator;
import com.helger.graph.IMutableGraphNode;

/**
 * Implementation of {@link com.helger.graph.IMutableGraphRelation} interface
 * with quick and dirty equals and hashCode (on ID only)
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class GraphRelationFast extends GraphRelation
{
  // Status vars
  private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  public GraphRelationFast (@NonNull final IMutableGraphNode aFrom, @NonNull final IMutableGraphNode aTo)
  {
    super (aFrom, aTo);
  }

  public GraphRelationFast (@Nullable final String sID, @NonNull final IMutableGraphNode aFrom, @NonNull final IMutableGraphNode aTo)
  {
    super (sID, aFrom, aTo);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final GraphRelationFast rhs = (GraphRelationFast) o;
    return getID ().equals (rhs.getID ());
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (getID ()).getHashCode ();
    return ret;
  }
}
