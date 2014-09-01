/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Utility class for accessing information specific to HTML documents. The HTML
 * DTD is expressed as three utility function groups. Two methods allow for
 * checking whether an element requires an open tag on printing (
 * {@link #isEmptyTag}) or on parsing ({@link #isOptionalClosing}).
 * <P>
 * Two other methods translate character references from name to value and from
 * value to name. A small entities resource is loaded into memory the first time
 * any of these methods is called for fast and efficient access.
 * 
 * @version $Revision: 699892 $ $Date: 2008-09-28 17:08:27 -0400 (Sun, 28 Sep
 *          2008) $
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 */
public final class HTMLdtd
{
  /**
   * Holds element definitions.
   */
  private static final Map <String, Integer> s_aElemDefs = new HashMap <String, Integer> ();

  private static final Map <String, String []> s_aBoolAttrs = new HashMap <String, String []> ();

  /**
   * Only opening tag should be printed.
   */
  private static final int ONLY_OPENING = 0x0001;

  /**
   * Element contains element content only.
   */
  private static final int ELEM_CONTENT = 0x0002;

  /**
   * Element preserve spaces.
   */
  private static final int PRESERVE = 0x0004;

  /**
   * Optional closing tag.
   */
  private static final int OPT_CLOSING = 0x0008;

  /**
   * Element is empty (also means only opening tag)
   */
  private static final int EMPTY = 0x0010 | ONLY_OPENING;

  /**
   * Allowed to appear in head.
   */
  private static final int ALLOWED_HEAD = 0x0020;

  /**
   * When opened, closes P.
   */
  private static final int CLOSE_P = 0x0040;

  /**
   * When opened, closes DD or DT.
   */
  private static final int CLOSE_DD_DT = 0x0080;

  /**
   * When opened, closes itself.
   */
  private static final int CLOSE_SELF = 0x0100;

  /**
   * When opened, closes another table section.
   */
  private static final int CLOSE_TABLE = 0x0200;

  /**
   * When opened, closes TH or TD.
   */
  private static final int CLOSE_TH_TD = 0x04000;

  private HTMLdtd ()
  {}

  private static boolean _isElement (@Nonnull final String sTagName, final int nFlag)
  {
    final Integer aFlags = s_aElemDefs.get (sTagName.toUpperCase (Locale.US));
    return aFlags != null && ((aFlags.intValue () & nFlag) == nFlag);
  }

  /**
   * Returns true if element is declared to be empty. HTML elements are defines
   * as empty in the DTD, not by the document syntax.
   * 
   * @param sTagName
   *        The element tag name (upper case)
   * @return True if element is empty
   */
  public static boolean isEmptyTag (@Nonnull final String sTagName)
  {
    return _isElement (sTagName, EMPTY);
  }

  /**
   * Returns true if element is declared to have element content. Whitespaces
   * appearing inside element content will be ignored, other text will simply
   * report an error.
   * 
   * @param sTagName
   *        The element tag name (upper case)
   * @return True if element content
   */
  public static boolean isElementContent (@Nonnull final String sTagName)
  {
    return _isElement (sTagName, ELEM_CONTENT);
  }

  /**
   * Returns true if element's textual contents preserves spaces. This only
   * applies to PRE and TEXTAREA, all other HTML elements do not preserve space.
   * 
   * @param sTagName
   *        The element tag name (upper case)
   * @return True if element's text content preserves spaces
   */
  public static boolean isPreserveSpace (@Nonnull final String sTagName)
  {
    return _isElement (sTagName, PRESERVE);
  }

  /**
   * Returns true if element's closing tag is optional and need not exist. An
   * error will not be reported for such elements if they are not closed. For
   * example, <tt>LI</tt> is most often not closed.
   * 
   * @param sTagName
   *        The element tag name (upper case)
   * @return True if closing tag implied
   */
  public static boolean isOptionalClosing (@Nonnull final String sTagName)
  {
    return _isElement (sTagName, OPT_CLOSING);
  }

  /**
   * Returns true if element's closing tag is generally not printed. For
   * example, <tt>LI</tt> should not print the closing tag.
   * 
   * @param sTagName
   *        The element tag name (upper case)
   * @return True if only opening tag should be printed
   */
  public static boolean isOnlyOpening (@Nonnull final String sTagName)
  {
    return _isElement (sTagName, ONLY_OPENING);
  }

  /**
   * Returns true if the opening of one element (<tt>sTagName</tt>) implies the
   * closing of another open element (<tt>openTag</tt>). For example, every
   * opening <tt>LI</tt> will close the previously open <tt>LI</tt>, and every
   * opening <tt>BODY</tt> will close the previously open <tt>HEAD</tt>.
   * 
   * @param sTagName
   *        The newly opened element
   * @param sOpenTagName
   *        The already opened element
   * @return True if closing tag closes opening tag
   */
  public static boolean isClosing (@Nonnull final String sTagName, @Nonnull final String sOpenTagName)
  {
    // Several elements are defined as closing the HEAD
    if (sOpenTagName.equalsIgnoreCase ("HEAD"))
      return !_isElement (sTagName, ALLOWED_HEAD);
    // P closes iteself
    if (sOpenTagName.equalsIgnoreCase ("P"))
      return _isElement (sTagName, CLOSE_P);
    // DT closes DD, DD closes DT
    if (sOpenTagName.equalsIgnoreCase ("DT") || sOpenTagName.equalsIgnoreCase ("DD"))
      return _isElement (sTagName, CLOSE_DD_DT);
    // LI and OPTION close themselves
    if (sOpenTagName.equalsIgnoreCase ("LI") || sOpenTagName.equalsIgnoreCase ("OPTION"))
      return _isElement (sTagName, CLOSE_SELF);
    // Each of these table sections closes all the others
    if (sOpenTagName.equalsIgnoreCase ("THEAD") ||
        sOpenTagName.equalsIgnoreCase ("TFOOT") ||
        sOpenTagName.equalsIgnoreCase ("TBODY") ||
        sOpenTagName.equalsIgnoreCase ("TR") ||
        sOpenTagName.equalsIgnoreCase ("COLGROUP"))
      return _isElement (sTagName, CLOSE_TABLE);
    // TD closes TH and TH closes TD
    if (sOpenTagName.equalsIgnoreCase ("TH") || sOpenTagName.equalsIgnoreCase ("TD"))
      return _isElement (sTagName, CLOSE_TH_TD);
    return false;
  }

  /**
   * Returns true if the specified attribute it a URI and should be escaped
   * appropriately. In HTML URIs are escaped differently than normal attributes.
   * 
   * @param sTagName
   *        The element's tag name
   * @param sAttrName
   *        The attribute's name
   * @return <code>true</code> if the passed combination is an URI attribute
   */
  public static boolean isURI (@Nonnull final String sTagName, @Nonnull final String sAttrName)
  {
    // Stupid checks.
    return sAttrName.equalsIgnoreCase ("href") || sAttrName.equalsIgnoreCase ("src");
  }

  /**
   * Returns true if the specified attribute is a boolean and should be printed
   * without the value. This applies to attributes that are true if they exist,
   * such as selected (OPTION/INPUT).
   * 
   * @param sTagName
   *        The element's tag name
   * @param sAttrName
   *        The attribute's name
   * @return <code>true</code> if the passed combination is a boolean value
   */
  public static boolean isBoolean (@Nonnull final String sTagName, @Nonnull final String sAttrName)
  {
    final String [] aAttrNames = s_aBoolAttrs.get (sTagName.toUpperCase (Locale.US));
    if (aAttrNames != null)
      for (final String sCurAttrName : aAttrNames)
        if (sCurAttrName.equalsIgnoreCase (sAttrName))
          return true;
    return false;
  }

  private static void _defineElement (@Nonnull final String name, final int nFlags)
  {
    s_aElemDefs.put (name, Integer.valueOf (nFlags));
  }

  private static void _defineBoolean (@Nonnull final String sTagName, @Nonnull final String... aAttrNames)
  {
    s_aBoolAttrs.put (sTagName, aAttrNames);
  }

  static
  {
    _defineElement ("ADDRESS", CLOSE_P);
    _defineElement ("AREA", EMPTY);
    _defineElement ("BASE", EMPTY | ALLOWED_HEAD);
    _defineElement ("BASEFONT", EMPTY);
    _defineElement ("BLOCKQUOTE", CLOSE_P);
    _defineElement ("BODY", OPT_CLOSING);
    _defineElement ("BR", EMPTY);
    _defineElement ("COL", EMPTY);
    _defineElement ("COLGROUP", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE);
    _defineElement ("DD", OPT_CLOSING | ONLY_OPENING | CLOSE_DD_DT);
    _defineElement ("DIV", CLOSE_P);
    _defineElement ("DL", ELEM_CONTENT | CLOSE_P);
    _defineElement ("DT", OPT_CLOSING | ONLY_OPENING | CLOSE_DD_DT);
    _defineElement ("FIELDSET", CLOSE_P);
    _defineElement ("FORM", CLOSE_P);
    _defineElement ("FRAME", EMPTY | OPT_CLOSING);
    _defineElement ("H1", CLOSE_P);
    _defineElement ("H2", CLOSE_P);
    _defineElement ("H3", CLOSE_P);
    _defineElement ("H4", CLOSE_P);
    _defineElement ("H5", CLOSE_P);
    _defineElement ("H6", CLOSE_P);
    _defineElement ("HEAD", ELEM_CONTENT | OPT_CLOSING);
    _defineElement ("HR", EMPTY | CLOSE_P);
    _defineElement ("HTML", ELEM_CONTENT | OPT_CLOSING);
    _defineElement ("IMG", EMPTY);
    _defineElement ("INPUT", EMPTY);
    _defineElement ("ISINDEX", EMPTY | ALLOWED_HEAD);
    _defineElement ("LI", OPT_CLOSING | ONLY_OPENING | CLOSE_SELF);
    _defineElement ("LINK", EMPTY | ALLOWED_HEAD);
    _defineElement ("MAP", ALLOWED_HEAD);
    _defineElement ("META", EMPTY | ALLOWED_HEAD);
    _defineElement ("OL", ELEM_CONTENT | CLOSE_P);
    _defineElement ("OPTGROUP", ELEM_CONTENT);
    _defineElement ("OPTION", OPT_CLOSING | ONLY_OPENING | CLOSE_SELF);
    _defineElement ("P", OPT_CLOSING | CLOSE_P | CLOSE_SELF);
    _defineElement ("PARAM", EMPTY);
    _defineElement ("PRE", PRESERVE | CLOSE_P);
    _defineElement ("SCRIPT", ALLOWED_HEAD | PRESERVE);
    _defineElement ("NOSCRIPT", ALLOWED_HEAD | PRESERVE);
    _defineElement ("SELECT", ELEM_CONTENT);
    _defineElement ("STYLE", ALLOWED_HEAD | PRESERVE);
    _defineElement ("TABLE", ELEM_CONTENT | CLOSE_P);
    _defineElement ("TBODY", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE);
    _defineElement ("TD", OPT_CLOSING | CLOSE_TH_TD);
    _defineElement ("TEXTAREA", PRESERVE);
    _defineElement ("TFOOT", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE);
    _defineElement ("TH", OPT_CLOSING | CLOSE_TH_TD);
    _defineElement ("THEAD", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE);
    _defineElement ("TITLE", ALLOWED_HEAD);
    _defineElement ("TR", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE);
    _defineElement ("UL", ELEM_CONTENT | CLOSE_P);

    _defineBoolean ("AREA", "href");
    _defineBoolean ("BUTTON", "disabled");
    _defineBoolean ("DIR", "compact");
    _defineBoolean ("DL", "compact");
    _defineBoolean ("FRAME", "noresize");
    _defineBoolean ("HR", "noshade");
    _defineBoolean ("IMAGE", "ismap");
    _defineBoolean ("INPUT", "defaultchecked", "checked", "readonly", "disabled");
    _defineBoolean ("LINK", "link");
    _defineBoolean ("MENU", "compact");
    _defineBoolean ("OBJECT", "declare");
    _defineBoolean ("OL", "compact");
    _defineBoolean ("OPTGROUP", "disabled");
    _defineBoolean ("OPTION", "default-selected", "selected", "disabled");
    _defineBoolean ("SCRIPT", "defer");
    _defineBoolean ("SELECT", "multiple", "disabled");
    _defineBoolean ("STYLE", "disabled");
    _defineBoolean ("TD", "nowrap");
    _defineBoolean ("TH", "nowrap");
    _defineBoolean ("TEXTAREA", "disabled", "readonly");
    _defineBoolean ("UL", "compact");
  }
}
