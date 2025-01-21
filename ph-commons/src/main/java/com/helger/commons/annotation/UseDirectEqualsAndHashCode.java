/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a class's native implementations of {@link #equals(Object)} and
 * {@link #hashCode()} should be used and no wrapper. This is only important to
 * the classes {@link com.helger.commons.equals.EqualsImplementationRegistry}
 * and {@link com.helger.commons.hashcode.HashCodeImplementationRegistry}.
 *
 * @author Philip Helger
 */
@Retention (RetentionPolicy.RUNTIME)
@Target ({ ElementType.TYPE })
@Documented
public @interface UseDirectEqualsAndHashCode
{
  String value() default "";
}
