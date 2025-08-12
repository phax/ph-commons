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
package com.helger.xml.transform;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.log.LogHelper;

import jakarta.annotation.Nonnull;

/**
 * {@link javax.xml.transform.ErrorListener} that simply logs data to a logger.
 *
 * @author Philip Helger
 */
@Immutable
public class LoggingTransformErrorListener extends AbstractTransformErrorListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingTransformErrorListener.class);

  private final Locale m_aDisplayLocale;

  public LoggingTransformErrorListener (@Nonnull final Locale aDisplayLocale)
  {
    m_aDisplayLocale = ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");
  }

  @Nonnull
  public Locale getDisplayLocale ()
  {
    return m_aDisplayLocale;
  }

  @Override
  protected void internalLog (@Nonnull final IError aResError)
  {
    final IErrorLevel aErrorLevel = aResError.getErrorLevel ();
    final String sText = aResError.getAsString (m_aDisplayLocale);
    LogHelper.log (LOGGER, aErrorLevel, sText);
  }
}
