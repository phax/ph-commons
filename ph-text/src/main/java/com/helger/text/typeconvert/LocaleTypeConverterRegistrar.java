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
package com.helger.text.typeconvert;

import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.IsSPIImplementation;
import com.helger.text.locale.LocaleCache;
import com.helger.typeconvert.ITypeConverterRegistrarSPI;
import com.helger.typeconvert.ITypeConverterRegistry;

/**
 * Register the locale specific type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class LocaleTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  public void registerTypeConverter (@NonNull final ITypeConverterRegistry aRegistry)
  {
    // Locale
    aRegistry.registerTypeConverter (String.class,
                                     Locale.class,
                                     sSource -> "".equals (sSource) ? Locale.ROOT : LocaleCache.getInstance ()
                                                                                               .getLocale (sSource));
  }
}
