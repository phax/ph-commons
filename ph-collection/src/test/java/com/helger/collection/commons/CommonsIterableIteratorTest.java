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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionTestHelper;
import com.helger.collection.helper.CollectionHelperExt;

/**
 * Test class for class {@link CommonsIterableIterator}.
 *
 * @author Philip Helger
 */
public final class CommonsIterableIteratorTest
{
  @Test
  public void testBasic ()
  {
    assertSame (CommonsIterableIterator.createEmpty (), CommonsIterableIterator.createEmpty ());
    ICommonsIterableIterator <String> iit = new CommonsIterableIterator <> (ArrayHelper.newArray ("Hallo",
                                                                                                  "Welt",
                                                                                                  "from",
                                                                                                  "Copenhagen"));
    assertNotNull (iit);
    assertNotNull (iit.iterator ());
    assertTrue (iit.hasNext ());
    assertEquals ("Hallo", iit.next ());

    iit = new CommonsIterableIterator <> (CollectionHelperExt.newList ("Hallo", "Welt", "from", "Copenhagen"));
    iit.next ();
    iit.remove ();

    assertEquals (3,
                  CollectionHelperExt.newList (new CommonsIterableIterator <> (new String [] { "a", "b", "c" }))
                                     .size ());
    assertEquals (3,
                  CollectionHelperExt.newList (new CommonsIterableIterator <> (CollectionHelperExt.newList ("a",
                                                                                                            "b",
                                                                                                            "c")))
                                     .size ());
    assertEquals (3,
                  CollectionHelperExt.newList (new CommonsIterableIterator <> (CollectionHelperExt.newList ("a",
                                                                                                            "b",
                                                                                                            "c")
                                                                                                  .iterator ()))
                                     .size ());
    CollectionTestHelper.testToStringImplementation (iit);

    try
    {
      new CommonsIterableIterator <> ((Iterator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
