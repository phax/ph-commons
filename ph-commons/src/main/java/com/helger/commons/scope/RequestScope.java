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
package com.helger.commons.scope;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation for non-web request scopes.
 *
 * @author Philip Helger
 */
public class RequestScope extends AbstractMapBasedScope implements IRequestScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestScope.class);

  private final String m_sSessionID;

  public RequestScope (@Nonnull @Nonempty final String sScopeID, @Nonnull @Nonempty final String sSessionID)
  {
    super (sScopeID);
    m_sSessionID = ValueEnforcer.notEmpty (sSessionID, "SessionID");

    // done initialization
    if (ScopeHelper.debugRequestScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created request scope '" +
                      sScopeID +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Nonnull
  @Nonempty
  public final String getSessionID (final boolean bCreateIfNotExisting)
  {
    return m_sSessionID;
  }

  public void initScope ()
  {}

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.debugRequestScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroying request scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.debugRequestScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed request scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  /**
   * Try to convert the passed value into a {@link List} of {@link String}. This
   * method is only called, if the passed value is non-<code>null</code>, if it
   * is not an String array or a single String.
   *
   * @param sName
   *        The name of the parameter to be queried. Just for informational
   *        purposes.
   * @param aValue
   *        The retrieved non-<code>null</code> attribute value which is neither
   *        a String nor a String array.
   * @param aDefault
   *        The default value to be returned, in case no type conversion could
   *        be found.
   * @return The converted value or the default value.
   */
  @Nullable
  @OverrideOnDemand
  protected List <String> getAttributeAsListCustom (@Nullable final String sName,
                                                    @Nonnull final Object aValue,
                                                    @Nullable final List <String> aDefault)
  {
    return aDefault;
  }

  @Nullable
  public List <String> getAttributeAsList (@Nullable final String sName, @Nullable final List <String> aDefault)
  {
    final Object aValue = getAttributeObject (sName);
    if (aValue == null)
      return null;
    if (aValue instanceof String [])
    {
      // multiple values passed in the request
      return CollectionHelper.newList ((String []) aValue);
    }
    if (aValue instanceof String)
    {
      // single value passed in the request
      return CollectionHelper.newList ((String) aValue);
    }
    return getAttributeAsListCustom (sName, aValue, aDefault);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("sessionID", m_sSessionID).toString ();
  }
}
