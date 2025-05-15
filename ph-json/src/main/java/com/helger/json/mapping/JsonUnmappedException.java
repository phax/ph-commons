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
package com.helger.json.mapping;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.string.StringHelper;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;

/**
 * This is a work around to read "exceptions" from JSON without actually having
 * the need to create "Exception" objects. It has the fields class name,
 * exception message and stack trace.
 *
 * @author Philip Helger
 * @since 11.0.5
 */
public class JsonUnmappedException extends Exception
{
  private final String m_sClassName;
  private final String m_sMessage;
  private final ICommonsList <String> m_aStackTraceLines;

  public JsonUnmappedException (@Nonnull final String sClassName,
                                @Nullable final String sMessage,
                                @Nonnull final ICommonsList <String> aStackTraceLines)
  {
    ValueEnforcer.notNull (sClassName, "ClassName");
    ValueEnforcer.notNull (aStackTraceLines, "StackTraceLines");
    m_sClassName = sClassName;
    m_sMessage = sMessage;
    m_aStackTraceLines = aStackTraceLines.getClone ();
  }

  /**
   * @return The fully qualified name of the exception class as in
   *         "java.lang.IllegalArgumentException"
   */
  @Nonnull
  public String getClassName ()
  {
    return m_sClassName;
  }

  /**
   * @return The message that was passed to the Exception. May be
   *         <code>null</code> if the exception only contained another
   *         exception.
   */
  @Override
  @Nullable
  public String getMessage ()
  {
    return m_sMessage;
  }

  /**
   * @return A non-null list of all stack trace lines in an arbitrary format.
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllStackTraceLines ()
  {
    return m_aStackTraceLines.getClone ();
  }

  @Nullable
  public IJsonObject getAsJson ()
  {
    return new JsonObject ().add (JsonMapper.JSON_CLASS, m_sClassName)
                            .addIfNotNull (JsonMapper.JSON_MESSAGE, m_sMessage)
                            .add (JsonMapper.JSON_STACK_TRACE,
                                  StringHelper.imploder ()
                                              .separator (StackTraceHelper.DEFAULT_LINE_SEPARATOR)
                                              .source (m_aStackTraceLines)
                                              .build ());
  }
}
