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
package com.helger.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.iterator.IterableIterator;
import com.helger.collection.stack.NonBlockingStack;
import com.helger.collection.stack.StackHelper;

/**
 * Test class for class {@link StackHelper}
 *
 * @author Philip Helger
 */
public final class StackHelperTest
{
  @Test
  public void testNewStackArray ()
  {
    final NonBlockingStack <String> aStack = StackHelper.newStack ("Hallo", "Welt");
    assertEquals (StackHelper.getStackCopyWithoutTop (aStack), StackHelper.newStack ("Hallo"));
    assertNotNull (aStack);
    assertEquals (2, aStack.size ());
    assertTrue (aStack.contains ("Welt"));
    assertTrue (aStack.contains ("Hallo"));
    assertEquals ("Welt", aStack.peek ());
    assertEquals ("Welt", aStack.pop ());
    assertEquals ("Hallo", aStack.peek ());
    assertEquals ("Hallo", aStack.pop ());
    assertTrue (aStack.isEmpty ());

    assertNull (StackHelper.getStackCopyWithoutTop (new NonBlockingStack <> ()));
  }

  @Test
  public void testNew ()
  {
    StackHelper.newStack ();
    StackHelper.newStack ("a");
    StackHelper.newStack ("a");
    StackHelper.newStack (new CommonsArrayList <> ("a"));
    StackHelper.newStack (new IterableIterator <> (new CommonsArrayList <> ("a")));
    StackHelper.newStack ((Iterable <String>) new CommonsArrayList <> ("a"));
    StackHelper.newStack (new CommonsArrayList <> ("a").iterator ());
    StackHelper.newStack (new CommonsArrayList <> ("a"), Objects::nonNull);
    StackHelper.newStackMapped (new CommonsArrayList <> ("a"), Object::toString);
    StackHelper.newStackMapped (new Object [] { "a" }, Object::toString);
  }

  @Test
  public void testNewStackSingleValue ()
  {
    NonBlockingStack <String> aStack = StackHelper.newStack ();
    assertNotNull (aStack);
    aStack = StackHelper.newStack ("Hallo");
    assertNotNull (aStack);
    assertEquals (1, aStack.size ());
    assertTrue (aStack.contains ("Hallo"));
    assertEquals ("Hallo", aStack.peek ());
    assertEquals ("Hallo", aStack.pop ());
  }
}
