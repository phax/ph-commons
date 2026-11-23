/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.unittest.property;

import org.jspecify.annotations.NonNull;

/**
 * Thrown to discard the current sample. This happens if a filter rejects too many generated values
 * in a row, if {@link PropertyAssume#that(boolean)} fails, or if a generator needs more choices than
 * available. A rejected sample is neither a success nor a failure. As this exception is used for
 * control flow only, it has no stack trace.
 *
 * @author Philip Helger
 */
public class SampleRejectedException extends RuntimeException
{
  /**
   * Constructor
   *
   * @param sMessage
   *        The reason why the sample was rejected. May not be <code>null</code>.
   */
  public SampleRejectedException (@NonNull final String sMessage)
  {
    super (sMessage, null, false, false);
  }
}
