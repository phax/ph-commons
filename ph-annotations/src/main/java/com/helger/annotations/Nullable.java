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

import com.helger.annotations.meta.TypeQualifierNickname;
import com.helger.annotations.meta.When;

/**
 * The annotated element could be null under some circumstances.
 * <p>
 * In general, this means developers will have to read the documentation to determine when a null
 * value is acceptable and whether it is necessary to check for a null value.
 * <p>
 * This annotation is useful mostly for overriding a {@link Nonnull} annotation. Static analysis
 * tools should generally treat the annotated items as though they had no annotation, unless they
 * are configured to minimize false negatives. Use {@link CheckForNull} to indicate that the element
 * value should always be checked for a null value.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@TypeQualifierNickname
@Nonnull (when = When.UNKNOWN)
@Retention (RetentionPolicy.RUNTIME)
public @interface Nullable
{
  /* empty */
}
