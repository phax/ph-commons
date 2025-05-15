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
package com.helger.settings;

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.cache.Cache;
import com.helger.settings.factory.ISettingsFactory;

/**
 * A cache for the Settings
 *
 * @author Philip Helger
 */
@ThreadSafe
public class SettingsCache extends Cache <String, ISettings>
{
  private final ISettingsFactory <?> m_aSettingsFactory;

  public SettingsCache (@Nonnull final ISettingsFactory <?> aSettingsFactory)
  {
    super (aSettingsFactory::apply, 500, SettingsCache.class.getName ());
    m_aSettingsFactory = ValueEnforcer.notNull (aSettingsFactory, "SettingsFactory");
  }

  /**
   * @return The settings factory as specified in the constructor.
   */
  @Nonnull
  public final ISettingsFactory <?> getSettingsFactory ()
  {
    return m_aSettingsFactory;
  }
}
