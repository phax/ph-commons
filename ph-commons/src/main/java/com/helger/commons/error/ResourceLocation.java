package com.helger.commons.error;

import com.helger.commons.error.location.ErrorLocation;

/**
 * @deprecated Replaced with {@link ErrorLocation}
 * @author Philip Helger
 */
@Deprecated
public class ResourceLocation extends ErrorLocation implements IResourceLocation
{
  public ResourceLocation (final String sResourceID)
  {
    super (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, null);
  }

  public ResourceLocation (final String sResourceID, final String sField)
  {
    super (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, sField);
  }

  public ResourceLocation (final String sResourceID, final int nLineNumber, final int nColumnNumber)
  {
    super (sResourceID, nLineNumber, nColumnNumber, null);
  }

  public ResourceLocation (final String sResourceID,
                           final int nLineNumber,
                           final int nColumnNumber,
                           final String sField)
  {
    super (sResourceID, nLineNumber, nColumnNumber, sField);
  }
}
