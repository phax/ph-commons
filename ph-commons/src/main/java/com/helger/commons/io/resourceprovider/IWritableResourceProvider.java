/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.resourceprovider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.io.resource.IWritableResource;

/**
 * Resource provider interface for readable and writable resources.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IWritableResourceProvider extends IReadableResourceProvider
{
  /**
   * Check if this resource provider can handle the resource with the passed
   * name. If there is no real check on whether your resource provider can
   * handle it, simply return <code>true</code>.
   *
   * @param sName
   *        The name to check. May be <code>null</code>.
   * @return <code>true</code> if the name is not <code>null</code> and can be
   *         handled by this provider, <code>false</code> otherwise.
   */
  boolean supportsWriting (@Nullable String sName);

  /**
   * Get the resource specified by the given name for writing.
   *
   * @param sName
   *        The name of the resource to resolve.
   * @return The writable resource. Never <code>null</code>.
   */
  @Nonnull
  IWritableResource getWritableResource (@Nonnull String sName);
}
