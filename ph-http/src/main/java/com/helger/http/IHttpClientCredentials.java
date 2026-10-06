/*
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
package com.helger.http;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;

/**
 * Base interface for client side HTTP credentials that can be transmitted in the
 * {@link CHttpHeader#AUTHORIZATION} HTTP header.
 *
 * @author Philip Helger
 * @since 12.5.1
 */
public interface IHttpClientCredentials
{
  /**
   * @return The value to be used for the {@link CHttpHeader#AUTHORIZATION} HTTP header, including
   *         the authentication scheme. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  String getRequestValue ();
}
