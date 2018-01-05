/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.collection.helper;

import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;

import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.commons.equals.EqualsHelper;

/**
 * Test class for class {@link QueueHelper}
 *
 * @author Philip Helger
 */
public final class QueueHelperTest
{
  @Test
  public void testNew ()
  {
    QueueHelper.newQueue ();
    QueueHelper.newQueue ("a");
    QueueHelper.newQueue (new String [] { "a" });
    QueueHelper.newQueue (new CommonsArrayList <> ("a"));
    QueueHelper.newQueue (new IterableIterator <> (new CommonsArrayList <> ("a")));
    QueueHelper.newQueue ((Iterable <String>) new CommonsArrayList <> ("a"));
    QueueHelper.newQueue (new CommonsArrayList <> ("a").iterator ());
    QueueHelper.newQueue (new CommonsArrayList <> ("a"), Objects::nonNull);
    QueueHelper.newQueueMapped (new CommonsArrayList <Object> ("a"), Object::toString);
    QueueHelper.newQueueMapped (new Object [] { "a" }, Object::toString);

    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aCont, QueueHelper.newQueue (aCont)));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));

    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), QueueHelper.newQueue (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));

    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (QueueHelper.newQueue (aCont))));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (EqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));
  }
}
