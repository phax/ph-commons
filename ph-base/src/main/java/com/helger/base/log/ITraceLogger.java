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

/**
 * Logging interface for the level <code>trace</code>. Implementations only need to provide
 * {@link #trace(String, Throwable)}, all other methods delegate to it by default.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
public interface ITraceLogger
{
  /**
   * Log a message with an optional throwable.
   *
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   * @param aThrowable
   *        An optional associated throwable. May be <code>null</code>.
   */
  void trace (@NonNull String sMsg, @Nullable Throwable aThrowable);

  /**
   * Log a message without a throwable.
   *
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   */
  default void trace (@NonNull final String sMsg)
  {
    trace (sMsg, null);
  }

  /**
   * Log a message that is only created if needed. This default implementation always evaluates the
   * supplier, implementations may be smarter.
   *
   * @param aMsgSupplier
   *        The message supplier. May not be <code>null</code>.
   */
  default void trace (@NonNull final Supplier <String> aMsgSupplier)
  {
    trace (aMsgSupplier.get (), null);
  }

  /**
   * Log a message that is only created if needed, with an optional throwable. This default
   * implementation always evaluates the supplier, implementations may be smarter.
   *
   * @param aMsgSupplier
   *        The message supplier. May not be <code>null</code>.
   * @param aThrowable
   *        An optional associated throwable. May be <code>null</code>.
   */
  default void trace (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    trace (aMsgSupplier.get (), aThrowable);
  }
}
