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
package com.helger.commons.supplementary.tools.collection;

import com.helger.base.lang.clazz.ClassHelper;

import jakarta.annotation.Nonnull;

enum EPrimitiveType
{
  BOOLEAN (boolean.class, Boolean.class),
  BYTE (byte.class, Byte.class),
  CHAR (char.class, Character.class),
  DOUBLE (double.class, Double.class),
  FLOAT (float.class, Float.class),
  INT (int.class, Integer.class),
  LONG (long.class, Long.class),
  SHORT (short.class, Short.class);

  final String m_sPrimitiveClass;
  final String m_sObjClass;

  EPrimitiveType (@Nonnull final Class <?> aPrimitiveClass, @Nonnull final Class <?> aObjClass)
  {
    m_sPrimitiveClass = ClassHelper.getClassLocalName (aPrimitiveClass);
    m_sObjClass = ClassHelper.getClassLocalName (aObjClass);
  }
}
