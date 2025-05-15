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
package com.helger.jaxb;

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.cache.Cache;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.log.ConditionalLogger;
import com.helger.commons.log.IHasConditionalLogger;

import jakarta.xml.bind.JAXBContext;

/**
 * Specific cache class for JAXB context elements. This is helpful, as the JAXB context creation is
 * a very time consuming task.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class JAXBContextCache extends Cache <JAXBContextCacheKey, JAXBContext> implements IHasConditionalLogger
{
  private static final class SingletonHolder
  {
    static final JAXBContextCache INSTANCE = new JAXBContextCache ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBContextCache.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER, !GlobalDebug.DEFAULT_SILENT_MODE);

  private static boolean s_bDefaultInstantiated = false;

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it is enabled.
   * @since 9.4.0
   */
  public static boolean isSilentMode ()
  {
    return CONDLOG.isDisabled ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable logging
   * @return The previous value of the silent mode.
   * @since 9.4.0
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return !CONDLOG.setEnabled (!bSilentMode);
  }

  private JAXBContextCache ()
  {
    super (aCacheKey -> aCacheKey.createJAXBContext (CONDLOG), 500, JAXBContextCache.class.getName ());
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static JAXBContextCache getInstance ()
  {
    final JAXBContextCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }
}
