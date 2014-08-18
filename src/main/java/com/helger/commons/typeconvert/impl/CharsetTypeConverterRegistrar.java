/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.typeconvert.impl;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.typeconvert.ITypeConverter;
import com.helger.commons.typeconvert.ITypeConverterRegistrarSPI;
import com.helger.commons.typeconvert.ITypeConverterRegistry;

/**
 * Register the locale specific type converter
 * 
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class CharsetTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  private static final class TypeConverterStringCharset implements ITypeConverter
  {
    public Charset convert (@Nonnull final Object aSource)
    {
      return CharsetManager.getCharsetFromName ((String) aSource);
    }
  }

  private static final class TypeConverterCharsetString implements ITypeConverter
  {
    @Nonnull
    public String convert (@Nonnull final Object aSource)
    {
      return ((Charset) aSource).name ();
    }
  }

  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // Charset
    aRegistry.registerTypeConverter (Charset.class, String.class, new TypeConverterCharsetString ());
    aRegistry.registerTypeConverter (String.class, Charset.class, new TypeConverterStringCharset ());
  }
}
