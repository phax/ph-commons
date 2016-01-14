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
package com.helger.commons.exception;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class of class {@link LoggedException}.
 *
 * @author Philip Helger
 */
public final class LoggedExceptionTest
{
  @Test
  @SuppressFBWarnings (value = { "RV_EXCEPTION_NOT_THROWN", "RV_RETURN_VALUE_IGNORED" },
                       justification = "only constructor tests")
  public void testAll ()
  {
    new LoggedException ();
    new LoggedException ("any text");
    new LoggedException (new Exception ());
    new LoggedException ("any text", new Exception ());
    new LoggedException (false);
    new LoggedException (false, "any text");
    new LoggedException (false, new Exception ());
    new LoggedException (false, "any text", new Exception ());
    LoggedException.newException (new Exception ());
    LoggedException.newException (new LoggedException ());
    LoggedException.newException (new LoggedRuntimeException ());
    LoggedException.newException ("any text", new Exception ());
    LoggedException.newException ("any text", new LoggedException ());
    LoggedException.newException ("any text", new LoggedRuntimeException ());
  }
}
