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
package com.helger.diagnostics.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

import com.helger.base.log.ILogHandler;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.list.ErrorList;

/**
 * Test class for class {@link ErrorListLogHandler}.
 *
 * @author Philip Helger
 */
public final class ErrorListLogHandlerTest
{
  @Test
  public void testBasic ()
  {
    final Exception aEx = new IllegalStateException ("mock");
    final ErrorList aList = new ErrorList ();
    final ILogHandler aHdl = new ErrorListLogHandler (aList);

    aHdl.info ("i");
    aHdl.warn ("w", aEx);
    aHdl.error ("e", aEx);
    aHdl.error ("e2");

    assertEquals (4, aList.size ());
    assertEquals (EErrorLevel.INFO, aList.get (0).getErrorLevel ());
    assertEquals ("i", aList.get (0).getErrorText (Locale.ROOT));
    assertNull (aList.get (0).getLinkedException ());
    assertEquals (EErrorLevel.WARN, aList.get (1).getErrorLevel ());
    assertSame (aEx, aList.get (1).getLinkedException ());
    assertEquals (EErrorLevel.ERROR, aList.get (2).getErrorLevel ());
    assertEquals (EErrorLevel.ERROR, aList.get (3).getErrorLevel ());
    assertEquals (2, aList.getAllErrors ().size ());
  }
}
