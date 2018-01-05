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
package com.helger.commons.location;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SimpleLocation}.
 *
 * @author Philip Helger
 */
public final class SimpleLocationTest
{
  @Test
  public void testBasic ()
  {
    SimpleLocation re = new SimpleLocation ("xx");
    assertEquals ("xx", re.getResourceID ());
    assertEquals (ILocation.ILLEGAL_NUMBER, re.getLineNumber ());
    assertEquals (ILocation.ILLEGAL_NUMBER, re.getColumnNumber ());
    assertEquals ("xx", re.getAsString ());

    re = new SimpleLocation ("xx", 5, ILocation.ILLEGAL_NUMBER);
    assertEquals ("xx", re.getResourceID ());
    assertEquals (5, re.getLineNumber ());
    assertEquals (ILocation.ILLEGAL_NUMBER, re.getColumnNumber ());
    assertEquals ("xx(5:?)", re.getAsString ());

    re = new SimpleLocation ("xx", 5, 7);
    assertEquals ("xx", re.getResourceID ());
    assertEquals (5, re.getLineNumber ());
    assertEquals (7, re.getColumnNumber ());
    assertEquals ("xx(5:7)", re.getAsString ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new SimpleLocation ("xx"),
                                                                       new SimpleLocation ("xx"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SimpleLocation ("xx"),
                                                                           new SimpleLocation ("xx2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SimpleLocation ("xx", 0, 1),
                                                                           new SimpleLocation ("xx", 1, 1));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SimpleLocation ("xx", 0, 1),
                                                                           new SimpleLocation ("xx", 0, 0));
  }
}
