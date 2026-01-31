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

import com.helger.annotation.meta.TypeQualifierValidator;
import com.helger.annotation.meta.When;

/**
 * This annotation is used to annotate a value that should only contain nonnegative values (ge; 0).
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
public @interface Nonnegative
{
  When when() default When.ALWAYS;

  class Checker implements TypeQualifierValidator <Nonnegative>
  {
    public When forConstantValue (final Nonnegative annotation, final Object v)
    {
      if (!(v instanceof Number))
        return When.NEVER;
      boolean isNegative;
      final Number value = (Number) v;
      if (value instanceof Long)
        isNegative = value.longValue () < 0;
      else
        if (value instanceof Double)
          isNegative = value.doubleValue () < 0;
        else
          if (value instanceof Float)
            isNegative = value.floatValue () < 0;
          else
            isNegative = value.intValue () < 0;

      if (isNegative)
        return When.NEVER;
      return When.ALWAYS;
    }
  }
}
