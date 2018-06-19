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
package com.helger.security.authentication.credentials;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of {@link ICredentialValidationResult} that uses multiple
 * ICredentialValidationResult instances.
 *
 * @author Philip Helger
 */
public class CredentialValidationResultList implements ICredentialValidationResult
{
  private final ICommonsList <ICredentialValidationResult> m_aResults;
  private final boolean m_bFailure;

  /**
   * Constructor with multiple results
   *
   * @param aResults
   *        The collection of results. May neither be <code>null</code> nor
   *        empty.
   */
  public CredentialValidationResultList (@Nonnull @Nonempty final Iterable <? extends ICredentialValidationResult> aResults)
  {
    ValueEnforcer.notEmpty (aResults, "Results");
    m_aResults = new CommonsArrayList <> (aResults);
    m_bFailure = m_aResults.containsAny (ICredentialValidationResult::isFailure);
  }

  public boolean isSuccess ()
  {
    return !m_bFailure;
  }

  @Override
  public boolean isFailure ()
  {
    return m_bFailure;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aDisplayLocale)
  {
    return StringHelper.getImplodedMapped ('\n', m_aResults, x -> x.getDisplayText (aDisplayLocale));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Results", m_aResults).append ("Failure", m_bFailure).getToString ();
  }
}
