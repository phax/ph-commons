/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.convert.IUnidirectionalConverter;
import com.helger.commons.convert.UnidirectionalConverterStringInteger;
import com.helger.commons.filter.impl.FilterNotNull;
import com.helger.commons.mock.AbstractPHTestCase;

/**
 * Test class for class {@link ContainerConversionHelper}
 *
 * @author Philip Helger
 */
public final class ContainerConversionHelperTest extends AbstractPHTestCase
{
  private static final class MyIntegerCompi implements Comparator <Integer>, Serializable
  {
    public MyIntegerCompi ()
    {}

    public int compare (final Integer aInt1, final Integer aInt2)
    {
      if (aInt1.intValue () == 4)
        return -1;
      if (aInt2.intValue () == 4)
        return +1;
      return aInt1.compareTo (aInt2);
    }
  }

  @Test
  public void testNewSetIteratorWithConverter ()
  {
    Iterator <String> it = CollectionHelper.newSet ("100", "-733").iterator ();
    Set <Integer> aSet = ContainerConversionHelper.newSet (it, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newUnmodifiableSet (it, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newOrderedSet (it, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (it, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

  }

  @Test
  public void testNewSetIterableWithConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSet (CollectionHelper.newList ("100", "-733"),
                                                           new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableSet (CollectionHelper.newList ("100", "-733"),
                                                         new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newOrderedSet (CollectionHelper.newList ("100", "-733"),
                                                    new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (CollectionHelper.newList ("100", "-733"),
                                                                new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));
  }

  @Test
  public void testNewSetIterableWithFilterAndConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSet (CollectionHelper.newList ("100", null, "-733"),
                                                           new FilterNotNull <String> (),
                                                           new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableSet (CollectionHelper.newList ("100", null, "-733"),
                                                         new FilterNotNull <String> (),
                                                         new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newOrderedSet (CollectionHelper.newList ("100", null, "-733"),
                                                    new FilterNotNull <String> (),
                                                    new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (CollectionHelper.newList ("100", null, "-733"),
                                                                new FilterNotNull <String> (),
                                                                new UnidirectionalConverterStringInteger (null));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));
  }

  @Test
  public void testNewListIterableWithConverter ()
  {
    final List <String> aSource = new ArrayList <String> ();
    assertTrue (aSource.add ("100"));
    assertTrue (aSource.add ("-721"));

    List <Integer> aList = ContainerConversionHelper.newList (aSource, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new ArrayList <String> (),
                                               new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));
  }

  @Test
  public void testNewListArrayWithConverter ()
  {
    final String [] aSource = new String [] { "100", "-721" };

    List <Integer> aList = ContainerConversionHelper.newList (aSource, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new String [0], new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource, new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));
  }

  @Test
  public void testNewListIterableWithFilterAndConverter ()
  {
    final List <String> aSource = new ArrayList <String> ();
    assertTrue (aSource.add ("100"));
    assertTrue (aSource.add (null));
    assertTrue (aSource.add ("-721"));

    List <Integer> aList = ContainerConversionHelper.newList (aSource,
                                                              new FilterNotNull <String> (),
                                                              new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new ArrayList <String> (),
                                               new FilterNotNull <String> (),
                                               new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource,
                                                           new FilterNotNull <String> (),
                                                           new UnidirectionalConverterStringInteger (null));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));
  }

  @Test
  public void testGetSortedIteratorWithConverter ()
  {
    try
    {
      // null iterator not allowed
      ContainerConversionHelper.getSorted ((Iterator <String>) null, new UnidirectionalConverterStringInteger (null));
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (),
                                           (IUnidirectionalConverter <String, Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList.iterator (),
                                                                        new UnidirectionalConverterStringInteger (null));
    assertEquals (aSorted.size (), 4);
    assertEquals (aSorted.get (0), Integer.valueOf (3));
    assertEquals (aSorted.get (1), Integer.valueOf (4));
    assertEquals (aSorted.get (2), Integer.valueOf (5));
    assertEquals (aSorted.get (3), Integer.valueOf (6));
  }

  @Test
  public void testGetSortedIterableWithConverter ()
  {
    try
    {
      // null iterator not allowed
      ContainerConversionHelper.getSorted ((Iterable <String>) null, new UnidirectionalConverterStringInteger (null));
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (), (IUnidirectionalConverter <String, Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList,
                                                                        new UnidirectionalConverterStringInteger (null));
    assertEquals (aSorted.size (), 4);
    assertEquals (aSorted.get (0), Integer.valueOf (3));
    assertEquals (aSorted.get (1), Integer.valueOf (4));
    assertEquals (aSorted.get (2), Integer.valueOf (5));
    assertEquals (aSorted.get (3), Integer.valueOf (6));
  }

  @Test
  public void testGetSortedIteratorWithConverterAndCompi ()
  {
    try
    {
      // null iterator not allowed
      ContainerConversionHelper.getSorted ((Iterator <String>) null,
                                           new UnidirectionalConverterStringInteger (null),
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (),
                                           (IUnidirectionalConverter <String, Integer>) null,
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null comparator not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (),
                                           new UnidirectionalConverterStringInteger (null),
                                           null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList.iterator (),
                                                                        new UnidirectionalConverterStringInteger (null),
                                                                        new MyIntegerCompi ());
    assertEquals (aSorted.size (), 4);
    assertEquals (aSorted.get (0), Integer.valueOf (4));
    assertEquals (aSorted.get (1), Integer.valueOf (3));
    assertEquals (aSorted.get (2), Integer.valueOf (5));
    assertEquals (aSorted.get (3), Integer.valueOf (6));
  }

  @Test
  public void testGetSortedIterableWithConverterAndCompi ()
  {
    try
    {
      // null iterator not allowed
      ContainerConversionHelper.getSorted ((Iterable <String>) null,
                                           new UnidirectionalConverterStringInteger (null),
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (),
                                           (IUnidirectionalConverter <String, Integer>) null,
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null comparator not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (),
                                           new UnidirectionalConverterStringInteger (null),
                                           null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList,
                                                                        new UnidirectionalConverterStringInteger (null),
                                                                        new MyIntegerCompi ());
    assertEquals (aSorted.size (), 4);
    assertEquals (aSorted.get (0), Integer.valueOf (4));
    assertEquals (aSorted.get (1), Integer.valueOf (3));
    assertEquals (aSorted.get (2), Integer.valueOf (5));
    assertEquals (aSorted.get (3), Integer.valueOf (6));
  }

  @Test
  public void testGetIteratorWithConversion ()
  {
    final Iterator <Integer> it = ContainerConversionHelper.getIterator (CollectionHelper.newList ("100", "-25"),
                                                                         new UnidirectionalConverterStringInteger (null));
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertEquals (Integer.valueOf (100), it.next ());
    assertTrue (it.hasNext ());
    assertEquals (Integer.valueOf (-25), it.next ());
    assertFalse (it.hasNext ());

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    it.remove ();
  }
}
