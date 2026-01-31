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
package com.helger.http.csp;

import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * CSP policy. It's a list of {@link CSPDirective}.<br>
 * See http://www.w3.org/TR/CSP2/ and https://www.w3.org/TR/CSP3/
 *
 * @author Philip Helger
 * @since 10.4.0
 */
@NotThreadSafe
public class CSPPolicy extends AbstractCSPPolicy <CSPDirective>
{
  public CSPPolicy ()
  {}
}
