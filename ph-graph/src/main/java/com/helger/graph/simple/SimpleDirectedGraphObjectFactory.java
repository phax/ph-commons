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

import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphObjectFactory;
import com.helger.graph.IMutableDirectedGraphRelation;
import com.helger.graph.impl.DirectedGraphNode;
import com.helger.graph.impl.DirectedGraphRelation;

/**
 * Default implementation of the {@link IMutableDirectedGraphObjectFactory} with
 * {@link DirectedGraphNode} and {@link DirectedGraphRelation}.
 *
 * @author Philip Helger
 */
public class SimpleDirectedGraphObjectFactory implements IMutableDirectedGraphObjectFactory
{
  @Nonnull
  public IMutableDirectedGraphNode createNode ()
  {
    return new DirectedGraphNode ();
  }

  @Nonnull
  public IMutableDirectedGraphNode createNode (@Nullable final String sID)
  {
    return new DirectedGraphNode (sID);
  }

  @Nonnull
  public IMutableDirectedGraphRelation createRelation (@Nonnull final IMutableDirectedGraphNode aFrom,
                                                       @Nonnull final IMutableDirectedGraphNode aTo)
  {
    return new DirectedGraphRelation (aFrom, aTo);
  }

  @Nonnull
  public IMutableDirectedGraphRelation createRelation (@Nullable final String sID,
                                                       @Nonnull final IMutableDirectedGraphNode aFrom,
                                                       @Nonnull final IMutableDirectedGraphNode aTo)
  {
    return new DirectedGraphRelation (sID, aFrom, aTo);
  }
}
