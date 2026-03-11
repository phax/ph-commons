/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.id.factory;

import org.jspecify.annotations.NonNull;

/**
 * A factory that creates String IDs based on a specified {@link IIntIDFactory}.
 * The implementation is as thread-safe as the used {@link IIntIDFactory}.
 *
 * @author Philip Helger
 */
public class StringIDFromIntIDFactory extends StringIDFactory
{
  /**
   * Constructor using the default prefix.
   *
   * @param aIntIDFactory
   *        The int ID factory to use. May not be <code>null</code>.
   */
  public StringIDFromIntIDFactory (@NonNull final IIntIDFactory aIntIDFactory)
  {
    this (aIntIDFactory, GlobalIDFactory.DEFAULT_PREFIX);
  }

  /**
   * Constructor with a custom prefix.
   *
   * @param aIntIDFactory
   *        The int ID factory to use. May not be <code>null</code>.
   * @param sPrefix
   *        The prefix to prepend to each generated ID. May not be <code>null</code>.
   */
  public StringIDFromIntIDFactory (@NonNull final IIntIDFactory aIntIDFactory, @NonNull final String sPrefix)
  {
    super (sPrefix, () -> Integer.toString (aIntIDFactory.getNewID ()));
  }
}
