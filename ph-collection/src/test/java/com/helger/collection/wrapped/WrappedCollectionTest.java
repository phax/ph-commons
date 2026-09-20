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
package com.helger.collection.wrapped;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedSet;

/**
 * Test class for class {@link WrappedCollection}, {@link WrappedList} and {@link WrappedSet}.
 *
 * @author Philip Helger
 */
public final class WrappedCollectionTest
{
  @Test
  public void testWrappedCollection ()
  {
    final ICommonsList <String> aBase = new CommonsArrayList <> ("a", "b");
    final WrappedCollection <String> aColl = new WrappedCollection <> (aBase);

    assertEquals (2, aColl.size ());
    assertFalse (aColl.isEmpty ());
    assertTrue (aColl.contains ("a"));
    assertTrue (aColl.containsAll (new CommonsArrayList <> ("a", "b")));
    assertNotNull (aColl.iterator ());
    assertNotNull (aColl.toString ());
    assertArrayEquals (new String [] { "a", "b" }, aColl.toArray ());
    assertArrayEquals (new String [] { "a", "b" }, aColl.toArray (new String [0]));

    // Changes are passed to the base collection
    assertTrue (aColl.add ("c"));
    assertEquals (3, aBase.size ());
    assertTrue (aColl.addAll (new CommonsArrayList <> ("d")));
    assertEquals (4, aBase.size ());
    assertTrue (aColl.remove ("d"));
    assertEquals (3, aBase.size ());
    assertTrue (aColl.removeAll (new CommonsArrayList <> ("c")));
    assertEquals (2, aBase.size ());
    assertTrue (aColl.retainAll (new CommonsArrayList <> ("a")));
    assertEquals (1, aBase.size ());

    aColl.clear ();
    assertTrue (aBase.isEmpty ());
    assertTrue (aColl.isEmpty ());

    // equals compares the wrapped collections and not the facades
    assertEquals (aColl, aColl);
    assertEquals (aColl, new WrappedCollection <> (new CommonsArrayList <String> ()));
    assertEquals (new WrappedCollection <> (new CommonsArrayList <String> ()).hashCode (), aColl.hashCode ());
    assertNotEquals (aColl, new WrappedCollection <> (new CommonsArrayList <> ("a")));
    assertNotEquals (aColl, null);
    assertNotEquals (aColl, "any other type");

    // A facade is also equal to the collection it wraps
    assertTrue (aColl.equals (aBase));
    assertTrue (aColl.equals (new CommonsArrayList <> ()));
    assertFalse (aColl.equals (new CommonsArrayList <> ("a")));
    assertEquals (aBase.hashCode (), aColl.hashCode ());
  }

  @Test
  public void testWrappedList ()
  {
    final ICommonsList <String> aBase = new CommonsArrayList <> ("a", "b");
    final WrappedList <String> aList = new WrappedList <> (aBase);

    assertEquals (2, aList.size ());
    assertEquals ("a", aList.get (0));
    assertEquals (0, aList.indexOf ("a"));
    assertNotNull (aList.toString ());

    final List <String> aClone = aList.getClone ();
    assertNotSame (aList, aClone);
    assertEquals (2, aClone.size ());

    // equals compares the wrapped collections and not the facades
    assertEquals (aList, aList);
    assertEquals (aList, new WrappedList <> (new CommonsArrayList <> ("a", "b")));
    assertEquals (new WrappedList <> (new CommonsArrayList <> ("a", "b")).hashCode (), aList.hashCode ());
    assertNotEquals (aList, new WrappedList <> (new CommonsArrayList <> ("a")));
    assertNotEquals (aList, null);
    assertNotEquals (aList, "any other type");

    // A facade is also equal to the collection it wraps
    assertTrue (aList.equals (aBase));
    assertTrue (aList.equals (new CommonsArrayList <> ("a", "b")));
    assertEquals (aBase.hashCode (), aList.hashCode ());

    // The clone is a new facade around the same list
    aList.clear ();
    assertTrue (aBase.isEmpty ());
    assertTrue (aClone.isEmpty ());
  }

  @Test
  public void testWrappedSet ()
  {
    final ICommonsOrderedSet <String> aBase = new CommonsLinkedHashSet <> ("a", "b");
    final WrappedSet <String> aSet = new WrappedSet <> (aBase);

    assertEquals (2, aSet.size ());
    assertFalse (aSet.isEmpty ());
    assertTrue (aSet.contains ("a"));
    assertTrue (aSet.containsAll (new CommonsArrayList <> ("a", "b")));
    assertNotNull (aSet.iterator ());
    assertNotNull (aSet.toString ());
    assertArrayEquals (new String [] { "a", "b" }, aSet.toArray ());
    assertArrayEquals (new String [] { "a", "b" }, aSet.toArray (new String [0]));

    final Set <String> aClone = aSet.getClone ();
    assertNotSame (aSet, aClone);
    assertEquals (2, aClone.size ());
    assertTrue (aClone.contains ("a"));

    assertTrue (aSet.add ("c"));
    assertEquals (3, aBase.size ());
    assertTrue (aSet.addAll (new CommonsArrayList <> ("d")));
    assertEquals (4, aBase.size ());
    assertTrue (aSet.remove ("d"));
    assertTrue (aSet.removeAll (new CommonsArrayList <> ("c")));
    assertTrue (aSet.retainAll (new CommonsArrayList <> ("a")));
    assertEquals (1, aBase.size ());

    aSet.clear ();
    assertTrue (aBase.isEmpty ());

    // equals compares the wrapped collections and not the facades
    assertEquals (aSet, aSet);
    assertEquals (aSet, new WrappedSet <> (new CommonsLinkedHashSet <String> ()));
    assertEquals (new WrappedSet <> (new CommonsLinkedHashSet <String> ()).hashCode (), aSet.hashCode ());
    assertNotEquals (aSet, new WrappedSet <> (new CommonsLinkedHashSet <> ("a")));
    assertNotEquals (aSet, null);
    assertNotEquals (aSet, "any other type");

    // A facade is also equal to the collection it wraps
    assertTrue (aSet.equals (aBase));
    final CommonsLinkedHashSet <String> aOther = new CommonsLinkedHashSet <> ();
    assertTrue (aSet.equals (aOther));
    assertFalse (aSet.equals (new CommonsLinkedHashSet <> ("a")));
    assertEquals (aBase.hashCode (), aSet.hashCode ());

    // The clone is a new facade around the same set
    assertTrue (aClone.isEmpty ());
  }
}
