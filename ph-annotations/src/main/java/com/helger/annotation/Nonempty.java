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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import com.helger.annotation.meta.TypeQualifierValidator;
import com.helger.annotation.meta.When;

/**
 * A specialization of the {@link org.jspecify.annotations.NonNull} annotation that is to be used
 * for String and collection parameters as well as return values only. It indicates that a string
 * may neither be <code>null</code> nor empty ( <code>""</code>) or that a collection may neither be
 * <code>null</code> nor empty).<br>
 * This means that the usage of this annotation implies the usage of the
 * {@link org.jspecify.annotations.NonNull} annotation but because of better FindBugs handling, the
 * {@link org.jspecify.annotations.NonNull} annotation must be present as well.
 *
 * @author Philip Helger
 */
@Retention (RetentionPolicy.CLASS)
@Target ({ ElementType.FIELD,
           ElementType.PARAMETER,
           ElementType.LOCAL_VARIABLE,
           ElementType.METHOD,
           ElementType.TYPE_USE })
@Documented
public @interface Nonempty
{
  When when() default When.ALWAYS;

  static class Checker implements TypeQualifierValidator <Nonempty>
  {
    public When forConstantValue (final Nonempty annotation, final Object value)
    {
      if (value instanceof final String s)
      {
        if (s.length () == 0)
          return When.NEVER;
        return When.ALWAYS;
      }
      if (value instanceof final Collection <?> c)
      {
        if (c.isEmpty ())
          return When.NEVER;
        return When.ALWAYS;
      }

      return When.NEVER;
    }
  }
}
