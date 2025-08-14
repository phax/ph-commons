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
package com.helger.collection;

import static com.helger.collection.CollectionFind.containsAnyNullElement;
import static com.helger.collection.CollectionFind.containsOnly;
import static com.helger.collection.CollectionFind.getFirstElement;
import static com.helger.collection.CollectionFind.getLastElement;
import static com.helger.collection.CollectionHelper.removeFirstElement;
import static com.helger.collection.CollectionHelper.removeLastElement;
import static com.helger.collection.helper.CollectionHelperExt.newList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.helger.base.string.Strings;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;

public final class CollectionFindTest
{
  @Test
  public void testContainsNullElement ()
  {
    assertFalse (containsAnyNullElement ((ICommonsList <String>) null));
    assertFalse (containsAnyNullElement (new CommonsArrayList <> ()));
    assertFalse (containsAnyNullElement (newList ("a")));
    assertFalse (containsAnyNullElement (newList ("a", "b", "c")));
    assertTrue (containsAnyNullElement (newList (null, "a")));
    assertTrue (containsAnyNullElement (newList ("a", null)));
    assertTrue (containsAnyNullElement (newList ((String) null)));
    assertTrue (containsAnyNullElement (newList (null, Integer.valueOf (5))));
  }

  @Test
  public void testContainsOnly ()
  {
    assertTrue (containsOnly (new CommonsArrayList <> ("a"), Strings::isNotEmpty));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", "b"), Strings::isNotEmpty));
    assertTrue (containsOnly (new CommonsArrayList <> ("a"), null));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", "b"), null));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", ""), null));

    assertFalse (containsOnly (new CommonsArrayList <> ("a", ""), Strings::isNotEmpty));
    assertFalse (containsOnly (new CommonsArrayList <> ("", ""), Strings::isNotEmpty));
    // Type required here
    assertFalse (containsOnly (new CommonsArrayList <String> (), Strings::isNotEmpty));
    assertFalse (containsOnly (new CommonsArrayList <> (), null));
  }

  @Test
  public void testFirstAndLast ()
  {
    final ICommonsList <String> aList = newList ("s1", "s2", "s3");
    final Set <String> aSet = new CommonsLinkedHashSet <> (aList);

    assertNull (removeFirstElement (new CommonsArrayList <> ()));
    assertNull (removeFirstElement ((ICommonsList <String>) null));

    assertEquals ("s1", getFirstElement (aList));
    assertEquals ("s1", getFirstElement (aSet));
    assertEquals ("s1", getFirstElement ((Iterable <String>) aSet));
    assertEquals ("s1", removeFirstElement (aList));
    assertNull (getFirstElement (new CommonsArrayList <> ()));
    assertNull (getFirstElement (new CommonsHashSet <> ()));
    assertNull (getFirstElement ((Iterable <String>) new CommonsHashSet <String> ()));
    assertNull (getFirstElement ((ICommonsList <String>) null));
    assertNull (getFirstElement ((Set <String>) null));
    assertNull (getFirstElement ((Iterable <String>) null));

    assertNull (removeLastElement (new CommonsArrayList <> ()));
    assertNull (removeLastElement ((ICommonsList <String>) null));

    assertEquals ("s3", getLastElement (aList));
    assertEquals ("s3", getLastElement (aSet));
    assertEquals ("s3", getLastElement ((Iterable <String>) aSet));
    assertEquals ("s3", removeLastElement (aList));
    assertNull (getLastElement (new CommonsArrayList <> ()));
    assertNull (getLastElement (new CommonsHashSet <> ()));
    assertNull (getLastElement ((Iterable <String>) new CommonsHashSet <String> ()));
    assertNull (getLastElement ((ICommonsList <String>) null));
    assertNull (getLastElement ((Set <String>) null));
    assertNull (getLastElement ((Iterable <String>) null));
  }

}
