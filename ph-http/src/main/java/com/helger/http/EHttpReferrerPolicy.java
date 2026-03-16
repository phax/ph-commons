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

/**
 * HTTP response header "Referrer-Policy" values. See
 * https://www.w3.org/TR/referrer-policy/
 *
 * @author Philip Helger
 */
public enum EHttpReferrerPolicy
{
  NONE (""),
  NO_REFERRER ("no-referrer"),
  NO_REFERRER_WHEN_DOWNGRADE ("no-referrer-when-downgrade"),
  SAME_ORIGIN ("same-origin"),
  ORIGIN ("origin"),
  STRICT_ORIGIN ("strict-origin"),
  ORIGIN_WHEN_CROSS_ORIGIN ("origin-when-cross-origin"),
  STRICT_ORIGIN_WHEN_CROSS_ORIGIN ("strict-origin-when-cross-origin"),
  UNSAFE_URL ("unsafe-url");

  private final String m_sValue;

  EHttpReferrerPolicy (@NonNull final String sValue)
  {
    m_sValue = sValue;
  }

  /**
   * @return The HTTP header value for this referrer policy. Never
   *         <code>null</code> but may be empty for {@link #NONE}.
   */
  @NonNull
  public String getValue ()
  {
    return m_sValue;
  }
}
