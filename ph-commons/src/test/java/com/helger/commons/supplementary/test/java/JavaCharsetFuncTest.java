/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.supplementary.test.java;

import java.nio.charset.Charset;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaCharsetFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaCharsetFuncTest.class);

  @Test
  public void testAllCharsets ()
  {
    for (final Map.Entry <String, Charset> aEntry : Charset.availableCharsets ().entrySet ())
      s_aLogger.info (aEntry.getKey () + " " + aEntry.getValue ().aliases ());
  }

  @Test
  public void testAllCharsetNamess ()
  {
    s_aLogger.info (Charset.availableCharsets ().keySet ().toString ());
  }
}
