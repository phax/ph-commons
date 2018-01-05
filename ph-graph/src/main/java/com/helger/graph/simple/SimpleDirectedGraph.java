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
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphObjectFactory;
import com.helger.graph.IMutableDirectedGraphRelation;
import com.helger.graph.impl.DirectedGraph;

/**
 * A simple graph object that bidirectionally links graph nodes.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SimpleDirectedGraph extends DirectedGraph implements ISimpleDirectedGraph
{
  public SimpleDirectedGraph ()
  {
    this (new SimpleDirectedGraphObjectFactory ());
  }

  public SimpleDirectedGraph (@Nonnull final IMutableDirectedGraphObjectFactory aFactory)
  {
    super (null, aFactory);
  }

  @Nonnull
  public IMutableDirectedGraphRelation createRelation (@Nonnull final String sFromNodeID,
                                                       @Nonnull final String sToNodeID)
  {
    final IMutableDirectedGraphNode aFromNode = getNodeOfID (sFromNodeID);
    if (aFromNode == null)
      throw new IllegalArgumentException ("Failed to resolve from node ID '" + sFromNodeID + "'");
    final IMutableDirectedGraphNode aToNode = getNodeOfID (sToNodeID);
    if (aToNode == null)
      throw new IllegalArgumentException ("Failed to resolve to node ID '" + sToNodeID + "'");
    return createRelation (aFromNode, aToNode);
  }

  @Nonnull
  public IMutableDirectedGraphRelation createRelation (@Nonnull @Nonempty final String sRelationID,
                                                       @Nonnull final String sFromNodeID,
                                                       @Nonnull final String sToNodeID)
  {
    final IMutableDirectedGraphNode aFromNode = getNodeOfID (sFromNodeID);
    if (aFromNode == null)
      throw new IllegalArgumentException ("Failed to resolve from node ID '" + sFromNodeID + "'");
    final IMutableDirectedGraphNode aToNode = getNodeOfID (sToNodeID);
    if (aToNode == null)
      throw new IllegalArgumentException ("Failed to resolve to node ID '" + sToNodeID + "'");
    return createRelation (sRelationID, aFromNode, aToNode);
  }
}
