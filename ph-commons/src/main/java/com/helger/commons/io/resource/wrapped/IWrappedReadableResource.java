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
package com.helger.commons.io.resource.wrapped;

import com.helger.annotation.Nonnull;

import com.helger.commons.io.resource.IReadableResource;

/**
 * Interface for readable resources that are "wrapped".
 *
 * @author Philip Helger
 */
public interface IWrappedReadableResource extends IReadableResource
{
  /**
   * @return The wrapped resource. May not be <code>null</code>.
   */
  @Nonnull
  IReadableResource getWrappedReadableResource ();

  default boolean isReadMultiple ()
  {
    return getWrappedReadableResource ().isReadMultiple ();
  }
}
