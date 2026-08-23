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
package com.helger.http.permissionspolicy;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.codec.RFC5234Helper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A single Permissions Policy directive. It consists of a directive name, an allow list value and
 * an optional reporting endpoint.
 *
 * @author Philip Helger
 * @since 12.3.6
 */
public class PermissionsPolicyDirective implements IPermissionsPolicyDirective
{
  private final String m_sName;
  private final String m_sValue;
  private final String m_sReportTo;

  /**
   * Check if the provided string is a valid Permissions Policy directive name. Valid names consist
   * of alpha, digit and hyphen characters only.
   *
   * @param sName
   *        The name to check. May be <code>null</code>.
   * @return <code>true</code> if the name is valid, <code>false</code> otherwise.
   */
  public static boolean isValidName (@Nullable final String sName)
  {
    if (StringHelper.isEmpty (sName))
    {
      // Empty name is not allowed
      return false;
    }

    final char [] aChars = sName.toCharArray ();
    for (final char c : aChars)
      if (!RFC5234Helper.isAlpha (c) && !RFC5234Helper.isDigit (c) && c != '-')
        return false;

    return true;
  }

  /**
   * Check if the provided string is a valid Permissions Policy directive value. Empty values are
   * allowed. The value must not contain commas or semicolons, as these separate the directives
   * respectively the reporting endpoint.
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if the value is valid, <code>false</code> otherwise.
   */
  public static boolean isValidValue (@Nullable final String sValue)
  {
    if (StringHelper.isEmpty (sValue))
    {
      // Empty values are allowed
      return true;
    }

    final char [] aChars = sValue.toCharArray ();
    for (final char c : aChars)
      if (!RFC5234Helper.isWSP (c) && (!RFC5234Helper.isVChar (c) || c == ';' || c == ','))
        return false;

    return true;
  }

  /**
   * Constructor using an allow list as the value.
   *
   * @param sName
   *        The directive name. May neither be <code>null</code> nor empty and must be a valid name.
   * @param aValue
   *        The allow list value. May be <code>null</code>.
   */
  public PermissionsPolicyDirective (@NonNull @Nonempty final String sName,
                                     @Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    this (sName, aValue == null ? null : aValue.getAsString (), null);
  }

  /**
   * Constructor using an allow list as the value and a reporting endpoint.
   *
   * @param sName
   *        The directive name. May neither be <code>null</code> nor empty and must be a valid name.
   * @param aValue
   *        The allow list value. May be <code>null</code>.
   * @param sReportTo
   *        The name of the reporting endpoint according to the <code>Reporting-Endpoints</code>
   *        response header. May be <code>null</code>.
   */
  public PermissionsPolicyDirective (@NonNull @Nonempty final String sName,
                                     @Nullable final AbstractPermissionsPolicyAllowList <?> aValue,
                                     @Nullable final String sReportTo)
  {
    this (sName, aValue == null ? null : aValue.getAsString (), sReportTo);
  }

  /**
   * Constructor using a string as the value.
   *
   * @param sName
   *        The directive name. May neither be <code>null</code> nor empty and must be a valid name.
   * @param sValue
   *        The directive value. May be <code>null</code>.
   */
  public PermissionsPolicyDirective (@NonNull @Nonempty final String sName, @Nullable final String sValue)
  {
    this (sName, sValue, null);
  }

  /**
   * Constructor using a string as the value and a reporting endpoint.
   *
   * @param sName
   *        The directive name. May neither be <code>null</code> nor empty and must be a valid name.
   * @param sValue
   *        The directive value. May be <code>null</code>.
   * @param sReportTo
   *        The name of the reporting endpoint according to the <code>Reporting-Endpoints</code>
   *        response header. May be <code>null</code>.
   */
  public PermissionsPolicyDirective (@NonNull @Nonempty final String sName,
                                     @Nullable final String sValue,
                                     @Nullable final String sReportTo)
  {
    ValueEnforcer.isTrue (isValidName (sName),
                          () -> "The Permissions Policy directive name '" + sName + "' is invalid!");
    ValueEnforcer.isTrue (isValidValue (sValue),
                          () -> "The Permissions Policy directive value '" + sValue + "' is invalid!");
    ValueEnforcer.isTrue (isValidValue (sReportTo),
                          () -> "The Permissions Policy reporting endpoint '" + sReportTo + "' is invalid!");
    m_sName = sName;
    m_sValue = sValue;
    m_sReportTo = sReportTo;
  }

