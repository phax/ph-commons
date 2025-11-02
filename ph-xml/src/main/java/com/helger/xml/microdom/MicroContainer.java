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
package com.helger.xml.microdom;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Default implementation of the {@link IMicroContainer} interface.
 *
 * @author Philip Helger
 */
public final class MicroContainer extends AbstractMicroNodeWithChildren implements IMicroContainer
{
  public MicroContainer ()
  {}

  public MicroContainer (@Nullable final IMicroNode... aChildNodes)
  {
    if (aChildNodes != null)
      for (final IMicroNode aChildNode : aChildNodes)
        addChild (aChildNode);
  }

  public MicroContainer (@Nullable final Iterable <? extends IMicroNode> aChildNodes)
  {
    if (aChildNodes != null)
      for (final IMicroNode aChildNode : aChildNodes)
        addChild (aChildNode);
  }

  @NonNull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.CONTAINER;
  }

  @NonNull
  public String getNodeName ()
  {
    return "#container";
  }

  @NonNull
  public IMicroContainer getClone ()
  {
    final IMicroContainer ret = new MicroContainer ();
    forAllChildren (aChildNode -> ret.addChild (aChildNode.getClone ()));
    return ret;
  }

  /**
   * Create a new {@link IMicroContainer} that contains clones of all passed
   * nodes
   *
   * @param aChildNodes
   *        The micro node array to add to the {@link IMicroContainer}
   * @return The created {@link IMicroContainer} and never <code>null</code>.
   */
  @NonNull
  public static IMicroContainer createWithClones (@Nullable final IMicroNode... aChildNodes)
  {
    final IMicroContainer ret = new MicroContainer ();
    if (aChildNodes != null)
      for (final IMicroNode aChildNode : aChildNodes)
        ret.addChild (aChildNode == null ? null : aChildNode.getClone ());
    return ret;
  }

  /**
   * Create a new {@link IMicroContainer} that contains clones of all passed
   * nodes
   *
   * @param aChildNodes
   *        The micro node container to add to the {@link IMicroContainer}
   * @return The created {@link IMicroContainer} and never <code>null</code>.
   */
  @NonNull
  public static IMicroContainer createWithClones (@Nullable final Iterable <? extends IMicroNode> aChildNodes)
  {
    final IMicroContainer ret = new MicroContainer ();
    if (aChildNodes != null)
      for (final IMicroNode aChildNode : aChildNodes)
        ret.addChild (aChildNode == null ? null : aChildNode.getClone ());
    return ret;
  }
}
