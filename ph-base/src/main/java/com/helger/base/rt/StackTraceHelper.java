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
package com.helger.base.rt;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;

/**
 * This class contains utility methods for handling stack traces.
 *
 * @author Philip Helger
 */
@Immutable
public final class StackTraceHelper
{
  /** the separator used to separate different lines of a stack */
  public static final String DEFAULT_LINE_SEPARATOR = "\n";

  /** elements to omit in stack traces */
  @CodingStyleguideUnaware
  private static final List <String> STACKTRACE_OMIT_UNITTEST = new ArrayList <> ();
  @CodingStyleguideUnaware
  private static final List <String> STACKTRACE_OMIT_APPSRV = new ArrayList <> ();

  static
  {
    // Unit test frameworks
    STACKTRACE_OMIT_UNITTEST.add ("org.testng");
    STACKTRACE_OMIT_UNITTEST.add ("org.junit.");
    STACKTRACE_OMIT_UNITTEST.add ("junit.framework.");

    // Application servers
    STACKTRACE_OMIT_APPSRV.add ("org.apache.catalina.core");
    STACKTRACE_OMIT_APPSRV.add ("org.mortbay.jetty.");
    STACKTRACE_OMIT_APPSRV.add ("org.eclipse.jetty.");
  }

  @PresentForCodeCoverage
  private static final StackTraceHelper INSTANCE = new StackTraceHelper ();

  private StackTraceHelper ()
  {}

  private static boolean _isUnitTestLine (@NonNull final String sStackTraceLine)
  {
    for (final String s : STACKTRACE_OMIT_UNITTEST)
      if (sStackTraceLine.startsWith (s))
        return true;
    return false;
  }

  private static boolean _isAppSrvLine (@NonNull final String sStackTraceLine)
  {
    for (final String s : STACKTRACE_OMIT_APPSRV)
      if (sStackTraceLine.startsWith (s))
        return true;
    return false;
  }

  private static boolean _isStopStackTraceListing (@NonNull final String sStackTraceLine)
  {
    return _isUnitTestLine (sStackTraceLine) || _isAppSrvLine (sStackTraceLine);
  }

  private static boolean _matchesParentStackTrace (@NonNull final StackTraceElement aElement,
                                                   @Nullable final StackTraceElement [] aParentElements)
  {
    if (aParentElements != null)
      for (final StackTraceElement aParentElement : aParentElements)
        if (aParentElement.equals (aElement))
          return true;
    return false;
  }

  private static void _appendSingleStackTraceToString (@NonNull final StringBuilder aSB,
                                                       @NonNull final StackTraceElement [] aStackTraceElements,
                                                       @Nullable final StackTraceElement [] aParentStackTraceElements,
                                                       final boolean bOmitCommonStackTraceElements,
                                                       @NonNull final String sLineSeparator)
  {
    // add main call stack
    for (int i = 0; i < aStackTraceElements.length; ++i)
    {
      final StackTraceElement aStackTraceElement = aStackTraceElements[i];
      final String sStackTraceElement = aStackTraceElement.toString ();

      // stop if some Catalina line occurs here (Apache Tomcat)
      // -> would lead to very long stack traces
      // -> also try to filter stack trace elements that are already contained
      // in a parent stack trace
      if ((bOmitCommonStackTraceElements && _isStopStackTraceListing (sStackTraceElement)) ||
          _matchesParentStackTrace (aStackTraceElement, aParentStackTraceElements))
      {
        // write number of omitted elements
        final int nOmitted = aStackTraceElements.length - i;
        aSB.append ("  [")
           .append (nOmitted)
           .append (nOmitted == 1 ? " element" : " elements")
           .append (" omitted -- ")
           .append (sStackTraceElement)
           .append (']')
           .append (sLineSeparator);
        break;
      }

      aSB.append (i + 1).append (".: ").append (sStackTraceElement).append (sLineSeparator);
    }
  }

  public static void appendStackToString (@NonNull final StringBuilder aSB,
                                          @NonNull final StackTraceElement [] aStackTraceElements)
  {
    appendStackToString (aSB, aStackTraceElements, DEFAULT_LINE_SEPARATOR);
  }

