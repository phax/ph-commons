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
package com.helger.commons.lang;

import java.io.PrintStream;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
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
  @ReturnsMutableCopy
  public static ICommonsList <String> getAllClassPathEntries ()
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    forAllClassPathEntries (ret::add);
    return ret;
  }

  /**
   * Add all class path entries into the provided target list.
   *
   * @param aConsumer
   *        The target consumer invoked for all entries. May not be
   *        <code>null</code>.
   */
  public static void forAllClassPathEntries (@Nonnull final Consumer <? super String> aConsumer)
  {
    StringHelper.explode (SystemProperties.getPathSeparator (), SystemProperties.getJavaClassPath (), aConsumer);
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
    forAllClassPathEntries (x -> {
      aPS.print (x);
      aPS.print (sItemSeparator);
    });
  }
}
