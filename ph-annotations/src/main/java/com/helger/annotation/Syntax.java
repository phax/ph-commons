/*
 * Original copyright partially by Apache Software Foundation
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
package com.helger.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.helger.annotation.meta.When;

/**
 * This annotation a value that is of a particular syntax, such as Java syntax or regular expression
 * syntax. This can be used to provide syntax checking of constant values at compile time, run time
 * checking at runtime, and can assist IDEs in deciding how to interpret String constants (e.g.,
 * should a refactoring that renames method {@code x()} to {@code y()} update the String constant
 * {@code "x()"}).
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
public @interface Syntax
{
  /**
   * Value indicating the particular syntax denoted by this annotation. Different tools will
   * recognize different syntaxes, but some proposed canonical values are:
   * <ul>
   * <li>"Java"</li>
   * <li>"RegEx"</li>
   * <li>"JavaScript"</li>
   * <li>"Ruby"</li>
   * <li>"Groovy"</li>
   * <li>"SQL"</li>
   * <li>"FormatString"</li>
   * </ul>
   * <p>
   * Syntax names can be followed by a colon and a list of key value pairs, separated by commas. For
   * example, "SQL:dialect=Oracle,version=2.3". Tools should ignore any keys they don't recognize.
   *
   * @return a name indicating the particular syntax.
   */
  String value();

  When when() default When.ALWAYS;
}
