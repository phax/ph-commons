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
package com.helger.security.keystore;

import java.security.KeyStore;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class contains the result of loading a key store from e.g. a
 * configuration file.
 *
 * @author Philip Helger
 */
public class LoadedKeyStore implements ISuccessIndicator
{
  private final KeyStore m_aKeyStore;
  private final EKeyStoreLoadError m_eError;
  private final String [] m_aErrorParams;

  public LoadedKeyStore (@Nullable final KeyStore aKeyStore,
                         @Nullable final EKeyStoreLoadError eError,
                         @Nullable final String... aErrorParams)
  {
    m_aKeyStore = aKeyStore;
    m_eError = eError;
    m_aErrorParams = aErrorParams;
  }

  public boolean isSuccess ()
  {
    return m_aKeyStore != null;
  }

  /**
   * @return The loaded key store. Never <code>null</code> in case of success.
   *         Always <code>null</code> in case of failure.
   */
  @Nullable
  public KeyStore getKeyStore ()
  {
    return m_aKeyStore;
  }

  /**
   * @return The error code. Never <code>null</code> in case of failure. Always
   *         <code>null</code> in case of success.
   */
  @Nullable
  public EKeyStoreLoadError getError ()
  {
    return m_eError;
  }

  /**
   * @return The error parameters. Never <code>null</code> in case of failure.
   *         Always <code>null</code> in case of success.
   */
  @Nullable
  public String [] getErrorParams ()
  {
    return m_eError == null ? null : ArrayHelper.getCopy (m_aErrorParams);
  }

  /**
   * Get the error text
   * 
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no error occurred, the error text otherwise.
   */
  @Nullable
  public String getErrorText (@Nonnull final Locale aContentLocale)
  {
    return m_eError == null ? null : m_eError.getDisplayTextWithArgs (aContentLocale, (Object []) m_aErrorParams);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("KeyStore", m_aKeyStore)
                                       .append ("Error", m_eError)
                                       .append ("ErrorParams", m_aErrorParams)
                                       .getToString ();
  }
}
