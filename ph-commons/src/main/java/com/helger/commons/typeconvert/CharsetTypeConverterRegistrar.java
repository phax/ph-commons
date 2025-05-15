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
package com.helger.commons.typeconvert;

import java.nio.charset.Charset;

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.IsSPIImplementation;
import com.helger.commons.charset.CharsetHelper;

/**
 * Register the locale specific type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class CharsetTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // Charset
    aRegistry.registerTypeConverter (Charset.class, String.class, Charset::name);
    aRegistry.registerTypeConverter (String.class, Charset.class, CharsetHelper::getCharsetFromName);
  }
}
