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
package com.helger.io.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for class {@link URLCleanser}.
 *
 * @author Philip Helger
 */
public final class URLCleanserTest
{
  @Test
  public void testGetCleanURL ()
  {
    assertEquals ("aeoeue", URLCleanser.getCleanURLPartWithoutUmlauts ("äöü"));
    assertEquals ("AeoeUe", URLCleanser.getCleanURLPartWithoutUmlauts ("ÄöÜ"));
    assertEquals ("Ae-Uesz", URLCleanser.getCleanURLPartWithoutUmlauts ("Ä Üß"));
    assertEquals ("Weisze-Waeste", URLCleanser.getCleanURLPartWithoutUmlauts ("Weiße Wäste"));
    assertEquals ("hallo", URLCleanser.getCleanURLPartWithoutUmlauts ("hállò"));
    assertEquals ("ffi", URLCleanser.getCleanURLPartWithoutUmlauts ("\uFB03"));
    assertEquals ("ffl", URLCleanser.getCleanURLPartWithoutUmlauts ("\uFB04"));
    assertEquals ("hallo;jsessionid=1234", URLCleanser.getCleanURLPartWithoutUmlauts ("hállò;jsessionid=1234"));
  }
}
