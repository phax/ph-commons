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
package com.helger.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.codec.URLParameterEncoder;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;
import com.helger.url.protocol.EURLProtocol;
import com.helger.url.protocol.URLProtocolRegistry;

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
    URLData aData = SimpleURLHelper.getAsURLData ("http://www.helger.com/folder?x=y&a=b#c", null);
    assertNotNull (aData);
    assertEquals (EURLProtocol.HTTP, URLProtocolRegistry.getInstance ().getProtocol (aData));
    assertEquals ("http://www.helger.com/folder", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().findFirst (x -> x.hasName ("x")).getValue ());
    assertEquals ("b", aData.params ().findFirst (x -> x.hasName ("a")).getValue ());
    assertEquals ("c", aData.getAnchor ());

    aData = SimpleURLHelper.getAsURLData ("?x=y&a=b#c", null);
    assertNotNull (aData);
    assertNull (URLProtocolRegistry.getInstance ().getProtocol (aData));
    assertEquals ("", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().findFirst (x -> x.hasName ("x")).getValue ());
    assertEquals ("b", aData.params ().findFirst (x -> x.hasName ("a")).getValue ());
    assertEquals ("c", aData.getAnchor ());

    aData = SimpleURLHelper.getAsURLData ("?x=y&=b#c", null);
    assertNotNull (aData);
    assertNull (URLProtocolRegistry.getInstance ().getProtocol (aData));
    assertEquals ("", aData.getPath ());
    assertEquals (1, aData.params ().size ());
    assertEquals ("y", aData.params ().findFirst (x -> x.hasName ("x")).getValue ());
    assertEquals ("c", aData.getAnchor ());
  }

  @Test
  public void testGetApplicationFormEncoded ()
  {
    final URLParameterEncoder enc = new URLParameterEncoder (StandardCharsets.UTF_8);
    assertNull (SimpleURLHelper.getQueryParametersAsString ((ICommonsList <URLParameter>) null, enc));
    assertNull (SimpleURLHelper.getQueryParametersAsString (new CommonsArrayList <> (), enc));
    assertEquals ("a=b", SimpleURLHelper.getQueryParametersAsString (Arrays.asList (new URLParameter ("a", "b")), enc));
    assertEquals ("a=b&c=d",
                  SimpleURLHelper.getQueryParametersAsString (Arrays.asList (new URLParameter ("a", "b"),
                                                                             new URLParameter ("c", "d")), enc));
    assertEquals ("a=b&c=d&e=f+g",
                  SimpleURLHelper.getQueryParametersAsString (Arrays.asList (new URLParameter ("a", "b"),
                                                                             new URLParameter ("c", "d"),
                                                                             new URLParameter ("e", "f g")), enc));
    assertEquals ("a=b&c=d%26e",
                  SimpleURLHelper.getQueryParametersAsString (Arrays.asList (new URLParameter ("a", "b"),
                                                                             new URLParameter ("c", "d&e")), enc));

    // Using identity encoder
    assertEquals ("a=b&c=d&e",
                  SimpleURLHelper.getQueryParametersAsString (Arrays.asList (new URLParameter ("a", "b"),
                                                                             new URLParameter ("c", "d&e")), null));
  }

}
