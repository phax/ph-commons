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
package com.helger.commons.math;

import java.util.List;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test base class for combinator testing
 *
 * @author Philip Helger
 */
public abstract class AbstractCombinationGeneratorTestCase extends AbstractCommonsTestCase
{
  protected static final List <String> HUGE_LIST = CollectionHelper.newUnmodifiableList ("a",
                                                                                         "b",
                                                                                         "c",
                                                                                         "d",
                                                                                         "e",
                                                                                         "f",
                                                                                         "g",
                                                                                         "h",
                                                                                         "i",
                                                                                         "j",
                                                                                         "k",
                                                                                         "l",
                                                                                         "m",
                                                                                         "a",
                                                                                         "b",
                                                                                         "c",
                                                                                         "d",
                                                                                         "e",
                                                                                         "f",
                                                                                         "g",
                                                                                         "h",
                                                                                         "i",
                                                                                         "j",
                                                                                         "k",
                                                                                         "l",
                                                                                         "m",
                                                                                         "n",
                                                                                         "o",
                                                                                         "p",
                                                                                         "q");
}
