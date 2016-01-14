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
package com.helger.commons.lang.priviledged;

import java.security.PrivilegedAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * A special privileged object, that calls <code>class.getClassLoader ()</code>
 *
 * @author Philip Helger
 */
public final class PrivilegedActionGetClassLoader implements PrivilegedAction <ClassLoader>
{
  private final Class <?> m_aBaseClass;

  public PrivilegedActionGetClassLoader (@Nonnull final Class <?> aBaseClass)
  {
    m_aBaseClass = ValueEnforcer.notNull (aBaseClass, "BaseClass");
  }

  @Nullable
  public ClassLoader run ()
  {
    return m_aBaseClass.getClassLoader ();
  }
}
