/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.string;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for class {@link StringImplode}.
 *
 * @author Philip Helger
 */
public final class StringImplodeTest
{
  @Test
  public void testImplodeIterable ()
  {
    final List <String> aList = Arrays.asList ("a", "b", "c");
    assertEquals ("", StringImplode.getImploded (".", (String []) null));
    assertEquals ("", StringImplode.getImploded (".", (List <String>) null));
    assertEquals ("a.b.c", StringImplode.getImploded (".", aList));
    assertEquals ("abc", StringImplode.getImploded ("", aList));
    assertEquals ("abc", StringImplode.getImploded (aList));
    assertEquals ("a.b.c", StringImplode.getImploded (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringImplode.getImploded ("", aList.toArray (new String [3])));
    assertEquals ("abc", StringImplode.getImploded (aList.toArray (new String [3])));

    assertEquals ("abc", StringImplode.getImploded (null, aList));
  }

  @Test
  public void testImplodeArray ()
  {
    final String [] aArray = { "a", "b", "c" };
    assertEquals ("a.b", StringImplode.getImploded (".", aArray, 0, 2));
    assertEquals ("ab", StringImplode.getImploded (aArray, 0, 2));
    assertEquals ("b.c", StringImplode.getImploded (".", aArray, 1, 2));
    assertEquals ("bc", StringImplode.getImploded (aArray, 1, 2));
    assertEquals ("", StringImplode.getImploded (".", aArray, 0, 0));
    assertEquals ("", StringImplode.getImploded (aArray, 0, 0));
    assertEquals ("", StringImplode.getImploded (".", aArray, 2, 0));
    assertEquals ("", StringImplode.getImploded (aArray, 2, 0));
    assertEquals ("", StringImplode.getImploded (".", null, 2, 0));
    assertEquals ("", StringImplode.getImploded (null, 2, 0));

    // null separator
    assertEquals ("abc", StringImplode.getImploded (null, aArray));
    // null separator
    assertEquals ("c", StringImplode.getImploded (null, aArray, 2, 2));
    assertEquals ("a.b", StringImplode.getImploded (".", aArray, -1, 2));
    assertEquals ("ab", StringImplode.getImploded (aArray, -1, 2));
    assertEquals ("a.b.c", StringImplode.getImploded (".", aArray, 0, -1));
    assertEquals ("abc", StringImplode.getImploded (aArray, 0, -1));
    // too long
    assertEquals ("c", StringImplode.getImploded (".", aArray, 2, 2));
    // too long
    assertEquals ("c", StringImplode.getImploded (aArray, 2, 2));
    // too long
    assertEquals ("a.b.c", StringImplode.getImploded (".", aArray, 0, 4));
    // too long
    assertEquals ("abc", StringImplode.getImploded (aArray, 0, 4));
  }

  @Test
  public void testImplodeMap ()
  {
    final Map <String, String> aMap = new LinkedHashMap <> ();
    aMap.put ("a", "true");
    aMap.put ("b", "true");
    aMap.put ("c", "false");
    assertEquals ("atruebtruecfalse", StringImplode.getImploded ("", "", aMap));
    assertEquals ("atrue,btrue,cfalse", StringImplode.getImploded (",", "", aMap));
    assertEquals ("a,trueb,truec,false", StringImplode.getImploded ("", ",", aMap));
    assertEquals ("a,true,b,true,c,false", StringImplode.getImploded (",", ",", aMap));
    assertEquals ("a:true,b:true,c:false", StringImplode.getImploded (",", ":", aMap));

    assertEquals ("a:trueb:truec:false", StringImplode.getImploded (null, ":", aMap));
    assertEquals ("atrue,btrue,cfalse", StringImplode.getImploded (",", null, aMap));
  }

  @Test
  public void testGetImplodedNonEmptyIterable ()
  {
    final List <String> aList = Arrays.asList (null, "a", "", "b", null, "c", "");
    assertEquals ("", StringImplode.getImplodedNonEmpty (".", (String []) null));
    assertEquals ("", StringImplode.getImplodedNonEmpty (".", (List <String>) null));
    assertEquals ("a.b.c", StringImplode.getImplodedNonEmpty (".", aList));
    assertEquals ("abc", StringImplode.getImplodedNonEmpty ("", aList));
    assertEquals ("a.b.c", StringImplode.getImplodedNonEmpty (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringImplode.getImplodedNonEmpty ("", aList.toArray (new String [3])));

    StringImplode.getImplodedNonEmpty (null, aList);
  }

  @Test
  public void testGetImplodedNonEmptyArray ()
  {
    final String [] aArray = { null, "a", "", "b", null, "c", "" };
    assertEquals ("a.b", StringImplode.getImplodedNonEmpty (".", aArray, 0, 4));
    assertEquals ("b.c", StringImplode.getImplodedNonEmpty (".", aArray, 2, 4));
    assertEquals ("", StringImplode.getImplodedNonEmpty (".", aArray, 0, 0));
    assertEquals ("", StringImplode.getImplodedNonEmpty (".", aArray, 4, 0));
    assertEquals ("", StringImplode.getImplodedNonEmpty (".", null, 4, 0));

    StringImplode.getImplodedNonEmpty (null, aArray, 2, 2);
    StringImplode.getImplodedNonEmpty (".", aArray, -1, 2);
    StringImplode.getImplodedNonEmpty (".", aArray, 0, -1);
    // too long
    StringImplode.getImplodedNonEmpty (".", aArray, 6, 2);
    // too long
    StringImplode.getImplodedNonEmpty (".", aArray, 0, 8);
    StringImplode.getImplodedNonEmpty (null, aArray);
  }

}
