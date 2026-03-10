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
package com.helger.config.source;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.misc.Translatable;
import com.helger.text.IMultilingualText;
import com.helger.text.display.IHasDisplayText;
import com.helger.text.resolve.DefaultTextResolver;
import com.helger.text.util.TextHelper;

/**
 * Defines the names for the types of configuration sources.
 *
 * @author Philip Helger
 * @since 12.1.4
 */
@Translatable
public enum EConfigSourceTypeName implements IHasDisplayText
{
  /**
   * A configuration value from a Java system property.
   */
  SYSTEM_PROPERTY ("System Property", "System Property"),
  /**
   * A configuration value from an environment variable.
   */
  ENVIRONMENT_VARIABLE ("Umgebungsvariable", "Environment variable"),
  /**
   * A configuration value from a file.
   */
  RESOURCE ("Datei", "Resource"),
  /**
   * A configuration value from any other source inside the application.
   */
  APPLICATION ("Programmatisch", "Application provided");

  private final IMultilingualText m_aTP;

  EConfigSourceTypeName (@NonNull final String sDE, @NonNull final String sEN)
  {
    m_aTP = TextHelper.create_DE_EN (sDE, sEN);
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getDisplayText (@NonNull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
