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
package com.helger.jaxb.validation;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IResourceError;

/**
 * An implementation of the JAXB {@link javax.xml.bind.ValidationEventHandler}
 * interface. It simply prints the messages to a logger before the original
 * handler handles them.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class LoggingValidationEventHandler extends AbstractValidationEventHandler
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingValidationEventHandler.class);

  public LoggingValidationEventHandler ()
  {
    super ();
  }

  public LoggingValidationEventHandler (@Nullable final ValidationEventHandler aOrigHandler)
  {
    super (aOrigHandler);
  }

  @Override
  protected void onEvent (@Nonnull final IResourceError aEvent)
  {
    // As JAXB messages are not localized, we can use a fixed locale here!
    final String sMsg = "JAXB " + aEvent.getAsString (Locale.US);
    if (aEvent.getErrorLevel ().isLessOrEqualSevereThan (EErrorLevel.WARN))
      s_aLogger.warn (sMsg, aEvent.getLinkedException ());
    else
      s_aLogger.error (sMsg, aEvent.getLinkedException ());
  }
}
