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
package com.helger.collection.queue;

import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.enumeration.EnumerationHelper;
import com.helger.collection.helper.CollectionEqualsHelper;

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
    QueueHelper.newQueue ("a");
    QueueHelper.newQueue (new CommonsArrayList <> ("a"));
    QueueHelper.newQueue (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    QueueHelper.newQueue ((Iterable <String>) new CommonsArrayList <> ("a"));
    QueueHelper.newQueue (new CommonsArrayList <> ("a").iterator ());
    QueueHelper.newQueue (new CommonsArrayList <> ("a"), Objects::nonNull);
    QueueHelper.newQueueMapped (new CommonsArrayList <> ("a"), Object::toString);
    QueueHelper.newQueueMapped (new Object [] { "a" }, Object::toString);

    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, QueueHelper.newQueue (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));

    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (), QueueHelper.newQueue (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));

    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (QueueHelper.newQueue (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (QueueHelper.newQueue (aCont), QueueHelper.newQueue (aCont)));
  }
}
