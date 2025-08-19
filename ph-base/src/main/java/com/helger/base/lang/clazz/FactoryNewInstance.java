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
package com.helger.base.lang.clazz;

import java.util.function.Supplier;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Implementation of {@link Supplier} that always creates a new instance via reflection
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The return type of the factory
 */
@Immutable
public class FactoryNewInstance <DATATYPE> implements Supplier <DATATYPE>
{
  private final Class <? extends DATATYPE> m_aClass;

  public FactoryNewInstance (@Nullable final Class <? extends DATATYPE> aClass, final boolean bCheckInstancable)
  {
    if (bCheckInstancable)
      ValueEnforcer.isTrue (ClassHelper.isInstancableClass (aClass),
                            () -> "The passed class '" +
                                  aClass +
                                  "' is not instancable or doesn't have a public no-argument constructor!");
    m_aClass = aClass;
  }

  @Nullable
  public Class <? extends DATATYPE> getFactoryClass ()
  {
    return m_aClass;
  }

  @Nullable
  public DATATYPE get ()
  {
    return GenericReflection.newInstance (m_aClass);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FactoryNewInstance <?> rhs = (FactoryNewInstance <?>) o;
    return EqualsHelper.equals (m_aClass, rhs.m_aClass);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aClass).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Class", m_aClass).getToString ();
  }

  @Nonnull
  public static <DATATYPE> FactoryNewInstance <DATATYPE> create (@Nullable final Class <DATATYPE> aClass)
  {
    return create (aClass, false);
  }

  @Nonnull
  public static <DATATYPE> FactoryNewInstance <DATATYPE> create (@Nullable final Class <DATATYPE> aClass,
                                                                 final boolean bCheckInstancable)
  {
    return new FactoryNewInstance <> (aClass, bCheckInstancable);
  }
}
