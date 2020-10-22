/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.jaxb.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for class {@link AdapterLocalTime}.
 *
 * @author Philip Helger
 */
public final class AdapterLocalTimeTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterLocalTime a = new AdapterLocalTime ();
    assertNull (a.unmarshal (null));
    assertNull (a.unmarshal (""));
    assertNull (a.unmarshal (" "));
    assertNull (a.unmarshal ("a"));
    assertNull (a.unmarshal ("a10:12:45"));
    assertNull (a.unmarshal ("10:12:45a"));
    assertNull (a.unmarshal ("10:12:45.a"));
    assertNull (a.unmarshal ("10:12:45.123a"));
    assertNull (a.unmarshal ("10:12:61"));
    assertNull (a.unmarshal ("10: 12:45"));
    assertNull (a.unmarshal ("10:12: 45"));
    assertNotNull (a.unmarshal ("10:12:45"));
    assertNotNull (a.unmarshal (" 10:12:45"));
    assertNotNull (a.unmarshal ("10:12:45 "));
    assertNotNull (a.unmarshal (" 10:12:45 "));
    assertNotNull (a.unmarshal (" 10:12:45.1 "));
    assertNotNull (a.unmarshal (" 10:12:45.12 "));
    assertNotNull (a.unmarshal (" 10:12:45.123 "));
  }
}
