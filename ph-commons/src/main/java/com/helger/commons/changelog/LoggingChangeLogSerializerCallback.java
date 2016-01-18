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
package com.helger.commons.changelog;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging implementation of {@link IChangeLogSerializerCallback}.
 *
 * @author Philip Helger
 */
public class LoggingChangeLogSerializerCallback implements IChangeLogSerializerCallback
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingChangeLogSerializerCallback.class);

  public void accept (@Nonnull final String sError)
  {
    s_aLogger.error (sError);
  }
}
