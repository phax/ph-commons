/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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
package com.helger.cli;

/**
 * Errors that can occur when parsing the command line.
 *
 * @author Philip Helger
 */
public enum ECmdLineParseError
{
  NON_REPEATABLE_OPTION_OCCURS_MORE_THAN_ONCE,
  TOO_LITTLE_REQUIRED_VALUES,
  REQUIRED_OPTION_IS_MISSING,
  ANOTHER_OPTION_OF_GROUP_ALREADY_PRESENT
}
