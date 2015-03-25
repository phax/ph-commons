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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ELockType;
import com.helger.commons.annotations.IsLocked;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.cache.AbstractNotifyingCache;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.transform.TransformSourceFactory;

/**
 * Abstract base class for caching abstract {@link Schema} objects. A
 * {@link Schema} is immutable and can therefore safely be used in
 * multi-threaded environments.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class DefaultSchemaCache extends AbstractNotifyingCache <List <? extends IReadableResource>, Schema>
{
  private final String m_sSchemaTypeName;
  private final SchemaFactory m_aSchemaFactory;
  private final ErrorHandler m_aErrorHandler;

  public DefaultSchemaCache (@Nonnull final String sSchemaTypeName,
                             @Nonnull final SchemaFactory aSchemaFactory,
                             @Nullable final ErrorHandler aErrorHandler,
                             @Nullable final LSResourceResolver aResourceResolver)
  {
    super (DefaultSchemaCache.class.getName () + "$" + sSchemaTypeName);
    ValueEnforcer.notNull (sSchemaTypeName, "SchemaTypeName");
    ValueEnforcer.notNull (aSchemaFactory, "SchemaFactory");
    m_sSchemaTypeName = sSchemaTypeName;
    m_aSchemaFactory = aSchemaFactory;
    m_aSchemaFactory.setErrorHandler (aErrorHandler);
    m_aSchemaFactory.setResourceResolver (aResourceResolver);
    m_aErrorHandler = aErrorHandler;
  }

  @Nonnull
  public String getSchemaTypeName ()
  {
    return m_sSchemaTypeName;
  }

  @Nonnull
  public SchemaFactory getSchemaFactory ()
  {
    return m_aSchemaFactory;
  }

  @Nullable
  public ErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  @Override
  @Nonnull
  @IsLocked (ELockType.WRITE)
  protected Schema getValueToCache (final List <? extends IReadableResource> aKey)
  {
    ValueEnforcer.notEmptyNoNullValue (aKey, "Resources");

    // Collect all sources
    final Source [] aSources = new Source [aKey.size ()];
    for (int i = 0; i < aKey.size (); ++i)
      aSources[i] = TransformSourceFactory.create (aKey.get (i));

    try
    {
      final Schema ret = m_aSchemaFactory.newSchema (aSources);
      if (ret == null)
        throw new IllegalStateException ("Failed to create " +
                                         m_sSchemaTypeName +
                                         " schema from " +
                                         Arrays.toString (aSources));
      return ret;
    }
    catch (final SAXException ex)
    {
      throw new IllegalArgumentException ("Failed to parse " +
                                          m_sSchemaTypeName +
                                          " from " +
                                          Arrays.toString (aSources), ex);
    }
  }

  /**
   * Get a cached {@link Schema} from a single resource.
   *
   * @param aResource
   *        The resource to parse into a {@link Schema}. May not be
   *        <code>null</code>.
   * @return Either the {@link Schema} from the cache or the newly compiled one.
   */
  @Nonnull
  public final Schema getSchema (@Nonnull final IReadableResource aResource)
  {
    ValueEnforcer.notNull (aResource, "Resource");

    return getFromCache (CollectionHelper.newList (aResource));
  }

  /**
   * Get a cached {@link Schema} that consists of multiple resources.
   *
   * @param aResources
   *        The resources to parse into a single {@link Schema}. May neither
   *        <code>null</code> nor empty nor may it contain <code>null</code>
   *        elements.
   * @return Either the {@link Schema} from the cache or the newly compiled one.
   */
  @Nonnull
  public final Schema getSchema (@Nonnull @Nonempty final IReadableResource... aResources)
  {
    ValueEnforcer.notEmptyNoNullValue (aResources, "Resources");

    return getFromCache (CollectionHelper.newList (aResources));
  }

  /**
   * Get a cached {@link Schema} that consists of multiple resources.
   *
   * @param aResources
   *        The resources to parse into a single {@link Schema}. May neither
   *        <code>null</code> nor empty nor may it contain <code>null</code>
   *        elements.
   * @return Either the {@link Schema} from the cache or the newly compiled one.
   */
  @Nonnull
  public final Schema getSchema (@Nonnull @Nonempty final Collection <? extends IReadableResource> aResources)
  {
    ValueEnforcer.notEmptyNoNullValue (aResources, "Resources");

    return getFromCache (CollectionHelper.newList (aResources));
  }

  /**
   * Utility method to get the validator for a given schema using the error
   * handler provided in the constructor.
   *
   * @param aSchema
   *        The schema for which the validator is to be retrieved. May not be
   *        <code>null</code>.
   * @return The validator and never <code>null</code>.
   */
  @Nonnull
  public final Validator getValidatorFromSchema (@Nonnull final Schema aSchema)
  {
    ValueEnforcer.notNull (aSchema, "Schema");

    final Validator aValidator = aSchema.newValidator ();
    aValidator.setErrorHandler (m_aErrorHandler);
    return aValidator;
  }

  /**
   * Get a new validator based on the {@link Schema} that consists of a single
   * resource.
   *
   * @param aResource
   *        The resource to parse into a single {@link Schema}. May not be
   *        <code>null</code>.
   * @return A new {@link Validator} object. Never <code>null</code>.
   * @see #getSchema(IReadableResource)
   */
  @Nonnull
  public final Validator getValidator (@Nonnull final IReadableResource aResource)
  {
    return getValidatorFromSchema (getSchema (aResource));
  }

  /**
   * Get a new validator based on the {@link Schema} that consists of multiple
   * resources.
   *
   * @param aResources
   *        The resources to parse into a single {@link Schema}. May neither
   *        <code>null</code> nor empty nor may it contain <code>null</code>
   *        elements.
   * @return A new {@link Validator} object. Never <code>null</code>.
   * @see #getSchema(IReadableResource...)
   */
  @Nonnull
  public final Validator getValidator (@Nonnull @Nonempty final IReadableResource... aResources)
  {
    return getValidatorFromSchema (getSchema (aResources));
  }

  /**
   * Get a new validator based on the {@link Schema} that consists of multiple
   * resources.
   *
   * @param aResources
   *        The resources to parse into a single {@link Schema}. May neither
   *        <code>null</code> nor empty nor may it contain <code>null</code>
   *        elements.
   * @return A new {@link Validator} object. Never <code>null</code>.
   * @see #getSchema(Collection)
   */
  @Nonnull
  public final Validator getValidator (@Nonnull @Nonempty final Collection <? extends IReadableResource> aResources)
  {
    return getValidatorFromSchema (getSchema (aResources));
  }

  /**
   * Utility method to remove a single resource from the schema cache.
   *
   * @param aKey
   *        The resource to remove
   * @return {@link EChange}.
   */
  @Nonnull
  public EChange removeFromCache (@Nullable final IReadableResource aKey)
  {
    return removeFromCache (CollectionHelper.newList (aKey));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("schemaTypeName", m_sSchemaTypeName).toString ();
  }
}
