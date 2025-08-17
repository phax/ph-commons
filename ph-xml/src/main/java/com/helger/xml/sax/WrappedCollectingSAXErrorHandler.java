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
package com.helger.xml.sax;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.equals.ValueEnforcer;
import com.helger.diagnostics.error.list.ErrorList;

import jakarta.annotation.Nonnull;

/**
 * An error handler implementation that stores all warnings, errors and fatal
 * errors. Derived from {@link CollectingSAXErrorHandler} since v9.2.0.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@ThreadSafe
public class WrappedCollectingSAXErrorHandler extends CollectingSAXErrorHandler
{
  public WrappedCollectingSAXErrorHandler (@Nonnull final ErrorList aErrorList)
  {
    super ( () -> ValueEnforcer.notNull (aErrorList, "ErrorList"));
  }

  /**
   * @return The error list object passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  public final ErrorList wrappedErrorList ()
  {
    return m_aErrors;
  }
}
