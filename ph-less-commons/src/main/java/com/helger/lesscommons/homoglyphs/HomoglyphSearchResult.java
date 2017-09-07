package com.helger.lesscommons.homoglyphs;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Search result of a homoglyph search.
 *
 * @author Rob Dawson
 * @author Philip Helger
 */
@Immutable
public class HomoglyphSearchResult implements Serializable
{
  private final int m_nIndex;
  private final String m_sMatch;
  private final String m_sWord;

  public HomoglyphSearchResult (@Nonnegative final int nIndex,
                                @Nonnull @Nonempty final String sMatch,
                                @Nonnull @Nonempty final String sWord)
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

  @Nonnull
  @Nonempty
  public String getMatch ()
  {
    return m_sMatch;
  }

  @Nonnull
  @Nonempty
  public String getWord ()
  {
    return m_sWord;
  }

  @Nonnull
  public String getAsString ()
  {
    return "'" + m_sMatch + "' at position " + m_nIndex + " matches '" + m_sWord + "'";
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Index", m_nIndex)
                                       .append ("Match", m_sMatch)
                                       .append ("Word", m_sWord)
                                       .getToString ();
  }
}
