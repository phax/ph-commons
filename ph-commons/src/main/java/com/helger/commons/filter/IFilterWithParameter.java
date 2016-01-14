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
package com.helger.commons.filter;

import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 * A generic filter interface for simple object selection based on a parameter.
 * If you don't need the parameter, use {@link IFilter} instead. This is
 * basically a serializable {@link BiPredicate}.
 *
 * @author Philip
 * @param <DATATYPE>
 *        The type of object to filter.
 * @param <PARAMTYPE>
 *        The type of the parameter
 */
@FunctionalInterface
public interface IFilterWithParameter <DATATYPE, PARAMTYPE> extends BiPredicate <DATATYPE, PARAMTYPE>, Serializable
{
  /* empty */
}