  /** {@inheritDoc} */
  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  /** {@inheritDoc} */
  @Nullable
  public final String getValue ()
  {
    return m_sValue;
  }

  /** {@inheritDoc} */
  @Nullable
  public final String getReportTo ()
  {
    return m_sReportTo;
  }

  /**
   * @param sReportTo
   *        The name of the reporting endpoint according to the <code>Reporting-Endpoints</code>
   *        response header. May be <code>null</code>.
   * @return A new {@link PermissionsPolicyDirective} with the same name and value as this one, but
   *         with the provided reporting endpoint. Never <code>null</code>.
   */
  @NonNull
  public PermissionsPolicyDirective getWithReportTo (@Nullable final String sReportTo)
  {
    return new PermissionsPolicyDirective (m_sName, m_sValue, sReportTo);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PermissionsPolicyDirective rhs = (PermissionsPolicyDirective) o;
    return m_sName.equals (rhs.m_sName) &&
      EqualsHelper.equals (m_sValue, rhs.m_sValue) &&
      EqualsHelper.equals (m_sReportTo, rhs.m_sReportTo);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sName).append (m_sValue).append (m_sReportTo).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Name", m_sName)
                                       .appendIfNotNull ("Value", m_sValue)
                                       .appendIfNotNull ("ReportTo", m_sReportTo)
                                       .getToString ();
  }

  /**
   * Controls whether the current document is allowed to gather information about device
   * acceleration through the <code>Accelerometer</code> interface.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createAccelerometer (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("accelerometer", aValue);
  }

  /**
   * Controls whether the current document is allowed to gather information about ambient light
   * levels through the <code>AmbientLightSensor</code> interface.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createAmbientLightSensor (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("ambient-light-sensor", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the <code>ariaNotify()</code> method to
   * fire screen reader announcements.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createAriaNotify (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("aria-notify", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Attribution Reporting API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createAttributionReporting (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("attribution-reporting", aValue);
  }

  /**
   * Controls whether the current document is allowed to autoplay media requested through the
   * <code>HTMLMediaElement</code> interface.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createAutoplay (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("autoplay", aValue);
  }

  /**
   * Controls whether the use of the Web Bluetooth API is allowed in the current document.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createBluetooth (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("bluetooth", aValue);
  }

  /**
   * Controls access to the Topics API for browsing topic information.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createBrowsingTopics (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("browsing-topics", aValue);
  }

  /**
   * Controls whether the current document is allowed to use video input devices.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createCamera (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("camera", aValue);
  }

  /**
   * Controls whether the current document is permitted to use the Captured Surface Control API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createCapturedSurfaceControl (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("captured-surface-control", aValue);
  }

  /**
   * Controls whether the current document is permitted to use
   * <code>NavigatorUAData.getHighEntropyValues()</code> to retrieve high-entropy user-agent data.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createChUaHighEntropyValues (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("ch-ua-high-entropy-values", aValue);
  }

  /**
   * Controls access to the Compute Pressure API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createComputePressure (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("compute-pressure", aValue);
  }

  /**
   * Controls whether the current document can be treated as cross-origin isolated.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createCrossOriginIsolated (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("cross-origin-isolated", aValue);
  }

  /**
   * Controls the allocation of the top-level origin's <code>fetchLater()</code> quota.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createDeferredFetch (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("deferred-fetch", aValue);
  }

  /**
   * Controls the allocation of the shared cross-origin subframe <code>fetchLater()</code> quota.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createDeferredFetchMinimal (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("deferred-fetch-minimal", aValue);
  }

  /**
   * Controls whether the current document is permitted to use <code>getDisplayMedia()</code> to
   * capture screen contents.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createDisplayCapture (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("display-capture", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Encrypted Media Extensions API
   * (EME).
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createEncryptedMedia (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("encrypted-media", aValue);
  }

  /**
   * Controls whether the current document is allowed to use
   * <code>Element.requestFullscreen()</code>.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createFullscreen (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("fullscreen", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Gamepad API.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createGamepad (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("gamepad", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Geolocation interface.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createGeolocation (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("geolocation", aValue);
  }

  /**
   * Controls whether the current document is allowed to gather information about device orientation
   * through the <code>Gyroscope</code> interface.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createGyroscope (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("gyroscope", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the WebHID API to connect to uncommon
   * human interface devices.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createHid (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("hid", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Federated Credential Management API
   * (FedCM).
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createIdentityCredentialsGet (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("identity-credentials-get", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Idle Detection API to detect user
   * interaction.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createIdleDetection (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("idle-detection", aValue);
  }

  /**
   * Controls access to the language detection functionality of the Translator and Language Detector
   * APIs.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLanguageDetector (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("language-detector", aValue);
  }

  /**
   * Controls access to the Prompt API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLanguageModel (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("language-model", aValue);
  }

  /**
   * Controls whether the current document is allowed to gather data on locally installed fonts via
   * <code>Window.queryLocalFonts()</code>.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLocalFonts (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("local-fonts", aValue);
  }

  /**
   * Controls whether the current document is allowed to make network requests to local addresses.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLocalNetwork (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("local-network", aValue);
  }

  /**
   * Controls whether the current document is allowed to make network requests to local and loopback
   * addresses.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLocalNetworkAccess (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("local-network-access", aValue);
  }

  /**
   * Controls whether the current document is allowed to make network requests to loopback
   * addresses.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createLoopbackNetwork (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("loopback-network", aValue);
  }

  /**
   * Controls whether the current document is allowed to gather information about device orientation
   * through the <code>Magnetometer</code> interface.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createMagnetometer (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("magnetometer", aValue);
  }

  /**
   * Controls whether the current document is allowed to use audio input devices.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createMicrophone (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("microphone", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Web MIDI API.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createMidi (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("midi", aValue);
  }

  /**
   * Controls access to the on-device speech recognition functionality of the Web Speech API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createOnDeviceSpeechRecognition (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("on-device-speech-recognition", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the WebOTP API to request one-time
   * passwords from SMS.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createOtpCredentials (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("otp-credentials", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Payment Request API.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPayment (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("payment", aValue);
  }

  /**
   * Controls whether the current document is allowed to play a video in Picture-in-Picture mode.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPictureInPicture (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("picture-in-picture", aValue);
  }

  /**
   * Controls usage of private state token <code>token-request</code> operations.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPrivateStateTokenIssuance (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("private-state-token-issuance", aValue);
  }

  /**
   * Controls usage of private state token <code>token-redemption</code> and
   * <code>send-redemption-record</code> operations.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPrivateStateTokenRedemption (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("private-state-token-redemption", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Web Authentication API to create
   * new asymmetric key credentials.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPublickeyCredentialsCreate (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("publickey-credentials-create", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Web Authentication API to retrieve
   * stored public key credentials.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createPublickeyCredentialsGet (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("publickey-credentials-get", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Screen Wake Lock API.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createScreenWakeLock (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("screen-wake-lock", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Web Serial API to communicate with
   * serial devices.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createSerial (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("serial", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Audio Output Devices API to list
   * and select speakers.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createSpeakerSelection (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("speaker-selection", aValue);
  }

  /**
   * Controls whether a third-party document is allowed to use the Storage Access API to request
   * access to unpartitioned cookies.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createStorageAccess (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("storage-access", aValue);
  }

  /**
   * Controls access to the Summarizer API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createSummarizer (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("summarizer", aValue);
  }

  /**
   * Controls access to the translation functionality of the Translator and Language Detector APIs.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createTranslator (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("translator", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the WebUSB API.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createUsb (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("usb", aValue);
  }

  /**
   * Controls whether the current document is allowed to use <code>Navigator.share()</code> of the
   * Web Share API.
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createWebShare (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("web-share", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the Window Management API to manage
   * windows on multiple displays.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createWindowManagement (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("window-management", aValue);
  }

  /**
   * Controls whether the current document is allowed to use the WebXR Device API to interact with a
   * WebXR session.
   * <p>
   * Note: this directive is experimental and may change or be removed in the future.
   * </p>
   *
   * @param aValue
   *        The allow list to use. May be <code>null</code>.
   * @return New {@link PermissionsPolicyDirective}. Never <code>null</code>.
   */
  @NonNull
  public static PermissionsPolicyDirective createXrSpatialTracking (@Nullable final AbstractPermissionsPolicyAllowList <?> aValue)
  {
    return new PermissionsPolicyDirective ("xr-spatial-tracking", aValue);
  }
}
