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
package com.helger.xml.mock;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.lang.GenericReflection;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroWriter;

/**
 * Helper methods for handling XML based testing
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLTestHelper
{
  private XMLTestHelper ()
  {}

  /**
   * Test if the {@link MicroTypeConverter} is OK. It converts it to XML and
   * back and than uses
   * {@link CommonsTestHelper#testDefaultImplementationWithEqualContentObject(Object, Object)}
   * to check for equality.
   *
   * @param <T>
   *        The data type to be used and returned
   * @param aObj
   *        The object to test
   * @return The object read after conversion
   */
  public static <T> T testMicroTypeConversion (@Nonnull final T aObj)
  {
    assertNotNull (aObj);

    // Write to XML
    final IMicroElement e = MicroTypeConverter.convertToMicroElement (aObj, "test");
    assertNotNull (e);

    // Read from XML
    final Object aObj2 = MicroTypeConverter.convertToNative (e, aObj.getClass ());
    assertNotNull (aObj2);

    // Write to XML again
    final IMicroElement e2 = MicroTypeConverter.convertToMicroElement (aObj2, "test");
    assertNotNull (e2);

    // Ensure XML representation is identical
    final String sXML1 = MicroWriter.getNodeAsString (e);
    final String sXML2 = MicroWriter.getNodeAsString (e2);
    CommonsTestHelper._assertEquals ("XML representation must be identical", sXML1, sXML2);

    // Ensure they are equals
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aObj, aObj2);

    return GenericReflection.uncheckedCast (aObj2);
  }
}
