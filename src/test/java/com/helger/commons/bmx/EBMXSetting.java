/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.bmx;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.annotations.ReturnsMutableCopy;

/**
 * Contains all the available Binary Micro XML (BMX) settings.
 * 
 * @author Philip Helger
 */
public enum EBMXSetting
{
  NO_STRINGTABLE (0x0001, false);

  private final int m_nValue;
  private final boolean m_bEnabledByDefault;

  private EBMXSetting (@Nonnegative final int nValue, final boolean bEnabledByDefault)
  {
    m_nValue = nValue;
    m_bEnabledByDefault = bEnabledByDefault;
  }

  public int getValue ()
  {
    return m_nValue;
  }

  public boolean isEnabledByDefault ()
  {
    return m_bEnabledByDefault;
  }

  /**
   * @return A list with all flags that are enabled by default. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EBMXSetting> getAllEnabledByDefault ()
  {
    final List <EBMXSetting> ret = new ArrayList <EBMXSetting> ();
    for (final EBMXSetting eSetting : values ())
      if (eSetting.isEnabledByDefault ())
        ret.add (eSetting);
    return ret;
  }
}
