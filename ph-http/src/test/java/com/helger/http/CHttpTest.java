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
package com.helger.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * Test class for class {@link CHttp}.
 *
 * @author Philip Helger
 */
public final class CHttpTest
{
  @Test
  public void testGetUnifiedMillis ()
  {
    assertEquals (0, CHttp.getUnifiedMillis (0));
    assertEquals (0, CHttp.getUnifiedMillis (999));
    assertEquals (1000, CHttp.getUnifiedMillis (1000));
    assertEquals (1000, CHttp.getUnifiedMillis (1999));
    assertEquals (2000, CHttp.getUnifiedMillis (2000));
  }

  @Test
  public void testConvertMillisToLocalDateTime ()
  {
    final LocalDateTime aLDT = CHttp.convertMillisToLocalDateTime (System.currentTimeMillis ());
    assertNotNull (aLDT);
    // Milliseconds are cut off
    assertEquals (0, aLDT.getNano ());

    assertEquals (CHttp.convertMillisToLocalDateTime (1000), CHttp.convertMillisToLocalDateTime (1999));
  }

  @Test
  public void testGetHttpResponseMessage ()
  {
    assertEquals ("OK", CHttp.getHttpResponseMessage (CHttp.HTTP_OK));
    assertEquals ("Not Found", CHttp.getHttpResponseMessage (CHttp.HTTP_NOT_FOUND));
    assertEquals ("Internal Server Error", CHttp.getHttpResponseMessage (CHttp.HTTP_INTERNAL_SERVER_ERROR));
    // Unknown response codes have a generic text
    assertEquals ("Unknown (299)", CHttp.getHttpResponseMessage (299));

    // All defined response codes must have a message
    for (int i = 100; i < 600; ++i)
      assertNotNull (CHttp.getHttpResponseMessage (i));
  }

  @Test
  public void testConstants ()
  {
    assertEquals ("\r\n", CHttp.EOL);
    assertNotNull (CHttp.HTTP_CHARSET);
    assertEquals (200, CHttp.HTTP_OK);
    assertEquals (404, CHttp.HTTP_NOT_FOUND);
    assertEquals (500, CHttp.HTTP_INTERNAL_SERVER_ERROR);
  }
}
