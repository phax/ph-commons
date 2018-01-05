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
package com.helger.collection.single;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link SingleElementList}.
 *
 * @author Philip Helger
 */
public final class SingleElementListTest
{
  private <T> void _clear (final List <T> aList)
  {
    aList.clear ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    assertEquals (0, aList.toArray ().length);
    assertEquals (0, aList.toArray (new Object [0]).length);
    assertFalse (aList.iterator ().hasNext ());
  }

  @Test
  public void testBasicStartFromEmpty ()
  {
    final SingleElementList <String> aList = new SingleElementList<> ();

    // empty list
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    _clear (aList);
    assertFalse (aList.contains ("any"));
    try
    {
      aList.get (0);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}

    // add an element
    assertTrue (aList.add ("any"));
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertTrue (aList.contains ("any"));
    assertFalse (aList.contains ("other"));
    assertTrue (aList.containsAll (new CommonsArrayList<> ("any")));
    assertFalse (aList.containsAll (new CommonsArrayList<> ("other")));
    assertFalse (aList.containsAll (new CommonsArrayList<> ("other")));
    assertEquals ("any", aList.get (0));
    assertEquals (1, aList.toArray ().length);
    assertEquals ("any", aList.toArray ()[0]);
    try
    {
      aList.get (1);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}
    assertEquals (0, aList.indexOf ("any"));
    assertEquals (-1, aList.indexOf ("other"));

    // cannot add another element
    try
    {
      aList.add ("other");
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    _clear (aList);
    aList.add ("other");
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertFalse (aList.contains ("any"));
    assertTrue (aList.contains ("other"));
    assertEquals ("other", aList.get (0));
    assertEquals (1, aList.toArray ().length);
    assertEquals ("other", aList.toArray ()[0]);

    // try remove by index
    try
    {
      aList.remove (1);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}
    assertEquals (1, aList.size ());
    assertEquals ("other", aList.remove (0));
    assertEquals (0, aList.size ());
  }

  @Test
  public void testBasicStartWithElement ()
  {
    final SingleElementList <String> aList = new SingleElementList<> ("any");
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertTrue (aList.contains ("any"));
    assertFalse (aList.contains ("other"));
    assertEquals ("any", aList.get (0));
    assertEquals (1, aList.toArray ().length);
    assertEquals ("any", aList.toArray ()[0]);
    assertEquals (0, aList.indexOf ("any"));
    assertEquals (0, aList.lastIndexOf ("any"));
    assertEquals (-1, aList.indexOf ("other"));
    assertEquals (-1, aList.lastIndexOf ("other"));

    // cannot add another element
    try
    {
      aList.add ("other");
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    _clear (aList);
    aList.add ("other");
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertFalse (aList.contains ("any"));
    assertTrue (aList.contains ("other"));
    assertEquals ("other", aList.get (0));
    assertEquals (1, aList.toArray ().length);
    assertEquals ("other", aList.toArray ()[0]);

    // try remove by object
    assertFalse (aList.remove ("any"));
    assertEquals (1, aList.size ());
    assertTrue (aList.remove ("other"));
    assertEquals (0, aList.size ());
  }

  @Test
  public void testAddAll ()
  {
    final SingleElementList <String> aList = new SingleElementList<> ("init");

    try
    {
      // list already contains sthg.
      aList.addAll (new CommonsArrayList<> ("Hallo", "Welt"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    _clear (aList);

    try
    {
      // more than one element provided
      aList.addAll (new CommonsArrayList<> ("Hallo", "Welt"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    assertTrue (aList.isEmpty ());

    aList.addAll (new CommonsArrayList<> ("Hallo"));
    assertEquals (1, aList.size ());
  }

  @Test
  public void testSubList ()
  {
    final SingleElementList <String> aList = new SingleElementList<> ("init");
    assertEquals (0, aList.subList (0, 0).size ());
    assertEquals (1, aList.subList (0, 1).size ());
    assertEquals (0, aList.subList (1, 1).size ());
    assertEquals ("init", aList.subList (0, 1).get (0));

    try
    {
      // illegal from index
      aList.subList (-1, 0);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}

    try
    {
      // illegal to index
      aList.subList (0, 2);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}

    _clear (aList);
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testIterate ()
  {
    SingleElementList <String> aList = new SingleElementList<> ("Hallo");
    assertEquals (1, aList.size ());
    assertTrue (aList.iterator ().hasNext ());
    assertEquals ("Hallo", aList.iterator ().next ());
    assertTrue (aList.listIterator ().hasNext ());
    assertEquals ("Hallo", aList.listIterator ().next ());
    assertTrue (aList.listIterator (0).hasNext ());
    assertEquals ("Hallo", aList.listIterator (0).next ());
    assertEquals ("Hallo", aList.remove (0));
    aList.set (0, "Hallo");
    assertTrue (aList.remove ("Hallo"));
    assertFalse (aList.remove ("Hallo"));

    aList = new SingleElementList<> ();
    assertEquals (0, aList.size ());
    assertFalse (aList.iterator ().hasNext ());
    assertFalse (aList.listIterator ().hasNext ());
    assertFalse (aList.removeAll (new CommonsArrayList<> ("Hallo", "Welt")));
    assertTrue (aList.isEmpty ());
    aList.set (0, "Hallo");
    assertTrue (aList.removeAll (new CommonsArrayList<> ("Hallo")));
    assertTrue (aList.isEmpty ());
    aList.set (0, "Hallo");
    assertTrue (aList.removeAll (new CommonsArrayList<> ("Hallo")));
    assertTrue (aList.isEmpty ());
    aList.set (0, "Hallo");
    assertFalse (aList.removeAll (new CommonsArrayList<> ("Hallo", "Welt")));
    assertTrue (aList.isEmpty ());
    aList.add (0, "Hallo");
    aList.remove (0);

    try
    {
      aList.remove (0);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}
    try
    {
      aList.set (-1, "xyz");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aList.set (1, "xyz");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aList.add (1, "xyz");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aList.addAll (0, new CommonsArrayList<> ("xyz", "ZZ"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aList.addAll (1, new CommonsArrayList<> ("xyz"));
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}
  }

  @Test
  public void testRetainAll ()
  {
    final SingleElementList <String> aList = new SingleElementList<> ("init");
    assertTrue (aList.retainAll (new CommonsArrayList<> ("init", "all")));
    assertTrue (aList.contains ("init"));
    assertFalse (aList.retainAll (new CommonsArrayList<> ("all")));
    assertFalse (aList.contains ("init"));
    assertFalse (aList.retainAll (new CommonsArrayList<> ("init")));
    assertFalse (aList.contains ("init"));
  }

  @Test
  public void testStdMethods ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new SingleElementList<> ("init"),
                                                                       new SingleElementList<> ("init"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SingleElementList<> ("init"),
                                                                           new SingleElementList<> ("init2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SingleElementList<> ("init"),
                                                                           new SingleElementList<> (Boolean.TRUE));
    assertArrayEquals (new SingleElementList<> ("init").toArray (), new SingleElementList<> ("init").toArray ());
    assertArrayEquals (new SingleElementList<> ("init").toArray (new String [0]),
                       new SingleElementList<> ("init").toArray (new String [0]));
    assertArrayEquals (new SingleElementList<> ("init").toArray (new String [1]),
                       new SingleElementList<> ("init").toArray (new String [1]));
    assertArrayEquals (new SingleElementList<> ("init").toArray (new String [5]),
                       new SingleElementList<> ("init").toArray (new String [5]));
  }
}
