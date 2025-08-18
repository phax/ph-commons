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
package com.helger.io.clazz;

import java.io.InputStream;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.lang.clazz.ClassHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * {@link Class} helper methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassHelperExt extends ClassHelper
{

  @PresentForCodeCoverage
  private static final ClassHelperExt INSTANCE = new ClassHelperExt ();

  private ClassHelperExt ()
  {}

  /**
   * Get the input stream of the passed resource using the class loader of the specified class only.
   * This is a sanity wrapper around <code>class.getResourceAsStream (sPath)</code>.
   *
   * @param aClass
   *        The class to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty. Internally it is
   *        ensured that the provided path does start with a slash.
   * @return <code>null</code> if the path could not be resolved using the specified class loader.
   */
  @Nullable
  public static InputStream getResourceAsStream (@Nonnull final Class <?> aClass, @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does start with a "/"
    final String sPathWithSlash = internalGetPathWithLeadingSlash (sPath);

    // returns null if not found
    final InputStream aIS = aClass.getResourceAsStream (sPathWithSlash);
    return StreamHelper.checkForInvalidFilterInputStream (aIS);
  }
}
