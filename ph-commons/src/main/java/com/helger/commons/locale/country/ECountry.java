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
package com.helger.commons.locale.country;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Country enum.
 *
 * @author Philip Helger
 */
public enum ECountry implements IHasDisplayText,IHasID <String>
{
  AD (ECountryName.AD),
  AE (ECountryName.AE),
  AF (ECountryName.AF),
  AG (ECountryName.AG),
  AI (ECountryName.AI),
  AL (ECountryName.AL),
  AM (ECountryName.AM),
  AN (ECountryName.AN),
  AO (ECountryName.AO),
  AQ (ECountryName.AQ),
  AR (ECountryName.AR),
  AS (ECountryName.AS),
  AT (ECountryName.AT),
  AU_ACT (ECountrySubName.AU_ACT),
  AU_NSW (ECountrySubName.AU_NSW),
  AU_NT (ECountrySubName.AU_NT),
  AU_QLD (ECountrySubName.AU_QLD),
  AU_SA (ECountrySubName.AU_SA),
  AU_TAS (ECountrySubName.AU_TAS),
  AU_VIC (ECountrySubName.AU_VIC),
  AU_WA (ECountrySubName.AU_WA),
  AU (ECountryName.AU),
  AW (ECountryName.AW),
  AX (ECountryName.AX),
  AZ (ECountryName.AZ),
  BA_FBH (ECountrySubName.BA_FBH),
  BA_RS (ECountrySubName.BA_RS),
  BA (ECountryName.BA),
  BB (ECountryName.BB),
  BD (ECountryName.BD),
  BE (ECountryName.BE),
  BF (ECountryName.BF),
  BG (ECountryName.BG),
  BH (ECountryName.BH),
  BI (ECountryName.BI),
  BJ (ECountryName.BJ),
  BM (ECountryName.BM),
  BN (ECountryName.BN),
  BO (ECountryName.BO),
  BR (ECountryName.BR),
  BS (ECountryName.BS),
  BT (ECountryName.BT),
  BV (ECountryName.BV),
  BW (ECountryName.BW),
  BY (ECountryName.BY),
  BZ (ECountryName.BZ),
  CA_AB (ECountrySubName.CA_AB),
  CA_BC (ECountrySubName.CA_BC),
  CA_MB (ECountrySubName.CA_MB),
  CA_NB (ECountrySubName.CA_NB),
  CA_NL (ECountrySubName.CA_NL),
  CA_NS (ECountrySubName.CA_NS),
  CA_NT (ECountrySubName.CA_NT),
  CA_NU (ECountrySubName.CA_NU),
  CA_ON (ECountrySubName.CA_ON),
  CA_PE (ECountrySubName.CA_PE),
  CA_QC (ECountrySubName.CA_QC),
  CA_SK (ECountrySubName.CA_SK),
  CA_YT (ECountrySubName.CA_YT),
  CA (ECountryName.CA),
  CC (ECountryName.CC),
  CD (ECountryName.CD),
  CF (ECountryName.CF),
  CG (ECountryName.CG),
  CH_AG (ECountrySubName.CH_AG),
  CH_AI (ECountrySubName.CH_AI),
  CH_AR (ECountrySubName.CH_AR),
  CH_BE (ECountrySubName.CH_BE),
  CH_BL (ECountrySubName.CH_BL),
  CH_BS (ECountrySubName.CH_BS),
  CH_FR (ECountrySubName.CH_FR),
  CH_GE (ECountrySubName.CH_GE),
  CH_GL (ECountrySubName.CH_GL),
  CH_GR (ECountrySubName.CH_GR),
  CH_JU (ECountrySubName.CH_JU),
  CH_LU (ECountrySubName.CH_LU),
  CH_NE (ECountrySubName.CH_NE),
  CH_NW (ECountrySubName.CH_NW),
  CH_OW (ECountrySubName.CH_OW),
  CH_SG (ECountrySubName.CH_SG),
  CH_SH (ECountrySubName.CH_SH),
  CH_SO (ECountrySubName.CH_SO),
  CH_SZ (ECountrySubName.CH_SZ),
  CH_TG (ECountrySubName.CH_TG),
  CH_TI (ECountrySubName.CH_TI),
  CH_UR (ECountrySubName.CH_UR),
  CH_VD (ECountrySubName.CH_VD),
  CH_VS (ECountrySubName.CH_VS),
  CH_ZG (ECountrySubName.CH_ZG),
  CH_ZH (ECountrySubName.CH_ZH),
  CH (ECountryName.CH),
  CI (ECountryName.CI),
  CK (ECountryName.CK),
  CL (ECountryName.CL),
  CM (ECountryName.CM),
  CN (ECountryName.CN),
  CO (ECountryName.CO),
  CR (ECountryName.CR),
  CU (ECountryName.CU),
  CV (ECountryName.CV),
  CX (ECountryName.CX),
  CY (ECountryName.CY),
  CZ (ECountryName.CZ),
  DE_B (ECountrySubName.DE_B),
  DE_BB (ECountrySubName.DE_BB),
  DE_BW (ECountrySubName.DE_BW),
  DE_BY (ECountrySubName.DE_BY),
  DE_HB (ECountrySubName.DE_HB),
  DE_HE (ECountrySubName.DE_HE),
  DE_HH (ECountrySubName.DE_HH),
  DE_MV (ECountrySubName.DE_MV),
  DE_NI (ECountrySubName.DE_NI),
  DE_NW (ECountrySubName.DE_NW),
  DE_RP (ECountrySubName.DE_RP),
  DE_SH (ECountrySubName.DE_SH),
  DE_SL (ECountrySubName.DE_SL),
  DE_SN (ECountrySubName.DE_SN),
  DE_ST (ECountrySubName.DE_ST),
  DE_TH (ECountrySubName.DE_TH),
  DE (ECountryName.DE),
  DJ (ECountryName.DJ),
  DK (ECountryName.DK),
  DM (ECountryName.DM),
  DO (ECountryName.DO),
  DZ (ECountryName.DZ),
  EC (ECountryName.EC),
  EE (ECountryName.EE),
  EG (ECountryName.EG),
  EH (ECountryName.EH),
  ER (ECountryName.ER),
  ES (ECountryName.ES),
  ET (ECountryName.ET),
  FI (ECountryName.FI),
  FJ (ECountryName.FJ),
  FK (ECountryName.FK),
  FM (ECountryName.FM),
  FO (ECountryName.FO),
  FR_BR (ECountrySubName.FR_BR),
  FR_GU (ECountrySubName.FR_GU),
  FR_GY (ECountrySubName.FR_GY),
  FR_HR (ECountrySubName.FR_HR),
  FR_LR (ECountrySubName.FR_LR),
  FR_MA (ECountrySubName.FR_MA),
  FR_MO (ECountrySubName.FR_MO),
  FR (ECountryName.FR),
  GA (ECountryName.GA),
  GB (ECountryName.GB),
  GD (ECountryName.GD),
  GE (ECountryName.GE),
  GF (ECountryName.GF),
  GG (ECountryName.GG),
  GH (ECountryName.GH),
  GI (ECountryName.GI),
  GL (ECountryName.GL),
  GM (ECountryName.GM),
  GN (ECountryName.GN),
  GP (ECountryName.GP),
  GQ (ECountryName.GQ),
  GR (ECountryName.GR),
  GS (ECountryName.GS),
  GT (ECountryName.GT),
  GU (ECountryName.GU),
  GW (ECountryName.GW),
  GY (ECountryName.GY),
  HK (ECountryName.HK),
  HM (ECountryName.HM),
  HN (ECountryName.HN),
  HR (ECountryName.HR),
  HT (ECountryName.HT),
  HU (ECountryName.HU),
  ID (ECountryName.ID),
  IE (ECountryName.IE),
  IL (ECountryName.IL),
  IM (ECountryName.IM),
  IN_AN (ECountrySubName.IN_AN),
  IN_AP (ECountrySubName.IN_AP),
  IN_AR (ECountrySubName.IN_AR),
  IN_AS (ECountrySubName.IN_AS),
  IN_BR (ECountrySubName.IN_BR),
  IN_CH (ECountrySubName.IN_CH),
  IN_CT (ECountrySubName.IN_CT),
  IN_DD (ECountrySubName.IN_DD),
  IN_DL (ECountrySubName.IN_DL),
  IN_DN (ECountrySubName.IN_DN),
  IN_GA (ECountrySubName.IN_GA),
  IN_GJ (ECountrySubName.IN_GJ),
  IN_HP (ECountrySubName.IN_HP),
  IN_HR (ECountrySubName.IN_HR),
  IN_JH (ECountrySubName.IN_JH),
  IN_JK (ECountrySubName.IN_JK),
  IN_KA (ECountrySubName.IN_KA),
  IN_KL (ECountrySubName.IN_KL),
  IN_LD (ECountrySubName.IN_LD),
  IN_MH (ECountrySubName.IN_MH),
  IN_ML (ECountrySubName.IN_ML),
  IN_MN (ECountrySubName.IN_MN),
  IN_MP (ECountrySubName.IN_MP),
  IN_MZ (ECountrySubName.IN_MZ),
  IN_NL (ECountrySubName.IN_NL),
  IN_OR (ECountrySubName.IN_OR),
  IN_PB (ECountrySubName.IN_PB),
  IN_PY (ECountrySubName.IN_PY),
  IN_RJ (ECountrySubName.IN_RJ),
  IN_SK (ECountrySubName.IN_SK),
  IN_TN (ECountrySubName.IN_TN),
  IN_TR (ECountrySubName.IN_TR),
  IN_UL (ECountrySubName.IN_UL),
  IN_UP (ECountrySubName.IN_UP),
  IN_WB (ECountrySubName.IN_WB),
  IN (ECountryName.IN),
  IO (ECountryName.IO),
  IQ (ECountryName.IQ),
  IR (ECountryName.IR),
  IS (ECountryName.IS),
  IT_BZ (ECountrySubName.IT_BZ),
  IT (ECountryName.IT),
  JE (ECountryName.JE),
  JM (ECountryName.JM),
  JO (ECountryName.JO),
  JP (ECountryName.JP),
  KE (ECountryName.KE),
  KG (ECountryName.KG),
  KH (ECountryName.KH),
  KI (ECountryName.KI),
  KM (ECountryName.KM),
  KN (ECountryName.KN),
  KP (ECountryName.KP),
  KR (ECountryName.KR),
  KW (ECountryName.KW),
  KY (ECountryName.KY),
  KZ (ECountryName.KZ),
  LA (ECountryName.LA),
  LB (ECountryName.LB),
  LC (ECountryName.LC),
  LI (ECountryName.LI),
  LK (ECountryName.LK),
  LR (ECountryName.LR),
  LS (ECountryName.LS),
  LT (ECountryName.LT),
  LU (ECountryName.LU),
  LV (ECountryName.LV),
  LY (ECountryName.LY),
  MA (ECountryName.MA),
  MC (ECountryName.MC),
  MD_CH (ECountrySubName.MD_CH),
  MD (ECountryName.MD),
  ME (ECountryName.ME),
  MG (ECountryName.MG),
  MH (ECountryName.MH),
  MK (ECountryName.MK),
  ML (ECountryName.ML),
  MM (ECountryName.MM),
  MN (ECountryName.MN),
  MO (ECountryName.MO),
  MP (ECountryName.MP),
  MQ (ECountryName.MQ),
  MR (ECountryName.MR),
  MS (ECountryName.MS),
  MT (ECountryName.MT),
  MU (ECountryName.MU),
  MV (ECountryName.MV),
  MW (ECountryName.MW),
  MX (ECountryName.MX),
  MY (ECountryName.MY),
  MZ (ECountryName.MZ),
  NA (ECountryName.NA),
  NC (ECountryName.NC),
  NE (ECountryName.NE),
  NF (ECountryName.NF),
  NG (ECountryName.NG),
  NI (ECountryName.NI),
  NL (ECountryName.NL),
  NO (ECountryName.NO),
  NP (ECountryName.NP),
  NR (ECountryName.NR),
  NU (ECountryName.NU),
  NZ (ECountryName.NZ),
  OM (ECountryName.OM),
  PA (ECountryName.PA),
  PE (ECountryName.PE),
  PF (ECountryName.PF),
  PG (ECountryName.PG),
  PH (ECountryName.PH),
  PK (ECountryName.PK),
  PL (ECountryName.PL),
  PM (ECountryName.PM),
  PN (ECountryName.PN),
  PR (ECountryName.PR),
  PS (ECountryName.PS),
  PT (ECountryName.PT),
  PW (ECountryName.PW),
  PY (ECountryName.PY),
  QA (ECountryName.QA),
  RE (ECountryName.RE),
  RO (ECountryName.RO),
  RS (ECountryName.RS),
  RU (ECountryName.RU),
  RW (ECountryName.RW),
  SA (ECountryName.SA),
  SB (ECountryName.SB),
  SC (ECountryName.SC),
  SD (ECountryName.SD),
  SE (ECountryName.SE),
  SG (ECountryName.SG),
  SH (ECountryName.SH),
  SI (ECountryName.SI),
  SJ (ECountryName.SJ),
  SK (ECountryName.SK),
  SL (ECountryName.SL),
  SM (ECountryName.SM),
  SN (ECountryName.SN),
  SO (ECountryName.SO),
  SR (ECountryName.SR),
  ST (ECountryName.ST),
  SV (ECountryName.SV),
  SY (ECountryName.SY),
  SZ (ECountryName.SZ),
  TC (ECountryName.TC),
  TD (ECountryName.TD),
  TF (ECountryName.TF),
  TG (ECountryName.TG),
  TH (ECountryName.TH),
  TJ (ECountryName.TJ),
  TK (ECountryName.TK),
  TL (ECountryName.TL),
  TM (ECountryName.TM),
  TN (ECountryName.TN),
  TO (ECountryName.TO),
  TR (ECountryName.TR),
  TT (ECountryName.TT),
  TV (ECountryName.TV),
  TW (ECountryName.TW),
  TZ (ECountryName.TZ),
  UA (ECountryName.UA),
  UG (ECountryName.UG),
  // UK is an invalid country code :( - must be GB
  UK (ECountryName.UK),
  UK_AL (ECountrySubName.UK_AL),
  UK_EN (ECountrySubName.UK_EN),
  UK_GU (ECountrySubName.UK_GU),
  UK_IM (ECountrySubName.UK_IM),
  UK_JE (ECountrySubName.UK_JE),
  UK_NI (ECountrySubName.UK_NI),
  UK_SC (ECountrySubName.UK_SC),
  UK_WA (ECountrySubName.UK_WA),
  UM (ECountryName.UM),
  US_AK (ECountrySubName.US_AK),
  US_AL (ECountrySubName.US_AL),
  US_AR (ECountrySubName.US_AR),
  US_AZ (ECountrySubName.US_AZ),
  US_BE (ECountrySubName.US_BE),
  US_CA (ECountrySubName.US_CA),
  US_CO (ECountrySubName.US_CO),
  US_CT (ECountrySubName.US_CT),
  US_DC (ECountrySubName.US_DC),
  US_DE (ECountrySubName.US_DE),
  US_FL (ECountrySubName.US_FL),
  US_GA (ECountrySubName.US_GA),
  US_HI (ECountrySubName.US_HI),
  US_IA (ECountrySubName.US_IA),
  US_ID (ECountrySubName.US_ID),
  US_IL (ECountrySubName.US_IL),
  US_IN (ECountrySubName.US_IN),
  US_KS (ECountrySubName.US_KS),
  US_KY (ECountrySubName.US_KY),
  US_LA (ECountrySubName.US_LA),
  US_MA (ECountrySubName.US_MA),
  US_MD (ECountrySubName.US_MD),
  US_ME (ECountrySubName.US_ME),
  US_MI (ECountrySubName.US_MI),
  US_MN (ECountrySubName.US_MN),
  US_MO (ECountrySubName.US_MO),
  US_MS (ECountrySubName.US_MS),
  US_MT (ECountrySubName.US_MT),
  US_NC_CE (ECountrySubName.US_NC_CE),
  US_NC_ME (ECountrySubName.US_NC_ME),
  US_NC (ECountrySubName.US_NC),
  US_ND (ECountrySubName.US_ND),
  US_NE (ECountrySubName.US_NE),
  US_NH (ECountrySubName.US_NH),
  US_NJ (ECountrySubName.US_NJ),
  US_NM (ECountrySubName.US_NM),
  US_NV (ECountrySubName.US_NV),
  US_NY_NYC (ECountrySubName.US_NY_NYC),
  US_NY (ECountrySubName.US_NY),
  US_OH (ECountrySubName.US_OH),
  US_OK (ECountrySubName.US_OK),
  US_OR (ECountrySubName.US_OR),
  US_PA (ECountrySubName.US_PA),
  US_RI (ECountrySubName.US_RI),
  US_SC (ECountrySubName.US_SC),
  US_SD (ECountrySubName.US_SD),
  US_TN (ECountrySubName.US_TN),
  US_TX (ECountrySubName.US_TX),
  US_UT (ECountrySubName.US_UT),
  US_VA (ECountrySubName.US_VA),
  US_VT (ECountrySubName.US_VT),
  US_WA (ECountrySubName.US_WA),
  US_WI (ECountrySubName.US_WI),
  US_WV (ECountrySubName.US_WV),
  US_WY (ECountrySubName.US_WY),
  US (ECountryName.US),
  UY (ECountryName.UY),
  UZ (ECountryName.UZ),
  VA (ECountryName.VA),
  VC (ECountryName.VC),
  VE (ECountryName.VE),
  VG (ECountryName.VG),
  VI (ECountryName.VI),
  VN (ECountryName.VN),
  VU (ECountryName.VU),
  WF (ECountryName.WF),
  WS (ECountryName.WS),
  YE (ECountryName.YE),
  YT (ECountryName.YT),
  ZA (ECountryName.ZA),
  ZM (ECountryName.ZM),
  ZW (ECountryName.ZW);

