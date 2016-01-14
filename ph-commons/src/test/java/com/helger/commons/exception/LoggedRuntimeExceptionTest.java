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
 * Test class of class {@link LoggedRuntimeException}.
 *
 * @author Philip Helger
 */
public final class LoggedRuntimeExceptionTest
{
  @Test
  @SuppressFBWarnings (value = { "RV_EXCEPTION_NOT_THROWN", "RV_RETURN_VALUE_IGNORED" },
                       justification = "only constructor tests")
  public void testAll ()
  {
    new LoggedRuntimeException ();
    new LoggedRuntimeException ("any text");
    new LoggedRuntimeException (new Exception ());
    new LoggedRuntimeException ("any text", new Exception ());
    new LoggedRuntimeException (false);
    new LoggedRuntimeException (false, "any text");
    new LoggedRuntimeException (false, new Exception ());
    new LoggedRuntimeException (false, "any text", new Exception ());
    LoggedRuntimeException.newException (new Exception ());
    LoggedRuntimeException.newException (new LoggedException ());
    LoggedRuntimeException.newException (new LoggedRuntimeException ());
    LoggedRuntimeException.newException ("any text", new Exception ());
    LoggedRuntimeException.newException ("any text", new LoggedException ());
    LoggedRuntimeException.newException ("any text", new LoggedRuntimeException ());
  }
}
