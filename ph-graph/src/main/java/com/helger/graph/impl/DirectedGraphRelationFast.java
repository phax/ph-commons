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
package com.helger.graph.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.hashcode.IHashCodeGenerator;
import com.helger.graph.IMutableDirectedGraphNode;

/**
 * Implementation of {@link com.helger.graph.IMutableDirectedGraphRelation}
 * interface with quick and dirty equals and hashCode (on ID only)
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphRelationFast extends DirectedGraphRelation
{
  // Status vars
  private transient int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  public DirectedGraphRelationFast (@Nonnull final IMutableDirectedGraphNode aFrom,
                                    @Nonnull final IMutableDirectedGraphNode aTo)
  {
    super (aFrom, aTo);
  }

  public DirectedGraphRelationFast (@Nullable final String sID,
                                    @Nonnull final IMutableDirectedGraphNode aFrom,
                                    @Nonnull final IMutableDirectedGraphNode aTo)
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
    final DirectedGraphRelationFast rhs = (DirectedGraphRelationFast) o;
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
