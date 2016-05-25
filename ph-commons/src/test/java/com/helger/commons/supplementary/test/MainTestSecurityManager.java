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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class MainTestSecurityManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainTestSecurityManager.class);

  /**
   * Run as follows: <br>
   * -Djava.security.manager
   * -Djava.security.policy=src/test/resources/grant-all.policy
   * -Djava.security.debug=all<br>
   * Content of grant-all.policy:
   *
   * <pre>
   * grant {
  // permission java.security.AllPermission;
  permission java.util.PropertyPermission  "java.security.policy", "write";
  permission java.lang.RuntimePermission   "accessDeclaredMembers";
  permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
  };
   * </pre>
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
    s_aLogger.info ("originalString: " + originalString);
  }
}
