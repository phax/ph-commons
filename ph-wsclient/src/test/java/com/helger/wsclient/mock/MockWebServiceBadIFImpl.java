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
package com.helger.wsclient.mock;

import jakarta.jws.WebService;

/**
 * A web service implementation whose declared endpoint interface is a class and not an interface.
 *
 * @author Philip Helger
 */
@WebService (endpointInterface = "java.lang.String")
public class MockWebServiceBadIFImpl
{
  /**
   * @param sInput
   *        Any input
   * @return Any output
   */
  public String echo (final String sInput)
  {
    return sInput;
  }
}
