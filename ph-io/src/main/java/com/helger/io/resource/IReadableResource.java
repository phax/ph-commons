/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.resource;

import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.io.iface.IHasInputStreamAndReader;

import jakarta.annotation.Nonnull;

/**
 * Base interface for an abstract readable resource.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IReadableResource extends IHasInputStreamAndReader, IResourceBase
{
  /**
   * Get a new resource of the same implementation type as this object but for a
   * different path.
   *
   * @param sPath
   *        The new path to use. May not be <code>null</code>.
   * @return The resource of the same implementation but a different path. May
   *         not be <code>null</code>.
   */
  @Nonnull
  IReadableResource getReadableCloneForPath (@Nonnull String sPath);
}
