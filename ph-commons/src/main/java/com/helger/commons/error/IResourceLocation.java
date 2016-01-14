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
package com.helger.commons.error;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.lang.IHasStringRepresentation;

/**
 * Interface indication the position of something within a single resource
 *
 * @author Philip Helger
 */
public interface IResourceLocation extends IHasStringRepresentation, Serializable
{
  /** Constant for an illegal row or column number */
  int ILLEGAL_NUMBER = CGlobal.ILLEGAL_UINT;

  /**
   * @return The ID of the resource where the error occurred. May be
   *         <code>null</code>.
   */
  @Nullable
  String getResourceID ();

  /**
   * @return The 1-based line number {@link #ILLEGAL_NUMBER} if no line number
   *         is present.
   */
  int getLineNumber ();

  /**
   * @return The 1-based column number {@link #ILLEGAL_NUMBER} if no column
   *         number is present.
   */
  int getColumnNumber ();

  /**
   * @return The field where the error occurred. Sometimes this field is
   *         available instead of the line- and column numbers. May be
   *         <code>null</code>.
   */
  @Nullable
  String getField ();
}
