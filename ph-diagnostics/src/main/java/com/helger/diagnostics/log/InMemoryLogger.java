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
package com.helger.diagnostics.log;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.state.EChange;
import com.helger.base.state.IClearable;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.diagnostics.error.level.IHasErrorLevels;

/**
 * Keeps a set of {@link LogMessage} objects in memory, offering an API similar to SLF4J.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class InMemoryLogger implements IHasErrorLevels <LogMessage>, IClearable
{
  private final ICommonsList <LogMessage> m_aMessages = new CommonsArrayList <> ();

  @NonNull
  @OverrideOnDemand
  protected LocalDateTime getCurrentLocalDateTime ()
  {
    // Should be PDTFactory.getCurrentLocalDateTime but it's not available due to dependency
    // limitations. That's why it became a protected method
    return LocalDateTime.now (Clock.systemDefaultZone ());
  }

  /**
   * Override this method to create a different LogMessage object or to filter certain log messages.
   *
   * @param aErrorLevel
   *        Error level. Never <code>null</code>.
   * @param aMsg
   *        The message object. Never <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   * @return The returned value. May be <code>null</code> in which case the message will not be
   *         logged.
   */
  @Nullable
  @OverrideOnDemand
  protected LogMessage createLogMessage (@NonNull final IErrorLevel aErrorLevel,
                                         @NonNull final Serializable aMsg,
                                         @Nullable final Throwable t)
  {
    return new LogMessage (getCurrentLocalDateTime (), aErrorLevel, aMsg, t);
  }

  /**
   * Callback method that is invoked after a message was added.
   *
   * @param aLogMessage
   *        The added log message. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void onAddLogMessage (@NonNull final LogMessage aLogMessage)
  {}

  /**
   * Log a message at the specified error level.
   *
   * @param eErrorLevel
   *        The error level to use. May not be <code>null</code>.
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   */
  public void log (@NonNull final IErrorLevel eErrorLevel, @NonNull final Serializable aMsg)
  {
    log (eErrorLevel, aMsg, null);
  }

  /**
   * Log a message at the specified error level with an optional exception.
   *
   * @param eErrorLevel
   *        The error level to use. May not be <code>null</code>.
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   */
  public void log (@NonNull final IErrorLevel eErrorLevel,
                   @NonNull final Serializable aMsg,
                   @Nullable final Throwable t)
  {
    final LogMessage aLogMessage = createLogMessage (eErrorLevel, aMsg, t);
    if (aLogMessage != null)
    {
      m_aMessages.add (aLogMessage);
      onAddLogMessage (aLogMessage);
    }
  }

  /**
   * Log a message at the error level.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   */
  public void error (@NonNull final Serializable aMsg)
  {
    error (aMsg, null);
  }

  /**
   * Log a message at the error level with an optional exception.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   */
  public void error (@NonNull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.ERROR, aMsg, t);
  }

  /**
   * Log a message at the warning level.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   */
  public void warn (@NonNull final Serializable aMsg)
  {
    warn (aMsg, null);
  }

  /**
   * Log a message at the warning level with an optional exception.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   */
  public void warn (@NonNull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.WARN, aMsg, t);
  }

  /**
   * Log a message at the info level.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   */
  public void info (@NonNull final Serializable aMsg)
  {
    log (EErrorLevel.INFO, aMsg, null);
  }

  /**
   * Log a message at the info level with an optional exception.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   */
  public void info (@NonNull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.INFO, aMsg, t);
  }

  /**
   * Log a message at the success level.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   */
  public void success (@NonNull final Serializable aMsg)
  {
    log (EErrorLevel.SUCCESS, aMsg, null);
  }

  /**
   * Log a message at the success level with an optional exception.
   *
   * @param aMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   */
  public void success (@NonNull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.SUCCESS, aMsg, t);
  }

  /**
   * @return A copy of all log messages. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <LogMessage> getAllMessages ()
  {
    return m_aMessages.getClone ();
  }

  /** {@inheritDoc} */
  @NonNull
  public Iterator <LogMessage> iterator ()
  {
    return m_aMessages.iterator ();
  }

  /**
   * @return The number of contained log messages. Always &ge; 0.
   */
  @Nonnegative
  public int size ()
  {
    return m_aMessages.size ();
  }

  /**
   * @return <code>true</code> if no log messages are contained, <code>false</code> otherwise.
   */
  public boolean isEmpty ()
  {
    return m_aMessages.isEmpty ();
  }

  /** {@inheritDoc} */
  @NonNull
  public EChange removeAll ()
  {
    return m_aMessages.removeAll ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("messages", m_aMessages).getToString ();
  }
}
