package com.helger.xml.mock;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import com.helger.commons.lang.GenericReflection;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroWriter;

public class XMLTestHelper
{

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
    final String sXML1 = MicroWriter.getXMLString (e);
    final String sXML2 = MicroWriter.getXMLString (e2);
    CommonsTestHelper._assertEquals ("XML representation must be identical", sXML1, sXML2);

    // Ensure they are equals
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aObj, aObj2);

    return GenericReflection.uncheckedCast (aObj2);
  }
}
