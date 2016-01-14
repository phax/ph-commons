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
package com.helger.commons.hashcode;

import com.helger.commons.lang.IAppendable;

/**
 * Base interface for hash code generators.
 *
 * @author Philip Helger
 */
public interface IHashCodeGenerator extends IAppendable <IHashCodeGenerator>
{
  /** Represents an illegal hash code that is never to be returned! */
  int ILLEGAL_HASHCODE = 0;

  /**
   * Retrieve the final hash code. Once this method has been called, no further
   * calls to append can be done since the hash value is locked!
   *
   * @return The finally completed hash code. The returned value is never
   *         {@link #ILLEGAL_HASHCODE}. If the calculated hash code would be
   *         {@link #ILLEGAL_HASHCODE} it is changed to -1 instead.
   */
  int getHashCode ();
}
