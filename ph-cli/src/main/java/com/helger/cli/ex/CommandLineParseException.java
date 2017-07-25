/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017 Philip Helger (www.helger.com)
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
package com.helger.cli.ex;

/**
 * Base for Exceptions thrown during parsing of a command-line.
 */
public class CommandLineParseException extends Exception
{
  /**
   * Construct a new <code>ParseException</code> with the specified detail
   * message.
   *
   * @param sMessage
   *        the detail message
   */
  public CommandLineParseException (final String sMessage)
  {
    super (sMessage);
  }
}
