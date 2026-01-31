/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.url;

import com.helger.annotation.concurrent.Immutable;

/**
 * Constants for URL usage
 *
 * @author Philip Helger
 */
@Immutable
public final class CURL
{
  /** Separator before first param: ? */
  public static final char QUESTIONMARK = '?';
  public static final String QUESTIONMARK_STR = Character.toString (QUESTIONMARK);

  /** Separator between params: &amp; */
  public static final char AMPERSAND = '&';
  public static final String AMPERSAND_STR = Character.toString (AMPERSAND);

  /** Separator between param name and param value: = */
  public static final char EQUALS = '=';
  public static final String EQUALS_STR = Character.toString (EQUALS);

  /** Separator between URL path and anchor name: # */
  public static final char HASH = '#';
  public static final String HASH_STR = Character.toString (HASH);

  /** The protocol for file resources */
  public static final String PROTOCOL_FILE = "file";

  private CURL ()
  {}
}
