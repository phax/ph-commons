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

import com.helger.base.state.ISuccessIndicator;

/**
 * The outcome of walking a ZIP archive with the {@link ZipWalker}.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
public enum EZipWalkResult implements ISuccessIndicator
{
  /** All entries were walked. */
  SUCCESS,
  /** The content does not start with a ZIP header (also for empty content). */
  NOT_A_ZIP,
  /** The archive or an entry could not be read, e.g. because it is truncated or corrupt. */
  BROKEN,
  /** The archive contains more entries than allowed. */
  MAX_ENTRY_COUNT_EXCEEDED,
  /** The total uncompressed size of the entries exceeds the allowed size. */
  MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED,
  /** Nested archives are deeper than allowed. */
  MAX_NESTING_DEPTH_EXCEEDED,
  /** The callback stopped the walk. */
  STOPPED_BY_CALLBACK;

  public boolean isSuccess ()
  {
    return this == SUCCESS;
  }
}
