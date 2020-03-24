package com.helger.config.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.config.source.appl.ConfigurationSourceFunction;

/**
 * Test class for class {@link MultiConfigurationSourceValueProvider}.
 *
 * @author Philip Helger
 */
public class MultiConfigurationSourceValueProviderTest
{
  @Test
  public void testBasic ()
  {
    final MultiConfigurationSourceValueProvider aMCSVP = new MultiConfigurationSourceValueProvider ();

    // Lower priority
    final ICommonsMap <String, String> aMap1 = new CommonsHashMap <> ();
    aMap1.put ("key1", "value1");
    aMap1.put ("key2", "value2");
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (110, aMap1::get));

    // Higher priority - should be returned
    final ICommonsMap <String, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put ("key1", "value2");
    aMap2.put ("key3", "value3");
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (111, aMap2::get));

    // Resolve
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key1"));
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key2"));
    assertEquals ("value3", aMCSVP.getConfigurationValue ("key3"));
    assertNull (aMCSVP.getConfigurationValue ("key4"));
  }
}
