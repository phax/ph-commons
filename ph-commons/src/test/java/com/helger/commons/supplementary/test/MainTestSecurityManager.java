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
package com.helger.commons.supplementary.test;

import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MainTestSecurityManager
{
  /**
   * Run as follows: <br>
   * -Djava.security.manager
   * -Djava.security.policy=src/test/resources/grant-all.policy
   * -Djava.security.debug=all
   *
   * @param args
   *        main args
   * @throws Exception
   *         Hopefully never
   */
  @SuppressFBWarnings (value = "DM_STRING_CTOR")
  public static void main (final String args[]) throws Exception
  {
    if (false)
      System.setSecurityManager (new SecurityManager ());

    // Important to use "new String!"
    final String originalString = new String ("abcdef");
    final Field field = String.class.getDeclaredField ("value");
    field.setAccessible (true);
    final Object obj = field.get (originalString);// will fail
    final char [] chars = (char []) obj;
    chars[0] = 'e';
    System.out.println ("originalString: " + originalString);
  }
}
