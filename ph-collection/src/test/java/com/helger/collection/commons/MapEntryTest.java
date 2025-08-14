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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.collection.CollectionTestHelper;

/**
 * Test class for class {@link MapEntry}.
 *
 * @author Philip Helger
 */
public final class MapEntryTest
{
  @Test
  public void testCtor ()
  {
    final MapEntry <String, Object> e = new MapEntry <> ("Key", "value");
    assertEquals ("Key", e.getKey ());
    assertEquals ("value", e.getValue ());
    assertEquals ("value", e.setValue ("new"));
    assertEquals ("new", e.getValue ());

    assertNotEquals (e, null);
    assertNotEquals (e, "bla");
    assertEquals (e, e);
    assertEquals (e, new MapEntry <> ("Key", "new"));
    assertNotEquals (e, new MapEntry <> ("Key", Integer.valueOf (17)));
    assertEquals (e.hashCode (), new MapEntry <String, Object> ("Key", "new").hashCode ());
    assertNotNull (e.toString ());

    CollectionTestHelper.testDefaultImplementationWithEqualContentObject (new MapEntry <> ("Key", "value"),
                                                                          new MapEntry <> ("Key", "value"));
    CollectionTestHelper.testDefaultImplementationWithDifferentContentObject (new MapEntry <> ("Key", "value"),
                                                                              new MapEntry <> ("Key", "value2"));
    CollectionTestHelper.testDefaultImplementationWithDifferentContentObject (new MapEntry <> ("Key", "value"),
                                                                              new MapEntry <> ("Key2", "value"));
  }
}
