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
package com.helger.commons.filter;

/**
 * Filter matching strategy for {@link IFilter} implementations based on
 * {@link AbstractFilter}.
 *
 * @author Philip Helger
 */
public enum EFilterMatchingStrategy
{
  /**
   * Match either this filter or an eventually present nested filter. If no
   * nested filter is present, only this filter must match.
   */
  MATCH_ANY,
  /**
   * Math both this filter and an eventually present nested filter. If no nested
   * filter is present, only this filter must match.
   */
  MATCH_ALL;
}
