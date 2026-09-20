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
package com.helger.typeconvert.spi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.typeconvert.impl.TypeConverter;

/**
 * Test class for class {@link CollectionTypeConverterRegistrar}.
 *
 * @author Philip Helger
 */
public final class CollectionTypeConverterRegistrarTest
{
  private static final ICommonsList <String> SRC_LIST = new CommonsArrayList <> ("a", "b");

  private static <T extends Comparable <T>> void _testToAllCollections (final Object aSrcArray,
                                                                        final ICommonsList <T> aExpected)
  {
    assertEquals (aExpected, TypeConverter.convert (aSrcArray, CommonsArrayList.class));
    assertEquals (new CommonsHashSet <> (aExpected), TypeConverter.convert (aSrcArray, CommonsHashSet.class));
    assertEquals (new CommonsLinkedHashSet <> (aExpected),
                  TypeConverter.convert (aSrcArray, CommonsLinkedHashSet.class));
    assertEquals (new CommonsTreeSet <> (aExpected), TypeConverter.convert (aSrcArray, CommonsTreeSet.class));
  }

  @Test
  public void testToListTypes ()
  {
    final ICommonsList <String> aExpected = new CommonsArrayList <> ("a", "b");
    final ICommonsList <String> aExpectedSingle = new CommonsArrayList <> ("a");

    for (final Class <?> aDstClass : new Class <?> [] { ArrayList.class,
                                                        Vector.class,
                                                        LinkedList.class,
                                                        CopyOnWriteArrayList.class,
                                                        List.class })
    {
      // Collection source
      assertEquals (aDstClass.getName (), aExpected, TypeConverter.convert (SRC_LIST, aDstClass));
      // Non-collection source
      assertEquals (aDstClass.getName (), aExpectedSingle, TypeConverter.convert ("a", aDstClass));
    }
  }

  @Test
  public void testToSetTypes ()
  {
    final Set <String> aExpected = new CommonsHashSet <> (SRC_LIST);
    final Set <String> aExpectedSingle = new CommonsHashSet <> ("a");

    for (final Class <?> aDstClass : new Class <?> [] { TreeSet.class,
                                                        LinkedHashSet.class,
                                                        CopyOnWriteArraySet.class,
                                                        Set.class })
    {
      // Collection source
      assertEquals (aDstClass.getName (), aExpected, TypeConverter.convert (SRC_LIST, aDstClass));
      // Non-collection source
      assertEquals (aDstClass.getName (), aExpectedSingle, TypeConverter.convert ("a", aDstClass));
    }
  }

