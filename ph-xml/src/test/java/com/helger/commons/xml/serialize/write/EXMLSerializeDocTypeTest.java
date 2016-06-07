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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EXMLSerializeDocType}.
 *
 * @author Philip Helger
 */
public final class EXMLSerializeDocTypeTest
{
  @Test
  public void testAll ()
  {
    for (final EXMLSerializeDocType e : EXMLSerializeDocType.values ())
      assertSame (e, EXMLSerializeDocType.valueOf (e.name ()));
    assertTrue (EXMLSerializeDocType.EMIT.isEmit ());
    assertFalse (EXMLSerializeDocType.IGNORE.isEmit ());
  }
}
