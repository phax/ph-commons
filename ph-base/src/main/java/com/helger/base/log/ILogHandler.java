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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * Abstraction of a message sink with the three levels info, warn and error (see
 * {@link IInfoLogger}, {@link IWarnLogger} and {@link IErrorLogger}). It allows a class to report
 * problems without knowing whether they are logged, collected, forwarded or ignored. Use
 * {@link #createForLogger(Logger)} for the usual "just log it" default,
 * {@link #createForConsumer(Consumer)} or {@link #createForBiConsumer(BiConsumer)} to plug in a
 * lambda and {@link #createNoOp()} to discard everything.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
public interface ILogHandler extends ITraceLogger, IDebugLogger, IInfoLogger, IWarnLogger, IErrorLogger
{
  /**
   * @param aLogger
   *        The SLF4J logger to log to. May not be <code>null</code>.
   * @return A log handler that logs to the passed logger on the matching level.
   */
  @NonNull
  static ILogHandler createForLogger (@NonNull final Logger aLogger)
  {
    return new LoggerLogHandler (aLogger);
  }

  /**
   * @return A log handler that discards everything. Never <code>null</code>.
   */
  @NonNull
  static ILogHandler createNoOp ()
  {
    return NoOpLogHandler.INSTANCE;
  }

  /**
   * @param aConsumer
   *        The consumer that receives the message of all levels. A throwable is not passed on. May
   *        not be <code>null</code>.
   * @return A log handler that forwards the messages of all levels to the passed consumer.
   */
  @NonNull
  static ILogHandler createForConsumer (@NonNull final Consumer <? super String> aConsumer)
  {
    return new ConsumerLogHandler (aConsumer);
  }

  /**
   * @param aConsumer
   *        The consumer that receives the message and the optional throwable of all levels. May not
   *        be <code>null</code>.
   * @return A log handler that forwards the messages of all levels to the passed consumer.
   */
  @NonNull
  static ILogHandler createForBiConsumer (@NonNull final BiConsumer <? super String, ? super Throwable> aConsumer)
  {
    return new ConsumerLogHandler (aConsumer);
  }
}
