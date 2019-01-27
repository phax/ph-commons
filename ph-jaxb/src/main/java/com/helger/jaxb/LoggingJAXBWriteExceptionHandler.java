/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.callback.exception.IExceptionCallback;

/**
 * Logging JAXB write Exception handler
 *
 * @author Philip Helger
 * @since 9.2.2
 */
public class LoggingJAXBWriteExceptionHandler implements IExceptionCallback <JAXBException>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingJAXBWriteExceptionHandler.class);

  public void onException (@Nonnull final JAXBException ex)
  {
    if (ex instanceof MarshalException)
      LOGGER.error ("Marshal exception writing object", ex);
    else
      LOGGER.warn ("JAXB Exception writing object", ex);
  }
}
