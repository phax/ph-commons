package com.helger.commons.supplementary.tools.collection;

import com.helger.commons.collection.PrimitiveCollectionHelper;

/**
 * Create the code for the {@link PrimitiveCollectionHelper} class.
 *
 * @author Philip Helger
 */
public class MainCreatePrimitiveCollectionHelperCode
{
  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ();
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

    System.out.println (aSB.toString ());
  }
}
