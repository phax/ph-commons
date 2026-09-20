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
package com.helger.typeconvert.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.helger.base.state.EContinue;
import com.helger.collection.commons.CommonsLinkedHashMap;

/**
 * Test class for class {@link AttributeContainerConcurrent}.
 *
 * @author Philip Helger
 */
public final class AttributeContainerConcurrentTest
{
  @Test
  public void testInit ()
  {
    final AttributeContainerConcurrent <String, String> x = new AttributeContainerConcurrent <> ();
    assertTrue (x.isEmpty ());
    assertTrue (x.putIn ("key", "value").isChanged ());
    assertFalse (x.putIn ("key", "value").isChanged ());
    assertEquals (1, x.size ());
    assertEquals ("value", x.getValue ("key"));

    // null keys are not supported
    assertFalse (x.containsKey (null));
    assertNull (x.get (null));
    assertTrue (x.containsKey ("key"));
    assertNull (x.get ("any other key"));
  }

  @Test
  public void testCtorMap ()
  {
    final Map <String, String> aSrc = new CommonsLinkedHashMap <> ();
    aSrc.put ("a", "b");
    final AttributeContainerConcurrent <String, String> x = new AttributeContainerConcurrent <> (aSrc);
    assertEquals (1, x.size ());
    assertEquals ("b", x.getValue ("a"));

    final AttributeContainerConcurrent <String, String> aClone = x.getClone ();
    assertNotSame (x, aClone);
    assertEquals (x, aClone);
    assertEquals (x.hashCode (), aClone.hashCode ());
    assertNotNull (x.toString ());

    assertNotEquals (x, null);
    assertNotEquals (x, "any other type");
    assertEquals (x, x);

    final AttributeContainerConcurrent <String, String> aOther = new AttributeContainerConcurrent <> ();
    assertNotEquals (x, aOther);
  }

  @Test
  public void testPutAllIn ()
  {
    final AttributeContainerConcurrent <String, String> x = new AttributeContainerConcurrent <> ();
    assertFalse (x.putAllIn (null).isChanged ());
    final Map <String, String> aSrc = new CommonsLinkedHashMap <> ();
    aSrc.put ("a", "b");
    aSrc.put ("c", "d");
    assertTrue (x.putAllIn (aSrc).isChanged ());
    assertEquals (2, x.size ());
    assertFalse (x.putAllIn (aSrc).isChanged ());
  }

  @Test
  public void testCallbacks ()
  {
    final AttributeContainerConcurrent <String, String> x = new AttributeContainerConcurrent <> ();
    assertNotNull (x.beforeSetValueCallbacks ());
    assertNotNull (x.afterSetValueCallbacks ());

    final AtomicInteger aAfterCount = new AtomicInteger (0);
    x.afterSetValueCallbacks ().add ( (sName, sOld, sNew) -> aAfterCount.incrementAndGet ());
    assertTrue (x.putIn ("key", "value").isChanged ());
    assertEquals (1, aAfterCount.get ());

    // A "break" in the before callback prevents the change
    x.beforeSetValueCallbacks ().add ( (sName, sNew) -> EContinue.BREAK);
    assertFalse (x.putIn ("key2", "value2").isChanged ());
    assertFalse (x.containsKey ("key2"));
    assertEquals (1, aAfterCount.get ());
  }
}
