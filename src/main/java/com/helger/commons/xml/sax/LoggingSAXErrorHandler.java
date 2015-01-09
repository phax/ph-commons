/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.sax;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.helger.commons.CGlobal;
import com.helger.commons.annotations.CodingStyleguideUnaware;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.log.LogUtils;

/**
 * java.xml error handler that simply logs data to a logger.
 * 
 * @author Philip Helger
 */
@Immutable
@CodingStyleguideUnaware ("logger too visible by purpose")
public class LoggingSAXErrorHandler extends AbstractSAXErrorHandler
{
  protected static final Logger s_aLogger = LoggerFactory.getLogger (LoggingSAXErrorHandler.class);
  private static final LoggingSAXErrorHandler s_aInstance = new LoggingSAXErrorHandler ();

  public LoggingSAXErrorHandler ()
  {}

  public LoggingSAXErrorHandler (@Nullable final ErrorHandler aWrappedErrorHandler)
  {
    super (aWrappedErrorHandler);
  }

  @Nonnull
  public static LoggingSAXErrorHandler getInstance ()
  {
    return s_aInstance;
  }

  @Nonnull
  @Nonempty
  @OverrideOnDemand
  protected String getErrorMessage (@Nonnull final EErrorLevel eErrorLevel, final SAXParseException aException)
  {
    // XXX As the SAX error messages are not localized at the moment, we can use
    // a fixed locale here
    return getSaxParseError (eErrorLevel, aException).getAsString (CGlobal.DEFAULT_LOCALE);
  }

  @Override
  protected void internalLog (@Nonnull final EErrorLevel eErrorLevel, final SAXParseException aException)
  {
    LogUtils.log (s_aLogger, eErrorLevel, getErrorMessage (eErrorLevel, aException));
  }
}
