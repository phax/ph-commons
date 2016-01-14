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
package com.helger.commons.microdom.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.AbstractReadOnlyMapBasedMultilingualText;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.MultilingualText;
import com.helger.commons.text.ReadOnlyMultilingualText;
import com.helger.commons.text.util.TextHelper;

/**
 * Test class for class {@link MultilingualTextMicroTypeConverterRegistrar}.
 *
 * @author Philip Helger
 */
public final class MultilingualTextMicroTypeConverterRegistrarTest
{
  @Test
  public void testMultiLingualText ()
  {
    final MultilingualText aMLT = new MultilingualText ();
    aMLT.setText (Locale.GERMAN, "Cumberlandstraße");
    aMLT.setText (Locale.CHINA, "Whatsoever");

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);
    assertNull (MicroTypeConverter.convertToMicroElement (null, "mtext"));

    final MultilingualText aMLT2 = MicroTypeConverter.convertToNative (aElement, MultilingualText.class);
    assertEquals (aMLT, aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, MultilingualText.class));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aMLT, aMLT2);
  }

  @Test
  public void testReadonlyMultiLingualText ()
  {
    final ReadOnlyMultilingualText aMLT = new ReadOnlyMultilingualText (CollectionHelper.newMap (new Locale [] { Locale.GERMAN,
                                                                                                                 Locale.CHINA },
                                                                                                 new String [] { "Cumberlandstraße",
                                                                                                                 "Whatspever" }));

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);

    final ReadOnlyMultilingualText aMLT2 = MicroTypeConverter.convertToNative (aElement,
                                                                               ReadOnlyMultilingualText.class);
    assertEquals (aMLT, aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, ReadOnlyMultilingualText.class));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aMLT, aMLT2);
  }

  @Test
  public void testTextProvider ()
  {
    final IMultilingualText aMLT = TextHelper.create_DE_EN ("de", "en");

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);

    // The result must be a ReadonlyMultiLingualText because it is the first
    // registered converter
    final IMultilingualText aMLT2 = MicroTypeConverter.convertToNative (aElement, IMultilingualText.class);
    assertTrue (aMLT2 instanceof ReadOnlyMultilingualText);
    assertEquals (new ReadOnlyMultilingualText (aMLT), aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, AbstractReadOnlyMapBasedMultilingualText.class));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyMultilingualText (aMLT), aMLT2);
  }
}
