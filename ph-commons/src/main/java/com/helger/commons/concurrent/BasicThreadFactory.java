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
package com.helger.commons.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.ToStringGenerator;

/**
 * <p>
 * An implementation of the {@code ThreadFactory} interface that provides some
 * configuration options for the threads it creates.
 * </p>
 * <p>
 * A {@code ThreadFactory} is used for instance by an {@code ExecutorService} to
 * create the threads it uses for executing tasks. In many cases users do not
 * have to care about a {@code ThreadFactory} because the default one used by an
 * {@code ExecutorService} will do. However, if there are special requirements
 * for the threads, a custom {@code ThreadFactory} has to be created.
 * </p>
 * <p>
 * This class provides some frequently needed configuration options for the
 * threads it creates. These are the following:
 * </p>
 * <ul>
 * <li>A name pattern for the threads created by this factory can be specified.
 * This is often useful if an application uses multiple executor services for
 * different purposes. If the names of the threads used by these services have
 * meaningful names, log output or exception traces can be much easier to read.
 * Naming patterns are <em>format strings</em> as used by the {@code
 * String.format()} method. The string can contain the place holder {@code %d}
 * which will be replaced by the number of the current thread ({@code
 * ThreadFactoryImpl} keeps a counter of the threads it has already created).
 * For instance, the naming pattern {@code "My %d. worker thread"} will result
 * in thread names like {@code "My 1. worker thread"}, {@code
 * "My 2. worker thread"} and so on.</li>
 * <li>A flag whether the threads created by this factory should be daemon
 * threads. This can impact the exit behavior of the current Java application
 * because the JVM shuts down if there are only daemon threads running.</li>
 * <li>The priority of the thread. Here an integer value can be provided. The
 * {@code java.lang.Thread} class defines constants for valid ranges of priority
 * values.</li>
 * <li>The {@code UncaughtExceptionHandler} for the thread. This handler is
 * called if an uncaught exception occurs within the thread.</li>
 * </ul>
 * <p>
 * {@code BasicThreadFactory} wraps another thread factory which actually
 * creates new threads. The configuration options are set on the threads created
 * by the wrapped thread factory. On construction time the factory to be wrapped
 * can be specified. If none is provided, a default {@code ThreadFactory} is
 * used.
 * </p>
 * <p>
 * Instances of {@code BasicThreadFactory} are not created directly, but the
 * nested {@code Builder} class is used for this purpose. Using the builder only
 * the configuration options an application is interested in need to be set. The
 * following example shows how a {@code BasicThreadFactory} is created and
 * installed in an {@code ExecutorService}:
 * </p>
 *
 * <pre>
 * // Create a factory that produces daemon threads with a naming pattern and
 * // a priority
 * BasicThreadFactory factory = new BasicThreadFactory.Builder ().setNamingPattern (&quot;workerthread-%d&quot;)
 *                                                               .setDaemon (true)
 *                                                               .setPriority (Thread.MAX_PRIORITY)
 *                                                               .build ();
 * // Create an executor service for single-threaded execution
 * ExecutorService exec = Executors.newSingleThreadExecutor (factory);
 * </pre>
 *
 * @since 8.5.3
 */
