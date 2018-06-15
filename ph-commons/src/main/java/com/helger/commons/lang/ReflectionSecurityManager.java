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
package com.helger.commons.lang;

import javax.annotation.Nonnull;

/**
 * A custom security manager that exposes the <code>getClassContext()</code>
 * information. <a href=
 * "http://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection?noredirect=1&lq=1">Source
 * on SO</a>
 *
 * @author Someone on SO
 */
public class ReflectionSecurityManager extends SecurityManager
{
  public static final ReflectionSecurityManager INSTANCE = new ReflectionSecurityManager ();

  @Nonnull
  public Class <?> [] getCallerClassContext ()
  {
    return getClassContext ();
  }

  @Nonnull
  public Class <?> getCallerClass (final int nCallStackDepth)
  {
    return getCallerClassContext ()[nCallStackDepth];
  }

  @Nonnull
  public String getCallerClassName (final int nCallStackDepth)
  {
    return getCallerClass (nCallStackDepth).getName ();
  }
}
