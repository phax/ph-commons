/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file;

import javax.annotation.Nonnegative;

/**
 * An enumeration that encapsulates all available file IO operation types.
 *
 * @author Philip Helger
 */
public enum EFileIOOperation
{
  COPY_DIR_RECURSIVE (2),
  COPY_FILE (2),
  CREATE_DIR (1),
  CREATE_DIR_RECURSIVE (1),
  DELETE_DIR (1),
  DELETE_DIR_RECURSIVE (1),
  DELETE_FILE (1),
  RENAME_DIR (2),
  RENAME_FILE (2);

  private final int m_nParamCount;

  private EFileIOOperation (@Nonnegative final int nParamCount)
  {
    m_nParamCount = nParamCount;
  }

  /**
   * @return The number of parameters (File objects) involved in this operation.
   *         Always &gt; 0.
   */
  @Nonnegative
  public int getParamCount ()
  {
    return m_nParamCount;
  }
}
