/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
  @NonNull
  public IMutableDirectedGraphNode createNode ()
  {
    return new DirectedGraphNode ();
  }

  @NonNull
  public IMutableDirectedGraphNode createNode (@Nullable final String sID)
  {
    return new DirectedGraphNode (sID);
  }

  @NonNull
  public IMutableDirectedGraphRelation createRelation (@NonNull final IMutableDirectedGraphNode aFrom,
                                                       @NonNull final IMutableDirectedGraphNode aTo)
  {
    return new DirectedGraphRelation (aFrom, aTo);
  }

  @NonNull
  public IMutableDirectedGraphRelation createRelation (@Nullable final String sID,
                                                       @NonNull final IMutableDirectedGraphNode aFrom,
                                                       @NonNull final IMutableDirectedGraphNode aTo)
  {
    return new DirectedGraphRelation (sID, aFrom, aTo);
  }
}
