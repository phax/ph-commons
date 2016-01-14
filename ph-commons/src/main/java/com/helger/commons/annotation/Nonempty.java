/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
 * A specialization of the {@link javax.annotation.Nonnull} annotation that is
 * to be used for String and collection parameters as well as return values
 * only. It indicates that a string may neither be <code>null</code> nor empty (
 * <code>""</code>) or that a collection may neither be <code>null</code> nor
 * empty).<br>
 * This means that the usage of this annotation implies the usage of the
 * {@link javax.annotation.Nonnull} annotation but because of better FindBugs
 * handling, the {@link javax.annotation.Nonnull} annotation must be present as
 * well.
 *
 * @author Philip Helger
 */
@Retention (RetentionPolicy.CLASS)
@Target ({ ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD })
@Documented
public @interface Nonempty
{
  String value() default "";
}
