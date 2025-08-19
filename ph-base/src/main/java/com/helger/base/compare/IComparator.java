/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.compare;

import java.io.Serializable;
import java.util.Comparator;

import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * A special interface that combines {@link Comparator} and {@link Serializable} for easier reuse
 * since {@link Comparator}s should be Serializable.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 */
@NotThreadSafe
@FunctionalInterface
public interface IComparator <DATATYPE> extends Comparator <DATATYPE>, Serializable
{
  /* empty */
}
