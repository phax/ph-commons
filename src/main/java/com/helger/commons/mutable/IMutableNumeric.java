/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.mutable;


/**
 * Base interface for mutable numeric values
 * 
 * @author Philip Helger
 */
public interface IMutableNumeric <IMPLTYPE extends IMutableNumeric <IMPLTYPE>> extends IMutable <IMPLTYPE>
{
  /**
   * @return <code>true</code> if the value is 0
   */
  boolean is0 ();

  /**
   * @return <code>true</code> if the value is not 0
   */
  boolean isNot0 ();

  /**
   * @return <code>true</code> if the value is &lt; 0
   */
  boolean isSmaller0 ();

  /**
   * @return <code>true</code> if the value is &le; 0
   */
  boolean isSmallerOrEqual0 ();

  /**
   * @return <code>true</code> if the value is &gt; 0
   */
  boolean isGreater0 ();

  /**
   * @return <code>true</code> if the value is &ge; 0
   */
  boolean isGreaterOrEqual0 ();
}
