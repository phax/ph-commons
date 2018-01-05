/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.io.relative;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.resource.IReadableResource;

/**
 * A read-only version of a path-relative IO component.
 *
 * @author Philip Helger
 */
public interface IPathRelativeIO extends Serializable
{
  /**
   * @return The base path. May be a file path, a URL or whatever. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getBasePath ();

  /**
   * Get the file system resource relative to the base path. This method CAN NOT
   * handle absolute paths!
   *
   * @param sRelativePath
   *        the relative path
   * @return The "absolute" {@link IReadableResource} and never
   *         <code>null</code>.
   */
  @Nonnull
  IReadableResource getResource (@Nonnull String sRelativePath);

  /**
   * Get the {@link InputStream} relative to the base path
   *
   * @param sRelativePath
   *        the relative path
   * @return <code>null</code> if the path does not exist
   */
  @Nullable
  default InputStream getInputStream (@Nonnull final String sRelativePath)
  {
    return getResource (sRelativePath).getInputStream ();
  }

  /**
   * Get the {@link Reader} relative to the base path
   *
   * @param sRelativePath
   *        the relative path
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return <code>null</code> if the path does not exist
   */
  @Nullable
  default Reader getReader (@Nonnull final String sRelativePath, @Nonnull final Charset aCharset)
  {
    return getResource (sRelativePath).getReader (aCharset);
  }
}
