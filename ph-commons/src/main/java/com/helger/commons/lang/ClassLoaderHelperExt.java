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

import java.io.InputStream;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.io.stream.StreamHelperExt;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * {@link ClassLoader} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassLoaderHelperExt extends ClassLoaderHelper
{
  @PresentForCodeCoverage
  private static final ClassLoaderHelperExt INSTANCE = new ClassLoaderHelperExt ();

  private ClassLoaderHelperExt ()
  {}

  /**
   * Get the input stream of the passed resource using the specified class loader only. This is a
   * sanity wrapper around <code>classLoader.getResourceAsStream (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty. Internally it is
   *        ensured that the provided path does NOT start with a slash.
   * @return <code>null</code> if the path could not be resolved using the specified class loader.
   */
  @Nullable
  public static InputStream getResourceAsStream (@Nonnull final ClassLoader aClassLoader,
                                                 @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = internalGetPathWithoutLeadingSlash (sPath);

    // returns null if not found
    final InputStream aIS = aClassLoader.getResourceAsStream (sPathWithoutSlash);
    return StreamHelperExt.checkForInvalidFilterInputStream (aIS);
  }
}
