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
package com.helger.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.helger.annotations.meta.TypeQualifier;
import com.helger.annotations.meta.When;

/**
 * This annotation is used to denote String values that are untainted, i.e. properly validated.
 * <p>
 * For example, this annotation should be used on the String value which represents SQL query to be
 * passed to database engine.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 * @see Tainted
 */
@Documented
@TypeQualifier
@Retention (RetentionPolicy.RUNTIME)
public @interface Untainted
{
  When when() default When.ALWAYS;
}
