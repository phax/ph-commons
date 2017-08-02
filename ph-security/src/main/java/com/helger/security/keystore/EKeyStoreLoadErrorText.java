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
  KEYSTORE_NO_PATH ("Kein KeyStore-Pfad angegeben.", "No keystore path provided."),
  KEYSTORE_LOAD_ERROR_NON_EXISTING ("Der KeyStore ''{0}'' konnte nicht gefunden werden. Technische Details: {1}",
                                    "Failed to locate keystore path ''{0}''. Technical details: {1}"),
  KEYSTORE_INVALID_PASSWORD ("Das Passwort für den KeyStore ''{0}'' ist falsch. Technische Details: {1}",
                             "Invalid password provided for keystore ''{0}''. Technical details: {1}"),
  KEYSTORE_LOAD_ERROR_FORMAT_ERROR ("Der KeyStore ''{0}'' hat ein ungültiges Format. Technische Details: {1}",
                                    "Failed to load the keystore ''{0}'' - invalid format. Technical details: {1}"),
  KEY_NO_ALIAS ("Kein Alias für den KeyStore angegeben.", "No alias for keystore entry provided."),
  KEY_NO_PASSWORD ("Kein Password für den Alias im KeyStore angegeben.", "No password for keystore entry provided."),
  KEY_INVALID_ALIAS ("Der Alias ''{0}'' konnte im KeyStore ''{1}'' nicht gefunden werden.",
                     "Failed to find alias ''{0}'' in keystore ''{1}''."),
  KEY_INVALID_TYPE ("Der Alias ''{0}'' aus dem KeyStore ''{1}'' hat den falschen Typ. Der technische Typ ist {2}.",
                    "Alias ''{0}'' in keystore ''{1}'' has an invalid type. The effective technical type is {2}."),
  KEY_INVALID_PASSWORD ("Das Passwort für den Alias ''{0}'' aus dem KeyStore ''{1}'' ist falsch. Technische Details: {2}.",
                        "Invalid password provided for alias ''{0}'' in keystore ''{1}''. Technical details: {2}."),
  KEY_LOAD_ERROR ("Generischer Fehler beim Laden von Alias ''{0}'' aus KeyStore ''{1}''. Technische Details: {2}.",
                  "Generic error loading alias ''{0}'' in keystore ''{1}''. Technical details: {2}.");

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
