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

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.state.EChange;

/**
 * Base interface for a single graph node.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IMutableGraphNode extends IMutableBaseGraphNode <IMutableGraphNode, IMutableGraphRelation>
{
  /**
   * Add a new relation.
   *
   * @param aRelation
   *        The relation to be added to this node. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed
   */
  @Nonnull
  EChange addRelation (@Nullable IMutableGraphRelation aRelation);

  /**
   * Remove a new relation.
   *
   * @param aRelation
   *        The relation to be removed from this node. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed
   */
  @Nonnull
  EChange removeRelation (@Nullable IMutableGraphRelation aRelation);
}
