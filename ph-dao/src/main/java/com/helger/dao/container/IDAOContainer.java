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
package com.helger.dao.container;

import java.util.function.Predicate;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.dao.IAutoSaveAware;
import com.helger.dao.IDAO;

/**
 * A marker interface for objects containing other DAOs.
 *
 * @author Philip Helger
 */
public interface IDAOContainer extends IAutoSaveAware
{
  /**
   * @return A list of all contained DAOs. The returned list may not contain
   *         <code>null</code> elements!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IDAO> getAllContainedDAOs ();

  boolean containsAny (@Nullable Predicate <? super IDAO> aFilter);
}
