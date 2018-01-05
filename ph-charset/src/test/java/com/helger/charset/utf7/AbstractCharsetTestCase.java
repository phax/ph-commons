/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.charset.utf7;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

abstract class AbstractCharsetTestCase
{
  protected Charset tested;

  protected String decode (final String string) throws UnsupportedEncodingException
  {
    return tested.decode (CharsetTestHelper.wrap (string)).toString ();
  }

  protected String encode (final String string) throws UnsupportedEncodingException
  {
    return CharsetTestHelper.asString (tested.encode (string));
  }
}
