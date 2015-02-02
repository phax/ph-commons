package com.helger.commons.mock;

import static org.junit.Assert.fail;

import org.junit.Test;

public class SPITest
{
  @Test
  public void testBasic ()
  {
    try
    {
      PHTestUtils.testIfAllSPIImplementationsAreValid ();
      fail ();
    }
    catch (final Exception ex)
    {
      // Since we're testing SPI inside this library, an exception is desired!
    }
  }
}
