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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link ContainerConversionHelper}
 *
 * @author Philip Helger
 */
public final class ContainerConversionHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testNewSetIteratorWithConverter ()
  {
    Iterator <String> it = IteratorHelper.getIterator ("100", "-733");
    Set <Integer> aSet = ContainerConversionHelper.newSetMapped (it, StringParser::parseIntObj);
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
  }

  @Test
  public void testNewSetIterableWithConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSetMapped (CollectionHelper.newList ("100", "-733"),
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
  }

  @Test
  public void testNewSetIterableWithFilterAndConverter ()
  {
    Set <Integer> aSet = ContainerConversionHelper.newSetMapped (CollectionHelper.newList ("100", null, "-733"),
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
  }

  @Test
  public void testNewListIterableWithConverter ()
  {
    final List <String> aSource = new ArrayList <String> ();
    assertTrue (aSource.add ("100"));
    assertTrue (aSource.add ("-721"));

    List <Integer> aList = ContainerConversionHelper.newListMapped (aSource, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newListMapped (new ArrayList <String> (), StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());
  }

  @Test
  public void testNewListArrayWithConverter ()
  {
    final String [] aSource = new String [] { "100", "-721" };

    List <Integer> aList = ContainerConversionHelper.newListMapped (aSource, StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newListMapped (new String [0], StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());
  }

  @Test
  public void testNewListIterableWithFilterAndConverter ()
  {
    final List <String> aSource = new ArrayList <> ();
    assertTrue (aSource.add ("100"));
    assertTrue (aSource.add (null));
    assertTrue (aSource.add ("-721"));

    List <Integer> aList = ContainerConversionHelper.newListMapped (aSource,
                                                                    Objects::nonNull,
                                                                    StringParser::parseIntObj);
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertTrue (aList.contains (Integer.valueOf (100)));
    assertTrue (aList.contains (Integer.valueOf (-721)));

    aList = ContainerConversionHelper.newListMapped (new ArrayList <> (), Objects::nonNull, StringParser::parseIntObj);
    assertNotNull (aList);
    assertTrue (aList.isEmpty ());

  }
}