public class BasicThreadFactory implements ThreadFactory
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (BasicThreadFactory.class);

  private static Thread.UncaughtExceptionHandler s_aDefaultUncaughtExceptionHandler = (t, e) -> {
    if (s_aLogger.isErrorEnabled ())
      s_aLogger.error ("Uncaught exception from Thread " + t.getName (), e);
  };

  /**
   * Set the default uncaught exception handler for future instances of
   * BasicThreadFactory. By default a logging exception handler is present.
   *
   * @param aHdl
   *        The handlers to be used. May not be <code>null</code>.
   * @since 9.0.0
   */
  public static void setDefaultUncaughtExceptionHandler (@Nonnull final Thread.UncaughtExceptionHandler aHdl)
  {
    ValueEnforcer.notNull (aHdl, "DefaultUncaughtExceptionHandler");
    s_aDefaultUncaughtExceptionHandler = aHdl;
  }

  /**
   * @return The default uncaught exception handler used. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler ()
  {
    return s_aDefaultUncaughtExceptionHandler;
  }

  /** A counter for the threads created by this factory. */
  private final AtomicLong m_aThreadCounter;

  /** Stores the wrapped factory. */
  private final ThreadFactory m_aWrappedFactory;

  /** Stores the uncaught exception handler. */
  private final Thread.UncaughtExceptionHandler m_aUncaughtExceptionHandler;

  /** Stores the naming pattern for newly created threads. */
  private final String m_sNamingPattern;

  /** Stores the priority. */
  private final Integer m_aPriority;

  /** Stores the daemon status flag. */
  private final ETriState m_eDaemon;

  /**
   * Creates a new instance of {@code ThreadFactoryImpl} and configures it from
   * the specified {@code Builder} object.
   *
   * @param aBuilder
   *        the {@code Builder} object
   */
  protected BasicThreadFactory (@Nonnull final Builder aBuilder)
  {
    m_aThreadCounter = new AtomicLong ();

    if (aBuilder.m_aWrappedFactory == null)
      m_aWrappedFactory = Executors.defaultThreadFactory ();
    else
      m_aWrappedFactory = aBuilder.m_aWrappedFactory;

    m_sNamingPattern = aBuilder.m_sNamingPattern;
    m_aPriority = aBuilder.m_nPriority;
    m_eDaemon = aBuilder.m_eDaemon;
    m_aUncaughtExceptionHandler = aBuilder.m_aUncaughtExceptionHandler;
  }

  /**
   * Returns the wrapped {@code ThreadFactory}. This factory is used for
   * actually creating threads. This method never returns <b>null</b>. If no
   * {@code ThreadFactory} was passed when this object was created, a default
   * thread factory is returned.
   *
   * @return the wrapped {@code ThreadFactory}
   */
  @Nonnull
  public final ThreadFactory getWrappedFactory ()
  {
    return m_aWrappedFactory;
  }

  /**
   * Returns the naming pattern for naming newly created threads. Result can be
   * <b>null</b> if no naming pattern was provided.
   *
   * @return the naming pattern
   */
  @Nonnull
  public final String getNamingPattern ()
  {
    return m_sNamingPattern;
  }

  /**
   * Returns the daemon flag. This flag determines whether newly created threads
   * should be daemon threads. If <b>true</b>, this factory object calls
   * {@code setDaemon(true)} on the newly created threads. Result can be
   * <b>null</b> if no daemon flag was provided at creation time.
   *
   * @return the daemon flag
   */
  @Nonnull
  public final ETriState getDaemon ()
  {
    return m_eDaemon;
  }

  /**
   * Returns the priority of the threads created by this factory. Result can be
   * <b>null</b> if no priority was specified.
   *
   * @return the priority for newly created threads
   */
  @Nullable
  public final Integer getPriority ()
  {
    return m_aPriority;
  }

  /**
   * Returns the {@code UncaughtExceptionHandler} for the threads created by
   * this factory. Result can be <b>null</b> if no handler was provided.
   *
   * @return the {@code UncaughtExceptionHandler}
   */
  public final Thread.UncaughtExceptionHandler getUncaughtExceptionHandler ()
  {
    return m_aUncaughtExceptionHandler;
  }

  /**
   * Returns the number of threads this factory has already created. This class
   * maintains an internal counter that is incremented each time the
   * {@link #newThread(Runnable)} method is invoked.
   *
   * @return the number of threads created by this factory
   */
  @Nonnull
  public long getThreadCount ()
  {
    return m_aThreadCounter.get ();
  }

  /**
   * Initializes the specified thread. This method is called by
   * {@link #newThread(Runnable)} after a new thread has been obtained from the
   * wrapped thread factory. It initializes the thread according to the options
   * set for this factory.
   *
   * @param aThread
   *        the thread to be initialized
   */
  @OverrideOnDemand
  protected void initializeThread (@Nonnull final Thread aThread)
  {
    if (m_sNamingPattern != null)
    {
      final Long aCount = Long.valueOf (m_aThreadCounter.incrementAndGet ());
      aThread.setName (String.format (CGlobal.DEFAULT_LOCALE, m_sNamingPattern, aCount));
    }

    if (m_aUncaughtExceptionHandler != null)
      aThread.setUncaughtExceptionHandler (m_aUncaughtExceptionHandler);

    if (m_aPriority != null)
      aThread.setPriority (m_aPriority.intValue ());

    if (m_eDaemon.isDefined ())
      aThread.setDaemon (m_eDaemon.getAsBooleanValue ());
  }

  /**
   * Creates a new thread. This implementation delegates to the wrapped factory
   * for creating the thread. Then, on the newly created thread the
   * corresponding configuration options are set.
   *
   * @param aRunnable
   *        the {@code Runnable} to be executed by the new thread
   * @return the newly created thread
   */
  @Nonnull
  public Thread newThread (@Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");
    final Thread t = getWrappedFactory ().newThread (aRunnable);
    initializeThread (t);
    return t;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ThreadCounter", m_aThreadCounter)
                                       .append ("WrappedFactory", m_aWrappedFactory)
                                       .append ("UncaughtExceptionHandler", m_aUncaughtExceptionHandler)
                                       .append ("NamingPattern", m_sNamingPattern)
                                       .append ("Priority", m_aPriority)
                                       .append ("Daemon", m_eDaemon)
                                       .getToString ();
  }

  /**
   * <p>
   * A <em>builder</em> class for creating instances of {@code
   * BasicThreadFactory}.
   * </p>
   * <p>
   * Using this builder class instances of {@code BasicThreadFactory} can be
   * created and initialized. The class provides methods that correspond to the
   * configuration options supported by {@code BasicThreadFactory}. Method
   * chaining is supported. Refer to the documentation of {@code
   * BasicThreadFactory} for a usage example.
   * </p>
   *
   * @version $Id: BasicThreadFactory.java 1583482 2014-03-31 22:54:57Z niallp $
   */
  public static class Builder
  {
    /** The wrapped factory. */
    private ThreadFactory m_aWrappedFactory;

    /** The uncaught exception handler. */
    private Thread.UncaughtExceptionHandler m_aUncaughtExceptionHandler = s_aDefaultUncaughtExceptionHandler;

    /** The naming pattern. */
    private String m_sNamingPattern;

    /** The priority. */
    private Integer m_nPriority;

    /** The daemon flag. */
    private ETriState m_eDaemon = ETriState.UNDEFINED;

    public Builder ()
    {}

    /**
     * Sets the {@code ThreadFactory} to be wrapped by the new {@code
     * BasicThreadFactory}.
     *
     * @param aWrappedFactory
     *        the wrapped {@code ThreadFactory} (must not be <b>null</b>)
     * @return a reference to this {@code Builder}
     * @throws NullPointerException
     *         if the passed in {@code ThreadFactory} is <b>null</b>
     */
    @Nonnull
    public final Builder setWrappedFactory (@Nonnull final ThreadFactory aWrappedFactory)
    {
      ValueEnforcer.notNull (aWrappedFactory, "Factory");
      m_aWrappedFactory = aWrappedFactory;
      return this;
    }

    /**
     * Sets the uncaught exception handler for the threads created by the new
     * {@code BasicThreadFactory}.
     *
     * @param aExceptionHandler
     *        the {@code UncaughtExceptionHandler} (must not be <b>null</b>)
     * @return a reference to this {@code Builder}
     * @throws NullPointerException
     *         if the exception handler is <b>null</b>
     */
    @Nonnull
    public final Builder setUncaughtExceptionHandler (@Nonnull final Thread.UncaughtExceptionHandler aExceptionHandler)
    {
      ValueEnforcer.notNull (aExceptionHandler, "ExceptionHandler");
      m_aUncaughtExceptionHandler = aExceptionHandler;
      return this;
    }

    /**
     * Sets the naming pattern to be used by the new {@code
     * BasicThreadFactory}. The formatting is done with
     * {@link String#format(String, Object...)} using the thread counter (type
     * long) as the only parameter.
     *
     * @param sNamingPattern
     *        the naming pattern (must not be <b>null</b>)
     * @return a reference to this {@code Builder}
     * @throws NullPointerException
     *         if the naming pattern is <b>null</b>
     */
    @Nonnull
    public final Builder setNamingPattern (@Nonnull final String sNamingPattern)
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
     * @return a reference to this {@code Builder}
     */
    @Nonnull
    public final Builder setPriority (final int nPriority)
    {
      m_nPriority = Integer.valueOf (nPriority);
      return this;
    }

    /**
     * Sets the daemon flag for the new {@code BasicThreadFactory}. If this flag
     * is set to <b>true</b> the new thread factory will create daemon threads.
     *
     * @param bDaemon
     *        the value of the daemon flag
     * @return a reference to this {@code Builder}
     */
    @Nonnull
    public final Builder setDaemon (final boolean bDaemon)
    {
      m_eDaemon = ETriState.valueOf (bDaemon);
      return this;
    }

    /**
     * Resets this builder. All configuration options are set to default values.
     * Note: If the {@link #build()} method was called, it is not necessary to
     * call {@code reset()} explicitly because this is done automatically.
     */
    public void reset ()
    {
      m_aWrappedFactory = null;
      m_aUncaughtExceptionHandler = null;
      m_sNamingPattern = null;
      m_nPriority = null;
      m_eDaemon = null;
    }

    /**
     * Creates a new {@code BasicThreadFactory} with all configuration options
     * that have been specified by calling methods on this builder. After
     * creating the factory {@link #reset()} is called.
     *
     * @return the new {@code BasicThreadFactory}
     */
    @Nonnull
    public BasicThreadFactory build ()
    {
      return build (true);
    }

    /**
     * Creates a new {@code BasicThreadFactory} with all configuration options
     * that have been specified by calling methods on this builder.
     *
     * @param bReset
     *        if true, {@link #reset()} is called after creating the factory
     * @return the new {@code BasicThreadFactory}
     */
    @Nonnull
    public BasicThreadFactory build (final boolean bReset)
    {
      final BasicThreadFactory ret = new BasicThreadFactory (this);
      if (bReset)
        reset ();
      return ret;
    }
  }
}
