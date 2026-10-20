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
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * An {@link ILogHandler} that forwards the messages of all levels to a single {@link Consumer} or
 * {@link BiConsumer}. The level is not passed on. If a {@link Consumer} is used, the throwable is
 * not passed on either.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
@Immutable
public final class ConsumerLogHandler implements ILogHandler
{
  private final BiConsumer <? super String, ? super Throwable> m_aConsumer;

  /**
   * Constructor
   *
   * @param aConsumer
   *        The consumer to receive the message. The throwable is ignored. May not be
   *        <code>null</code>.
   */
  public ConsumerLogHandler (@NonNull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aConsumer = (sMsg, aThrowable) -> aConsumer.accept (sMsg);
  }

  /**
   * Constructor
   *
   * @param aConsumer
   *        The consumer to receive the message and the optional throwable. May not be
   *        <code>null</code>.
   */
  public ConsumerLogHandler (@NonNull final BiConsumer <? super String, ? super Throwable> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aConsumer = aConsumer;
  }

  public void trace (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aConsumer.accept (sMsg, aThrowable);
  }

  public void debug (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aConsumer.accept (sMsg, aThrowable);
  }

  public void info (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aConsumer.accept (sMsg, aThrowable);
  }

  public void warn (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aConsumer.accept (sMsg, aThrowable);
  }

  public void error (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aConsumer.accept (sMsg, aThrowable);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Consumer", m_aConsumer).getToString ();
  }
}
