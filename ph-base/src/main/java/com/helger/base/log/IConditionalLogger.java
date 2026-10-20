/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.log;

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.misc.ChangeNextMajorRelease;
import com.helger.annotation.misc.DevelopersNote;

/**
 * Conditional logger activity interface. It combines all five log levels and is therefore also an
 * {@link ILogHandler}. The methods taking an {@link Exception} are kept for binary compatibility
 * and delegate to the methods taking a {@link Throwable}.
 *
 * @author Philip Helger
 * @since 11.1.4
 */
@ChangeNextMajorRelease ("Remove interface")
@Deprecated (since = "12.5.2")
public interface IConditionalLogger extends ILogHandler
{
  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void trace (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    trace (aMsgSupplier, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void trace (@NonNull final String sMsg, @Nullable final Exception ex)
  {
    trace (sMsg, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void debug (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    debug (aMsgSupplier, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void debug (@NonNull final String sMsg, @Nullable final Exception ex)
  {
    debug (sMsg, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void info (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    info (aMsgSupplier, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void info (@NonNull final String sMsg, @Nullable final Exception ex)
  {
    info (sMsg, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void warn (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    warn (aMsgSupplier, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void warn (@NonNull final String sMsg, @Nullable final Exception ex)
  {
    warn (sMsg, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void error (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    error (aMsgSupplier, (Throwable) ex);
  }

  @Deprecated (since = "12.5.2")
  @DevelopersNote ("No need to do anything - in the next major release, this will be removed and the Throwable version will be used")
  default void error (@NonNull final String sMsg, @Nullable final Exception ex)
  {
    error (sMsg, (Throwable) ex);
  }
}
