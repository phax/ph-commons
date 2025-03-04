package com.helger.commons.io.resource.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link ReadableResourceString}.
 *
 * @author Philip Helger
 */
public final class ReadableResourceStringTest
{
  @Test
  public void testBasic ()
  {
    final ReadableResourceString aRes = new ReadableResourceString ("Test", StandardCharsets.UTF_8);
    assertNotNull (aRes.getResourceID ());
    assertTrue (aRes.getResourceID ().contains ("string-"));
    assertEquals ("", aRes.getPath ());
    assertEquals (4, aRes.getAllBytes ().length);

    aRes.setPath ("Mock");
    assertEquals ("Mock", aRes.getPath ());
  }
}
