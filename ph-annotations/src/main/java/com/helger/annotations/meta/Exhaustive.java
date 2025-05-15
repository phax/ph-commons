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
package com.helger.annotations.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation can be applied to the value() element of an annotation that is annotated as a
 * TypeQualifier. This is only appropriate if the value field returns a value that is an
 * Enumeration.
 * <p>
 * Applications of the type qualifier with different values are exclusive, and the enumeration is an
 * exhaustive list of the possible values.
 * <p>
 * For example, the following defines a type qualifier such that if you know a value is neither
 * {@literal @Foo(Color.Red)} or {@literal @Foo(Color.Blue)}, then the value must be
 * {@literal @Foo(Color.Green)}. And if you know it is {@literal @Foo(Color.Green)}, you know it
 * cannot be {@literal @Foo(Color.Red)} or {@literal @Foo(Color.Blue)}
 *
 * <pre>
 * &#064;TypeQualifier
 * &#064;interface Foo
 * {
 *   enum Color
 *   {
 *     RED,
 *     BLUE,
 *     GREEN
 *   };
 *
 *   &#064;Exhaustive
 *   Color value();
 * }
 * </pre>
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
public @interface Exhaustive
{
  /* empty */
}
