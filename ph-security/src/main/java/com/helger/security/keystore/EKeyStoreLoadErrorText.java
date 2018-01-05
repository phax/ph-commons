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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Translatable;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayTextWithArgs;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.util.TextHelper;

@Translatable
public enum EKeyStoreLoadErrorText implements IHasDisplayTextWithArgs
{
  KEYSTORE_NO_PATH ("Kein KeyStore-Pfad angegeben.", "No key store path provided."),
  KEYSTORE_LOAD_ERROR_NON_EXISTING ("Der KeyStore ''{0}'' konnte nicht gefunden werden. Technische Details: {1}",
                                    "Failed to locate key store path ''{0}''. Technical details: {1}"),
  KEYSTORE_INVALID_PASSWORD ("Das Passwort für den KeyStore ''{0}'' ist falsch. Technische Details: {1}",
                             "Invalid password provided for key store ''{0}''. Technical details: {1}"),
  KEYSTORE_LOAD_ERROR_FORMAT_ERROR ("Der KeyStore ''{0}'' hat ein ungültiges Format. Technische Details: {1}",
                                    "Failed to load the key store ''{0}'' - invalid format. Technical details: {1}"),
  KEY_NO_ALIAS ("Kein Alias für den KeyStore angegeben.", "No alias for key store entry provided."),
  KEY_NO_PASSWORD ("Kein Password für den Alias im KeyStore angegeben.", "No password for key store entry provided."),
  KEY_INVALID_ALIAS ("Der Alias ''{0}'' konnte im KeyStore ''{1}'' nicht gefunden werden.",
                     "Failed to find alias ''{0}'' in key store ''{1}''."),
  KEY_INVALID_TYPE ("Der Alias ''{0}'' aus dem KeyStore ''{1}'' hat den falschen Typ. Der technische Typ ist {2}.",
                    "Alias ''{0}'' in key store ''{1}'' has an invalid type. The effective technical type is {2}."),
  KEY_INVALID_PASSWORD ("Das Passwort für den Alias ''{0}'' aus dem KeyStore ''{1}'' ist falsch. Technische Details: {2}.",
                        "Invalid password provided for alias ''{0}'' in key store ''{1}''. Technical details: {2}."),
  KEY_LOAD_ERROR ("Generischer Fehler beim Laden von Alias ''{0}'' aus KeyStore ''{1}''. Technische Details: {2}.",
                  "Generic error loading alias ''{0}'' in key store ''{1}''. Technical details: {2}.");

  private final IMultilingualText m_aTP;

  private EKeyStoreLoadErrorText (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    m_aTP = TextHelper.create_DE_EN (sDE, sEN);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
