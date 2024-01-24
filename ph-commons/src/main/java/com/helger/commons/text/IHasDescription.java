/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.text;

import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;

/**
 * Base interface for objects that have a locale <b>independent</b> description.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasDescription
{
  /**
   * @return The description of this object. May be <code>null</code>.
   */
  @Nullable
  String getDescription ();

  /**
   * @return <code>true</code> if a description is present, <code>false</code>
   *         otherwise.
   */
  default boolean hasDescription ()
  {
    return StringHelper.hasText (getDescription ());
  }
}
