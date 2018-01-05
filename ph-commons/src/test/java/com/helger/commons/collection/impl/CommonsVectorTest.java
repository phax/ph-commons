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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CommonsVector}.
 *
 * @author Philip Helger
 */
public final class CommonsVectorTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsList <String> aTest = new CommonsVector<> ();
    assertTrue (aTest.isEmpty ());
    aTest.add ("aaa");
    aTest.add ("bbb");
    aTest.add ("ccc");

    CommonsTestHelper.testDefaultSerialization (aTest);
    CommonsTestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsVector <String> aTest = new CommonsVector<> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsVector<> (5);
    assertEquals (0, aTest.size ());

    aTest = new CommonsVector<> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsVector<> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsVector<> (new CommonsVector<> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsVector<> ((Iterable <String>) new CommonsVector<> ("a", "b", "c", "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsVector<> (new CommonsVector<> (Integer.valueOf (1), Integer.valueOf (2)), x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsVector<> ((Iterable <Integer>) new CommonsVector<> (Integer.valueOf (1),
                                                                           Integer.valueOf (2),
                                                                           Integer.valueOf (4)),
                                 x -> x.toString ());
    assertEquals (3, aTest.size ());
  }
}
