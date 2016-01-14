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
package com.helger.commons.callback;

/**
 * Simple callback interface to allow generic iteration with a typed callback
 * function. This is similar to {@link INonThrowingRunnable} except that a
 * parameter is present.
 *
 * @author Philip Helger
 * @param <PARAMTYPE>
 *        The type of the parameter that is required for executing the callback.
 */
@FunctionalInterface
public interface INonThrowingRunnableWithParameter <PARAMTYPE>
                                                   extends IThrowingRunnableWithParameter <PARAMTYPE, Exception>
{
  /**
   * The callback method that is invoked.
   *
   * @param aCurrentObject
   *        The current object. May be <code>null</code> or non-
   *        <code>null</code> depending on the implementation.
   */
  void run (PARAMTYPE aCurrentObject);
}
