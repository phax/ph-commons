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
package com.helger.json.parser.errorhandler;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.json.parser.JsonParseException;

/**
 * An implementation of {@link IJsonParseExceptionCallback} that logs all
 * {@link JsonParseException}s to a Logger.
 *
 * @author Philip Helger
 */
@Immutable
public class LoggingJsonParseExceptionCallback implements IJsonParseExceptionCallback
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingJsonParseExceptionCallback.class);

  public LoggingJsonParseExceptionCallback ()
  {}

  @Nonnull
  @Nonempty
  public static String createLoggingStringParseError (@Nonnull final JsonParseException ex)
  {
    // Is null if the constructor with String only was used
    return ex.getMessage ();
  }

  public void onException (@Nonnull final JsonParseException ex)
  {
    LOGGER.error (createLoggingStringParseError (ex));
  }
}
