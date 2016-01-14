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
package com.helger.commons.system;

import java.nio.charset.Charset;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Get information about the system we're running on.
 *
 * @author Philip Helger
 */
@Immutable
public final class SystemHelper
{
  private static final int s_nNumberOfProcessors;

  static
  {
    s_nNumberOfProcessors = Runtime.getRuntime ().availableProcessors ();
  }

  @PresentForCodeCoverage
  private static final SystemHelper s_aInstance = new SystemHelper ();

  private SystemHelper ()
  {}

  /**
   * @return The number of processors the computer has.
   */
  @Nonnegative
  public static int getNumberOfProcessors ()
  {
    return s_nNumberOfProcessors;
  }

  /**
   * @return The current processor architecture and never <code>null</code>.
   */
  @Nonnull
  public static EProcessorArchitecture getProcessorArchitecture ()
  {
    return EProcessorArchitecture.getCurrentArchitecture ();
  }

  /**
   * @return The operating system we're running on.
   */
  @Nonnull
  public static EOperatingSystem getOperatingSystem ()
  {
    return EOperatingSystem.getCurrentOS ();
  }

  /**
   * @return The name and version of the operating system we're running on.
   */
  @Nonnull
  public static String getOperatingSystemName ()
  {
    return EOperatingSystem.getCurrentOSName () + " [" + EOperatingSystem.getCurrentOSVersion () + "]";
  }

  /**
   * @return The current Java version that is running. Never <code>null</code>.
   */
  @Nonnull
  public static EJavaVersion getJavaVersion ()
  {
    return EJavaVersion.getCurrentVersion ();
  }

  /**
   * @return The vendor of the Java Virtual Machine (JVM) that we're operating
   *         on.
   */
  @Nonnull
  public static EJVMVendor getJVMVendor ()
  {
    return EJVMVendor.getCurrentVendor ();
  }

  /**
   * @return The system locale.
   */
  @Nonnull
  public static Locale getSystemLocale ()
  {
    return Locale.getDefault ();
  }

  /**
   * @return The system charset.
   */
  @Nonnull
  public static Charset getSystemCharset ()
  {
    return Charset.defaultCharset ();
  }

  /**
   * @return The name of the system charset.
   */
  @Nonnull
  public static String getSystemCharsetName ()
  {
    return getSystemCharset ().name ();
  }

  /**
   * Returns the amount of free memory in the Java Virtual Machine.
   *
   * @return an approximation to the total amount of memory currently available
   *         for future allocated objects, measured in bytes.
   */
  @Nonnegative
  public static long getFreeMemory ()
  {
    return Runtime.getRuntime ().freeMemory ();
  }

  /**
   * Returns the maximum amount of memory that the Java virtual machine will
   * attempt to use. If there is no inherent limit then the value
   * {@link java.lang.Long#MAX_VALUE} will be returned.
   *
   * @return the maximum amount of memory that the virtual machine will attempt
   *         to use, measured in bytes
   */
  @Nonnegative
  public static long getMaxMemory ()
  {
    return Runtime.getRuntime ().maxMemory ();
  }

  /**
   * Returns the total amount of memory in the Java virtual machine. The value
   * returned by this method may vary over time, depending on the host
   * environment.
   * <p>
   * Note that the amount of memory required to hold an object of any given type
   * may be implementation-dependent.
   * </p>
   *
   * @return the total amount of memory currently available for current and
   *         future objects, measured in bytes.
   */
  @Nonnegative
  public static long getTotalMemory ()
  {
    return Runtime.getRuntime ().totalMemory ();
  }
}
