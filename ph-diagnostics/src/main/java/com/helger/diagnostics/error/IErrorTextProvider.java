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
package com.helger.diagnostics.error;

import java.util.Locale;

import jakarta.annotation.Nonnull;

/**
 * Interface for converting an {@link IError} to a printable representation.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@FunctionalInterface
public interface IErrorTextProvider
{
  /**
   * Get the provided error as a String
   *
   * @param aError
   *        The error to be converted. May not be <code>null</code>.
   * @param aContentLocale
   *        The content locale to be used. May not be <code>null</code>.
   * @return A non-<code>null</code> String.
   */
  @Nonnull
  String getErrorText (@Nonnull IError aError, @Nonnull Locale aContentLocale);
}
