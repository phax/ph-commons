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
package com.helger.commons.io.resource;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;

/**
 * Base interface for an abstract readable resource.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IResourceBase extends Serializable
{
  /**
   * @return A non-<code>null</code> resource ID used e.g. for system IDs in XML
   *         resolving.
   */
  @Nonnull
  String getResourceID ();

  /**
   * @return The requested path. Never <code>null</code>
   */
  @Nonnull
  String getPath ();

  /**
   * @return <code>true</code> if the resource exists, <code>false</code>
   *         otherwise.
   */
  boolean exists ();

  /**
   * @return the URL representation of this resource. May be <code>null</code>
   *         if this resource cannot be represented as an URL.
   */
  @Nullable
  URL getAsURL ();

  /**
   * @return the File representation of this resource. May be <code>null</code>
   *         if this resource cannot be represented as a file.
   */
  @Nullable
  File getAsFile ();
}
