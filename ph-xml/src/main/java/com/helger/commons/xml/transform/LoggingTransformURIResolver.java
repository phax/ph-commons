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
package com.helger.commons.xml.transform;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link javax.xml.transform.URIResolver} that logs to a logger but does not
 * resolve anything.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class LoggingTransformURIResolver extends AbstractTransformURIResolver
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingTransformURIResolver.class);

  public LoggingTransformURIResolver ()
  {
    super ();
  }

  public LoggingTransformURIResolver (@Nullable final URIResolver aWrappedURIResolver)
  {
    super (aWrappedURIResolver);
  }

  @Override
  protected Source internalResolve (final String sHref, final String sBase) throws TransformerException
  {
    s_aLogger.info ("URIResolver.resolve (" + sHref + ", " + sBase + ")");
    return null;
  }
}
