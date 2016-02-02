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
package com.helger.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;

import com.helger.commons.compare.IComparator;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link ContainerConversionHelper}
 *
 * @author Philip Helger
 */
public final class ContainerConversionHelperTest extends AbstractCommonsTestCase
{
  private static final class MyIntegerCompi implements IComparator <Integer>
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
    Set <Integer> aSet = ContainerConversionHelper.newSet (it, StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newUnmodifiableSet (it, StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newOrderedSet (it, StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    it = CollectionHelper.newSet ("100", "-733").iterator ();
    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (it, StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

  }

  @Test
  public void testNewSetIterableWithConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSet (CollectionHelper.newList ("100", "-733"),
                                                           StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableSet (CollectionHelper.newList ("100", "-733"),
                                                         StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newOrderedSet (CollectionHelper.newList ("100", "-733"),
                                                    StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (CollectionHelper.newList ("100", "-733"),
                                                                StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));
  }

  @Test
  public void testNewSetIterableWithFilterAndConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSet (CollectionHelper.newList ("100", null, "-733"),
                                                           Objects::nonNull,
                                                           StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableSet (CollectionHelper.newList ("100", null, "-733"),
                                                         Objects::nonNull,
                                                         StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newOrderedSet (CollectionHelper.newList ("100", null, "-733"),
                                                    Objects::nonNull,
                                                    StringParser::parseIntObj);
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (Integer.valueOf (100)));
    assertTrue (aSet.contains (Integer.valueOf (-733)));

    aSet = ContainerConversionHelper.newUnmodifiableOrderedSet (CollectionHelper.newList ("100", null, "-733"),
                                                                Objects::nonNull,
                                                                StringParser::parseIntObj);
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

    List <Integer> aList = ContainerConversionHelper.newList (aSource, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new ArrayList <String> (), StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));
  }

  @Test
  public void testNewListArrayWithConverter ()
  {
    final String [] aSource = new String [] { "100", "-721" };

    List <Integer> aList = ContainerConversionHelper.newList (aSource, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new String [0], StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource, StringParser::parseIntObj);
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

    List <Integer> aList = ContainerConversionHelper.newList (aSource, Objects::nonNull, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newList (new ArrayList <String> (), Objects::nonNull, StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

    aList = ContainerConversionHelper.newUnmodifiableList (aSource, Objects::nonNull, StringParser::parseIntObj);
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
      ContainerConversionHelper.getSorted ((Iterator <String>) null, StringParser::parseIntObj);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (), (Function <String, Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList.iterator (), StringParser::parseIntObj);
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
      ContainerConversionHelper.getSorted ((Iterable <String>) null, StringParser::parseIntObj);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (), (Function <String, Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList, StringParser::parseIntObj);
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
      ContainerConversionHelper.getSorted ((Iterator <String>) null, StringParser::parseIntObj, new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (),
                                           (Function <String, Integer>) null,
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null comparator not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> ().iterator (), StringParser::parseIntObj, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList.iterator (),
                                                                        StringParser::parseIntObj,
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
      ContainerConversionHelper.getSorted ((Iterable <String>) null, StringParser::parseIntObj, new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null converter not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (),
                                           (Function <String, Integer>) null,
                                           new MyIntegerCompi ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null comparator not allowed
      ContainerConversionHelper.getSorted (new ArrayList <String> (), StringParser::parseIntObj, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final List <String> aList = CollectionHelper.newList ("6", "5", "4", "3");
    final List <Integer> aSorted = ContainerConversionHelper.getSorted (aList,
                                                                        StringParser::parseIntObj,
                                                                        new MyIntegerCompi ());
    assertEquals (aSorted.size (), 4);
    assertEquals (aSorted.get (0), Integer.valueOf (4));
    assertEquals (aSorted.get (1), Integer.valueOf (3));
    assertEquals (aSorted.get (2), Integer.valueOf (5));
    assertEquals (aSorted.get (3), Integer.valueOf (6));
  }
}
