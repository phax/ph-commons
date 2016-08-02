/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.xml;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;

/**
 * Iterate child elements of a single node. Does not iterate recursively. Does
 * not return the source node.
 *
 * @author Philip Helger
 */
@Deprecated
public class ChildNodeIterator extends NodeListIterator
{
  public ChildNodeIterator (@Nonnull final Node aStartNode)
  {
    super (aStartNode == null ? null : aStartNode.getChildNodes ());
  }
}
