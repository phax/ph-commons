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
package com.helger.commons.csv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import javax.annotation.concurrent.Immutable;

/**
 * Constants for the CSV handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class CCSV
{
  /**
   * The default separator to use if none is supplied to the constructor.
   */
  public static final char DEFAULT_SEPARATOR = ',';
  /**
   * The default quote character to use if none is supplied to the constructor.
   */
  public static final char DEFAULT_QUOTE_CHARACTER = '"';
  /**
   * The default escape character to use if none is supplied to the constructor.
   */
  public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
  /**
   * The default strict quote behavior to use if none is supplied to the
   * constructor.
   */
  public static final boolean DEFAULT_STRICT_QUOTES = false;
  /**
   * The default leading whitespace behavior to use if none is supplied to the
   * constructor.
   */
  public static final boolean DEFAULT_IGNORE_LEADING_WHITESPACE = true;
  /**
   * If the quote character is set to null then there is no quote character.
   */
  public static final boolean DEFAULT_IGNORE_QUOTATIONS = false;
  /**
   * The average size of a line read by openCSV (used for setting the size of
   * StringBuilders).
   */
  public static final int INITIAL_STRING_SIZE = 128;
  /**
   * This is the "null" character - if a value is set to this then it is
   * ignored.
   */
  public static final char NULL_CHARACTER = '\0';

  public static final boolean DEFAULT_KEEP_CR = false;
  public static final boolean DEFAULT_VERIFY_READER = true;
  /**
   * The default line to start reading.
   */
  public static final int DEFAULT_SKIP_LINES = 0;

  private CCSV ()
  {}
}
