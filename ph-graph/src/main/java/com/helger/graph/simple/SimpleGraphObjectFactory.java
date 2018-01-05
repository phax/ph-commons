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
package com.helger.graph.simple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.graph.IMutableGraphNode;
import com.helger.graph.IMutableGraphObjectFactory;
import com.helger.graph.IMutableGraphRelation;
import com.helger.graph.impl.GraphNode;
import com.helger.graph.impl.GraphRelation;

/**
 * Default implementation of the {@link IMutableGraphObjectFactory} with
 * {@link GraphNode} and {@link GraphRelation}.
 *
 * @author Philip Helger
 */
public class SimpleGraphObjectFactory implements IMutableGraphObjectFactory
{
  @Nonnull
  public IMutableGraphNode createNode ()
  {
    return new GraphNode ();
  }

  @Nonnull
  public IMutableGraphNode createNode (@Nullable final String sID)
  {
    return new GraphNode (sID);
  }

  @Nonnull
  public IMutableGraphRelation createRelation (@Nonnull final IMutableGraphNode aFrom,
                                               @Nonnull final IMutableGraphNode aTo)
  {
    return new GraphRelation (aFrom, aTo);
  }

  @Nonnull
  public IMutableGraphRelation createRelation (@Nullable final String sID,
                                               @Nonnull final IMutableGraphNode aFrom,
                                               @Nonnull final IMutableGraphNode aTo)
  {
    return new GraphRelation (sID, aFrom, aTo);
  }
}
