package com.helger.commons.supplementary.tools.collection;

import javax.annotation.Nonnull;

import com.helger.commons.lang.ClassHelper;

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

  private EPrimitiveType (@Nonnull final Class <?> aPrimitiveClass, @Nonnull final Class <?> aObjClass)
  {
    m_sPrimitiveClass = ClassHelper.getClassLocalName (aPrimitiveClass);
    m_sObjClass = ClassHelper.getClassLocalName (aObjClass);
  }
}