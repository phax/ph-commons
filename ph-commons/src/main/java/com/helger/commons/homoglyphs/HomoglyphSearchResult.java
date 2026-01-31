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
package com.helger.commons.homoglyphs;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Search result of a homoglyph search.
 *
 * @author Rob Dawson
 * @author Philip Helger
 */
@Immutable
public class HomoglyphSearchResult
{
  private final int m_nIndex;
  private final String m_sMatch;
  private final String m_sWord;

  public HomoglyphSearchResult (@Nonnegative final int nIndex,
                                @NonNull @Nonempty final String sMatch,
                                @NonNull @Nonempty final String sWord)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    ValueEnforcer.notEmpty (sMatch, "Match");
    ValueEnforcer.notEmpty (sWord, "Word");
    m_nIndex = nIndex;
    m_sMatch = sMatch;
    m_sWord = sWord;
  }

  @Nonnegative
  public int getIndex ()
  {
    return m_nIndex;
  }

  @NonNull
  @Nonempty
  public String getMatch ()
  {
    return m_sMatch;
  }

  @NonNull
  @Nonempty
  public String getWord ()
  {
    return m_sWord;
  }

  @NonNull
  public String getAsString ()
  {
    return "'" + m_sMatch + "' at position " + m_nIndex + " matches '" + m_sWord + "'";
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Index", m_nIndex).append ("Match", m_sMatch).append ("Word", m_sWord).getToString ();
  }
}