  public static void appendStackToString (@NonNull final StringBuilder aSB,
                                          @NonNull final StackTraceElement [] aStackTraceElements,
                                          @NonNull final String sLineSeparator)
  {
    ValueEnforcer.notNull (aSB, "StringBuilder");
    ValueEnforcer.notNull (aStackTraceElements, "StackTraceElements");
    ValueEnforcer.notNull (sLineSeparator, "LineSeparator");

    _appendSingleStackTraceToString (aSB, aStackTraceElements, null, true, sLineSeparator);
  }

  @NonNull
  public static String getStackAsString (@NonNull final StackTraceElement [] aStackTraceElements,
                                         final boolean bOmitCommonStackTraceElements)
  {
    return getStackAsString (aStackTraceElements, bOmitCommonStackTraceElements, DEFAULT_LINE_SEPARATOR);
  }

  @NonNull
  public static String getStackAsString (@NonNull final StackTraceElement [] aStackTraceElements,
                                         final boolean bOmitCommonStackTraceElements,
                                         @NonNull final String sLineSeparator)
  {
    ValueEnforcer.notNull (aStackTraceElements, "StackTraceElements");
    ValueEnforcer.notNull (sLineSeparator, "LineSeparator");

    final StringBuilder aSB = new StringBuilder ();
    _appendSingleStackTraceToString (aSB, aStackTraceElements, null, bOmitCommonStackTraceElements, sLineSeparator);

    // avoid having a separator at the end -> remove if present
    if (sLineSeparator.length () > 0)
      if (StringHelper.endsWith (aSB, sLineSeparator))
        aSB.delete (aSB.length () - sLineSeparator.length (), aSB.length ());

    return aSB.toString ();
  }

  @NonNull
  public static String getStackAsString (@NonNull final StackTraceElement [] aStackTraceElements)
  {
    return getStackAsString (aStackTraceElements, true, DEFAULT_LINE_SEPARATOR);
  }

  @NonNull
  public static String getStackAsString (@NonNull final Thread aThread)
  {
    return getStackAsString (aThread.getStackTrace (), true, DEFAULT_LINE_SEPARATOR);
  }

  @NonNull
  public static String getStackAsString (@NonNull final Thread aThread, final boolean bOmitCommonStackTraceElements)
  {
    return getStackAsString (aThread.getStackTrace (), bOmitCommonStackTraceElements, DEFAULT_LINE_SEPARATOR);
  }

  @NonNull
  public static String getStackAsString (@NonNull final Thread aThread,
                                         final boolean bOmitCommonStackTraceElements,
                                         @NonNull final String sLineSeparator)
  {
    return getStackAsString (aThread.getStackTrace (), bOmitCommonStackTraceElements, sLineSeparator);
  }

  @NonNull
  public static String getCurrentThreadStackAsString ()
  {
    return getStackAsString (Thread.currentThread ().getStackTrace (), true, DEFAULT_LINE_SEPARATOR);
  }

  private static StringBuilder _getRecursiveStackAsStringBuilder (@NonNull final Throwable aThrowable,
                                                                  @Nullable final Throwable aParentThrowable,
                                                                  @Nullable final StringBuilder aInitialSB,
                                                                  @Nonnegative final int nLevel,
                                                                  final boolean bOmitCommonStackTraceElements,
                                                                  @NonNull final String sLineSeparator)
  {
    // init string buffer with estimated size (very rough guess)
    final StringBuilder aSB = aInitialSB == null ? new StringBuilder () : aInitialSB;

    // add exception name (+ exception message)
    aSB.append (aThrowable.toString ()).append (sLineSeparator);

    // add main call stack
    _appendSingleStackTraceToString (aSB,
                                     aThrowable.getStackTrace (),
                                     aParentThrowable == null ? null : aParentThrowable.getStackTrace (),
                                     bOmitCommonStackTraceElements,
                                     sLineSeparator);

    // recursively print all causing stacks - reuse StringBuilder
    if (aThrowable.getCause () != null)
    {
      aSB.append ("==> [").append (nLevel).append ("] caused by ");
      _getRecursiveStackAsStringBuilder (aThrowable.getCause (),
                                         aThrowable,
                                         aSB,
                                         nLevel + 1,
                                         bOmitCommonStackTraceElements,
                                         sLineSeparator);
    }
    return aSB;
  }

