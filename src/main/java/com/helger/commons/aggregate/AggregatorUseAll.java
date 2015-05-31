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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Always use the complete list of results.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to aggregate
 */
@Immutable
public class AggregatorUseAll <DATATYPE> extends AbstractAggregator <DATATYPE, Collection <DATATYPE>>
{
  @Nullable
  public Collection <DATATYPE> aggregate (@Nullable final Collection <DATATYPE> aResults)
  {
    return aResults;
  }
}
