/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.hashcode.IHashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class encapsulates a String pattern and a set of options to be used in
 * parsing. <br>
 * Note: cannot be Serializable because DateTimeFormatter is not Serializable
 *
 * @author Philip Helger
 */
@ThreadSafe
@DevelopersNote ("The mutable m_nHashCode does not contradict thread safety")
public final class DateTimeFormatterPattern
{
  private final String m_sPattern;
  private final ResolverStyle m_eResolverStyle;
  private final DateTimeFormatter m_aFormatter;

  // Status vars
  private transient int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  public DateTimeFormatterPattern (@Nonnull @Nonempty final String sPattern,
                                   @Nonnull final ResolverStyle eResolverStyle)
  {
    ValueEnforcer.notEmpty (sPattern, "RegEx");
    ValueEnforcer.notNull (eResolverStyle, "ResolverStyle");
    m_sPattern = sPattern;
    m_eResolverStyle = eResolverStyle;
    m_aFormatter = new DateTimeFormatterBuilder ().appendPattern (sPattern)
                                                  .toFormatter ()
                                                  .withResolverStyle (m_eResolverStyle);
  }

  /**
   * @return The source pattern string. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getPattern ()
  {
    return m_sPattern;
  }

  /**
   * @return The resolver style as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public ResolverStyle getResolverStyle ()
  {
    return m_eResolverStyle;
  }

  /**
   * @return The precompiled formatter. Never <code>null</code>.
   */
  @Nonnull
  public DateTimeFormatter getAsFormatter ()
  {
    return m_aFormatter;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final DateTimeFormatterPattern rhs = (DateTimeFormatterPattern) o;
    // m_aFormatter is a state variable
    return m_sPattern.equals (rhs.m_sPattern) && m_eResolverStyle.equals (rhs.m_eResolverStyle);
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (m_sPattern).append (m_eResolverStyle).getHashCode ();
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Pattern", m_sPattern)
                                       .append ("ResolverStyle", m_eResolverStyle)
                                       .getToString ();
  }
}
