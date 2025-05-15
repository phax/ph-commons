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
package com.helger.commons.concurrent;

import java.util.concurrent.ThreadFactory;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.builder.IResettableBuilder;
import com.helger.commons.state.ETriState;

/**
 * <p>
 * A <em>builder</em> class for creating instances of {@code
 * BasicThreadFactory}.
 * </p>
 * <p>
 * Using this builder class instances of {@code BasicThreadFactory} can be created and
 * initialized. The class provides methods that correspond to the configuration options supported
 * by {@code BasicThreadFactory}. Method chaining is supported. Refer to the documentation of
 * {@code
 * BasicThreadFactory} for a usage example.
 * </p>
 *
 * @version $Id: BasicThreadFactory.java 1583482 2014-03-31 22:54:57Z niallp $
 */
public class BasicThreadFactoryBuilder implements IResettableBuilder <BasicThreadFactory>
{
  /** The wrapped factory. */
  ThreadFactory m_aWrappedFactory;

  /** The ThreadGroup for the default ThreadFactory */
  ThreadGroup m_aThreadGroup;

  /** The uncaught exception handler. */
  Thread.UncaughtExceptionHandler m_aUncaughtExceptionHandler = BasicThreadFactory.s_aDefaultUncaughtExceptionHandler;

  /** The naming pattern. */
  String m_sNamingPattern;

  /** The priority. */
  Integer m_nPriority;

  /** The daemon flag. */
  ETriState m_eDaemon = ETriState.UNDEFINED;

  public BasicThreadFactoryBuilder ()
  {}

  /**
   * Sets the {@code ThreadFactory} to be wrapped by the new {@code
   * BasicThreadFactory}.
   *
   * @param aWrappedFactory
   *        the wrapped {@code ThreadFactory} (must not be <b>null</b>)
   * @return this for chaining
   * @throws NullPointerException
   *         if the passed in {@code ThreadFactory} is <b>null</b>
   */
  @Nonnull
  public final BasicThreadFactoryBuilder wrappedFactory (@Nonnull final ThreadFactory aWrappedFactory)
  {
    ValueEnforcer.notNull (aWrappedFactory, "Factory");
    m_aWrappedFactory = aWrappedFactory;
    return this;
  }

  /**
   * Sets the {@code ThreadGroup} to be used by the default thread factory. If
   * {@link #wrappedFactory(ThreadFactory)} is used, this setting is useless.
   *
   * @param aThreadGroup
   *        the {@code ThreadGroup} to use. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final BasicThreadFactoryBuilder threadGroup (@Nullable final ThreadGroup aThreadGroup)
  {
    m_aThreadGroup = aThreadGroup;
    return this;
  }

  /**
   * Sets the uncaught exception handler for the threads created by the new
   * {@code BasicThreadFactory}.
   *
   * @param aExceptionHandler
   *        the {@code UncaughtExceptionHandler} (must not be <b>null</b>)
   * @return this for chaining
   * @throws NullPointerException
   *         if the exception handler is <b>null</b>
   */
  @Nonnull
  public final BasicThreadFactoryBuilder uncaughtExceptionHandler (@Nonnull final Thread.UncaughtExceptionHandler aExceptionHandler)
  {
    ValueEnforcer.notNull (aExceptionHandler, "ExceptionHandler");
    m_aUncaughtExceptionHandler = aExceptionHandler;
    return this;
  }

  /**
   * Sets the naming pattern to be used by the new {@code
   * BasicThreadFactory}. The formatting is done with {@link String#format(String, Object...)}
   * using the thread counter (type long) as the only parameter.
   *
   * @param sNamingPattern
   *        the naming pattern (must not be <b>null</b>)
   * @return this for chaining
   * @throws NullPointerException
   *         if the naming pattern is <b>null</b>
   */
  @Nonnull
  public final BasicThreadFactoryBuilder namingPattern (@Nonnull final String sNamingPattern)
  {
    ValueEnforcer.notNull (sNamingPattern, "NamingPattern");
    m_sNamingPattern = sNamingPattern;
    return this;
  }

  /**
   * Sets the priority for the threads created by the new {@code
   * BasicThreadFactory}.
   *
   * @param nPriority
   *        the priority
   * @return this for chaining
   */
  @Nonnull
  public final BasicThreadFactoryBuilder priority (final int nPriority)
  {
    m_nPriority = Integer.valueOf (nPriority);
    return this;
  }

  /**
   * Sets the daemon flag for the new {@code BasicThreadFactory}. If this flag is set to
   * <b>true</b> the new thread factory will create daemon threads.
   *
   * @param bDaemon
   *        the value of the daemon flag
   * @return this for chaining
   */
  @Nonnull
  public final BasicThreadFactoryBuilder daemon (final boolean bDaemon)
  {
    m_eDaemon = ETriState.valueOf (bDaemon);
    return this;
  }

  /**
   * Resets this builder. All configuration options are set to default values. Note: If the
   * {@link #build()} method was called, it is not necessary to call {@code reset()} explicitly
   * because this is done automatically.
   */
  public void reset ()
  {
    m_aWrappedFactory = null;
    m_aThreadGroup = null;
    m_aUncaughtExceptionHandler = null;
    m_sNamingPattern = null;
    m_nPriority = null;
    m_eDaemon = null;
  }

  /**
   * Creates a new {@code BasicThreadFactory} with all configuration options that have been
   * specified by calling methods on this builder. After creating the factory {@link #reset()} is
   * called.
   *
   * @return the new {@code BasicThreadFactory}
   */
  @Nonnull
  public BasicThreadFactory build ()
  {
    return new BasicThreadFactory (this);
  }
}