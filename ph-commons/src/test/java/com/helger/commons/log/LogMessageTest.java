/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Test;

import com.helger.base.mock.exception.MockException;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.datetime.helper.PDTFactory;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link LogMessage}.
 *
 * @author Philip Helger
 */
public final class LogMessageTest
{
  @Test
  public void testAll ()
  {
    final LogMessage lm = new LogMessage (EErrorLevel.WARN, "Msg", new MockException ());
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    assertTrue (lm.getIssueDateTime ().equals (aNow) || lm.getIssueDateTime ().isBefore (aNow));
    assertSame (EErrorLevel.WARN, lm.getErrorLevel ());
    assertEquals ("Msg", lm.getMessage ());
    assertNotNull (lm.getThrowable ());
    TestHelper.testToStringImplementation (lm);

    try
    {
      new LogMessage (null, "Msg", new MockException ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new LogMessage (EErrorLevel.WARN, null, new MockException ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
