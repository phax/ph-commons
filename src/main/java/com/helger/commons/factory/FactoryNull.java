/**
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
package com.helger.commons.factory;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;

/**
 * An instance of {@link IFactory} that always returns <code>null</code>.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Return type of the factory
 */
public final class FactoryNull <DATATYPE> implements IFactory <DATATYPE>, Serializable
{
  private static final FactoryNull <Object> s_aInstance = new FactoryNull <Object> ();

  private FactoryNull ()
  {}

  @Nullable
  public DATATYPE create ()
  {
    return null;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }

  @Nonnull
  public static <DATATYPE> FactoryNull <DATATYPE> getInstance ()
  {
    return GenericReflection.<FactoryNull <Object>, FactoryNull <DATATYPE>> uncheckedCast (s_aInstance);
  }
}
