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
package com.helger.http.header.specific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;
import com.helger.http.header.QValue;

/**
 * Test class for class {@link AcceptLanguageHandler} and
 * {@link AcceptLanguageList}.
 *
 * @author Philip Helger
 */
public final class AcceptLanguageHandlerTest
{
  @Test
  public void testEmpty ()
  {
    // Empty means "everything is supported"
    final AcceptLanguageList aList = AcceptLanguageHandler.getAcceptLanguages ("");
    assertTrue (aList.supportsLanguage ("de"));
    assertTrue (aList.supportsLanguage ("en"));
    assertFalse (aList.explicitlySupportsLanguage ("de"));
    assertTrue (aList.explicitlySupportsLanguage (AcceptLanguageHandler.ANY_LANGUAGE));
    CommonsAssert.assertEquals (QValue.MAX_QUALITY, aList.getQualityOfLanguage ("de"));

    assertEquals (aList.getAsHttpHeaderValue (), AcceptLanguageHandler.getAcceptLanguages (null).getAsHttpHeaderValue ());
  }

  @Test
  public void testSingle ()
  {
    final AcceptLanguageList aList = AcceptLanguageHandler.getAcceptLanguages ("de");
    assertTrue (aList.supportsLanguage ("de"));
    assertTrue (aList.explicitlySupportsLanguage ("de"));
    // Case insensitive
    assertTrue (aList.supportsLanguage ("DE"));
    // Not supported and no "*" present
    assertFalse (aList.supportsLanguage ("en"));
    CommonsAssert.assertEquals (QValue.MIN_QUALITY, aList.getQualityOfLanguage ("en"));
  }

  @Test
  public void testMultiWithQuality ()
  {
    final AcceptLanguageList aList = AcceptLanguageHandler.getAcceptLanguages ("de, en;q=0.8, fr;q=0");
    CommonsAssert.assertEquals (1, aList.getQualityOfLanguage ("de"));
    CommonsAssert.assertEquals (0.8, aList.getQualityOfLanguage ("en"));
    CommonsAssert.assertEquals (0, aList.getQualityOfLanguage ("fr"));
    assertTrue (aList.supportsLanguage ("en"));
    assertFalse (aList.supportsLanguage ("fr"));
    assertFalse (aList.explicitlySupportsLanguage ("fr"));

    assertEquals (3, aList.getAllQValues ().size ());
    assertEquals (1, aList.getAllQValuesLowerThan (0.8).size ());
    assertEquals (2, aList.getAllQValuesLowerOrEqual (0.8).size ());
    assertEquals (1, aList.getAllQValuesGreaterThan (0.8).size ());
    assertEquals (2, aList.getAllQValuesGreaterOrEqual (0.8).size ());

    assertNotNull (aList.getAsHttpHeaderValue ());
    assertNotNull (aList.toString ());

    assertEquals (aList, AcceptLanguageHandler.getAcceptLanguages ("de, en;q=0.8, fr;q=0"));
    assertEquals (aList.hashCode (), AcceptLanguageHandler.getAcceptLanguages ("de, en;q=0.8, fr;q=0").hashCode ());
    assertNotEquals (aList, null);
    assertNotEquals (aList, "any other type");
    assertNotEquals (aList, AcceptLanguageHandler.getAcceptLanguages ("de"));
  }

  @Test
  public void testInvalidQuality ()
  {
    // An unparsable quality falls back to the maximum
    final AcceptLanguageList aList = AcceptLanguageHandler.getAcceptLanguages ("de;q=bla");
    CommonsAssert.assertEquals (QValue.MAX_QUALITY, aList.getQualityOfLanguage ("de"));

    // Something that is not a quality at all
    final AcceptLanguageList aList2 = AcceptLanguageHandler.getAcceptLanguages ("de;whatsoever");
    CommonsAssert.assertEquals (QValue.MAX_QUALITY, aList2.getQualityOfLanguage ("de"));
  }
}
