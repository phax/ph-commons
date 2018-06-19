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
package com.helger.json.parser;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Read-only parsing position with a line and a column number.
 * 
 * @author Philip Helger
 */
public interface IJsonParsePosition extends Serializable
{
  /**
   * @return The current line number. First line has a value of 1.
   */
  @Nonnegative
  int getLineNumber ();

  /**
   * @return The current column number. First column has a value of 1.
   */
  @Nonnegative
  int getColumnNumber ();

  @Nonnull
  String getAsString ();
}
