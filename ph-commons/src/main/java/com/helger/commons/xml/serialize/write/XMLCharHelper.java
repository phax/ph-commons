/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.write;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;

/**
 * This class contains XML utility methods for character handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLCharHelper
{
  private static final BitSet INVALID_NAME_START_CHAR_XML10 = new BitSet (0x10000);
  private static final BitSet INVALID_NAME_START_CHAR_XML11 = new BitSet (0x10000);
  private static final BitSet INVALID_NAME_CHAR_XML10 = new BitSet (0x10000);
  private static final BitSet INVALID_NAME_CHAR_XML11 = new BitSet (0x10000);
  /** This is used for XML 1.0 text, CDATA and attribute value */
  private static final BitSet INVALID_VALUE_CHAR_XML10 = new BitSet (0x10000);
  /** This is used for XML 1.1 text values */
  private static final BitSet INVALID_TEXT_VALUE_CHAR_XML11 = new BitSet (0x10000);
  /** This is used for XML 1.1 CDATA values */
  private static final BitSet INVALID_CDATA_VALUE_CHAR_XML11 = new BitSet (0x10000);
  /** This is used for XML 1.1 CDATA and attribute values */
  private static final BitSet INVALID_ATTR_VALUE_CHAR_XML11 = new BitSet (0x10000);
  /** For all HTML values */
  private static final BitSet INVALID_CHAR_HTML = new BitSet (0x10000);

  static
  {
    for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
    {
      INVALID_NAME_START_CHAR_XML10.set (c,
                                         (c >= 0x0 && c <= 0x40) ||
                                            (c >= 0x5b && c <= 0x5e) ||
                                            (c == 0x60) ||
                                            (c >= 0x7b && c <= 0xbf) ||
                                            (c == 0xd7) ||
                                            (c == 0xf7) ||
                                            (c >= 0x132 && c <= 0x133) ||
                                            (c >= 0x13f && c <= 0x140) ||
                                            (c == 0x149) ||
                                            (c == 0x17f) ||
                                            (c >= 0x1c4 && c <= 0x1cc) ||
                                            (c >= 0x1f1 && c <= 0x1f3) ||
                                            (c >= 0x1f6 && c <= 0x1f9) ||
                                            (c >= 0x218 && c <= 0x24f) ||
                                            (c >= 0x2a9 && c <= 0x2ba) ||
                                            (c >= 0x2c2 && c <= 0x385) ||
                                            (c == 0x387) ||
                                            (c == 0x38b) ||
                                            (c == 0x38d) ||
                                            (c == 0x3a2) ||
                                            (c == 0x3cf) ||
                                            (c >= 0x3d7 && c <= 0x3d9) ||
                                            (c == 0x3db) ||
                                            (c == 0x3dd) ||
                                            (c == 0x3df) ||
                                            (c == 0x3e1) ||
                                            (c >= 0x3f4 && c <= 0x400) ||
                                            (c == 0x40d) ||
                                            (c == 0x450) ||
                                            (c == 0x45d) ||
                                            (c >= 0x482 && c <= 0x48f) ||
                                            (c >= 0x4c5 && c <= 0x4c6) ||
                                            (c >= 0x4c9 && c <= 0x4ca) ||
                                            (c >= 0x4cd && c <= 0x4cf) ||
                                            (c >= 0x4ec && c <= 0x4ed) ||
                                            (c >= 0x4f6 && c <= 0x4f7) ||
                                            (c >= 0x4fa && c <= 0x530) ||
                                            (c >= 0x557 && c <= 0x558) ||
                                            (c >= 0x55a && c <= 0x560) ||
                                            (c >= 0x587 && c <= 0x5cf) ||
                                            (c >= 0x5eb && c <= 0x5ef) ||
                                            (c >= 0x5f3 && c <= 0x620) ||
                                            (c >= 0x63b && c <= 0x640) ||
                                            (c >= 0x64b && c <= 0x670) ||
                                            (c >= 0x6b8 && c <= 0x6b9) ||
                                            (c == 0x6bf) ||
                                            (c == 0x6cf) ||
                                            (c == 0x6d4) ||
                                            (c >= 0x6d6 && c <= 0x6e4) ||
                                            (c >= 0x6e7 && c <= 0x904) ||
                                            (c >= 0x93a && c <= 0x93c) ||
                                            (c >= 0x93e && c <= 0x957) ||
                                            (c >= 0x962 && c <= 0x984) ||
                                            (c >= 0x98d && c <= 0x98e) ||
                                            (c >= 0x991 && c <= 0x992) ||
                                            (c == 0x9a9) ||
                                            (c == 0x9b1) ||
                                            (c >= 0x9b3 && c <= 0x9b5) ||
                                            (c >= 0x9ba && c <= 0x9db) ||
                                            (c == 0x9de) ||
                                            (c >= 0x9e2 && c <= 0x9ef) ||
                                            (c >= 0x9f2 && c <= 0xa04) ||
                                            (c >= 0xa0b && c <= 0xa0e) ||
                                            (c >= 0xa11 && c <= 0xa12) ||
                                            (c == 0xa29) ||
                                            (c == 0xa31) ||
                                            (c == 0xa34) ||
                                            (c == 0xa37) ||
                                            (c >= 0xa3a && c <= 0xa58) ||
                                            (c == 0xa5d) ||
                                            (c >= 0xa5f && c <= 0xa71) ||
                                            (c >= 0xa75 && c <= 0xa84) ||
                                            (c == 0xa8c) ||
                                            (c == 0xa8e) ||
                                            (c == 0xa92) ||
                                            (c == 0xaa9) ||
                                            (c == 0xab1) ||
                                            (c == 0xab4) ||
                                            (c >= 0xaba && c <= 0xabc) ||
                                            (c >= 0xabe && c <= 0xadf) ||
                                            (c >= 0xae1 && c <= 0xb04) ||
                                            (c >= 0xb0d && c <= 0xb0e) ||
                                            (c >= 0xb11 && c <= 0xb12) ||
                                            (c == 0xb29) ||
                                            (c == 0xb31) ||
                                            (c >= 0xb34 && c <= 0xb35) ||
                                            (c >= 0xb3a && c <= 0xb3c) ||
                                            (c >= 0xb3e && c <= 0xb5b) ||
                                            (c == 0xb5e) ||
                                            (c >= 0xb62 && c <= 0xb84) ||
                                            (c >= 0xb8b && c <= 0xb8d) ||
                                            (c == 0xb91) ||
                                            (c >= 0xb96 && c <= 0xb98) ||
                                            (c == 0xb9b) ||
                                            (c == 0xb9d) ||
                                            (c >= 0xba0 && c <= 0xba2) ||
                                            (c >= 0xba5 && c <= 0xba7) ||
                                            (c >= 0xbab && c <= 0xbad) ||
                                            (c == 0xbb6) ||
                                            (c >= 0xbba && c <= 0xc04) ||
                                            (c == 0xc0d) ||
                                            (c == 0xc11) ||
                                            (c == 0xc29) ||
                                            (c == 0xc34) ||
                                            (c >= 0xc3a && c <= 0xc5f) ||
                                            (c >= 0xc62 && c <= 0xc84) ||
                                            (c == 0xc8d) ||
                                            (c == 0xc91) ||
                                            (c == 0xca9) ||
                                            (c == 0xcb4) ||
                                            (c >= 0xcba && c <= 0xcdd) ||
                                            (c == 0xcdf) ||
                                            (c >= 0xce2 && c <= 0xd04) ||
                                            (c == 0xd0d) ||
                                            (c == 0xd11) ||
                                            (c == 0xd29) ||
                                            (c >= 0xd3a && c <= 0xd5f) ||
                                            (c >= 0xd62 && c <= 0xe00) ||
                                            (c == 0xe2f) ||
                                            (c == 0xe31) ||
                                            (c >= 0xe34 && c <= 0xe3f) ||
                                            (c >= 0xe46 && c <= 0xe80) ||
                                            (c == 0xe83) ||
                                            (c >= 0xe85 && c <= 0xe86) ||
                                            (c == 0xe89) ||
                                            (c >= 0xe8b && c <= 0xe8c) ||
                                            (c >= 0xe8e && c <= 0xe93) ||
                                            (c == 0xe98) ||
                                            (c == 0xea0) ||
                                            (c == 0xea4) ||
                                            (c == 0xea6) ||
                                            (c >= 0xea8 && c <= 0xea9) ||
                                            (c == 0xeac) ||
                                            (c == 0xeaf) ||
                                            (c == 0xeb1) ||
                                            (c >= 0xeb4 && c <= 0xebc) ||
                                            (c >= 0xebe && c <= 0xebf) ||
                                            (c >= 0xec5 && c <= 0xf3f) ||
                                            (c == 0xf48) ||
                                            (c >= 0xf6a && c <= 0x109f) ||
                                            (c >= 0x10c6 && c <= 0x10cf) ||
                                            (c >= 0x10f7 && c <= 0x10ff) ||
                                            (c == 0x1101) ||
                                            (c == 0x1104) ||
                                            (c == 0x1108) ||
                                            (c == 0x110a) ||
                                            (c == 0x110d) ||
                                            (c >= 0x1113 && c <= 0x113b) ||
                                            (c == 0x113d) ||
                                            (c == 0x113f) ||
                                            (c >= 0x1141 && c <= 0x114b) ||
                                            (c == 0x114d) ||
                                            (c == 0x114f) ||
                                            (c >= 0x1151 && c <= 0x1153) ||
                                            (c >= 0x1156 && c <= 0x1158) ||
                                            (c >= 0x115a && c <= 0x115e) ||
                                            (c == 0x1162) ||
                                            (c == 0x1164) ||
                                            (c == 0x1166) ||
                                            (c == 0x1168) ||
                                            (c >= 0x116a && c <= 0x116c) ||
                                            (c >= 0x116f && c <= 0x1171) ||
                                            (c == 0x1174) ||
                                            (c >= 0x1176 && c <= 0x119d) ||
                                            (c >= 0x119f && c <= 0x11a7) ||
                                            (c >= 0x11a9 && c <= 0x11aa) ||
                                            (c >= 0x11ac && c <= 0x11ad) ||
                                            (c >= 0x11b0 && c <= 0x11b6) ||
                                            (c == 0x11b9) ||
                                            (c == 0x11bb) ||
                                            (c >= 0x11c3 && c <= 0x11ea) ||
                                            (c >= 0x11ec && c <= 0x11ef) ||
                                            (c >= 0x11f1 && c <= 0x11f8) ||
                                            (c >= 0x11fa && c <= 0x1dff) ||
                                            (c >= 0x1e9c && c <= 0x1e9f) ||
                                            (c >= 0x1efa && c <= 0x1eff) ||
                                            (c >= 0x1f16 && c <= 0x1f17) ||
                                            (c >= 0x1f1e && c <= 0x1f1f) ||
                                            (c >= 0x1f46 && c <= 0x1f47) ||
                                            (c >= 0x1f4e && c <= 0x1f4f) ||
                                            (c == 0x1f58) ||
                                            (c == 0x1f5a) ||
                                            (c == 0x1f5c) ||
                                            (c == 0x1f5e) ||
                                            (c >= 0x1f7e && c <= 0x1f7f) ||
                                            (c == 0x1fb5) ||
                                            (c == 0x1fbd) ||
                                            (c >= 0x1fbf && c <= 0x1fc1) ||
                                            (c == 0x1fc5) ||
                                            (c >= 0x1fcd && c <= 0x1fcf) ||
                                            (c >= 0x1fd4 && c <= 0x1fd5) ||
                                            (c >= 0x1fdc && c <= 0x1fdf) ||
                                            (c >= 0x1fed && c <= 0x1ff1) ||
                                            (c == 0x1ff5) ||
                                            (c >= 0x1ffd && c <= 0x2125) ||
                                            (c >= 0x2127 && c <= 0x2129) ||
                                            (c >= 0x212c && c <= 0x212d) ||
                                            (c >= 0x212f && c <= 0x217f) ||
                                            (c >= 0x2183 && c <= 0x3006) ||
                                            (c >= 0x3008 && c <= 0x3020) ||
                                            (c >= 0x302a && c <= 0x3040) ||
                                            (c >= 0x3095 && c <= 0x30a0) ||
                                            (c >= 0x30fb && c <= 0x3104) ||
                                            (c >= 0x312d && c <= 0x4dff) ||
                                            (c >= 0x9fa6 && c <= 0xabff) ||
                                            (c >= 0xd7a4 && c <= 0xffff));
      INVALID_NAME_START_CHAR_XML11.set (c,
                                         (c >= 0x0 && c <= 0x40) ||
                                            (c >= 0x5b && c <= 0x5e) ||
                                            (c == 0x60) ||
                                            (c >= 0x7b && c <= 0xbf) ||
                                            (c == 0xd7) ||
                                            (c == 0xf7) ||
                                            (c >= 0x300 && c <= 0x36f) ||
                                            (c == 0x37e) ||
                                            (c >= 0x2000 && c <= 0x200b) ||
                                            (c >= 0x200e && c <= 0x206f) ||
                                            (c >= 0x2190 && c <= 0x2bff) ||
                                            (c >= 0x2ff0 && c <= 0x3000) ||
                                            (c >= 0xd800 && c <= 0xf8ff) ||
                                            (c >= 0xfdd0 && c <= 0xfdef) ||
                                            (c >= 0xfffe && c <= 0xffff));
      INVALID_NAME_CHAR_XML10.set (c,
                                   (c >= 0x0 && c <= 0x2c) ||
                                      (c == 0x2f) ||
                                      (c >= 0x3a && c <= 0x40) ||
                                      (c >= 0x5b && c <= 0x5e) ||
                                      (c == 0x60) ||
                                      (c >= 0x7b && c <= 0xb6) ||
                                      (c >= 0xb8 && c <= 0xbf) ||
                                      (c == 0xd7) ||
                                      (c == 0xf7) ||
                                      (c >= 0x132 && c <= 0x133) ||
                                      (c >= 0x13f && c <= 0x140) ||
                                      (c == 0x149) ||
                                      (c == 0x17f) ||
                                      (c >= 0x1c4 && c <= 0x1cc) ||
                                      (c >= 0x1f1 && c <= 0x1f3) ||
                                      (c >= 0x1f6 && c <= 0x1f9) ||
                                      (c >= 0x218 && c <= 0x24f) ||
                                      (c >= 0x2a9 && c <= 0x2ba) ||
                                      (c >= 0x2c2 && c <= 0x2cf) ||
                                      (c >= 0x2d2 && c <= 0x2ff) ||
                                      (c >= 0x346 && c <= 0x35f) ||
                                      (c >= 0x362 && c <= 0x385) ||
                                      (c == 0x38b) ||
                                      (c == 0x38d) ||
                                      (c == 0x3a2) ||
                                      (c == 0x3cf) ||
                                      (c >= 0x3d7 && c <= 0x3d9) ||
                                      (c == 0x3db) ||
                                      (c == 0x3dd) ||
                                      (c == 0x3df) ||
                                      (c == 0x3e1) ||
                                      (c >= 0x3f4 && c <= 0x400) ||
                                      (c == 0x40d) ||
                                      (c == 0x450) ||
                                      (c == 0x45d) ||
                                      (c == 0x482) ||
                                      (c >= 0x487 && c <= 0x48f) ||
                                      (c >= 0x4c5 && c <= 0x4c6) ||
                                      (c >= 0x4c9 && c <= 0x4ca) ||
                                      (c >= 0x4cd && c <= 0x4cf) ||
                                      (c >= 0x4ec && c <= 0x4ed) ||
                                      (c >= 0x4f6 && c <= 0x4f7) ||
                                      (c >= 0x4fa && c <= 0x530) ||
                                      (c >= 0x557 && c <= 0x558) ||
                                      (c >= 0x55a && c <= 0x560) ||
                                      (c >= 0x587 && c <= 0x590) ||
                                      (c == 0x5a2) ||
                                      (c == 0x5ba) ||
                                      (c == 0x5be) ||
                                      (c == 0x5c0) ||
                                      (c == 0x5c3) ||
                                      (c >= 0x5c5 && c <= 0x5cf) ||
                                      (c >= 0x5eb && c <= 0x5ef) ||
                                      (c >= 0x5f3 && c <= 0x620) ||
                                      (c >= 0x63b && c <= 0x63f) ||
                                      (c >= 0x653 && c <= 0x65f) ||
                                      (c >= 0x66a && c <= 0x66f) ||
                                      (c >= 0x6b8 && c <= 0x6b9) ||
                                      (c == 0x6bf) ||
                                      (c == 0x6cf) ||
                                      (c == 0x6d4) ||
                                      (c == 0x6e9) ||
                                      (c >= 0x6ee && c <= 0x6ef) ||
                                      (c >= 0x6fa && c <= 0x900) ||
                                      (c == 0x904) ||
                                      (c >= 0x93a && c <= 0x93b) ||
                                      (c >= 0x94e && c <= 0x950) ||
                                      (c >= 0x955 && c <= 0x957) ||
                                      (c >= 0x964 && c <= 0x965) ||
                                      (c >= 0x970 && c <= 0x980) ||
                                      (c == 0x984) ||
                                      (c >= 0x98d && c <= 0x98e) ||
                                      (c >= 0x991 && c <= 0x992) ||
                                      (c == 0x9a9) ||
                                      (c == 0x9b1) ||
                                      (c >= 0x9b3 && c <= 0x9b5) ||
                                      (c >= 0x9ba && c <= 0x9bb) ||
                                      (c == 0x9bd) ||
                                      (c >= 0x9c5 && c <= 0x9c6) ||
                                      (c >= 0x9c9 && c <= 0x9ca) ||
                                      (c >= 0x9ce && c <= 0x9d6) ||
                                      (c >= 0x9d8 && c <= 0x9db) ||
                                      (c == 0x9de) ||
                                      (c >= 0x9e4 && c <= 0x9e5) ||
                                      (c >= 0x9f2 && c <= 0xa01) ||
                                      (c >= 0xa03 && c <= 0xa04) ||
                                      (c >= 0xa0b && c <= 0xa0e) ||
                                      (c >= 0xa11 && c <= 0xa12) ||
                                      (c == 0xa29) ||
                                      (c == 0xa31) ||
                                      (c == 0xa34) ||
                                      (c == 0xa37) ||
                                      (c >= 0xa3a && c <= 0xa3b) ||
                                      (c == 0xa3d) ||
                                      (c >= 0xa43 && c <= 0xa46) ||
                                      (c >= 0xa49 && c <= 0xa4a) ||
                                      (c >= 0xa4e && c <= 0xa58) ||
                                      (c == 0xa5d) ||
                                      (c >= 0xa5f && c <= 0xa65) ||
                                      (c >= 0xa75 && c <= 0xa80) ||
                                      (c == 0xa84) ||
                                      (c == 0xa8c) ||
                                      (c == 0xa8e) ||
                                      (c == 0xa92) ||
                                      (c == 0xaa9) ||
                                      (c == 0xab1) ||
                                      (c == 0xab4) ||
                                      (c >= 0xaba && c <= 0xabb) ||
                                      (c == 0xac6) ||
                                      (c == 0xaca) ||
                                      (c >= 0xace && c <= 0xadf) ||
                                      (c >= 0xae1 && c <= 0xae5) ||
                                      (c >= 0xaf0 && c <= 0xb00) ||
                                      (c == 0xb04) ||
                                      (c >= 0xb0d && c <= 0xb0e) ||
                                      (c >= 0xb11 && c <= 0xb12) ||
                                      (c == 0xb29) ||
                                      (c == 0xb31) ||
                                      (c >= 0xb34 && c <= 0xb35) ||
                                      (c >= 0xb3a && c <= 0xb3b) ||
                                      (c >= 0xb44 && c <= 0xb46) ||
                                      (c >= 0xb49 && c <= 0xb4a) ||
                                      (c >= 0xb4e && c <= 0xb55) ||
                                      (c >= 0xb58 && c <= 0xb5b) ||
                                      (c == 0xb5e) ||
                                      (c >= 0xb62 && c <= 0xb65) ||
                                      (c >= 0xb70 && c <= 0xb81) ||
                                      (c == 0xb84) ||
                                      (c >= 0xb8b && c <= 0xb8d) ||
                                      (c == 0xb91) ||
                                      (c >= 0xb96 && c <= 0xb98) ||
                                      (c == 0xb9b) ||
                                      (c == 0xb9d) ||
                                      (c >= 0xba0 && c <= 0xba2) ||
                                      (c >= 0xba5 && c <= 0xba7) ||
                                      (c >= 0xbab && c <= 0xbad) ||
                                      (c == 0xbb6) ||
                                      (c >= 0xbba && c <= 0xbbd) ||
                                      (c >= 0xbc3 && c <= 0xbc5) ||
                                      (c == 0xbc9) ||
                                      (c >= 0xbce && c <= 0xbd6) ||
                                      (c >= 0xbd8 && c <= 0xbe6) ||
                                      (c >= 0xbf0 && c <= 0xc00) ||
                                      (c == 0xc04) ||
                                      (c == 0xc0d) ||
                                      (c == 0xc11) ||
                                      (c == 0xc29) ||
                                      (c == 0xc34) ||
                                      (c >= 0xc3a && c <= 0xc3d) ||
                                      (c == 0xc45) ||
                                      (c == 0xc49) ||
                                      (c >= 0xc4e && c <= 0xc54) ||
                                      (c >= 0xc57 && c <= 0xc5f) ||
                                      (c >= 0xc62 && c <= 0xc65) ||
                                      (c >= 0xc70 && c <= 0xc81) ||
                                      (c == 0xc84) ||
                                      (c == 0xc8d) ||
                                      (c == 0xc91) ||
                                      (c == 0xca9) ||
                                      (c == 0xcb4) ||
                                      (c >= 0xcba && c <= 0xcbd) ||
                                      (c == 0xcc5) ||
                                      (c == 0xcc9) ||
                                      (c >= 0xcce && c <= 0xcd4) ||
                                      (c >= 0xcd7 && c <= 0xcdd) ||
                                      (c == 0xcdf) ||
                                      (c >= 0xce2 && c <= 0xce5) ||
                                      (c >= 0xcf0 && c <= 0xd01) ||
                                      (c == 0xd04) ||
                                      (c == 0xd0d) ||
                                      (c == 0xd11) ||
                                      (c == 0xd29) ||
                                      (c >= 0xd3a && c <= 0xd3d) ||
                                      (c >= 0xd44 && c <= 0xd45) ||
                                      (c == 0xd49) ||
                                      (c >= 0xd4e && c <= 0xd56) ||
                                      (c >= 0xd58 && c <= 0xd5f) ||
                                      (c >= 0xd62 && c <= 0xd65) ||
                                      (c >= 0xd70 && c <= 0xe00) ||
                                      (c == 0xe2f) ||
                                      (c >= 0xe3b && c <= 0xe3f) ||
                                      (c == 0xe4f) ||
                                      (c >= 0xe5a && c <= 0xe80) ||
                                      (c == 0xe83) ||
                                      (c >= 0xe85 && c <= 0xe86) ||
                                      (c == 0xe89) ||
                                      (c >= 0xe8b && c <= 0xe8c) ||
                                      (c >= 0xe8e && c <= 0xe93) ||
                                      (c == 0xe98) ||
                                      (c == 0xea0) ||
                                      (c == 0xea4) ||
                                      (c == 0xea6) ||
                                      (c >= 0xea8 && c <= 0xea9) ||
                                      (c == 0xeac) ||
                                      (c == 0xeaf) ||
                                      (c == 0xeba) ||
                                      (c >= 0xebe && c <= 0xebf) ||
                                      (c == 0xec5) ||
                                      (c == 0xec7) ||
                                      (c >= 0xece && c <= 0xecf) ||
                                      (c >= 0xeda && c <= 0xf17) ||
                                      (c >= 0xf1a && c <= 0xf1f) ||
                                      (c >= 0xf2a && c <= 0xf34) ||
                                      (c == 0xf36) ||
                                      (c == 0xf38) ||
                                      (c >= 0xf3a && c <= 0xf3d) ||
                                      (c == 0xf48) ||
                                      (c >= 0xf6a && c <= 0xf70) ||
                                      (c == 0xf85) ||
                                      (c >= 0xf8c && c <= 0xf8f) ||
                                      (c == 0xf96) ||
                                      (c == 0xf98) ||
                                      (c >= 0xfae && c <= 0xfb0) ||
                                      (c == 0xfb8) ||
                                      (c >= 0xfba && c <= 0x109f) ||
                                      (c >= 0x10c6 && c <= 0x10cf) ||
                                      (c >= 0x10f7 && c <= 0x10ff) ||
                                      (c == 0x1101) ||
                                      (c == 0x1104) ||
                                      (c == 0x1108) ||
                                      (c == 0x110a) ||
                                      (c == 0x110d) ||
                                      (c >= 0x1113 && c <= 0x113b) ||
                                      (c == 0x113d) ||
                                      (c == 0x113f) ||
                                      (c >= 0x1141 && c <= 0x114b) ||
                                      (c == 0x114d) ||
                                      (c == 0x114f) ||
                                      (c >= 0x1151 && c <= 0x1153) ||
                                      (c >= 0x1156 && c <= 0x1158) ||
                                      (c >= 0x115a && c <= 0x115e) ||
                                      (c == 0x1162) ||
                                      (c == 0x1164) ||
                                      (c == 0x1166) ||
                                      (c == 0x1168) ||
                                      (c >= 0x116a && c <= 0x116c) ||
                                      (c >= 0x116f && c <= 0x1171) ||
                                      (c == 0x1174) ||
                                      (c >= 0x1176 && c <= 0x119d) ||
                                      (c >= 0x119f && c <= 0x11a7) ||
                                      (c >= 0x11a9 && c <= 0x11aa) ||
                                      (c >= 0x11ac && c <= 0x11ad) ||
                                      (c >= 0x11b0 && c <= 0x11b6) ||
                                      (c == 0x11b9) ||
                                      (c == 0x11bb) ||
                                      (c >= 0x11c3 && c <= 0x11ea) ||
                                      (c >= 0x11ec && c <= 0x11ef) ||
                                      (c >= 0x11f1 && c <= 0x11f8) ||
                                      (c >= 0x11fa && c <= 0x1dff) ||
                                      (c >= 0x1e9c && c <= 0x1e9f) ||
                                      (c >= 0x1efa && c <= 0x1eff) ||
                                      (c >= 0x1f16 && c <= 0x1f17) ||
                                      (c >= 0x1f1e && c <= 0x1f1f) ||
                                      (c >= 0x1f46 && c <= 0x1f47) ||
                                      (c >= 0x1f4e && c <= 0x1f4f) ||
                                      (c == 0x1f58) ||
                                      (c == 0x1f5a) ||
                                      (c == 0x1f5c) ||
                                      (c == 0x1f5e) ||
                                      (c >= 0x1f7e && c <= 0x1f7f) ||
                                      (c == 0x1fb5) ||
                                      (c == 0x1fbd) ||
                                      (c >= 0x1fbf && c <= 0x1fc1) ||
                                      (c == 0x1fc5) ||
                                      (c >= 0x1fcd && c <= 0x1fcf) ||
                                      (c >= 0x1fd4 && c <= 0x1fd5) ||
                                      (c >= 0x1fdc && c <= 0x1fdf) ||
                                      (c >= 0x1fed && c <= 0x1ff1) ||
                                      (c == 0x1ff5) ||
                                      (c >= 0x1ffd && c <= 0x20cf) ||
                                      (c >= 0x20dd && c <= 0x20e0) ||
                                      (c >= 0x20e2 && c <= 0x2125) ||
                                      (c >= 0x2127 && c <= 0x2129) ||
                                      (c >= 0x212c && c <= 0x212d) ||
                                      (c >= 0x212f && c <= 0x217f) ||
                                      (c >= 0x2183 && c <= 0x3004) ||
                                      (c == 0x3006) ||
                                      (c >= 0x3008 && c <= 0x3020) ||
                                      (c == 0x3030) ||
                                      (c >= 0x3036 && c <= 0x3040) ||
                                      (c >= 0x3095 && c <= 0x3098) ||
                                      (c >= 0x309b && c <= 0x309c) ||
                                      (c >= 0x309f && c <= 0x30a0) ||
                                      (c == 0x30fb) ||
                                      (c >= 0x30ff && c <= 0x3104) ||
                                      (c >= 0x312d && c <= 0x4dff) ||
                                      (c >= 0x9fa6 && c <= 0xabff) ||
                                      (c >= 0xd7a4 && c <= 0xffff));
      INVALID_NAME_CHAR_XML11.set (c,
                                   (c >= 0x0 && c <= 0x2c) ||
                                      (c == 0x2f) ||
                                      (c >= 0x3a && c <= 0x40) ||
                                      (c >= 0x5b && c <= 0x5e) ||
                                      (c == 0x60) ||
                                      (c >= 0x7b && c <= 0xb6) ||
                                      (c >= 0xb8 && c <= 0xbf) ||
                                      (c == 0xd7) ||
                                      (c == 0xf7) ||
                                      (c == 0x37e) ||
                                      (c >= 0x2000 && c <= 0x200b) ||
                                      (c >= 0x200e && c <= 0x203e) ||
                                      (c >= 0x2041 && c <= 0x206f) ||
                                      (c >= 0x2190 && c <= 0x2bff) ||
                                      (c >= 0x2ff0 && c <= 0x3000) ||
                                      (c >= 0xd800 && c <= 0xf8ff) ||
                                      (c >= 0xfdd0 && c <= 0xfdef) ||
                                      (c >= 0xfffe && c <= 0xffff));
      INVALID_VALUE_CHAR_XML10.set (c,
                                    (c >= 0x0 && c <= 0x8) ||
                                       (c >= 0xb && c <= 0xc) ||
                                       (c >= 0xe && c <= 0x1f) ||
                                       (c >= 0xd800 && c <= 0xdfff) ||
                                       (c >= 0xfffe && c <= 0xffff));
      INVALID_TEXT_VALUE_CHAR_XML11.set (c, (c == 0x0) || (c >= 0xd800 && c <= 0xdfff) || (c >= 0xfffe && c <= 0xffff));
      INVALID_CDATA_VALUE_CHAR_XML11.set (c,
                                          (c >= 0x0 && c <= 0x8) ||
                                             (c >= 0xb && c <= 0xc) ||
                                             (c >= 0xe && c <= 0x1f) ||
                                             (c >= 0x7f && c <= 0x9f) ||
                                             (c >= 0xd800 && c <= 0xdfff) ||
                                             (c >= 0xfffe && c <= 0xffff));
      INVALID_ATTR_VALUE_CHAR_XML11.set (c,
                                         (c == 0x0) ||
                                            (c >= 0x7f && c <= 0x84) ||
                                            (c >= 0x86 && c <= 0x9f) ||
                                            (c >= 0xd800 && c <= 0xdfff) ||
                                            (c >= 0xfffe && c <= 0xffff));
      /**
       * Source: http://www.w3.org/TR/REC-html40/sgml/sgmldecl.html with sanity
       * handling for 0x80 - 0x9f
       */
      INVALID_CHAR_HTML.set (c,
                             (c >= 0x0 && c <= 0x8) ||
                                (c >= 0xb && c <= 0xc) ||
                                (c >= 0xe && c <= 0x1f) ||
                                (c == 0x7f) ||
                                // (c >= 0x80 && c <= 0x9f) ||
                                (c >= 0xd800 && c <= 0xdfff) ||
                                (c >= 0xfffe && c <= 0xffff));
    }
  }

  @PresentForCodeCoverage
  private static final XMLCharHelper s_aInstance = new XMLCharHelper ();

  private XMLCharHelper ()
  {}

  /**
   * Check if the passed character is invalid for an element or attribute name
   * on the first position
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  public static boolean isInvalidXMLNameStartChar (@Nonnull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return INVALID_NAME_START_CHAR_XML10.get (c);
      case XML_11:
        return INVALID_NAME_START_CHAR_XML11.get (c);
      case HTML:
        return INVALID_CHAR_HTML.get (c);
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
  }

  /**
   * Check if the passed character is invalid for an element or attribute name
   * after the first position
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  public static boolean isInvalidXMLNameChar (@Nonnull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return INVALID_NAME_CHAR_XML10.get (c);
      case XML_11:
        return INVALID_NAME_CHAR_XML11.get (c);
      case HTML:
        return INVALID_CHAR_HTML.get (c);
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
  }

  public static boolean containsInvalidXMLNameChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final String s)
  {
    return s != null && containsInvalidXMLNameChar (eXMLVersion, s.toCharArray ());
  }

  public static boolean containsInvalidXMLNameChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final char [] aChars)
  {
    if (aChars != null)
    {
      int nIndex = 0;
      for (final char c : aChars)
      {
        if (nIndex == 0)
        {
          if (isInvalidXMLNameStartChar (eXMLVersion, c))
            return true;
        }
        else
        {
          if (isInvalidXMLNameChar (eXMLVersion, c))
            return true;
        }
        ++nIndex;
      }
    }
    return false;
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLNameChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                           @Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLNameChars (eXMLVersion, s.toCharArray ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLNameChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                           @Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return null;

    final Set <Character> aRes = new LinkedHashSet <Character> ();
    int nIndex = 0;
    for (final char c : aChars)
    {
      if (nIndex == 0)
      {
        if (isInvalidXMLNameStartChar (eXMLVersion, c))
          aRes.add (Character.valueOf (c));
      }
      else
      {
        if (isInvalidXMLNameChar (eXMLVersion, c))
          aRes.add (Character.valueOf (c));
      }
      ++nIndex;
    }
    return aRes;
  }

  /**
   * Check if the passed character is invalid for a text node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  public static boolean isInvalidXMLTextChar (@Nonnull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return INVALID_VALUE_CHAR_XML10.get (c);
      case XML_11:
        return INVALID_TEXT_VALUE_CHAR_XML11.get (c);
      case HTML:
        return INVALID_CHAR_HTML.get (c);
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
  }

  public static boolean containsInvalidXMLTextChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final String s)
  {
    return s != null && containsInvalidXMLTextChar (eXMLVersion, s.toCharArray ());
  }

  public static boolean containsInvalidXMLTextChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final char [] aChars)
  {
    if (aChars != null)
      for (final char c : aChars)
        if (isInvalidXMLTextChar (eXMLVersion, c))
          return true;
    return false;
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLTextChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                           @Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLTextChars (eXMLVersion, s.toCharArray ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLTextChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                           @Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return null;

    final Set <Character> aRes = new LinkedHashSet <Character> ();
    for (final char c : aChars)
      if (isInvalidXMLTextChar (eXMLVersion, c))
        aRes.add (Character.valueOf (c));
    return aRes;
  }

  /**
   * Check if the passed character is invalid for a CDATA node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  public static boolean isInvalidXMLCDATAChar (@Nonnull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return INVALID_VALUE_CHAR_XML10.get (c);
      case XML_11:
        return INVALID_CDATA_VALUE_CHAR_XML11.get (c);
      case HTML:
        return INVALID_CHAR_HTML.get (c);
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
  }

  public static boolean containsInvalidXMLCDATAChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                     @Nullable final String s)
  {
    return s != null && containsInvalidXMLCDATAChar (eXMLVersion, s.toCharArray ());
  }

  public static boolean containsInvalidXMLCDATAChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                     @Nullable final char [] aChars)
  {
    if (aChars != null)
      for (final char c : aChars)
        if (isInvalidXMLCDATAChar (eXMLVersion, c))
          return true;
    return false;
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLCDATAChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                            @Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLCDATAChars (eXMLVersion, s.toCharArray ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLCDATAChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                            @Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return null;

    final Set <Character> aRes = new LinkedHashSet <Character> ();
    for (final char c : aChars)
      if (isInvalidXMLCDATAChar (eXMLVersion, c))
        aRes.add (Character.valueOf (c));
    return aRes;
  }

  /**
   * Check if the passed character is invalid for a attribute value node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  public static boolean isInvalidXMLAttributeValueChar (@Nonnull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return INVALID_VALUE_CHAR_XML10.get (c);
      case XML_11:
        return INVALID_ATTR_VALUE_CHAR_XML11.get (c);
      case HTML:
        return INVALID_CHAR_HTML.get (c);
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
  }

  public static boolean containsInvalidXMLAttributeValueChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                              @Nullable final String s)
  {
    return s != null && containsInvalidXMLAttributeValueChar (eXMLVersion, s.toCharArray ());
  }

  public static boolean containsInvalidXMLAttributeValueChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                              @Nullable final char [] aChars)
  {
    if (aChars != null)
      for (final char c : aChars)
        if (isInvalidXMLAttributeValueChar (eXMLVersion, c))
          return true;
    return false;
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLAttributeValueChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                                     @Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLAttributeValueChars (eXMLVersion, s.toCharArray ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLAttributeValueChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                                     @Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return null;

    final Set <Character> aRes = new LinkedHashSet <Character> ();
    for (final char c : aChars)
      if (isInvalidXMLAttributeValueChar (eXMLVersion, c))
        aRes.add (Character.valueOf (c));
    return aRes;
  }

  public static boolean containsInvalidXMLChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                @Nonnull final EXMLCharMode eXMLCharMode,
                                                @Nullable final String s)
  {
    return s != null && containsInvalidXMLChar (eXMLVersion, eXMLCharMode, s.toCharArray ());
  }

  public static boolean containsInvalidXMLChar (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                @Nonnull final EXMLCharMode eXMLCharMode,
                                                @Nullable final char [] aChars)
  {
    switch (eXMLCharMode)
    {
      case ELEMENT_NAME:
      case ATTRIBUTE_NAME:
        return containsInvalidXMLNameChar (eXMLVersion, aChars);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
      case ATTRIBUTE_VALUE_SINGLE_QUOTES:
        return containsInvalidXMLAttributeValueChar (eXMLVersion, aChars);
      case TEXT:
        return containsInvalidXMLTextChar (eXMLVersion, aChars);
      case CDATA:
        return containsInvalidXMLCDATAChar (eXMLVersion, aChars);
      default:
        throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    }
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                       @Nonnull final EXMLCharMode eXMLCharMode,
                                                       @Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLChars (eXMLVersion, eXMLCharMode, s.toCharArray ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static Set <Character> getAllInvalidXMLChars (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                       @Nonnull final EXMLCharMode eXMLCharMode,
                                                       @Nullable final char [] aChars)
  {
    switch (eXMLCharMode)
    {
      case ELEMENT_NAME:
      case ATTRIBUTE_NAME:
        return getAllInvalidXMLNameChars (eXMLVersion, aChars);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
      case ATTRIBUTE_VALUE_SINGLE_QUOTES:
        return getAllInvalidXMLAttributeValueChars (eXMLVersion, aChars);
      case TEXT:
        return getAllInvalidXMLTextChars (eXMLVersion, aChars);
      case CDATA:
        return getAllInvalidXMLCDATAChars (eXMLVersion, aChars);
      default:
        throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    }
  }
}
