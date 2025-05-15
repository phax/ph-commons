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
package com.helger.scope;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Interface for a single request scope object.
 *
 * @author Philip Helger
 */
public interface IRequestScope extends IScope
{
  /**
   * Shortcut for <code>getSessionID(true)</code>
   *
   * @return The session ID associated with this request. May be
   *         <code>null</code> if no session ID is present and no session should
   *         be created.
   */
  @Nonnull
  @Nonempty
  default String getSessionID ()
  {
    return getSessionID (true);
  }

  /**
   * @param bCreateIfNotExisting
   *        if <code>true</code> a session ID is created if needed
   * @return The session ID associated with this request. May be
   *         <code>null</code> if no session ID is present and no session should
   *         be created.
   */
  @Nullable
  String getSessionID (boolean bCreateIfNotExisting);
}
