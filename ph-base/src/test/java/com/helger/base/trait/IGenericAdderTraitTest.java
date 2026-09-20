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
package com.helger.base.trait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test class for the default methods of {@link IGenericAdderTrait}.
 *
 * @author Philip Helger
 */
public final class IGenericAdderTraitTest
{
  @SafeVarargs
  private static <T> List <T> listOf (final T... aValues)
  {
    return Arrays.asList (aValues);
  }

  @Test
  public void testThisAsT ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    assertSame (a, a.thisAsT ());
    assertSame (MockStringValue.CONVERTER, a.getTypeConverterTo ());
  }

  @Test
  public void testAddSingleValue ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    assertSame (a, a.add (new MockStringValue ("v")));
    assertEquals (listOf ("v"), a.getAllAsString ());
  }

  @Test
  public void testAddPrimitives ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add (true);
    a.add ((byte) 1);
    a.add ('c');
    a.add (1.5d);
    a.add (2.5f);
    a.add (3);
    a.add (4L);
    a.add ((short) 5);

    assertEquals (listOf ("true", "1", "c", "1.5", "2.5", "3", "4", "5"), a.getAllAsString ());
  }

  @Test
  public void testAddObject ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add ("any");
    a.add ((Object) null);
    assertEquals (listOf ("any", null), a.getAllAsString ());
  }

  @Test
  public void testAddIf ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addIf (new MockStringValue ("yes"), x -> true);
    a.addIf (new MockStringValue ("no"), x -> false);
    assertEquals (listOf ("yes"), a.getAllAsString ());

    // The Object based overload
    a.addIf ((Object) "obj-yes", x -> true);
    a.addIf ((Object) "obj-no", x -> false);
    assertEquals (listOf ("yes", "obj-yes"), a.getAllAsString ());
  }

  @Test
  public void testAddIfNotNull ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addIfNotNull (new MockStringValue ("v"));
    a.addIfNotNull (null);
    assertEquals (listOf ("v"), a.getAllAsString ());
  }

  @Test
  public void testAddAtPrimitives ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add ("base");
    a.addAt (0, true);
    a.addAt (0, (byte) 1);
    a.addAt (0, 'c');
    a.addAt (0, 1.5d);
    a.addAt (0, 2.5f);
    a.addAt (0, 3);
    a.addAt (0, 4L);
    a.addAt (0, (short) 5);
    a.addAt (0, "obj");

    assertEquals (listOf ("obj", "5", "4", "3", "2.5", "1.5", "c", "1", "true", "base"), a.getAllAsString ());
  }

  @Test
  public void testAddAllPrimitiveArrays ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addAll (new boolean [] { true, false });
    a.addAll (new byte [] { 1, 2 });
    a.addAll (new char [] { 'a', 'b' });
    a.addAll (new double [] { 1.5d });
    a.addAll (new float [] { 2.5f });
    a.addAll (new int [] { 3 });
    a.addAll (new long [] { 4L });
    a.addAll (new short [] { 5 });

    assertEquals (listOf ("true", "false", "1", "2", "a", "b", "1.5", "2.5", "3", "4", "5"), a.getAllAsString ());
  }

  @Test
  public void testAddAllObjectsAndIterable ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addAll (new Object [] { "a", "b" });
    a.addAll ((Object []) null);
    a.addAll (listOf ("c", "d"));
    a.addAll ((Iterable <?>) null);
    assertEquals (listOf ("a", "b", "c", "d"), a.getAllAsString ());
  }

  @Test
  public void testAddAllMapped ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addAllMapped (new String [] { "a", "b" }, x -> new MockStringValue (x.toUpperCase (java.util.Locale.ROOT)));
    a.addAllMapped ((String []) null, MockStringValue::new);
    a.addAllMapped (listOf ("c"), x -> new MockStringValue (x.toUpperCase (java.util.Locale.ROOT)));
    a.addAllMapped ((Iterable <String>) null, MockStringValue::new);
    assertEquals (listOf ("A", "B", "C"), a.getAllAsString ());
  }

  @Test
  public void testAddAllAtPrimitiveArrays ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add ("base");
    a.addAllAt (0, new boolean [] { true });
    a.addAllAt (0, new byte [] { 1 });
    a.addAllAt (0, new char [] { 'a' });
    a.addAllAt (0, new double [] { 1.5d });
    a.addAllAt (0, new float [] { 2.5f });
    a.addAllAt (0, new int [] { 3 });
    a.addAllAt (0, new long [] { 4L });
    a.addAllAt (0, new short [] { 5 });

    assertEquals (listOf ("5", "4", "3", "2.5", "1.5", "a", "1", "true", "base"), a.getAllAsString ());
  }

  @Test
  public void testAddAllAtObjectsAndIterable ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add ("base");
    a.addAllAt (0, new Object [] { "a", "b" });
    a.addAllAt (0, (Object []) null);
    a.addAllAt (0, listOf ("c"));
    a.addAllAt (0, (Iterable <?>) null);
    assertEquals (listOf ("c", "a", "b", "base"), a.getAllAsString ());
  }

  @Test
  public void testAddAllMappedAt ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.add ("base");
    a.addAllMappedAt (0, new String [] { "a" }, x -> new MockStringValue (x.toUpperCase (java.util.Locale.ROOT)));
    a.addAllMappedAt (0, (String []) null, MockStringValue::new);
    a.addAllMappedAt (0, listOf ("b"), x -> new MockStringValue (x.toUpperCase (java.util.Locale.ROOT)));
    a.addAllMappedAt (0, (Iterable <String>) null, MockStringValue::new);
    assertEquals (listOf ("B", "A", "base"), a.getAllAsString ());
  }

  @Test
  public void testEmptyArraysChangeNothing ()
  {
    final MockAdderTrait a = new MockAdderTrait ();
    a.addAll (new boolean [0]);
    a.addAll (new byte [0]);
    a.addAll (new char [0]);
    a.addAll (new double [0]);
    a.addAll (new float [0]);
    a.addAll (new int [0]);
    a.addAll (new long [0]);
    a.addAll (new short [0]);
    a.addAll (new Object [0]);
    assertTrue (a.getAll ().isEmpty ());

    a.addAllAt (0, new boolean [0]);
    a.addAllAt (0, new byte [0]);
    a.addAllAt (0, new char [0]);
    a.addAllAt (0, new double [0]);
    a.addAllAt (0, new float [0]);
    a.addAllAt (0, new int [0]);
    a.addAllAt (0, new long [0]);
    a.addAllAt (0, new short [0]);
    a.addAllAt (0, new Object [0]);
    assertTrue (a.getAll ().isEmpty ());
  }
}
