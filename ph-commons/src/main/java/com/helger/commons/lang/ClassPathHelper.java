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
package com.helger.commons.lang;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.system.SystemProperties;

/**
 * Utility class for dealing with the Java class path.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassPathHelper
{
  @PresentForCodeCoverage
  private static final ClassPathHelper s_aInstance = new ClassPathHelper ();

  private ClassPathHelper ()
  {}

  /**
   * @return A non-<code>null</code> list of all directories and files currently
   *         in the class path.
   */
  @Nonnull
  public static List <String> getAllClassPathEntries ()
  {
    return StringHelper.getExploded (SystemProperties.getPathSeparator (), SystemProperties.getJavaClassPath ());
  }

  /**
   * Add all class path entries into the provided target list.
   *
   * @param aTarget
   *        The target collection to be filled. May not be null.
   */
  public static void getAllClassPathEntries (@Nonnull final Collection <String> aTarget)
  {
    StringHelper.getExploded (SystemProperties.getPathSeparator (), SystemProperties.getJavaClassPath (), -1, aTarget);
  }

  /**
   * Print all class path entries on the passed print stream, using the system
   * line separator
   *
   * @param aPS
   *        The print stream to print to. May not be <code>null</code>.
   */
  public static void printClassPathEntries (@Nonnull final PrintStream aPS)
  {
    printClassPathEntries (aPS, ENewLineMode.DEFAULT.getText ());
  }

  /**
   * Print all class path entries on the passed print stream, using the passed
   * separator
   *
   * @param aPS
   *        The print stream to print to. May not be <code>null</code>.
   * @param sItemSeparator
   *        The separator to be printed between each item.
   */
  public static void printClassPathEntries (@Nonnull final PrintStream aPS, @Nonnull final String sItemSeparator)
  {
    for (final String sClassPathEntry : getAllClassPathEntries ())
    {
      aPS.print (sClassPathEntry);
      aPS.print (sItemSeparator);
    }
  }
}
