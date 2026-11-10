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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

/**
 * Test class for class {@link CommonsArrayDeque}.
 *
 * @author Philip Helger
 */
public final class CommonsArrayDequeTest
{
  @Test
  public void testBasic ()
  {
    final CommonsArrayDeque <String> aDeque = new CommonsArrayDeque <> ();
    assertTrue (aDeque.isEmpty ());
    aDeque.push ("b");
    aDeque.push ("a");
    aDeque.addLast ("c");
    assertEquals (3, aDeque.size ());
    assertEquals ("a", aDeque.peekFirst ());
    assertEquals ("c", aDeque.peekLast ());

    // Methods from ICommonsCollection
    assertEquals (new CommonsArrayList <> ("a", "b", "c"), aDeque.getCopyAsList ());
    assertTrue (aDeque.containsAny (x -> x.equals ("b")));
    assertEquals (1, aDeque.getCount (x -> x.equals ("c")));

    final CommonsArrayDeque <String> aClone = aDeque.getClone ();
    assertNotSame (aDeque, aClone);
    assertEquals (new CommonsArrayList <> (aDeque), new CommonsArrayList <> (aClone));
    aClone.pop ();
    assertEquals (3, aDeque.size ());
    assertEquals (2, aClone.size ());

    assertEquals ("a", aDeque.pop ());
    assertEquals ("c", aDeque.pollLast ());
    assertEquals ("b", aDeque.poll ());
    assertTrue (aDeque.isEmpty ());

    try
    {
      aDeque.add (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testCtor ()
  {
    CommonsArrayDeque <String> aDeque = new CommonsArrayDeque <> (5);
    assertTrue (aDeque.isEmpty ());

    aDeque = new CommonsArrayDeque <> ((Collection <String>) null);
    assertTrue (aDeque.isEmpty ());
    aDeque = new CommonsArrayDeque <> ((Iterable <String>) null);
    assertTrue (aDeque.isEmpty ());
    aDeque = new CommonsArrayDeque <> ((String []) null);
    assertTrue (aDeque.isEmpty ());

    aDeque = new CommonsArrayDeque <> ("only");
    assertEquals (1, aDeque.size ());

    aDeque = new CommonsArrayDeque <> ("a", "b", "c");
    assertEquals ("a", aDeque.getFirst ());
    assertEquals ("c", aDeque.getLast ());

    aDeque = new CommonsArrayDeque <> (new CommonsArrayList <> ("x", "y"));
    assertEquals ("x", aDeque.getFirst ());

    aDeque = new CommonsArrayDeque <> ((Iterable <String>) new CommonsArrayList <> ("x", "y", "z"));
    assertEquals (3, aDeque.size ());

    aDeque = new CommonsArrayDeque <> (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                       x -> x.toString ());
    assertEquals ("1", aDeque.getFirst ());

    aDeque = new CommonsArrayDeque <> (new Integer [] { Integer.valueOf (3), Integer.valueOf (4) }, x -> x.toString ());
    assertEquals ("4", aDeque.getLast ());
  }
}
