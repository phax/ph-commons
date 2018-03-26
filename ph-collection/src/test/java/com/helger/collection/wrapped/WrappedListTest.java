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
package com.helger.collection.wrapped;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.lang.GenericReflection;

/**
 * Test class for class {@link WrappedList}.
 *
 * @author Philip Helger
 */
public final class WrappedListTest
{
  private static <T> void _testList (final List <T> aList, final Class <T> aClass) throws InstantiationException,
                                                                                   IllegalAccessException,
                                                                                   NoSuchMethodException,
                                                                                   SecurityException,
                                                                                   InvocationTargetException
  {
    final Constructor <T> aCtor = aClass.getConstructor ();

    // empty at the beginning
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    assertFalse (aList.iterator ().hasNext ());
    assertFalse (aList.listIterator ().hasNext ());
    assertFalse (aList.listIterator (0).hasNext ());

    // clear empty list :)
    aList.clear ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());

    // add null
    assertTrue (aList.add (null));
    assertNull (aList.get (0));
    assertEquals (1, aList.size ());

    // add new
    aList.add (0, aCtor.newInstance ());
    assertNotNull (aList.get (0));
    assertEquals (2, aList.size ());

    // add all
    aList.addAll (new CommonsArrayList <> (aCtor.newInstance (), aCtor.newInstance ()));

    // add all
    aList.addAll (1, new CommonsArrayList <> (aCtor.newInstance (), aCtor.newInstance ()));

    // and empty instance is already contained
    assertTrue (aList.contains (aCtor.newInstance ()));

    // contains all
    assertTrue (aList.containsAll (new CommonsArrayList <> (aCtor.newInstance (), aCtor.newInstance ())));

    // indexOf
    assertEquals (0, aList.indexOf (aCtor.newInstance ()));

    // check last index
    assertEquals (aList.size () - 1, aList.lastIndexOf (aCtor.newInstance ()));

    // remove
    final int nOldSize = aList.size ();
    assertTrue (aList.remove (aCtor.newInstance ()));
    assertEquals (nOldSize - 1, aList.size ());

    assertNotNull (aList.remove (0));
    assertEquals (nOldSize - 2, aList.size ());

    assertTrue (aList.removeAll (new CommonsArrayList <> (aCtor.newInstance ())));
    assertEquals (1, aList.size ());
    assertNull (aList.get (0));

    assertTrue (aList.retainAll (new CommonsArrayList <> (aCtor.newInstance ())));
    assertEquals (0, aList.size ());

    // re-fill
    assertTrue (aList.add (null));
    assertTrue (aList.add (aCtor.newInstance ()));
    assertEquals (2, aList.size ());
    assertNull (aList.set (0, aCtor.newInstance ()));
    assertEquals (2, aList.size ());
    assertEquals (aList, aList.subList (0, 2));

    // array
    assertArrayEquals (new Object [] { aCtor.newInstance (), aCtor.newInstance () }, aList.toArray ());
    assertNotNull (aList.toArray (GenericReflection.uncheckedCast (Array.newInstance (aClass, 27))));
    assertTrue (aList.hashCode () > 0);
    assertNotNull (aList.toString ());
  }

  @Test
  public void testList () throws Exception
  {
    _testList (new WrappedList <> (new CommonsArrayList <> ()), String.class);
  }
}
