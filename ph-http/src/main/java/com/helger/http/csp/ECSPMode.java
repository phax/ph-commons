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
package com.helger.http.csp;

/**
 * Defines the CSP usage mode.
 *
 * @author Philip Helger
 * @since 10.4.0
 */
public enum ECSPMode
{
  CSP_OFF,
  CSP_REPORTING_ONLY,
  CSP_ON;

  public boolean isReportingOnly ()
  {
    return this == CSP_REPORTING_ONLY;
  }

  public boolean isReporting ()
  {
    return this == CSP_REPORTING_ONLY || this == CSP_ON;
  }
}
