/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.helger.commons.callback.exception.IExceptionCallback;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;

/**
 * Logging JAXB read Exception handler
 *
 * @author Philip Helger
 * @since 9.2.2
 */
public class LoggingJAXBReadExceptionHandler implements IExceptionCallback <JAXBException>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingJAXBReadExceptionHandler.class);

  public void onException (@Nonnull final JAXBException ex)
  {
    if (ex instanceof UnmarshalException)
    {
      // The JAXB specification does not mandate how the JAXB provider
      // must behave when attempting to unmarshal invalid XML data. In
      // those cases, the JAXB provider is allowed to terminate the
      // call to unmarshal with an UnmarshalException.
      final Throwable aLinked = ((UnmarshalException) ex).getLinkedException ();
      if (aLinked instanceof SAXParseException)
        LOGGER.error ("Failed to parse XML document: " + aLinked.getMessage ());
      else
        LOGGER.error ("Unmarshal exception reading document", ex);
    }
    else
      LOGGER.warn ("JAXB Exception reading document", ex);
  }
}
