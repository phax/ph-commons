package com.helger.commons.xml.serialize.write;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of {@link IXMLBracketModeDeterminator} for real XML (and
 * XHTML). Only the presence of children determine whether "open close" or
 * "self closed" is used.
 *
 * @author Philip Helger
 */
public class XMLBracketModeDeterminatorXML implements IXMLBracketModeDeterminator
{
  @Nonnull
  public EXMLSerializeBracketMode getBracketMode (@Nullable final String sNamespacePrefix,
                                                  @Nonnull final String sTagName,
                                                  @Nullable final Map <QName, String> aAttrs,
                                                  final boolean bHasChildren)
  {
    if (bHasChildren)
      return EXMLSerializeBracketMode.OPEN_CLOSE;
    return EXMLSerializeBracketMode.SELF_CLOSED;
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
