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
package com.helger.annotation.meta;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;

/**
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 * @param <A>
 *        The annotation class to validate.
 */
public interface TypeQualifierValidator <A extends Annotation>
{
  /**
   * Given a type qualifier, check to see if a known specific constant value is an instance of the
   * set of values denoted by the qualifier.
   *
   * @param annotation
   *        the type qualifier. May not be <code>null</code>.
   * @param value
   *        the value to check
   * @return a value indicating whether or not the value is an member of the values denoted by the
   *         type qualifier
   */
  @Nonnull
  When forConstantValue (@Nonnull A annotation, Object value);
}
