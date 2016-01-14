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
package com.helger.commons.collection.attr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MapBasedReadOnlyAttributeContainer}.
 *
 * @author Philip Helger
 */
public final class MapBasedReadOnlyAttributeContainerTest
{
  @Test
  public void testInit ()
  {
    MapBasedReadOnlyAttributeContainer <String, Serializable> x = new MapBasedReadOnlyAttributeContainer <String, Serializable> (CollectionHelper.newMap (new String [] {},
                                                                                                                                                          new Serializable [] {}));
    assertNotNull (x.getAllAttributeNames ());
    assertTrue (x.getAllAttributeNames ().isEmpty ());
    assertTrue (x.isEmpty ());

    x = new MapBasedReadOnlyAttributeContainer <String, Serializable> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                "key2",
                                                                                                                "key3" },
                                                                                                new Serializable [] { "value",
                                                                                                                      "value2",
                                                                                                                      null }));
    assertEquals (3, x.getAttributeCount ());
    assertFalse (x.isEmpty ());
    assertEquals ("value2", x.getAttributeObject ("key2"));
    assertEquals ("value2", x.getAttributeAsString ("key2"));
    assertEquals ("value2", x.getCastedAttribute ("key2"));
    assertEquals ("def", x.getCastedAttribute ("key none", "def"));
    assertTrue (x.containsAttribute ("key2"));
    assertTrue (x.containsAttribute ("key3"));
    assertNull (x.getAttributeObject ("key3"));
    assertFalse (x.getAllAttributes ().isEmpty ());
    assertFalse (x.getAllAttributeNames ().isEmpty ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MapBasedReadOnlyAttributeContainer <String, Serializable> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                               "key2" },
                                                                                                                                                               new Serializable [] { "value",
                                                                                                                                                                                     "value2" })),
                                                                       new MapBasedReadOnlyAttributeContainer <String, Serializable> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                               "key2" },
                                                                                                                                                               new Serializable [] { "value",
                                                                                                                                                                                     "value2" })));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MapBasedReadOnlyAttributeContainer <String, Serializable> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                                   "key2" },
                                                                                                                                                                   new Serializable [] { "value",
                                                                                                                                                                                         "value2" })),
                                                                           new MapBasedAttributeContainerAny <String> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                "key2" },
                                                                                                                                                new Serializable [] { "value",
                                                                                                                                                                      "value" })));

    try
    {
      new MapBasedReadOnlyAttributeContainer <String, Serializable> ((Map <String, Serializable>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
