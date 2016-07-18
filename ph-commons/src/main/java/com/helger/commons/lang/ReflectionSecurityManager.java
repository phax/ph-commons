package com.helger.commons.lang;

/**
 * A custom security manager that exposes the getClassContext() information.
 * Source:
 * http://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection?noredirect=1&lq=1
 */
public class ReflectionSecurityManager extends SecurityManager
{
  public static final ReflectionSecurityManager INSTANCE = new ReflectionSecurityManager ();

  public Class <?> getCallerClass (final int nCallStackDepth)
  {
    return getClassContext ()[nCallStackDepth];
  }

  public String getCallerClassName (final int nCallStackDepth)
  {
    return getCallerClass (nCallStackDepth).getName ();
  }
}