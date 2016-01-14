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
package com.helger.commons.state;

import javax.annotation.Nonnull;

/**
 * Very simple interface for an object that has finished/unfinished indication.
 *
 * @author Philip Helger
 */
public interface IFinishIndicator
{
  /**
   * @return <code>true</code> if finished and <code>false</code> if unfinished.
   */
  boolean isFinished ();

  /**
   * @return <code>true</code> if unfinished and <code>false</code> if finished.
   */
  default boolean isUnfinished ()
  {
    return !isFinished ();
  }

  @Nonnull
  default EFinish or (@Nonnull final IFinishIndicator aFinish)
  {
    return EFinish.valueOf (isFinished () || aFinish.isFinished ());
  }

  @Nonnull
  default EFinish and (@Nonnull final IFinishIndicator aFinish)
  {
    return EFinish.valueOf (isFinished () && aFinish.isFinished ());
  }
}
