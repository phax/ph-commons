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
package com.helger.json.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

import com.helger.base.location.ILocation;
import com.helger.base.location.SimpleLocation;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringHelper;
import com.helger.json.IJsonObject;

/**
 * Test class for class {@link JsonMapper}.
 *
 * @author Philip Helger
 */
public final class JsonMapperTest
{
  @Test
  public void testTriState ()
  {
    for (final ETriState e : ETriState.values ())
    {
      final String sJson = JsonMapper.getJsonTriState (e);
      assertTrue (StringHelper.isNotEmpty (sJson));
      assertSame (e, JsonMapper.getAsTriState (sJson));
    }

    // boolean
    assertEquals (JsonMapper.getJsonTriState (ETriState.TRUE), JsonMapper.getJsonTriState (true));
    assertEquals (JsonMapper.getJsonTriState (ETriState.FALSE), JsonMapper.getJsonTriState (false));

    // null
    assertNull (JsonMapper.getJsonTriState (null));
    assertNull (JsonMapper.getAsTriState (null));
  }

  @Test
  public void testLocation ()
  {
    final Consumer <? super ILocation> aGoodTester = x -> {
      final IJsonObject aJson = JsonMapper.getJsonSimpleLocation (x);
      assertNotNull (aJson);

      final ILocation aLoc = JsonMapper.getAsSimpleLocation (aJson);
      assertNotNull (aLoc);

      assertEquals (x, aLoc);
    };
    aGoodTester.accept (new SimpleLocation ("res"));
    aGoodTester.accept (new SimpleLocation ("res", 17, 4));

    assertNull (JsonMapper.getJsonSimpleLocation (null));
    assertNull (JsonMapper.getJsonSimpleLocation (new SimpleLocation (null)));
  }
}
