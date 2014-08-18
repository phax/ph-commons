/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link SingleMapEntry}.
 * 
 * @author Philip Helger
 */
public final class SingleMapEntryTest
{
  @Test
  public void testCtor ()
  {
    final SingleMapEntry <String, Object> e = new SingleMapEntry <String, Object> ("Key", "value");
    assertEquals ("Key", e.getKey ());
    assertEquals ("value", e.getValue ());
    assertEquals ("value", e.setValue ("new"));
    assertEquals ("new", e.getValue ());

    assertFalse (e.equals (null));
    assertFalse (e.equals ("bla"));
    assertEquals (e, e);
    assertEquals (e, new SingleMapEntry <String, Object> ("Key", "new"));
    assertFalse (e.equals (new SingleMapEntry <String, Object> ("Key", Integer.valueOf (17))));
    assertEquals (e.hashCode (), new SingleMapEntry <String, Object> ("Key", "new").hashCode ());
    assertNotNull (e.toString ());

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new SingleMapEntry <String, Object> ("Key", "value"),
                                                                    new SingleMapEntry <String, Object> ("Key", "value"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new SingleMapEntry <String, Object> ("Key",
                                                                                                             "value"),
                                                                        new SingleMapEntry <String, Object> ("Key",
                                                                                                             "value2"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new SingleMapEntry <String, Object> ("Key",
                                                                                                             "value"),
                                                                        new SingleMapEntry <String, Object> ("Key2",
                                                                                                             "value"));
  }
}
