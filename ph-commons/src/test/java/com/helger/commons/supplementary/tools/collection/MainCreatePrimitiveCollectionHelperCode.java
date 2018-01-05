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
package com.helger.commons.supplementary.tools.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.PrimitiveCollectionHelper;

/**
 * Create the code for the {@link PrimitiveCollectionHelper} class.
 *
 * @author Philip Helger
 */
public final class MainCreatePrimitiveCollectionHelperCode
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainCreatePrimitiveCollectionHelperCode.class);

  private MainCreatePrimitiveCollectionHelperCode ()
  {}

  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ().append ("\n");
    for (final ECollectionType eCollectionType : ECollectionType.values ())
      if (!eCollectionType.isMap ())
        for (final EPrimitiveType ePrim : EPrimitiveType.values ())
        {
          // Collection<Primitive> newPrimitiveCollection (Primitive...)
          aSB.append ("@Nonnull\n")
             .append ("@ReturnsMutableCopy\n")
             .append ("public static ")
             .append (eCollectionType.m_sClassName)
             .append (" <")
             .append (ePrim.m_sObjClass)
             .append ("> newPrimitive")
             .append (eCollectionType.m_sSuffix)
             .append ("(@Nullable final ")
             .append (ePrim.m_sPrimitiveClass)
             .append ("... aValues)\n")
             .append ("{\n")
             .append ("final ")
             .append (eCollectionType.m_sClassName)
             .append (" <")
             .append (ePrim.m_sObjClass)
             .append ("> ret = CollectionHelper.new")
             .append (eCollectionType.m_sSuffix)
             .append (" ();\n")
             .append ("if (aValues != null)\n")
             .append ("for (final ")
             .append (ePrim.m_sPrimitiveClass)
             .append (" aValue : aValues)\n")
             .append ("ret.add (")
             .append (ePrim.m_sObjClass)
             .append (".valueOf (aValue));\n")
             .append ("return ret;\n")
             .append ("}\n");

          // Collection<Primitive> newUnmodifiablePrimitiveCollection
          // (Primitive...)
          if (false)
            aSB.append ("@Nonnull\n")
               .append ("@ReturnsImmutableObject\n")
               .append ("public static ")
               .append (eCollectionType.m_sUnmodifiableClassName)
               .append (" <")
               .append (ePrim.m_sObjClass)
               .append ("> newUnmodifiablePrimitive")
               .append (eCollectionType.m_sSuffix)
               .append ("(@Nullable final ")
               .append (ePrim.m_sPrimitiveClass)
               .append ("... aValues)\n")
               .append ("{\n")
               .append ("return CollectionHelper.makeUnmodifiable (newPrimitive")
               .append (eCollectionType.m_sSuffix)
               .append (" (aValues));\n")
               .append ("}\n");
        }

    s_aLogger.info (aSB.toString ());
  }
}
