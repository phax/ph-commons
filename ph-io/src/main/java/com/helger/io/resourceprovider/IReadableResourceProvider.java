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
package com.helger.io.resourceprovider;

import java.io.InputStream;

import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Resource provider interface for readable resources.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IReadableResourceProvider
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
  boolean supportsReading (@Nullable String sName);

  /**
   * Get the resource specified by the given name for reading.
   *
   * @param sName
   *        The name of the resource to resolve.
   * @return The readable resource. Never <code>null</code>.
   */
  @Nonnull
  IReadableResource getReadableResource (@Nonnull String sName);

  /**
   * Get the {@link InputStream} specified by the given name for reading. This
   * method may be called without prior call to
   * {@link #supportsReading(String)}.
   *
   * @param sName
   *        The name of the resource to resolve.
   * @return The {@link InputStream}. May be <code>null</code> if the underlying
   *         resource does not exist or if {@link #supportsReading(String)}
   *         returns <code>false</code>.
   */
  @Nullable
  default InputStream getInputStream (@Nonnull final String sName)
  {
    if (!supportsReading (sName))
      return null;
    return getReadableResource (sName).getInputStream ();
  }
}
