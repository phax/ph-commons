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
import javax.annotation.Nullable;

/**
 * Factory interface for creating directed graph relations.
 *
 * @author Philip Helger
 */
public interface IMutableDirectedGraphRelationFactory
{
  /**
   * Create a new relation from the passed from-node to the to-node.
   *
   * @param aFrom
   *        The from node. May not be <code>null</code>.
   * @param aTo
   *        The to node. May not be <code>null</code>.
   * @return The created graph relation and never <code>null</code>.
   */
  @Nonnull
  IMutableDirectedGraphRelation createRelation (@Nonnull IMutableDirectedGraphNode aFrom,
                                                @Nonnull IMutableDirectedGraphNode aTo);

  /**
   * Create a new relation from the passed from-node to the to-node using a
   * previously known ID.
   *
   * @param sID
   *        The ID of the relation to be created. If it is <code>null</code> or
   *        empty a new unique ID is created.
   * @param aFrom
   *        The from node. May not be <code>null</code>.
   * @param aTo
   *        The to node. May not be <code>null</code>.
   * @return The created graph relation and never <code>null</code>.
   */
  @Nonnull
  IMutableDirectedGraphRelation createRelation (@Nullable String sID,
                                                @Nonnull IMutableDirectedGraphNode aFrom,
                                                @Nonnull IMutableDirectedGraphNode aTo);
}
