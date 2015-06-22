package com.helger.commons.xml.serialize.write;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of {@link IXMLBracketModeDeterminator} for HTML5. This is not
 * applicable for XHTML or HTML4.
 *
 * @author Philip Helger
 */
public class XMLBracketModeDeterminatorHTML5 implements IXMLBracketModeDeterminator
{
  // Source: http://www.w3.org/TR/html-markup/syntax.html#void-element
  private static final Set <String> VOID_ELEMENTS = CollectionHelper.newSet ("AREA",
                                                                             "BASE",
                                                                             "BR",
                                                                             "COL",
                                                                             "COMMAND",
                                                                             "EMBED",
                                                                             "HR",
                                                                             "IMG",
                                                                             "INPUT",
                                                                             "KEYGEN",
                                                                             "LINK",
                                                                             "META",
                                                                             "PARAM",
                                                                             "SOURCE",
                                                                             "TRACKE",
                                                                             "WBR");

  private static boolean _isVoidElement (@Nonnull final String sTagName)
  {
    return VOID_ELEMENTS.contains (sTagName.toUpperCase (Locale.US));
  }

  @Nonnull
  public EXMLSerializeBracketMode getBracketMode (@Nullable final String sNamespacePrefix,
                                                  @Nonnull final String sTagName,
                                                  @Nullable final Map <QName, String> aAttrs,
                                                  final boolean bHasChildren)
  {
    // In HTML all tags are closed, if not explicitly marked as empty
    if (!bHasChildren && _isVoidElement (sTagName))
      return EXMLSerializeBracketMode.OPEN_ONLY;

    return EXMLSerializeBracketMode.OPEN_CLOSE;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
