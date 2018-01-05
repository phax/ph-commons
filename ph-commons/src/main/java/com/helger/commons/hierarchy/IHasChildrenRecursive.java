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
package com.helger.commons.hierarchy;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

/**
 * A simple interface, indicating that an item has children which also has
 * children etc.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of the children.
 */
public interface IHasChildrenRecursive <CHILDTYPE extends IHasChildrenRecursive <CHILDTYPE>>
                                       extends IHasChildren <CHILDTYPE>
{
  default void forAllChildrenRecursive (@Nonnull final Consumer <? super CHILDTYPE> aConsumer)
  {
    forAllChildren (aChildNode -> {
      aConsumer.accept (aChildNode);
      aChildNode.forAllChildrenRecursive (aConsumer);
    });
  }

  default void forAllChildrenRecursive (@Nonnull final Predicate <? super CHILDTYPE> aFilter,
                                        @Nonnull final Consumer <? super CHILDTYPE> aConsumer)
  {
    forAllChildren (aChildNode -> {
      if (aFilter.test (aChildNode))
        aConsumer.accept (aChildNode);
      aChildNode.forAllChildrenRecursive (aFilter, aConsumer);
    });
  }
}
