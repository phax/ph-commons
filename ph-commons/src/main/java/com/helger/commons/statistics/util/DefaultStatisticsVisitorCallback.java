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
package com.helger.commons.statistics.util;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.statistics.IStatisticsHandlerCache;
import com.helger.commons.statistics.IStatisticsHandlerCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedSize;
import com.helger.commons.statistics.IStatisticsHandlerKeyedTimer;
import com.helger.commons.statistics.IStatisticsHandlerSize;
import com.helger.commons.statistics.IStatisticsHandlerTimer;

/**
 * Default implementation of the {@link IStatisticsVisitorCallback} interface
 * that does nothing.
 *
 * @author Philip Helger
 */
public class DefaultStatisticsVisitorCallback implements IStatisticsVisitorCallback
{
  @OverrideOnDemand
  public void onCache (final String sName, final IStatisticsHandlerCache aHandler)
  {}

  @OverrideOnDemand
  public void onTimer (final String sName, final IStatisticsHandlerTimer aHandler)
  {}

  @OverrideOnDemand
  public void onKeyedTimer (final String sName, final IStatisticsHandlerKeyedTimer aHandler)
  {}

  @OverrideOnDemand
  public void onSize (final String sName, final IStatisticsHandlerSize aHandler)
  {}

  @OverrideOnDemand
  public void onKeyedSize (final String sName, final IStatisticsHandlerKeyedSize aHandler)
  {}

  @OverrideOnDemand
  public void onCounter (final String sName, final IStatisticsHandlerCounter aHandler)
  {}

  @OverrideOnDemand
  public void onKeyedCounter (final String sName, final IStatisticsHandlerKeyedCounter aHandler)
  {}
}
