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
package com.helger.annotation.misc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method returns a mutable object but containing a copy of the
 * internal state. The different to {@link ReturnsMutableObject} is, that this
 * annotation indicates a copy is used, whereas {@link ReturnsMutableObject}
 * means that the internal contained is returned <b>and</b> is mutable!
 *
 * @author Philip Helger
 */
@Retention (RetentionPolicy.CLASS)
@Target ({ ElementType.METHOD })
@Documented
public @interface ReturnsMutableCopy
{
  String value() default "";
}
