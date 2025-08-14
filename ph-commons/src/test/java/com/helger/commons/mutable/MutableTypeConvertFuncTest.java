package com.helger.commons.mutable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableBoolean;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.typeconvert.TypeConverter;

public class MutableTypeConvertFuncTest
{

  @Test
  public void testTypeConversionBoolean ()
  {
    final MutableBoolean x = new MutableBoolean (true);
    final Boolean b = TypeConverter.convert (x, Boolean.class);
    assertNotNull (b);
    assertTrue (b.booleanValue ());
    assertTrue (TypeConverter.convertToBoolean (x));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (x,
                                                                       TypeConverter.convert (true,
                                                                                              MutableBoolean.class));
  }

}
