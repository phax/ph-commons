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
package com.helger.commons.messagedigest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;

/**
 * A selection of common hash algorithms.
 *
 * @author Philip Helger
 */
public enum EMessageDigestAlgorithm
{
  MD5 ("MD5"),
  SHA_1 ("SHA-1"),
  SHA_256 ("SHA-256"),
  SHA_384 ("SHA-384"),
  SHA_512 ("SHA-512");

  private final String m_sAlgorithm;

  private EMessageDigestAlgorithm (@Nonnull @Nonempty final String sAlgorithm)
  {
    m_sAlgorithm = sAlgorithm;
  }

  @Nonnull
  @Nonempty
  public String getAlgorithm ()
  {
    return m_sAlgorithm;
  }

  @Nullable
  public static EMessageDigestAlgorithm getFromStringIgnoreCase (@Nullable final String sAlgorithm)
  {
    if (StringHelper.hasText (sAlgorithm))
      for (final EMessageDigestAlgorithm eMD : EMessageDigestAlgorithm.values ())
        if (eMD.m_sAlgorithm.equalsIgnoreCase (sAlgorithm))
          return eMD;
    return null;
  }
}
