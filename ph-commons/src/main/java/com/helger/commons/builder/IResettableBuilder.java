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
package com.helger.commons.builder;

import javax.annotation.Nonnull;

/**
 * Marker interface for a resettable builder
 *
 * @author Philip Helger
 * @param <T>
 *        The type to be build
 * @since 10.0.0
 */
public interface IResettableBuilder <T> extends IBuilder <T>
{
  /**
   * Reset the builder, so that a second object may be build with the same
   * builder.
   */
  void reset ();

  /**
   * Build the object
   *
   * @param bReset
   *        <code>true</code> if the builder should be reset after building,
   *        <code>false</code> if not.
   * @return The built object. May not be <code>null</code>.
   */
  @Nonnull
  default T build (final boolean bReset)
  {
    final T ret = build ();
    if (bReset)
      reset ();
    return ret;
  }
}
