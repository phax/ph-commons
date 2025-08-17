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
package com.helger.xml.sax;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.commons.log.LogHelper;
import com.helger.diagnostics.error.level.IErrorLevel;

import jakarta.annotation.Nonnull;

/**
 * java.xml error handler that simply logs data to a logger.
 *
 * @author Philip Helger
 */
@Immutable
public class LoggingSAXErrorHandler extends AbstractSAXErrorHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingSAXErrorHandler.class);

  public LoggingSAXErrorHandler ()
  {}

  @Nonnull
  @Nonempty
  @OverrideOnDemand
  protected String getErrorMessage (@Nonnull final IErrorLevel aErrorLevel, final SAXParseException aException)
  {
    // As the SAX error messages are not localized at the moment, we can use
    // a fixed locale here
    return getSaxParseError (aErrorLevel, aException).getAsString (Locale.ROOT);
  }

  @Override
  protected void internalLog (@Nonnull final IErrorLevel aErrorLevel, final SAXParseException aException)
  {
    LogHelper.log (LOGGER, aErrorLevel, getErrorMessage (aErrorLevel, aException));
  }
}
