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
package com.helger.commons.combine;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.helger.commons.annotations.MustImplementEqualsAndHashcode;

/**
 * Base interface for combining any two objects together.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects to be combined.
 */
@MustImplementEqualsAndHashcode
public interface ICombinator <DATATYPE> extends Serializable
{
  /**
   * Combine the passed elements in the correct order.
   * 
   * @param aFirst
   *        The first element.
   * @param aSecond
   *        The second element.
   * @return The combination of first and second.
   */
  @Nullable
  DATATYPE combine (@Nullable DATATYPE aFirst, @Nullable DATATYPE aSecond);
}
