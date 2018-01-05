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
package edu.umd.cs.findbugs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to suppress FindBugs warnings.
 */
@Retention (RetentionPolicy.CLASS)
public @interface SuppressFBWarnings
{
  /**
   * @return The set of FindBugs warnings that are to be suppressed in annotated
   *         element. The value can be a bug category, kind or pattern.
   */
  String [] value() default {};

  /**
   * @return Optional documentation of the reason why the warning is suppressed
   */
  String justification() default "";
}
