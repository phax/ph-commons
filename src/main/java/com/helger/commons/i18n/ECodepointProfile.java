/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.i18n;

import javax.annotation.Nonnull;

/**
 * @author Apache Abdera
 */
public enum ECodepointProfile
{
  NONE (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return true;
    }
  }),
  ALPHA (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isAlpha (codepoint);
    }
  }),
  ALPHANUM (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isAlphaDigit (codepoint);
    }
  }),
  FRAGMENT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isFragment (codepoint);
    }
  }),
  IFRAGMENT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_ifragment (codepoint);
    }
  }),
  PATH (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isPath (codepoint);
    }
  }),
  IPATH (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_ipath (codepoint);
    }
  }),
  IUSERINFO (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iuserinfo (codepoint);
    }
  }),
  USERINFO (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isUserInfo (codepoint);
    }
  }),
  QUERY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isQuery (codepoint);
    }
  }),
  IQUERY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iquery (codepoint);
    }
  }),
  SCHEME (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isScheme (codepoint);
    }
  }),
  PATHNODELIMS (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isPathNoDelims (codepoint);
    }
  }),
  IPATHNODELIMS (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_ipathnodelims (codepoint);
    }
  }),
  IPATHNODELIMS_SEG (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_ipathnodelims (codepoint) && codepoint != '@' && codepoint != ':';
    }
  }),
  IREGNAME (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iregname (codepoint);
    }
  }),
  IHOST (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_ihost (codepoint);
    }
  }),
  IPRIVATE (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iprivate (codepoint);
    }
  }),
  RESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isReserved (codepoint);
    }
  }),
  IUNRESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iunreserved (codepoint);
    }
  }),
  UNRESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isUnreserved (codepoint);
    }
  }),
  SCHEMESPECIFICPART (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_iunreserved (codepoint) &&
             !CodepointUtils.isReserved (codepoint) &&
             !CodepointUtils.is_iprivate (codepoint) &&
             !CodepointUtils.isPctEnc (codepoint) &&
             codepoint != '#';
    }
  }),
  AUTHORITY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.is_regname (codepoint) &&
             !CodepointUtils.isUserInfo (codepoint) &&
             !CodepointUtils.isGenDelim (codepoint);
    }
  }),
  ASCIISANSCRLF (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.inRange (codepoint, 1, 9) && !CodepointUtils.inRange (codepoint, 14, 127);
    }
  }),
  PCT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.isPctEnc (codepoint);
    }
  }),
  STD3ASCIIRULES (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointUtils.inRange (codepoint, 0x0000, 0x002C) &&
             !CodepointUtils.inRange (codepoint, 0x002E, 0x002F) &&
             !CodepointUtils.inRange (codepoint, 0x003A, 0x0040) &&
             !CodepointUtils.inRange (codepoint, 0x005B, 0x005E) &&
             !CodepointUtils.inRange (codepoint, 0x0060, 0x0060) &&
             !CodepointUtils.inRange (codepoint, 0x007B, 0x007F);
    }
  });

  private final ICodepointFilter m_aFilter;

  private ECodepointProfile (@Nonnull final ICodepointFilter aFilter)
  {
    m_aFilter = aFilter;
  }

  @Nonnull
  public ICodepointFilter getFilter ()
  {
    return m_aFilter;
  }

  public boolean check (final int codepoint)
  {
    return m_aFilter.accept (codepoint);
  }
}
