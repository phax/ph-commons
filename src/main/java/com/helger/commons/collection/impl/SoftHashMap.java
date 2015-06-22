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
package com.helger.commons.collection.impl;

import java.util.HashMap;

/**
 * Soft {@link HashMap} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br />
 * The entrySet implementation is from org.hypergraphdb.util
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public class SoftHashMap <K, V> extends AbstractSoftMap <K, V>
{
  public SoftHashMap ()
  {
    super (new HashMap <K, SoftValue <K, V>> ());
  }
}