  private final String m_sID;
  private final String m_sISOCountryCode;
  private final IHasDisplayText m_aName;
  private final boolean m_bIsCountrySub;
  private final Locale m_aCountry;

  private ECountry (@Nonnull final IHasDisplayText eName)
  {
    m_sID = name ().toLowerCase (Locale.US);
    // Work around for illegal country code "UK"
    final String sISOCountryCode = StringHelper.getExplodedArray ('_', m_sID)[0];
    m_sISOCountryCode = sISOCountryCode.equals ("uk") ? "gb" : sISOCountryCode;
    m_aName = eName;
    m_bIsCountrySub = m_sID.indexOf ('_') != -1;
    m_aCountry = CountryCache.getInstance ().getCountry (m_sISOCountryCode);
    if (m_aCountry == null)
      throw new IllegalStateException ("Failed to resolve ISO country code " + m_sISOCountryCode);
  }

  /**
   * @return the country ID (incl. all state information). Always lowercase!
   */
  @Nonnull
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return the ISO country code (the part before the first underscore). Always
   *         lowercase.
   */
  @Nonnull
  public String getISOCountryCode ()
  {
    return m_sISOCountryCode;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aName.getDisplayText (aContentLocale);
  }

  public boolean isCountrySub ()
  {
    return m_bIsCountrySub;
  }

  public Locale getAsLocale ()
  {
    return m_aCountry;
  }

  @Nullable
  public static ECountry getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ECountry.class, sID);
  }

  /**
   * Try to find the country with the passed ISO code. Country sub-codes are
   * ignored in this method.
   *
   * @param sISO
   *        The ISO code to search. Case sensitivity does not matter. May be
   *        <code>null</code>.
   * @return The matching country or <code>null</code> if the ISO code was not
   *         resolved.
   */
  @Nullable
  public static ECountry getFromISOCodeOrNull (@Nullable final String sISO)
  {
    if (StringHelper.hasText (sISO))
    {
      final String sRealISO = sISO.toLowerCase (Locale.US);
      for (final ECountry eCountry : values ())
        if (!eCountry.isCountrySub ())
          if (eCountry.m_sISOCountryCode.equals (sRealISO))
            return eCountry;
    }
    return null;
  }

  /**
   * @return A non-<code>null</code> set of all contained country locales in
   *         this enumeration.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <Locale> getCountryListAsLocales ()
  {
    final Set <Locale> ret = new HashSet <Locale> ();
    for (final ECountry eCountry : values ())
      if (!eCountry.isCountrySub ())
        ret.add (eCountry.getAsLocale ());
    return ret;
  }
}
