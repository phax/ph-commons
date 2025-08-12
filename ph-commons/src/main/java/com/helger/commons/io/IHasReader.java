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
package com.helger.commons.io;

import java.io.Reader;

import com.helger.commons.io.stream.StreamHelper;

import jakarta.annotation.Nullable;

/**
 * A callback interface to retrieve {@link Reader} objects.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasReader
{
  /**
   * Get the reader to read from the object. Each time this method is call, a
   * new {@link Reader} needs to be created!
   *
   * @return <code>null</code> if resolving failed.
   */
  @Nullable
  Reader getReader ();

  /**
   * Get a buffered reader to read from the object. Each time this method is
   * call, a new {@link Reader} needs to be created!
   *
   * @return <code>null</code> if resolving failed.
   * @since 9.1.8
   */
  @Nullable
  default Reader getBufferedReader ()
  {
    final Reader aReader = getReader ();
    return aReader == null ? null : StreamHelper.getBuffered (aReader);
  }
}
