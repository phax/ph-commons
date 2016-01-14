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
 * A simple interface that looks like {@link java.lang.Runnable} but may throw
 * an exception on its execution.<br>
 * Note: It is not possible to extend {@link java.lang.Runnable} directly, as
 * derived interfaces are not allowed to add exception specifications.
 *
 * @author Philip Helger
 * @param <EXTYPE>
 *        Exception type to be thrown
 */
@FunctionalInterface
public interface IThrowingRunnable <EXTYPE extends Throwable>
{
  /**
   * Run it.
   *
   * @throws EXTYPE
   *         In case something goes wrong.
   */
  void run () throws EXTYPE;
}
