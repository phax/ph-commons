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
 * A source list to be used in a CSP directive ({@link CSPDirective}). It's just
 * a convenient way to build a CSP directive value.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CSPSourceList extends AbstractCSPSourceList <CSPSourceList>
{
  public CSPSourceList ()
  {}
}
