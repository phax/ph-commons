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
package com.helger.commons.xml.serialize.read;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.callback.exception.LoggingExceptionCallback;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.xml.sax.AbstractSAXErrorHandler;

/**
 * A special version of the {@link LoggingExceptionCallback} that handles the
 * most common XML exceptions in a nice way :)
 *
 * @author Philip Helger
 */
public class XMLLoggingExceptionCallback extends LoggingExceptionCallback
{
  public XMLLoggingExceptionCallback ()
  {}

  public XMLLoggingExceptionCallback (@Nonnull final IErrorLevel aErrorLevel)
  {
    super (aErrorLevel);
  }

  @Override
  @Nonnull
  @Nonempty
  @OverrideOnDemand
  protected String getLogMessage (@Nullable final Throwable t)
  {
    if (t instanceof SAXParseException)
    {
      final SAXParseException ex = (SAXParseException) t;
      return AbstractSAXErrorHandler.getSaxParseError (EErrorLevel.ERROR, ex).getAsString (CGlobal.DEFAULT_LOCALE);
    }
    if (t instanceof SAXException)
    {
      return "Error parsing XML document";
    }
    if (t instanceof UnknownHostException)
    {
      // Must be checked before IOException because it is an IOException
      // Caught if entity resolver failed
      return "Failed to resolve entity host: " + t.getMessage ();
    }
    if (t instanceof IOException)
    {
      return "Error reading XML document: " + t.getMessage ();
    }
    return super.getLogMessage (t);
  }

  @Override
  protected boolean isLogException (@Nullable final Throwable t)
  {
    if (t instanceof UnknownHostException)
      return false;
    return super.isLogException (t);
  }
}
