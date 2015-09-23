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
package com.helger.commons.functional;

/**
 * Represents a function that accepts one argument and produces a result.<br>
 * This method is comparable to Java 8 <code>java.util.function.Function</code>
 * and works as a placeholder.
 *
 * @author Philip Helger
 * @param <T>
 *        the type of the input to the function
 * @param <R>
 *        the type of the result of the function
 * @since 6.2.1
 */
public interface IFunction <T, R>
{
  /**
   * Applies this function to the given argument.
   *
   * @param t
   *        the function argument
   * @return the function result
   */
  R apply (T t);
}
