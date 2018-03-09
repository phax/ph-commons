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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaExecOrderTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaExecOrderTest.class);

  @Test
  public void testV1 ()
  {
    s_aLogger.info ("v1");
    int jjnewStateCnt = 1;
    int startsAt = 2;
    int i;
    if ((i = jjnewStateCnt) == (startsAt = 62 - (jjnewStateCnt = startsAt)))
    {}
    else
    {}
    s_aLogger.info ("i=" + i);
    s_aLogger.info ("jjnewStateCnt=" + jjnewStateCnt);
    s_aLogger.info ("startsAt=" + startsAt);
  }

  @Test
  public void testV2 ()
  {
    s_aLogger.info ("v2");
    int jjnewStateCnt = 1;
    int startsAt = 2;
    int i;
    i = jjnewStateCnt;
    jjnewStateCnt = startsAt;
    startsAt = 62 - jjnewStateCnt;
    if (i == startsAt)
    {}
    else
    {}
    s_aLogger.info ("i=" + i);
    s_aLogger.info ("jjnewStateCnt=" + jjnewStateCnt);
    s_aLogger.info ("startsAt=" + startsAt);
  }

  private int jjnewStateCnt ()
  {
    s_aLogger.info ("jjnewStateCnt get");
    return 1;
  }

  private int startsAt ()
  {
    s_aLogger.info ("startsAt get");
    return 2;
  }

  private int jjnewStateCnt (final int x)
  {
    s_aLogger.info ("jjnewStateCnt set " + x);
    return x;
  }

  private int startsAt (final int x)
  {
    s_aLogger.info ("startsAt set " + x);
    return x;
  }

  @Test
  public void testV3 ()
  {
    s_aLogger.info ("v3");
    int i;
    if ((i = jjnewStateCnt ()) == (startsAt (62 - (jjnewStateCnt (startsAt ())))))
    {}
    else
    {}
    s_aLogger.info ("result: " + i);
  }
}
