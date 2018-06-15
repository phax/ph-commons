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
package com.helger.commons.lang.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.StringHelper;

public class LoggingInvocationHandler implements InvocationHandler
{
  public static final boolean DEFAULT_PROXY_RETURN_VALUES = true;
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingInvocationHandler.class);

  private final Object m_aActualTarget;
  private final String m_sLogPrefix;
  private boolean m_bProxyReturnValues = DEFAULT_PROXY_RETURN_VALUES;

  public LoggingInvocationHandler (@Nonnull final Object aActualTarget)
  {
    m_aActualTarget = aActualTarget;
    // Calculate only once
    m_sLogPrefix = "[@" + StringHelper.getHexStringLeadingZero (System.identityHashCode (aActualTarget), 8) + "] ";
  }

  /**
   * @return <code>true</code> if return values are returned as proxied object
   *         (the default) or <code>false</code> if the proxied return values
   *         are returned "as-is".
   */
  public boolean isProxyReturnValues ()
  {
    return m_bProxyReturnValues;
  }

  /**
   * @param bProxyReturnValues
   *        <code>true</code> to enable automatic proxying of return values (if
   *        the declared return type is an interface; activated by default) or
   *        <code>false</code> to disable this feature.
   * @return this for chaining
   */
  @Nonnull
  public LoggingInvocationHandler setProxyReturnValues (final boolean bProxyReturnValues)
  {
    m_bProxyReturnValues = bProxyReturnValues;
    return this;
  }

  @Nonnull
  private static String _getParameter (@Nullable final Parameter [] aParams, @Nullable final Object [] aArgs)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (aParams != null)
    {
      final int nCount = aParams.length;
      for (int i = 0; i < nCount; ++i)
      {
        final Parameter aParam = aParams[i];
        final Object aArg = aArgs[i];
        if (aSB.length () > 0)
          aSB.append (", ");
        aSB.append (aParam.getType ().getSimpleName ())
           .append (' ')
           .append (aParam.getName ())
           .append (" = ")
           .append (aArg);
      }
    }
    return aSB.toString ();
  }

  @Nullable
  public Object invoke (@Nonnull final Object aProxy,
                        @Nonnull final Method aMethod,
                        @Nonnull final Object [] aArgs) throws Throwable
  {
    final Class <?> aReturnType = aMethod.getReturnType ();
    final String sMethod = m_sLogPrefix +
                           aReturnType.getSimpleName () +
                           " " +
                           m_aActualTarget.getClass ().getSimpleName () +
                           "." +
                           aMethod.getName () +
                           " (" +
                           _getParameter (aMethod.getParameters (), aArgs) +
                           ")";
    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info (sMethod + " - invoke");
    final Object ret = aMethod.invoke (m_aActualTarget, aArgs);

    if (aReturnType == Void.TYPE)
    {
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info (sMethod + " - return");
      return null;
    }
    if (ret == null)
    {
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info (sMethod + " - return null");
      return null;
    }
    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info (sMethod + " - return " + ret);
    if (m_bProxyReturnValues && aReturnType.isInterface ())
    {
      // Proxy result type only if it is an interface
      return proxying (aReturnType, ret);
    }
    return ret;
  }

  @Nonnull
  public static <T> T proxying (@Nonnull final Class <? extends T> aInterfaceClass, @Nonnull final T aActualTarget)
  {
    return proxying (aInterfaceClass, aActualTarget, LoggingInvocationHandler::new);
  }

  @Nonnull
  public static <T> T proxying (@Nonnull final Class <? extends T> aInterfaceClass,
                                @Nonnull final T aActualTarget,
                                @Nonnull final Function <? super T, ? extends InvocationHandler> aFactory)
  {
    ValueEnforcer.isTrue (aInterfaceClass.isInterface (), "Only interface classes can be proxied!");
    final Object ret = Proxy.newProxyInstance (aInterfaceClass.getClassLoader (),
                                               new Class <?> [] { aInterfaceClass },
                                               aFactory.apply (aActualTarget));
    return GenericReflection.uncheckedCast (ret);
  }
}
