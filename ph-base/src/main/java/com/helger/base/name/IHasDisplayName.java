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
package com.helger.base.name;

import java.util.Comparator;

import jakarta.annotation.Nonnull;

/**
 * Base interface for objects that have a locale <b>independent</b> display name.<br>
 * I contract to {@link IHasName} this interface is meant for display names of objects (e.g. user
 * names or folder names).
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasDisplayName
{
  /**
   * @return The display name of this object. Should never be <code>null</code>.
   */
  @Nonnull
  String getDisplayName ();

  @Nonnull
  static Comparator <IHasDisplayName> getComparatorDisplayName ()
  {
    return Comparator.comparing (IHasDisplayName::getDisplayName);
  }
}
