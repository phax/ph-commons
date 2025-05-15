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
 * Used to annotate a value that may be either negative or nonnegative, and indicates that uses of
 * it should check for negative values before using it in a way that requires the value to be
 * nonnegative, and check for it being nonnegative before using it in a way that requires it to be
 * negative.
 *
 * @author Findbugs JSR 305
 * @since 12.0.0 in this package
 */
@Documented
@TypeQualifierNickname
@Nonnegative (when = When.MAYBE)
@Retention (RetentionPolicy.RUNTIME)
public @interface CheckForSigned
{
  /* empty */
}
