/*
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2025 Philip Helger (www.helger.com)
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
package com.helger.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This qualifier is applied to an annotation to denote that the annotation should be treated as a
 * type qualifier.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@Target (ElementType.ANNOTATION_TYPE)
@Retention (RetentionPolicy.RUNTIME)
public @interface TypeQualifier
{
  /**
   * Describes the kinds of values the qualifier can be applied to. If a numeric class is provided
   * (e.g., Number.class or Integer.class) then the annotation can also be applied to the
   * corresponding primitive numeric types.
   *
   * @return a class object which denotes the type of the values the original annotation can be
   *         applied to.
   */
  Class <?> applicableTo() default Object.class;
}
