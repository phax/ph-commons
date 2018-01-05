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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaCharToLowercaseFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaCharToLowercaseFuncTest.class);
  private static final Locale L_DE = new Locale ("de");

  @Test
  public void testAllCharsets ()
  {
    for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
    {
      final char cUp = Character.toString ((char) c).toUpperCase (L_DE).charAt (0);
      if (c == 'a')
        assertEquals ('A', cUp);

      if (cUp != c)
        if (false)
          s_aLogger.info ("c(" + (char) c + ") ==> (" + cUp + ")");
    }
  }
}
