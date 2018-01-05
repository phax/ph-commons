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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public final class UTF7CharsetOptionalFuncTest extends AbstractCharsetTestCase
{
  @Before
  public void setUp () throws Exception
  {
    tested = new UTF7Charset ("X-UTF-7-Optional", new String [] {}, true);
  }

  @Test
  public void testDecodeOptionalCharsUTF7 () throws Exception
  {
    assertEquals ("~!@", decode ("+AH4AIQBA-"));
  }

  @Test
  public void testDecodeOptionalCharsPlain () throws Exception
  {
    assertEquals ("!\"#$%*;<=>@[]^_'{|}", decode ("!\"#$%*;<=>@[]^_'{|}"));
  }

  @Test
  public void testEncodeOptionalCharsUTF7 () throws Exception
  {
    assertEquals ("!\"#$%*;<=>@[]^_`{|}", encode ("!\"#$%*;<=>@[]^_`{|}"));
  }
}
