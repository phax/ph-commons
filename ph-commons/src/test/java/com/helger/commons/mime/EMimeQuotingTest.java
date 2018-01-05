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
package com.helger.commons.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class for class {@link EMimeContentType}
 *
 * @author Philip Helger
 */
public final class EMimeQuotingTest
{
  @Test
  public void testAll ()
  {
    final String [] aTest = new String [] { "http://www.helger.com",
                                            "äöü",
                                            "1234567890!\"§$%&/()0",
                                            "bla foo fasel :;,.'*+" };

    for (final EMimeQuoting e : EMimeQuoting.values ())
      if (e != EMimeQuoting.QUOTED_STRING)
        for (final String sSrc : aTest)
        {
          final String sEncoded = e.getQuotedString (sSrc);
          assertNotNull (sEncoded);
          final String sDecoded = e.getUnquotedString (sEncoded);
          assertNotNull (sDecoded);
          assertEquals (e.name (), sSrc, sDecoded);
        }
  }
}
