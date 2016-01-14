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
package com.helger.commons.mime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single MIME type as the combination of the content type and the
 * sub-type and parameters.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MimeType implements IMimeType, Comparable <MimeType>
{
  /** The content type (text, application etc.) */
  private final EMimeContentType m_eContentType;

  /** The sub type top be added to the content type. */
  private final String m_sContentSubType;

  /**
   * The MIME type string including content type and sub type - for performance
   * reasons only
   */
  private final String m_sMainTypeAsString;

  /** This list of parameters - optional */
  private List <MimeTypeParameter> m_aParameters;

  /**
   * Kind of a copy constructor
   *
   * @param aOther
   *        The other object to copy the data from
   */
  public MimeType (@Nonnull final IMimeType aOther)
  {
    this (aOther.getContentType (), aOther.getContentSubType (), aOther.getAllParameters ());
  }

  /**
   * Constructor without parameters. To construct the MIME type "text/xml" you
   * need to pass {@link EMimeContentType#TEXT} and the String "xml" to this
   * constructor.
   *
   * @param eContentType
   *        MIME content type. May not be <code>null</code>.
   * @param sContentSubType
   *        MIME content sub type. May neither be <code>null</code> nor empty.
   */
  public MimeType (@Nonnull final EMimeContentType eContentType, @Nonnull @Nonempty final String sContentSubType)
  {
    this (eContentType, sContentSubType, (Collection <? extends MimeTypeParameter>) null);
  }

  /**
   * Constructor without parameters. To construct the MIME type "text/xml" you
   * need to pass {@link EMimeContentType#TEXT} and the String "xml" to this
   * constructor.
   *
   * @param eContentType
   *        MIME content type. May not be <code>null</code>.
   * @param sContentSubType
   *        MIME content sub type. May neither be <code>null</code> nor empty.
   * @param aParameters
   *        MIME type parameters. May be <code>null</code> or empty.
   */
  public MimeType (@Nonnull final EMimeContentType eContentType,
                   @Nonnull @Nonempty final String sContentSubType,
                   @Nullable final Collection <? extends MimeTypeParameter> aParameters)
  {
    ValueEnforcer.notNull (eContentType, "ContentType");
    ValueEnforcer.notEmpty (sContentSubType, "ContentSubType");

    m_eContentType = eContentType;
    m_sContentSubType = sContentSubType;
    m_sMainTypeAsString = m_eContentType.getText () + CMimeType.SEPARATOR_CONTENTTYPE_SUBTYPE + m_sContentSubType;
    m_aParameters = CollectionHelper.isEmpty (aParameters) ? null : CollectionHelper.newList (aParameters);
  }

  @Nonnull
  public EMimeContentType getContentType ()
  {
    return m_eContentType;
  }

  @Nonnull
  @Nonempty
  public String getContentSubType ()
  {
    return m_sContentSubType;
  }

  @Nonnull
  private String _getParametersAsString (@Nonnull final EMimeQuoting eQuotingAlgorithm)
  {
    final StringBuilder aSB = new StringBuilder ();
    // Append all parameters
    for (final MimeTypeParameter aParameter : m_aParameters)
    {
      aSB.append (CMimeType.SEPARATOR_PARAMETER)
         .append (aParameter.getAttribute ())
         .append (CMimeType.SEPARATOR_PARAMETER_NAME_VALUE)
         .append (aParameter.getValueQuotedIfNecessary (eQuotingAlgorithm));
    }
    return aSB.toString ();
  }

  @Nonnull
  @Nonempty
  public String getAsString (@Nonnull final EMimeQuoting eQuotingAlgorithm)
  {
    ValueEnforcer.notNull (eQuotingAlgorithm, "QuotingAlgorithm");

    if (CollectionHelper.isEmpty (m_aParameters))
    {
      // No parameters - return as is
      return m_sMainTypeAsString;
    }

    return m_sMainTypeAsString + _getParametersAsString (eQuotingAlgorithm);
  }

  @Nonnull
  public String getAsStringWithoutParameters ()
  {
    return m_sMainTypeAsString;
  }

  @Nonnull
  public String getParametersAsString (@Nonnull final EMimeQuoting eQuotingAlgorithm)
  {
    ValueEnforcer.notNull (eQuotingAlgorithm, "QuotingAlgorithm");

    if (CollectionHelper.isEmpty (m_aParameters))
      return "";

    return _getParametersAsString (eQuotingAlgorithm);
  }

  /**
   * Add a parameter.
   *
   * @param sAttribute
   *        Parameter name. Must neither be <code>null</code> nor empty and must
   *        match {@link MimeTypeParser#isToken(String)}.
   * @param sValue
   *        The value to use. May neither be <code>null</code> nor empty. Must
   *        not be a valid MIME token.
   * @return this
   */
  @Nonnull
  public MimeType addParameter (@Nonnull @Nonempty final String sAttribute, @Nonnull @Nonempty final String sValue)
  {
    return addParameter (new MimeTypeParameter (sAttribute, sValue));
  }

  /**
   * Add a parameter.
   *
   * @param aParameter
   *        The parameter to be added. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public MimeType addParameter (@Nonnull final MimeTypeParameter aParameter)
  {
    ValueEnforcer.notNull (aParameter, "Parameter");

    if (m_aParameters == null)
      m_aParameters = new ArrayList <MimeTypeParameter> ();
    m_aParameters.add (aParameter);
    return this;
  }

  /**
   * Remove the specified parameter from this MIME type.
   *
   * @param aParameter
   *        The parameter to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if removal was successful
   */
  @Nonnull
  public EChange removeParameter (@Nullable final MimeTypeParameter aParameter)
  {
    return EChange.valueOf (m_aParameters != null && m_aParameters.remove (aParameter));
  }

  /**
   * Remove the parameter at the specified index.
   *
   * @param nIndex
   *        The index to remove. Should be &ge; 0.
   * @return {@link EChange#CHANGED} if removal was successful
   */
  @Nonnull
  public EChange removeParameterAtIndex (final int nIndex)
  {
    return CollectionHelper.removeElementAtIndex (m_aParameters, nIndex);
  }

  /**
   * Remove all existing parameters.
   *
   * @return {@link EChange#CHANGED} if at least one parameter was present
   */
  @Nonnull
  public EChange removeAllParameters ()
  {
    if (CollectionHelper.isEmpty (m_aParameters))
      return EChange.UNCHANGED;
    m_aParameters.clear ();
    return EChange.CHANGED;
  }

  /**
   * Remove the parameter with the specified name.
   *
   * @param sParamName
   *        The name of the parameter to remove. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the parameter was removed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  public EChange removeParameterWithName (@Nullable final String sParamName)
  {
    if (StringHelper.hasText (sParamName) && m_aParameters != null)
    {
      final int nMax = m_aParameters.size ();
      for (int i = 0; i < nMax; ++i)
      {
        final MimeTypeParameter aParam = m_aParameters.get (i);
        if (aParam.getAttribute ().equals (sParamName))
        {
          m_aParameters.remove (i);
          return EChange.CHANGED;
        }
      }
    }
    return EChange.UNCHANGED;
  }

  public boolean hasAnyParameters ()
  {
    return CollectionHelper.isNotEmpty (m_aParameters);
  }

  @Nonnegative
  public int getParameterCount ()
  {
    return CollectionHelper.getSize (m_aParameters);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <MimeTypeParameter> getAllParameters ()
  {
    return CollectionHelper.newList (m_aParameters);
  }

  @Nullable
  public MimeTypeParameter getParameterAtIndex (@Nonnegative final int nIndex)
  {
    return CollectionHelper.getSafe (m_aParameters, nIndex);
  }

  @Nullable
  public MimeTypeParameter getParameterWithName (@Nullable final String sParamName)
  {
    if (StringHelper.hasText (sParamName) && m_aParameters != null)
      for (final MimeTypeParameter aParam : m_aParameters)
        if (aParam.getAttribute ().equals (sParamName))
          return aParam;
    return null;
  }

  @Nullable
  public String getParameterValueWithName (@Nullable final String sParamName)
  {
    final MimeTypeParameter aParam = getParameterWithName (sParamName);
    return aParam == null ? null : aParam.getValue ();
  }

  @Nonnull
  public MimeType getClone ()
  {
    return new MimeType (m_eContentType, m_sContentSubType, m_aParameters);
  }

  @Nonnull
  public MimeType getCopyWithoutParameters ()
  {
    return new MimeType (m_eContentType, m_sContentSubType);
  }

  public int compareTo (final MimeType o)
  {
    return m_sMainTypeAsString.compareTo (o.m_sMainTypeAsString);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MimeType rhs = (MimeType) o;
    return m_eContentType.equals (rhs.m_eContentType) &&
           m_sContentSubType.equals (rhs.m_sContentSubType) &&
           EqualsHelper.equals (m_aParameters, rhs.m_aParameters);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eContentType)
                                       .append (m_sContentSubType)
                                       .append (m_aParameters)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("contentType", m_eContentType)
                                       .append ("subType", m_sContentSubType)
                                       .appendIfNotNull ("parameters", m_aParameters)
                                       .toString ();
  }
}
