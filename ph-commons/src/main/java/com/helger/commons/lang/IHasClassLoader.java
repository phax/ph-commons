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
package com.helger.commons.lang;

import jakarta.annotation.Nullable;

/**
 * Base interface for all objects having optional support for custom
 * {@link ClassLoader} objects.<br>
 * Note: when you implement this interface and want to have a
 * {@link ClassLoader} as a member, please consider storing it as a
 * {@link java.lang.ref.WeakReference} to avoid potential memory leaks.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasClassLoader
{
  /**
   * @return The class loader to use. May be <code>null</code>.
   */
  @Nullable
  ClassLoader getClassLoader ();

  /**
   * Check if this object has a custom class loader or not.
   *
   * @return <code>true</code> if a class loader is present, <code>false</code>
   *         if not.
   * @see #getClassLoader()
   * @since 9.0.0
   */
  default boolean hasClassLoader ()
  {
    return getClassLoader () != null;
  }
}