  /**
   * Get the stack trace of a throwable as string.
   *
   * @param t
   *        The throwable to be converted. May be <code>null</code>.
   * @return the stack trace as newline separated string. If the passed Throwable is
   *         <code>null</code> an empty string is returned.
   */
  @NonNull
  public static String getStackAsString (@Nullable final Throwable t)
  {
    return getStackAsString (t, true, DEFAULT_LINE_SEPARATOR);
  }

  /**
   * Get the stack trace of a throwable as string.
   *
   * @param t
   *        The throwable to be converted. May be <code>null</code>.
   * @param bOmitCommonStackTraceElements
   *        If <code>true</code> the stack trace is cut after certain class names occurring. If
   *        <code>false</code> the complete stack trace is returned.
   * @return the stack trace as newline separated string. If the passed Throwable is
   *         <code>null</code> an empty string is returned.
   */
  @NonNull
  public static String getStackAsString (@Nullable final Throwable t, final boolean bOmitCommonStackTraceElements)
  {
    return getStackAsString (t, bOmitCommonStackTraceElements, DEFAULT_LINE_SEPARATOR);
  }

  /**
   * Get the stack trace of a throwable as string.
   *
   * @param t
   *        The throwable to be converted. May be <code>null</code>.
   * @param bOmitCommonStackTraceElements
   *        If <code>true</code> the stack trace is cut after certain class names occurring. If
   *        <code>false</code> the complete stack trace is returned.
   * @param sLineSeparator
   *        The line separator to use. May not be <code>null</code>.
   * @return the stack trace as newline separated string. If the passed Throwable is
   *         <code>null</code> an empty string is returned.
   * @since 9.3.6
   */
  @NonNull
  public static String getStackAsString (@Nullable final Throwable t,
                                         final boolean bOmitCommonStackTraceElements,
                                         @NonNull final String sLineSeparator)
  {
    ValueEnforcer.notNull (sLineSeparator, "LineSeparator");

    if (t == null)
      return "";

    // convert call stack to string
    final StringBuilder aCallStack = _getRecursiveStackAsStringBuilder (t,
                                                                        null,
                                                                        null,
                                                                        1,
                                                                        bOmitCommonStackTraceElements,
                                                                        sLineSeparator);

    // avoid having a separator at the end -> remove if present
    if (sLineSeparator.length () > 0)
      if (StringHelper.endsWith (aCallStack, sLineSeparator))
        aCallStack.delete (aCallStack.length () - sLineSeparator.length (), aCallStack.length ());

    // no changes
    return aCallStack.toString ();
  }

  /**
   * Check if the passed stack trace array contains a unit test element. Known unit test frameworks
   * are JUnit and TestNG.
   *
   * @param t
   *        The {@link Throwable} whose stack trace should be scanned for unit test classes. May be
   *        <code>null</code>.
   * @return <code>true</code> if at least one stack trace element is from a known unit test
   *         framework.
   */
  public static boolean containsUnitTestElement (@Nullable final Throwable t)
  {
    return t != null && containsUnitTestElement (t.getStackTrace ());
  }

  /**
   * Check if the passed stack trace array contains a unit test element. Known unit test frameworks
   * are JUnit and TestNG.
   *
   * @param aStackTrace
   *        The stack trace array to be scanned. May be <code>null</code>.
   * @return <code>true</code> if at least one stack trace element is from a known unit test
   *         framework.
   */
  public static boolean containsUnitTestElement (@Nullable final StackTraceElement [] aStackTrace)
  {
    if (aStackTrace != null)
      for (final StackTraceElement aStackTraceElement : aStackTrace)
      {
        final String sStackTraceLine = aStackTraceElement.toString ();
        if (_isUnitTestLine (sStackTraceLine))
          return true;
      }
    return false;
  }
}
