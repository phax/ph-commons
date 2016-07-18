package com.helger.commons.lang;

import javax.annotation.Nonnull;

/**
 * A custom security manager that exposes the getClassContext() information.
 * Source:
 * http://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection?noredirect=1&lq=1
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
  public Class <?> getCallerClass (final int nCallStackDepth) throws ArrayIndexOutOfBoundsException
  {
    return getCallerClassContext ()[nCallStackDepth];
  }

  @Nonnull
  public String getCallerClassName (final int nCallStackDepth) throws ArrayIndexOutOfBoundsException
  {
    return getCallerClass (nCallStackDepth).getName ();
  }
}
