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
package com.helger.lesscommons.i18n;

import java.util.function.IntPredicate;

import javax.annotation.Nonnull;

/**
 * @author Apache Abdera
 */
public enum ECodepointProfile
{
  NONE (codepoint -> true),
  ALPHA (codepoint -> !CodepointHelper.isAlpha (codepoint)),
  ALPHANUM (codepoint -> !CodepointHelper.isAlphaDigit (codepoint)),
  FRAGMENT (codepoint -> !CodepointHelper.isFragment (codepoint)),
  IFRAGMENT (codepoint -> !CodepointHelper.is_ifragment (codepoint)),
  PATH (codepoint -> !CodepointHelper.isPath (codepoint)),
  IPATH (codepoint -> !CodepointHelper.is_ipath (codepoint)),
  IUSERINFO (codepoint -> !CodepointHelper.is_iuserinfo (codepoint)),
  USERINFO (codepoint -> !CodepointHelper.isUserInfo (codepoint)),
  QUERY (codepoint -> !CodepointHelper.isQuery (codepoint)),
  IQUERY (codepoint -> !CodepointHelper.is_iquery (codepoint)),
  SCHEME (codepoint -> !CodepointHelper.isScheme (codepoint)),
  PATHNODELIMS (codepoint -> !CodepointHelper.isPathNoDelims (codepoint)),
  IPATHNODELIMS (codepoint -> !CodepointHelper.is_ipathnodelims (codepoint)),
  IPATHNODELIMS_SEG (codepoint -> !CodepointHelper.is_ipathnodelims (codepoint) &&
                                  codepoint != '@' &&
                                  codepoint != ':'),
  IREGNAME (codepoint -> !CodepointHelper.is_iregname (codepoint)),
  IHOST (codepoint -> !CodepointHelper.is_ihost (codepoint)),
  IPRIVATE (codepoint -> !CodepointHelper.is_iprivate (codepoint)),
  RESERVED (codepoint -> !CodepointHelper.isReserved (codepoint)),
  IUNRESERVED (codepoint -> !CodepointHelper.is_iunreserved (codepoint)),
  UNRESERVED (codepoint -> !CodepointHelper.isUnreserved (codepoint)),
  SCHEMESPECIFICPART (codepoint -> !CodepointHelper.is_iunreserved (codepoint) &&
                                   !CodepointHelper.isReserved (codepoint) &&
                                   !CodepointHelper.is_iprivate (codepoint) &&
                                   !CodepointHelper.isPctEnc (codepoint) &&
                                   codepoint != '#'),
  AUTHORITY (codepoint -> !CodepointHelper.is_regname (codepoint) &&
                          !CodepointHelper.isUserInfo (codepoint) &&
                          !CodepointHelper.isGenDelim (codepoint)),
  ASCIISANSCRLF (codepoint -> !CodepointHelper.inRange (codepoint, 1, 9) &&
                              !CodepointHelper.inRange (codepoint, 14, 127)),
  PCT (codepoint -> !CodepointHelper.isPctEnc (codepoint)),
  STD3ASCIIRULES (codepoint -> !CodepointHelper.inRange (codepoint, 0x0000, 0x002C) &&
                               !CodepointHelper.inRange (codepoint, 0x002E, 0x002F) &&
                               !CodepointHelper.inRange (codepoint, 0x003A, 0x0040) &&
                               !CodepointHelper.inRange (codepoint, 0x005B, 0x005E) &&
                               !CodepointHelper.inRange (codepoint, 0x0060, 0x0060) &&
                               !CodepointHelper.inRange (codepoint, 0x007B, 0x007F));

  private final IntPredicate m_aFilter;

  private ECodepointProfile (@Nonnull final IntPredicate aFilter)
  {
    m_aFilter = aFilter;
  }

  @Nonnull
  public IntPredicate getFilter ()
  {
    return m_aFilter;
  }

  public boolean check (final int codepoint)
  {
    return m_aFilter.test (codepoint);
  }
}
