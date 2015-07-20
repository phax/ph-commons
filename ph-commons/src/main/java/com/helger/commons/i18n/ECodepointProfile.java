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
      return !CodepointHelper.isAlpha (codepoint);
    }
  }),
  ALPHANUM (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isAlphaDigit (codepoint);
    }
  }),
  FRAGMENT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isFragment (codepoint);
    }
  }),
  IFRAGMENT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_ifragment (codepoint);
    }
  }),
  PATH (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isPath (codepoint);
    }
  }),
  IPATH (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_ipath (codepoint);
    }
  }),
  IUSERINFO (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iuserinfo (codepoint);
    }
  }),
  USERINFO (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isUserInfo (codepoint);
    }
  }),
  QUERY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isQuery (codepoint);
    }
  }),
  IQUERY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iquery (codepoint);
    }
  }),
  SCHEME (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isScheme (codepoint);
    }
  }),
  PATHNODELIMS (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isPathNoDelims (codepoint);
    }
  }),
  IPATHNODELIMS (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_ipathnodelims (codepoint);
    }
  }),
  IPATHNODELIMS_SEG (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_ipathnodelims (codepoint) && codepoint != '@' && codepoint != ':';
    }
  }),
  IREGNAME (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iregname (codepoint);
    }
  }),
  IHOST (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_ihost (codepoint);
    }
  }),
  IPRIVATE (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iprivate (codepoint);
    }
  }),
  RESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isReserved (codepoint);
    }
  }),
  IUNRESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iunreserved (codepoint);
    }
  }),
  UNRESERVED (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isUnreserved (codepoint);
    }
  }),
  SCHEMESPECIFICPART (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_iunreserved (codepoint) &&
             !CodepointHelper.isReserved (codepoint) &&
             !CodepointHelper.is_iprivate (codepoint) &&
             !CodepointHelper.isPctEnc (codepoint) &&
             codepoint != '#';
    }
  }),
  AUTHORITY (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.is_regname (codepoint) &&
             !CodepointHelper.isUserInfo (codepoint) &&
             !CodepointHelper.isGenDelim (codepoint);
    }
  }),
  ASCIISANSCRLF (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.inRange (codepoint, 1, 9) && !CodepointHelper.inRange (codepoint, 14, 127);
    }
  }),
  PCT (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.isPctEnc (codepoint);
    }
  }),
  STD3ASCIIRULES (new ICodepointFilter ()
  {
    public boolean accept (final int codepoint)
    {
      return !CodepointHelper.inRange (codepoint, 0x0000, 0x002C) &&
             !CodepointHelper.inRange (codepoint, 0x002E, 0x002F) &&
             !CodepointHelper.inRange (codepoint, 0x003A, 0x0040) &&
             !CodepointHelper.inRange (codepoint, 0x005B, 0x005E) &&
             !CodepointHelper.inRange (codepoint, 0x0060, 0x0060) &&
             !CodepointHelper.inRange (codepoint, 0x007B, 0x007F);
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
