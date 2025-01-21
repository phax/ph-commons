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
package com.helger.commons.statistics;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link IMutableStatisticsHandlerKeyedTimer}
 *
 * @author Philip Helger
 */
@ThreadSafe
public class StatisticsHandlerKeyedTimer extends AbstractStatisticsHandlerKeyedNumeric implements
                                         IMutableStatisticsHandlerKeyedTimer
{
  private static final Logger LOGGER = LoggerFactory.getLogger (StatisticsHandlerKeyedTimer.class);

  public void addTime (@Nullable final String sKey, @Nonnegative final long nMillis)
  {
    if (nMillis < 0)
      LOGGER.warn ("A negative value (" + nMillis + ") for key '" + sKey + "' is added to " + getClass ().getName ());
    addValue (sKey, nMillis);
  }
}
