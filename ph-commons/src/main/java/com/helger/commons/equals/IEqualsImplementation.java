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
package com.helger.commons.equals;

import javax.annotation.Nonnull;

/**
 * Interface to implement for checking if two objects are identical. This
 * interface is only used within the {@link EqualsImplementationRegistry}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IEqualsImplementation
{
  /**
   * Check if the passed two objects are identical or not.
   *
   * @param aObj1
   *        First object. May not be <code>null</code>.
   * @param aObj2
   *        Second object. May not be <code>null</code>.
   * @return <code>true</code> if the passed objects are equals according to the
   *         specification of Object.class.
   */
  boolean areEqual (@Nonnull Object aObj1, @Nonnull Object aObj2);
}
