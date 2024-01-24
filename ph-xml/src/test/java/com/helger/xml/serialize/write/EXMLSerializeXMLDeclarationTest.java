/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EXMLSerializeXMLDeclaration}.
 *
 * @author Philip Helger
 */
public final class EXMLSerializeXMLDeclarationTest
{
  @Test
  public void testAll ()
  {
    for (final EXMLSerializeXMLDeclaration e : EXMLSerializeXMLDeclaration.values ())
      assertSame (e, EXMLSerializeXMLDeclaration.valueOf (e.name ()));

    assertTrue (EXMLSerializeXMLDeclaration.EMIT.isEmit ());
    assertTrue (EXMLSerializeXMLDeclaration.EMIT_NO_STANDALONE.isEmit ());
    assertFalse (EXMLSerializeXMLDeclaration.IGNORE.isEmit ());

    assertTrue (EXMLSerializeXMLDeclaration.EMIT.isEmitStandalone ());
    assertFalse (EXMLSerializeXMLDeclaration.EMIT_NO_STANDALONE.isEmitStandalone ());
    assertFalse (EXMLSerializeXMLDeclaration.IGNORE.isEmitStandalone ());
  }
}
