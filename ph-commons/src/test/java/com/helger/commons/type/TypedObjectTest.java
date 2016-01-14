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
package com.helger.commons.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link TypedObject}.
 *
 * @author Philip Helger
 */
public final class TypedObjectTest
{
  @Test
  public void testAll () throws Exception
  {
    final ObjectType ot1 = new ObjectType ("type1");
    final ObjectType ot2 = new ObjectType ("type2");
    final TypedObject <String> t1 = TypedObject.create (ot1, "id1");
    assertSame (ot1, t1.getObjectType ());
    assertEquals ("id1", t1.getID ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t1, TypedObject.create (ot1, "id1"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t1, TypedObject.create (t1));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t1, TypedObject.create (ot1, "id2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t1, TypedObject.create (ot2, "id1"));

    // Serialization
    CommonsTestHelper.testDefaultSerialization (t1);
  }
}
