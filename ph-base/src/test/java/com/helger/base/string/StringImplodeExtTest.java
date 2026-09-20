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
package com.helger.base.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

/**
 * Additional test class for class {@link StringImplode}, covering the builder API and the map based
 * variants.
 *
 * @author Philip Helger
 */
public final class StringImplodeExtTest
{
  private static final List <String> ELEMENTS = Arrays.asList ("a", "b", "c");

  private static Map <String, String> _map ()
  {
    final Map <String, String> ret = new LinkedHashMap <> ();
    ret.put ("k1", "v1");
    ret.put ("k2", "v2");
    return ret;
  }

  @Test
  public void testImploderBuilderBasics ()
  {
    assertEquals ("abc", StringImplode.imploder ().source (ELEMENTS).build ());
    assertEquals ("a,b,c", StringImplode.imploder ().source (ELEMENTS).separator (',').build ());
    assertEquals ("a::b::c", StringImplode.imploder ().source (ELEMENTS).separator ("::").build ());
    assertEquals ("abc", StringImplode.imploder ().source ("a", "b", "c").build ());

    // A null source gives an empty result
    assertEquals ("", StringImplode.imploder ().source ((List <String>) null).build ());
    assertEquals ("", StringImplode.imploder ().source ((String []) null).build ());
  }

  @Test
  public void testImploderBuilderOffsetAndLength ()
  {
    assertEquals ("b,c", StringImplode.imploder ().source (ELEMENTS).separator (',').offset (1).build ());
    assertEquals ("a,b",
                  StringImplode.imploder ().source (ELEMENTS).separator (',').offset (0).length (2).build ());
  }

  @Test
  public void testImploderBuilderFilter ()
  {
    final List <String> aWithEmpty = Arrays.asList ("a", "", "c");
    assertEquals ("a,c",
                  StringImplode.imploder ().source (aWithEmpty).separator (',').filterNonEmpty ().build ());
    assertEquals ("a,c",
                  StringImplode.imploder ()
                               .source (ELEMENTS)
                               .separator (',')
                               .filter (x -> !"b".equals (x))
                               .build ());
    // A null filter keeps everything
    assertEquals ("a,b,c", StringImplode.imploder ().source (ELEMENTS).separator (',').filter (null).build ());
  }

