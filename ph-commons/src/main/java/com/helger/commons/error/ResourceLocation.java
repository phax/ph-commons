package com.helger.commons.error;

import javax.annotation.Nullable;

import com.helger.commons.error.location.ErrorLocation;

/**
 * @deprecated Replaced with {@link ErrorLocation}
 * @author Philip Helger
 */
@Deprecated
public class ResourceLocation extends ErrorLocation implements IResourceLocation
{
  private final String m_sField;

  public ResourceLocation (@Nullable final String sResourceID)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, null);
  }

  public ResourceLocation (@Nullable final String sResourceID, final String sField)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, sField);
  }

  public ResourceLocation (@Nullable final String sResourceID, final int nLineNumber, final int nColumnNumber)
  {
    this (sResourceID, nLineNumber, nColumnNumber, null);
  }

  public ResourceLocation (@Nullable final String sResourceID,
                           final int nLineNumber,
                           final int nColumnNumber,
                           @Nullable final String sField)
  {
    super (sResourceID, nLineNumber, nColumnNumber);
    m_sField = sField;
  }

  @Nullable
  public String getField ()
  {
    return m_sField;
  }
}
