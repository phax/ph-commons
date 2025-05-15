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
package com.helger.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.helger.annotation.meta.TypeQualifier;
import com.helger.annotation.meta.TypeQualifierValidator;
import com.helger.annotation.meta.When;

/**
 * The annotated element must not be null.
 * <p>
 * Annotated fields must not be null after construction has completed.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@TypeQualifier
@Retention (RetentionPolicy.RUNTIME)
public @interface Nonnull
{
  When when() default When.ALWAYS;

  class Checker implements TypeQualifierValidator <Nonnull>
  {
    public When forConstantValue (final Nonnull qualifierArgument, final Object value)
    {
      if (value == null)
        return When.NEVER;
      return When.ALWAYS;
    }
  }
}
