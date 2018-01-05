/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.json;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Json read and write constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class CJson
{
  /** character opening a JSON array */
  public static final char ARRAY_START = '[';
  /** character closing a JSON array */
  public static final char ARRAY_END = ']';
  /** character opening a JSON object */
  public static final char OBJECT_START = '{';
  /** character closing a JSON object */
  public static final char OBJECT_END = '}';
  /** character used as separator between items in collections */
  public static final char ITEM_SEPARATOR = ',';
  /** character used for assignments (between name and value) */
  public static final char NAME_VALUE_SEPARATOR = ':';

  public static final String COMMENT_START = "/*";
  public static final String COMMENT_END = "*/";

  public static final String KEYWORD_TRUE = "true";
  public static final String KEYWORD_FALSE = "false";
  public static final String KEYWORD_NULL = "null";

  @PresentForCodeCoverage
  private static final CJson s_aInstance = new CJson ();

  private CJson ()
  {}
}
