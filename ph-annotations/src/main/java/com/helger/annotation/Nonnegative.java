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
 * This annotation is used to annotate a value that should only contain nonnegative values (&ge; 0).
 * With <code>zeroAllowed = false</code> the value must be positive (&gt; 0).
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

  /**
   * @return <code>true</code> if 0 is a valid value (&ge; 0, the default), <code>false</code> if
   *         the value must be positive (&gt; 0).
   * @since 12.8.0
   */
  boolean zeroAllowed() default true;

  class Checker implements TypeQualifierValidator <Nonnegative>
  {
    /**
     * Validate whether the provided constant value is nonnegative, or positive if zero is not
     * allowed.
     *
     * @param annotation
     *        The {@link Nonnegative} annotation instance. Must not be <code>null</code>.
     * @param v
     *        The constant value to validate.
     * @return {@link When#ALWAYS} if the value is nonnegative (positive if zero is not allowed),
     *         {@link When#NEVER} otherwise.
     */
    public When forConstantValue (final Nonnegative annotation, final Object v)
    {
      if (!(v instanceof Number))
        return When.NEVER;
      // The sign of the value: < 0, 0 or > 0
      final int nSignum;
      final Number value = (Number) v;
      if (value instanceof Long)
        nSignum = Long.signum (value.longValue ());
      else
        if (value instanceof Double)
          nSignum = (int) Math.signum (value.doubleValue ());
        else
          if (value instanceof Float)
            nSignum = (int) Math.signum (value.floatValue ());
          else
            nSignum = Integer.signum (value.intValue ());

      if (nSignum < 0 || (nSignum == 0 && !annotation.zeroAllowed ()))
        return When.NEVER;
      return When.ALWAYS;
    }
  }
}