  @Test
  public void testPrimitiveArrayToCollections ()
  {
    _testToAllCollections (new boolean [] { true, false }, new CommonsArrayList <> (Boolean.TRUE, Boolean.FALSE));
    _testToAllCollections (new byte [] { 1, 2 },
                           new CommonsArrayList <> (Byte.valueOf ((byte) 1), Byte.valueOf ((byte) 2)));
    _testToAllCollections (new char [] { 'a', 'b' },
                           new CommonsArrayList <> (Character.valueOf ('a'), Character.valueOf ('b')));
    _testToAllCollections (new double [] { 1, 2 }, new CommonsArrayList <> (Double.valueOf (1), Double.valueOf (2)));
    _testToAllCollections (new float [] { 1, 2 }, new CommonsArrayList <> (Float.valueOf (1), Float.valueOf (2)));
    _testToAllCollections (new int [] { 1, 2 }, new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)));
    _testToAllCollections (new long [] { 1, 2 }, new CommonsArrayList <> (Long.valueOf (1), Long.valueOf (2)));
    _testToAllCollections (new short [] { 1, 2 },
                           new CommonsArrayList <> (Short.valueOf ((short) 1), Short.valueOf ((short) 2)));
  }

  @Test
  public void testToBooleanArray ()
  {
    final boolean [] aExpected = new boolean [] { true, false };
    // From array
    assertArrayEquals (aExpected,
                       TypeConverter.convert (new Boolean [] { Boolean.TRUE, Boolean.FALSE }, boolean [].class));
    // From Collection
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("true", "false"), boolean [].class));
    // From single object
    assertArrayEquals (new boolean [] { true }, TypeConverter.convert ("true", boolean [].class));
  }

  @Test
  public void testToByteArray ()
  {
    final byte [] aExpected = new byte [] { 1, 2 };
    // From array
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, byte [].class));
    // From Collection
    assertArrayEquals (aExpected,
                       TypeConverter.convert (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                              byte [].class));
    // From single object - String is handled by the explicit Base64 converter
    assertArrayEquals (new byte [] { 1 }, TypeConverter.convert (Integer.valueOf (1), byte [].class));
  }

  @Test
  public void testToCharArray ()
  {
    final char [] aExpected = new char [] { 'a', 'b' };
    // From array
    assertArrayEquals (aExpected,
                       TypeConverter.convert (new Character [] { Character.valueOf ('a'), Character.valueOf ('b') },
                                              char [].class));
    // From Collection
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("a", "b"), char [].class));
    // From single object - String is handled by the explicit converter
    assertArrayEquals (new char [] { 'a' }, TypeConverter.convert (Character.valueOf ('a'), char [].class));
    // char[] to String and back
    assertEquals ("ab", TypeConverter.convert (aExpected, String.class));
    assertArrayEquals (aExpected, TypeConverter.convert ("ab", char [].class));
  }

  @Test
  public void testToDoubleArray ()
  {
    final double [] aExpected = new double [] { 1, 2 };
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, double [].class), 0.0001);
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("1", "2"), double [].class), 0.0001);
    assertArrayEquals (new double [] { 1 }, TypeConverter.convert ("1", double [].class), 0.0001);
  }

  @Test
  public void testToFloatArray ()
  {
    final float [] aExpected = new float [] { 1, 2 };
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, float [].class), 0.0001f);
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("1", "2"), float [].class), 0.0001f);
    assertArrayEquals (new float [] { 1 }, TypeConverter.convert ("1", float [].class), 0.0001f);
  }

  @Test
  public void testToIntArray ()
  {
    final int [] aExpected = new int [] { 1, 2 };
    assertArrayEquals (aExpected, TypeConverter.convert (new long [] { 1, 2 }, int [].class));
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("1", "2"), int [].class));
    assertArrayEquals (new int [] { 1 }, TypeConverter.convert ("1", int [].class));
  }

  @Test
  public void testToLongArray ()
  {
    final long [] aExpected = new long [] { 1, 2 };
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, long [].class));
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("1", "2"), long [].class));
    assertArrayEquals (new long [] { 1 }, TypeConverter.convert ("1", long [].class));
  }

  @Test
  public void testToShortArray ()
  {
    final short [] aExpected = new short [] { 1, 2 };
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, short [].class));
    assertArrayEquals (aExpected, TypeConverter.convert (new CommonsArrayList <> ("1", "2"), short [].class));
    assertArrayEquals (new short [] { 1 }, TypeConverter.convert ("1", short [].class));
  }

  @Test
  public void testToStringArray ()
  {
    final String [] aExpected = new String [] { "1", "2" };
    assertArrayEquals (aExpected, TypeConverter.convert (new int [] { 1, 2 }, String [].class));
    assertArrayEquals (aExpected,
                       TypeConverter.convert (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                              String [].class));
    assertArrayEquals (new String [] { "1" }, TypeConverter.convert (Integer.valueOf (1), String [].class));
  }

  @Test
  public void testByteArrayToString ()
  {
    final byte [] aBytes = "abc".getBytes (StandardCharsets.UTF_8);
    final String sBase64 = TypeConverter.convert (aBytes, String.class);
    assertTrue (sBase64.length () > 0);
    assertArrayEquals (aBytes, TypeConverter.convert (sBase64, byte [].class));
  }
}
