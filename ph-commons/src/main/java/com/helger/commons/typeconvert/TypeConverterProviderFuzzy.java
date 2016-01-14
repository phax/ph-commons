/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A fuzzy type converter provider. Implemented as a singleton.
 *
 * @author Philip Helger
 */
public final class TypeConverterProviderFuzzy implements ITypeConverterProvider
{
  private static final TypeConverterProviderFuzzy s_aInstance = new TypeConverterProviderFuzzy ();

  private TypeConverterProviderFuzzy ()
  {}

  @Nonnull
  public static TypeConverterProviderFuzzy getInstance ()
  {
    return s_aInstance;
  }

  @SuppressWarnings ("unchecked")
  @Nullable
  public ITypeConverter <Object, Object> getTypeConverter (@Nonnull final Class <?> aSrcClass,
                                                           @Nonnull final Class <?> aDstClass)
  {
    return (ITypeConverter <Object, Object>) TypeConverterRegistry.getInstance ().getFuzzyConverter (aSrcClass,
                                                                                                     aDstClass);
  }
}
