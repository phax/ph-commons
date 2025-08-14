/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.security.oscp;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

import com.helger.base.state.ISuccessIndicator;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

import jakarta.annotation.Nullable;

/**
 * OSCP response status as enum.
 *
 * @author Philip Helger
 */
public enum EOCSPResponseStatus implements ISuccessIndicator, IHasIntID
{
  SUCCESSFUL (OCSPResponseStatus.SUCCESSFUL),
  MALFORMED_REQUEST (OCSPResponseStatus.MALFORMED_REQUEST),
  INTERNAL_ERROR (OCSPResponseStatus.INTERNAL_ERROR),
  TRY_LATER (OCSPResponseStatus.TRY_LATER),
  SIG_REQUIRED (OCSPResponseStatus.SIG_REQUIRED),
  UNAUTHORIZED (OCSPResponseStatus.UNAUTHORIZED);

  private final int m_nValue;

  EOCSPResponseStatus (final int nValue)
  {
    m_nValue = nValue;
  }

  public int getID ()
  {
    return m_nValue;
  }

  public boolean isSuccess ()
  {
    return this == SUCCESSFUL;
  }

  @Nullable
  public static EOCSPResponseStatus getFromValueOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EOCSPResponseStatus.class, nID);
  }
}