  @Test
  public void testGetImplodedCollection ()
  {
    assertEquals ("abc", StringImplode.getImploded (ELEMENTS));
    assertEquals ("a,b,c", StringImplode.getImploded (',', ELEMENTS));
    assertEquals ("a::b::c", StringImplode.getImploded ("::", ELEMENTS));
    assertEquals ("", StringImplode.getImploded ((List <String>) null));

    assertEquals ("A,B,C",
                  StringImplode.getImplodedMapped (',', ELEMENTS, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("A::B::C",
                  StringImplode.getImplodedMapped ("::", ELEMENTS, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("ABC", StringImplode.getImplodedMapped (ELEMENTS, x -> x.toUpperCase (Locale.ROOT)));
  }

  @Test
  public void testGetImplodedArray ()
  {
    final String [] aArr = { "a", "b", "c" };
    assertEquals ("abc", StringImplode.getImploded (aArr));
    assertEquals ("a,b,c", StringImplode.getImploded (',', aArr));
    assertEquals ("a::b::c", StringImplode.getImploded ("::", aArr));
    assertEquals ("b,c", StringImplode.getImploded (',', aArr, 1, 2));
    assertEquals ("bc", StringImplode.getImploded (aArr, 1, 2));
    assertEquals ("b::c", StringImplode.getImploded ("::", aArr, 1, 2));

    assertEquals ("A,B,C", StringImplode.getImplodedMapped (',', aArr, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("A::B::C", StringImplode.getImplodedMapped ("::", aArr, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("B,C", StringImplode.getImplodedMapped (',', aArr, 1, 2, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("B::C", StringImplode.getImplodedMapped ("::", aArr, 1, 2, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("ABC", StringImplode.getImplodedMapped (aArr, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("BC", StringImplode.getImplodedMapped (aArr, 1, 2, x -> x.toUpperCase (Locale.ROOT)));
  }

  @Test
  public void testGetImplodedNonEmpty ()
  {
    final List <String> aWithEmpty = Arrays.asList ("a", "", "c");
    assertEquals ("ac", StringImplode.getImplodedNonEmpty (aWithEmpty));
    assertEquals ("a,c", StringImplode.getImplodedNonEmpty (',', aWithEmpty));
    assertEquals ("a::c", StringImplode.getImplodedNonEmpty ("::", aWithEmpty));

    final String [] aArr = { "a", "", "c" };
    assertEquals ("a,c", StringImplode.getImplodedNonEmpty (',', aArr));
    assertEquals ("a::c", StringImplode.getImplodedNonEmpty ("::", aArr));

    assertEquals ("A,C",
                  StringImplode.getImplodedMappedNonEmpty (',', aWithEmpty, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("A::C",
                  StringImplode.getImplodedMappedNonEmpty ("::", aWithEmpty, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("AC", StringImplode.getImplodedMappedNonEmpty (aWithEmpty, x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("AC", StringImplode.getImplodedMappedNonEmpty (aArr, x -> x.toUpperCase (Locale.ROOT)));
  }

  @Test
  public void testImploderMapBuilder ()
  {
    assertEquals ("k1=v1&k2=v2",
                  StringImplode.imploderMap ()
                               .source (_map ())
                               .separatorOuter ('&')
                               .separatorInner ('=')
                               .build ());
    assertEquals ("k1=v1::k2=v2",
                  StringImplode.imploderMap ()
                               .source (_map ())
                               .separatorOuter ("::")
                               .separatorInner ("=")
                               .build ());

    // A null source gives an empty result
    assertEquals ("", StringImplode.imploderMap ().source ((Map <?, ?>) null).build ());

    // The mapped source
    assertEquals ("K1=V1&K2=V2",
                  StringImplode.imploderMap ()
                               .source (_map (),
                                        x -> x.toUpperCase (Locale.ROOT),
                                        x -> x.toUpperCase (Locale.ROOT))
                               .separatorOuter ('&')
                               .separatorInner ('=')
                               .build ());
  }

  @Test
  public void testImploderMapFilters ()
  {
    final Map <String, String> aMap = new LinkedHashMap <> ();
    aMap.put ("k1", "v1");
    aMap.put ("", "v2");
    aMap.put ("k3", "");

    assertEquals ("k1=v1&k3=",
                  StringImplode.imploderMap ()
                               .source (aMap)
                               .separatorOuter ('&')
                               .separatorInner ('=')
                               .filterKeyNonEmpty ()
                               .build ());
    assertEquals ("k1=v1&=v2",
                  StringImplode.imploderMap ()
                               .source (aMap)
                               .separatorOuter ('&')
                               .separatorInner ('=')
                               .filterValueNonEmpty ()
                               .build ());
    assertNotNull (StringImplode.imploderMap ()
                                .source (aMap)
                                .separatorOuter ('&')
                                .separatorInner ('=')
                                .filterKey (x -> true)
                                .filterValue (x -> true)
                                .build ());
  }

  @Test
  public void testGetImplodedMap ()
  {
    assertEquals ("k1=v1&k2=v2", StringImplode.getImploded ('&', '=', _map ()));
    assertEquals ("k1=v1::k2=v2", StringImplode.getImploded ("::", "=", _map ()));

    assertEquals ("K1=V1&K2=V2",
                  StringImplode.getImplodedMapped ('&',
                                                    '=',
                                                    _map (),
                                                    x -> x.toUpperCase (Locale.ROOT),
                                                    x -> x.toUpperCase (Locale.ROOT)));
    assertEquals ("K1=V1::K2=V2",
                  StringImplode.getImplodedMapped ("::",
                                                    "=",
                                                    _map (),
                                                    x -> x.toUpperCase (Locale.ROOT),
                                                    x -> x.toUpperCase (Locale.ROOT)));
  }
}
