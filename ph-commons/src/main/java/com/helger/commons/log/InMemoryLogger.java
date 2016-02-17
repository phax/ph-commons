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
package com.helger.commons.log;

import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.error.IHasErrorLevels;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Keeps a set of {@link LogMessage} objects in memory, offering an API similar
 * to SLF4J.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class InMemoryLogger implements Iterable <LogMessage>, IHasSize, IClearable, IHasErrorLevels, Serializable
{
  private final ICommonsList <LogMessage> m_aMessages = new CommonsList <> ();

  /**
   * Override this method to create a different LogMessage object or to filter
   * certain log messages.
   *
   * @param eErrorLevel
   *        Error level. Never <code>null</code>.
   * @param aMsg
   *        The message object. Never <code>null</code>.
   * @param t
   *        An optional exception. May be <code>null</code>.
   * @return The returned value. May be <code>null</code> in which case the
   *         message will not be logged.
   */
  @Nullable
  @OverrideOnDemand
  protected LogMessage createLogMessage (@Nonnull final IErrorLevel eErrorLevel,
                                         @Nonnull final Serializable aMsg,
                                         @Nullable final Throwable t)
  {
    return new LogMessage (eErrorLevel, aMsg, t);
  }

  /**
   * Callback method that is invoked after a message was added.
   *
   * @param aLogMessage
   *        The added log message. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void onAddLogMessage (@Nonnull final LogMessage aLogMessage)
  {}

  public void log (@Nonnull final IErrorLevel eErrorLevel, @Nonnull final Serializable aMsg)
  {
    log (eErrorLevel, aMsg, null);
  }

  public void log (@Nonnull final IErrorLevel eErrorLevel,
                   @Nonnull final Serializable aMsg,
                   @Nullable final Throwable t)
  {
    final LogMessage aLogMessage = createLogMessage (eErrorLevel, aMsg, t);
    if (aLogMessage != null)
    {
      m_aMessages.add (aLogMessage);
      onAddLogMessage (aLogMessage);
    }
  }

  public void error (@Nonnull final Serializable aMsg)
  {
    error (aMsg, null);
  }

  public void error (@Nonnull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.ERROR, aMsg, t);
  }

  public void warn (@Nonnull final Serializable aMsg)
  {
    warn (aMsg, null);
  }

  public void warn (@Nonnull final Serializable aMsg, @Nullable final Throwable t)
  {
    log (EErrorLevel.WARN, aMsg, t);
  }

  public void info (@Nonnull final Serializable aMsg)
  {
    log (EErrorLevel.INFO, aMsg, null);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <LogMessage> getAllMessages ()
  {
    return m_aMessages.getCopy ();
  }

  @Nonnull
  public Iterator <LogMessage> iterator ()
  {
    return m_aMessages.iterator ();
  }

  @Nonnegative
  public int getSize ()
  {
    return m_aMessages.size ();
  }

  public boolean isEmpty ()
  {
    return m_aMessages.isEmpty ();
  }

  public boolean containsOnlySuccess ()
  {
    return m_aMessages.containsOnly (e -> e.isSuccess ());
  }

  public boolean containsAtLeastOneSuccess ()
  {
    return m_aMessages.containsAny (e -> e.isSuccess ());
  }

  public boolean containsNoSuccess ()
  {
    return m_aMessages.containsNone (e -> e.isSuccess ());
  }

  @Nonnegative
  public int getSuccessCount ()
  {
    return m_aMessages.getCount (e -> e.isSuccess ());
  }

  public boolean containsOnlyFailure ()
  {
    return m_aMessages.containsOnly (e -> e.isFailure ());
  }

  public boolean containsAtLeastOneFailure ()
  {
    return m_aMessages.containsAny (e -> e.isFailure ());
  }

  public boolean containsNoFailure ()
  {
    return m_aMessages.containsNone (e -> e.isFailure ());
  }

  @Nonnegative
  public int getFailureCount ()
  {
    return m_aMessages.getCount (e -> e.isFailure ());
  }

  public boolean containsOnlyError ()
  {
    return m_aMessages.containsOnly (e -> e.isError ());
  }

  public boolean containsAtLeastOneError ()
  {
    return m_aMessages.containsAny (e -> e.isError ());
  }

  public boolean containsNoError ()
  {
    return m_aMessages.containsNone (e -> e.isError ());
  }

  @Nonnegative
  public int getErrorCount ()
  {
    return m_aMessages.getCount (e -> e.isError ());
  }

  @Nonnull
  public IErrorLevel getMostSevereErrorLevel ()
  {
    IErrorLevel aRet = EErrorLevel.SUCCESS;
    for (final LogMessage aMessage : m_aMessages)
    {
      final IErrorLevel aCur = aMessage.getErrorLevel ();
      if (aCur.isMoreSevereThan (aRet))
      {
        aRet = aCur;
        if (aRet.isHighest ())
          break;
      }
    }
    return aRet;
  }

  @Nonnull
  public EChange clear ()
  {
    return m_aMessages.removeAll ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("messages", m_aMessages).toString ();
  }
}
