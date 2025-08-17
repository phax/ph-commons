/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.http.header.specific;

import java.util.Locale;
import java.util.function.Function;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.misc.ChangeNextMajorRelease;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.http.header.AbstractQValueList;
import com.helger.http.header.QValue;

import jakarta.annotation.Nonnull;

/**
 * Represents a list of Accept-Language values as specified in the HTTP header
 *
 * @author Philip Helger
 */
public class AcceptLanguageList extends AbstractQValueList <String>
{
  public AcceptLanguageList ()
  {}

  @Nonnull
  private static String _unify (@Nonnull final String sLanguage)
  {
    return sLanguage.toLowerCase (Locale.US);
  }

  // TODO 10.x make chainable
  @ChangeNextMajorRelease ("Make chainable")
  public void addLanguage (@Nonnull final String sLanguage, @Nonnegative final double dQuality)
  {
    ValueEnforcer.notEmpty (sLanguage, "Language");
    qvalueMap ().put (_unify (sLanguage), new QValue (dQuality));
  }

  /**
   * Return the associated quality of the given language.
   *
   * @param sLanguage
   *        The language name to query. May not be <code>null</code>.
   * @return The associated {@link QValue}. Never <code>null</code>.
   */
  @Nonnull
  public QValue getQValueOfLanguage (@Nonnull final String sLanguage)
  {
    ValueEnforcer.notNull (sLanguage, "Language");

    // Find language direct
    QValue aQuality = qvalueMap ().get (_unify (sLanguage));
    if (aQuality == null)
    {
      // If not explicitly given, check for "*"
      aQuality = qvalueMap ().get (AcceptLanguageHandler.ANY_LANGUAGE);
      if (aQuality == null)
      {
        // Neither language nor "*" is present
        // -> assume minimum quality
        return QValue.MIN_QVALUE;
      }
    }
    return aQuality;
  }

  /**
   * Return the associated quality of the given language.
   *
   * @param sLanguage
   *        The language name to query. May not be <code>null</code>.
   * @return 0 means not accepted, 1 means fully accepted.
   */
  public double getQualityOfLanguage (@Nonnull final String sLanguage)
  {
    return getQValueOfLanguage (sLanguage).getQuality ();
  }

  public boolean supportsLanguage (@Nonnull final String sLanguage)
  {
    return getQValueOfLanguage (sLanguage).isAboveMinimumQuality ();
  }

  public boolean explicitlySupportsLanguage (@Nonnull final String sLanguage)
  {
    ValueEnforcer.notNull (sLanguage, "Language");

    final QValue aQuality = qvalueMap ().get (_unify (sLanguage));
    return aQuality != null && aQuality.isAboveMinimumQuality ();
  }

  @Override
  @Nonnull
  public String getAsHttpHeaderValue ()
  {
    return getAsHttpHeaderValue (Function.identity ());
  }
}
