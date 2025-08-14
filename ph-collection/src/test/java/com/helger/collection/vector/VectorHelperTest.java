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
package com.helger.collection.vector;

import java.util.Objects;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsIterableIterator;

/**
 * Test class for class {@link VectorHelper}
 *
 * @author Philip Helger
 */
public final class VectorHelperTest
{
  @Test
  public void testNew ()
  {
    VectorHelper.newVector ();
    VectorHelper.newVector ("a");
    VectorHelper.newVector ("a");
    VectorHelper.newVector (new CommonsArrayList <> ("a"));
    VectorHelper.newVector (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    VectorHelper.newVector ((Iterable <String>) new CommonsArrayList <> ("a"));
    VectorHelper.newVector (new CommonsArrayList <> ("a").iterator ());
    VectorHelper.newVector (new CommonsArrayList <> ("a"), Objects::nonNull);
    VectorHelper.newVectorMapped (new CommonsArrayList <> ("a"), Object::toString);
    VectorHelper.newVectorMapped (new Object [] { "a" }, Object::toString);
  }
}
