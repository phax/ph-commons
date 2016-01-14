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
package com.helger.commons.xml.dom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents different DOM features.<br>
 * See http://www.w3.org/TR/DOM-Level-3-Core/core.html#DOMFeatures
 *
 * @author Philip Helger
 */
public enum EXMLDOMFeature implements IHasID <String>
{
  DOM_FEATURE_CORE ("Core"),
  DOM_FEATURE_XML ("XML"),
  DOM_FEATURE_EVENTS ("Events"),
  DOM_FEATURE_UI_EVENTS ("UIEvents"),
  DOM_FEATURE_MOUSE_EVENTS ("MouseEvents"),
  DOM_FEATURE_TEXT_EVENTS ("TextEvents"),
  DOM_FEATURE_KEYBOARD_EVENTS ("KeyboardEvents"),
  DOM_FEATURE_MUTATION_EVENTS ("MutationEvents"),
  DOM_FEATURE_MUTATION_NAME_EVENTS ("MutationNameEvents"),
  DOM_FEATURE_HTML_EVENTS ("HTMLEvents"),
  DOM_FEATURE_LS ("LS"),
  DOM_FEATURE_LS_ASYNC ("LS-Async"),
  DOM_FEATURE_VALIDATION ("Validation"),
  DOM_FEATURE_XPATH ("XPath");

  private final String m_sID;

  private EXMLDOMFeature (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getPlusFeature ()
  {
    return '+' + m_sID;
  }

  @Nullable
  public static EXMLDOMFeature getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLDOMFeature.class, sID);
  }
}
