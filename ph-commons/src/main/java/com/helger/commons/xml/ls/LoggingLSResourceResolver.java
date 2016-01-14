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
package com.helger.commons.xml.ls;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * A logging only {@link LSResourceResolver} implementation.
 *
 * @author Philip Helger
 */
public class LoggingLSResourceResolver extends AbstractLSResourceResolver
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingLSResourceResolver.class);

  public LoggingLSResourceResolver ()
  {}

  @Override
  @Nullable
  public LSInput mainResolveResource (@Nullable final String sType,
                                      @Nullable final String sNamespaceURI,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId,
                                      @Nullable final String sBaseURI)
  {
    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info ("mainResolveResource (" +
                      sType +
                      ", " +
                      sNamespaceURI +
                      ", " +
                      sPublicId +
                      ", " +
                      sSystemId +
                      ", " +
                      sBaseURI +
                      ")");
    return null;
  }
}
