/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.schema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

import com.helger.commons.xml.ls.SimpleLSResourceResolver;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;

/**
 * This class is used to cache XML schema objects.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public class XMLSchemaCache extends DefaultSchemaCache
{
  private static final class SingletonHolder
  {
    static final XMLSchemaCache s_aInstance = new XMLSchemaCache (LoggingSAXErrorHandler.getInstance (),
                                                                  new SimpleLSResourceResolver ());
  }

  private static boolean s_bDefaultInstantiated = false;

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler)
  {
    this (aErrorHandler, null);
  }

  public XMLSchemaCache (@Nullable final LSResourceResolver aResourceResolver)
  {
    this (null, aResourceResolver);
  }

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    super ("XSD", SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI), aErrorHandler, aResourceResolver);
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static XMLSchemaCache getInstance ()
  {
    s_bDefaultInstantiated = true;
    return SingletonHolder.s_aInstance;
  }
}
