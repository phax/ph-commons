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
package com.helger.commons.error.location;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ErrorLocation}.
 *
 * @author Philip Helger
 */
public final class ErrorLocationTest
{
  @Test
  public void testBasic ()
  {
    ErrorLocation re = new ErrorLocation ("xx", -1, -1, "field");
    assertEquals ("xx", re.getResourceID ());
    assertEquals (IErrorLocation.ILLEGAL_NUMBER, re.getLineNumber ());
    assertEquals (IErrorLocation.ILLEGAL_NUMBER, re.getColumnNumber ());
    assertEquals ("field", re.getField ());
    assertEquals ("xx @ field", re.getAsString ());

    re = new ErrorLocation ("xx", 5, IErrorLocation.ILLEGAL_NUMBER, "field");
    assertEquals ("xx", re.getResourceID ());
    assertEquals (5, re.getLineNumber ());
    assertEquals (IErrorLocation.ILLEGAL_NUMBER, re.getColumnNumber ());
    assertEquals ("field", re.getField ());
    assertEquals ("xx(5:?) @ field", re.getAsString ());

    re = new ErrorLocation ("xx", 5, 7, "field");
    assertEquals ("xx", re.getResourceID ());
    assertEquals (5, re.getLineNumber ());
    assertEquals (7, re.getColumnNumber ());
    assertEquals ("field", re.getField ());
    assertEquals ("xx(5:7) @ field", re.getAsString ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ErrorLocation ("xx", -1, -1, "field"),
                                                                       new ErrorLocation ("xx", -1, -1, "field"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ErrorLocation ("xx", -1, -1, "field"),
                                                                           new ErrorLocation ("xx2", -1, -1, "field"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ErrorLocation ("xx", -1, -1, "field"),
                                                                           new ErrorLocation ("xx", -1, -1, "field2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ErrorLocation ("xx", 0, 1, "field"),
                                                                           new ErrorLocation ("xx", 0, 0, "field"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ErrorLocation ("xx", 0, 1, "field"),
                                                                           new ErrorLocation ("xx", 1, 1, "field"));
  }
}
