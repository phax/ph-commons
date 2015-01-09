/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.io;

import javax.annotation.Nonnull;

import com.helger.commons.state.EChange;

/**
 * This is an interface for objects that support batch processing, to avoid
 * unnecessary IO operations.
 * 
 * @author Philip Helger
 */
public interface IBatchable
{
  /**
   * @return <code>true</code> if batch processing is currently active,
   *         <code>false</code> otherwise.
   */
  boolean isBatchProcessingActive ();

  /**
   * Start batch processing on this object. This should only have an effect on
   * the first call.
   * 
   * @return {@link EChange#CHANGED} if batch processing was enabled for this
   *         object, {@link EChange#UNCHANGED} if batch processing is already
   *         active.
   */
  @Nonnull
  EChange startBatchProcessing ();

  /**
   * Stop batch processing on this object and commit any pending changes.
   * 
   * @return {@link EChange#CHANGED} if batch processing was successfully
   *         stopped, {@link EChange#UNCHANGED} if batch processing was not
   *         active.
   */
  @Nonnull
  EChange stopBatchProcessingAndCommit ();
}
