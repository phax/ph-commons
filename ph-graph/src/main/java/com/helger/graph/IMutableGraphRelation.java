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
package com.helger.graph;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;

/**
 * Base interface for a single undirected graph relation.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IMutableGraphRelation extends IMutableBaseGraphRelation <IMutableGraphNode, IMutableGraphRelation>
{
  /**
   * @return Node1 of this relation. Never <code>null</code>.
   */
  @Nonnull
  IMutableGraphNode getNode1 ();

  /**
   * @return The ID of node1 of this relation. Never <code>null</code>.
   */
  @Nonnull
  default String getNode1ID ()
  {
    return getNode1 ().getID ();
  }

  /**
   * @return Node2 of this relation. Never <code>null</code>.
   */
  @Nonnull
  IMutableGraphNode getNode2 ();

  /**
   * @return The ID of node2 of this relation. Never <code>null</code>.
   */
  @Nonnull
  default String getNode2ID ()
  {
    return getNode2 ().getID ();
  }
}
