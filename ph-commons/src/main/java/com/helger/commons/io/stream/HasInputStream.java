/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.io.stream;

import java.io.InputStream;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.string.ToStringGenerator;

/**
 * Special implementation of {@link IHasInputStream} with that has an
 * InputStream supplier that can be read more than once!
 *
 * @author Philip Helger
 */
@Immutable
public class HasInputStream implements IHasInputStream
{
  private final Supplier <? extends InputStream> m_aISP;
  private final boolean m_bReadMultiple;

  /**
   * Constructor
   *
   * @param aISP
   *        {@link InputStream} supplier. May not be <code>null</code>.
   * @param bReadMultiple
   *        <code>true</code> if the supplier can be invoked more than once
   *        (e.g. from a byte[]) or <code>false</code> if it can be invoked only
   *        once (e.g. from an open socket).
   */
  public HasInputStream (@Nonnull final Supplier <? extends InputStream> aISP, final boolean bReadMultiple)
  {
    m_aISP = ValueEnforcer.notNull (aISP, "ISP");
    m_bReadMultiple = bReadMultiple;
  }

  public final boolean isReadMultiple ()
  {
    return m_bReadMultiple;
  }

  public final InputStream getInputStream ()
  {
    return m_aISP.get ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ISP", m_aISP).append ("ReadMultiple", m_bReadMultiple).getToString ();
  }

  /**
   * Create a new object with a supplier that can read multiple times.
   *
   * @param aISP
   *        {@link InputStream} provider. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static HasInputStream multiple (@Nonnull final Supplier <? extends InputStream> aISP)
  {
    return new HasInputStream (aISP, true);
  }

  /**
   * Create a new object with a supplier that can be read only once.
   *
   * @param aISP
   *        {@link InputStream} provider. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static HasInputStream once (@Nonnull final Supplier <? extends InputStream> aISP)
  {
    return new HasInputStream (aISP, false);
  }

  private static final class HISNBBAOS implements IHasInputStream
  {
    private final NonBlockingByteArrayOutputStream m_aBAOS;

    public HISNBBAOS (@Nonnull final NonBlockingByteArrayOutputStream aBAOS)
    {
      m_aBAOS = aBAOS;
    }

    @Nonnull
    public InputStream getInputStream ()
    {
      return m_aBAOS.getAsInputStream ();
    }

    public boolean isReadMultiple ()
    {
      return true;
    }
  }

  /**
   * Get a special implementation of {@link IHasInputStream} for
   * {@link NonBlockingByteArrayOutputStream}. This input stream can be read
   * multiple times.
   *
   * @param aBAOS
   *        Source stream. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 9.2.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static IHasInputStream create (@Nonnull final NonBlockingByteArrayOutputStream aBAOS)
  {
    ValueEnforcer.notNull (aBAOS, "BAOS");
    return new HISNBBAOS (aBAOS);
  }
}
