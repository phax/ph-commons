/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.tls;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.ICommonsList;

/**
 * TLS Configuration mode read-only interface.
 *
 * @author Philip Helger
 * @since 9.0.5
 */
public interface ITLSConfigurationMode
{
  /**
   * @return A list of supported TLS versions in the correct order. May not be
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsList <ETLSVersion> getAllTLSVersions ();

  /**
   * @return A list of the IDs of the supported TLS versions in the correct
   *         order. May not be <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  default ICommonsList <String> getAllTLSVersionIDs ()
  {
    return getAllTLSVersions ().getAllMapped (ETLSVersion::getID);
  }

  /**
   * @return A list of the IDs of the supported TLS versions in the correct
   *         order. May be <code>null</code> if no TLS versions are defined.
   */
  @Nullable
  default String [] getAllTLSVersionIDsAsArray ()
  {
    final ICommonsList <String> aList = getAllTLSVersionIDs ();
    return aList.isEmpty () ? null : aList.toArray (new String [aList.size ()]);
  }

  /**
   * @return All cipher suites in the correct order. May not be
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsList <String> getAllCipherSuites ();

  /**
   * @return All cipher suites in the correct order. May be <code>null</code> if
   *         no cipher suite is defined.
   */
  @Nullable
  default String [] getAllCipherSuitesAsArray ()
  {
    final ICommonsList <String> aList = getAllCipherSuites ();
    return aList.isEmpty () ? null : aList.toArray (new String [aList.size ()]);
  }
}
