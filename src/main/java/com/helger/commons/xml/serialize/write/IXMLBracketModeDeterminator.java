package com.helger.commons.xml.serialize.write;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

/**
 * Interface used in writing XML to determine which of the bracket strategies as
 * outlined in {@link EXMLSerializeBracketMode} should be used for a single
 * element.
 * 
 * @author Philip Helger
 */
public interface IXMLBracketModeDeterminator
{
  /**
   * Determine the bracket mode for an XML element.
   *
   * @param sNamespacePrefix
   *        Optional namespace prefix. May be <code>null</code>.
   * @param sTagName
   *        Tag name
   * @param aAttrs
   *        Optional set of attributes.
   * @param bHasChildren
   *        <code>true</code> if the current element has children
   * @return The bracket mode to be used. May not be <code>null</code>.
   */
  @Nonnull
  EXMLSerializeBracketMode getBracketMode (@Nullable String sNamespacePrefix,
                                           @Nonnull String sTagName,
                                           @Nullable Map <QName, String> aAttrs,
                                           boolean bHasChildren);
}
