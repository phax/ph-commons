package com.helger.commons.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.url.EURLProtocol;

/**
 * Test class for class {@link SimpleURLHelper}.
 *
 * @author Philip Helger
 */
public final class SimpleURLHelperTest
{
  @Test
  public void testGetURLData ()
  {
    ISimpleURL aData = SimpleURLHelper.getAsURLData ("http://www.helger.com/folder?x=y&a=b#c");
    assertNotNull (aData);
    assertEquals (EURLProtocol.HTTP, aData.getProtocol ());
    assertEquals ("http://www.helger.com/folder", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("b", aData.params ().getFirstParamValue ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = SimpleURLHelper.getAsURLData ("?x=y&a=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("b", aData.params ().getFirstParamValue ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = SimpleURLHelper.getAsURLData ("?x=y&=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (1, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("c", aData.getAnchor ());
  }

  @Test
  public void testGetApplicationFormEncoded ()
  {
    final URLParameterEncoder enc = new URLParameterEncoder (StandardCharsets.UTF_8);
    assertNull (SimpleURLHelper.getQueryParametersAsString ((URLParameterList) null, enc));
    assertNull (SimpleURLHelper.getQueryParametersAsString (new URLParameterList (), enc));
    assertEquals ("a=b", SimpleURLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b"), enc));
    assertEquals ("a=b&c=d",
                  SimpleURLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d"),
                                                              enc));
    assertEquals ("a=b&c=d&e=f+g",
                  SimpleURLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b")
                                                                                     .add ("c", "d")
                                                                                     .add ("e", "f g"), enc));
    assertEquals ("a=b&c=d%26e",
                  SimpleURLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d&e"),
                                                              enc));

    // Using identity encoder
    assertEquals ("a=b&c=d&e",
                  SimpleURLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d&e"),
                                                              null));
  }

}
