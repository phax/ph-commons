/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.statistics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedSize;

import jakarta.annotation.Nullable;

/**
 * Default implementation of {@link IMutableStatisticsHandlerKeyedSize}
 *
 * @author Philip Helger
 */
@ThreadSafe
public class StatisticsHandlerKeyedSize extends AbstractStatisticsHandlerKeyedNumeric implements
                                        IMutableStatisticsHandlerKeyedSize
{
  private static final Logger LOGGER = LoggerFactory.getLogger (StatisticsHandlerKeyedSize.class);

  public void addSize (@Nullable final String sKey, @Nonnegative final long nSize)
  {
    if (nSize < 0)
      LOGGER.warn ("A negative value (" + nSize + ") for key '" + sKey + "' is added to " + getClass ().getName ());
    addValue (sKey, nSize);
  }
}
