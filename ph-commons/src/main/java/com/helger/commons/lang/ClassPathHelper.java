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

import java.io.PrintStream;
import java.util.function.Consumer;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.system.SystemProperties;

import jakarta.annotation.Nonnull;

/**
 * Utility class for dealing with the Java class path.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassPathHelper
{
  @PresentForCodeCoverage
  private static final ClassPathHelper INSTANCE = new ClassPathHelper ();

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
   * Iterate all class path entries and invoke the provided consumer.
   *
   * @param aConsumer
   *        The target consumer invoked for all entries. May not be
   *        <code>null</code>.
   */
  public static void forAllClassPathEntries (@Nonnull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
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
