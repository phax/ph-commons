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
package com.helger.io.zip;

import java.io.IOException;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;

/**
 * Thrown by {@link LimitedZipInputStream} if the content is not a ZIP archive or if a limit of the
 * {@link ZipWalkerSettings} is exceeded.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
public class InvalidZipException extends IOException
{
  private final EZipWalkResult m_eReason;

  /**
   * Constructor.
   *
   * @param eReason
   *        The reason. One of {@link EZipWalkResult#NOT_A_ZIP},
   *        {@link EZipWalkResult#MAX_ENTRY_COUNT_EXCEEDED} or
   *        {@link EZipWalkResult#MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED}. May not be
   *        <code>null</code>.
   * @param sMessage
   *        The error message. May not be <code>null</code>.
   */
  public InvalidZipException (@NonNull final EZipWalkResult eReason, @NonNull final String sMessage)
  {
    super (sMessage);
    ValueEnforcer.notNull (eReason, "Reason");
    m_eReason = eReason;
  }

  /**
   * @return The reason why the ZIP archive was refused. Never <code>null</code>.
   */
  @NonNull
  public final EZipWalkResult getReason ()
  {
    return m_eReason;
  }
}
