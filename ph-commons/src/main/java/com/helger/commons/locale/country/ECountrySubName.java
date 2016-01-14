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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Translatable;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.util.TextHelper;

/**
 * Country sub-element name enum.
 *
 * @author Philip Helger
 */
@Translatable
public enum ECountrySubName implements IHasDisplayText
{
  AU_ACT ("Australisches Hauptstadt-Territorium", "Australian Capital Territory"),
  AU_NSW ("Neu Süd-Wales", "New South Wales"),
  AU_NT ("Nördliches Territorium", "Nothern Territory"),
  AU_QLD ("Queensland", "Queensland"),
  AU_SA ("Süd-Australien", "South Australia"),
  AU_TAS ("Tasmanien", "Tasmania"),
  AU_VIC ("Victoria", "Victoria"),
  AU_WA ("West-Australien", "West Australia"),
  BA_FBH ("Föderation Bosnien und Herzegowina", "Federation of Bosnia and Herzegovina"),
  BA_RS ("Republik Srpska", "Republic of Srpska"),
  CA_AB ("Alberta", "Alberta"),
  CA_BC ("Britisch Columbia", "British Columbia"),
  CA_MB ("Manitoba", "Manitoba"),
  CA_NB ("New Brunswick", "New Brunswick"),
  CA_NL ("Neufundland und Labrador", "Newfoundland and Labrador"),
  CA_NS ("Nova Scotia", "Nova Scotia"),
  CA_NT ("Nordwest-Territorien", "Northwest Territories"),
  CA_NU ("Nunavut", "Nunavut"),
  CA_ON ("Ontario", "Ontario"),
  CA_PE ("Prinz Edward Insel", "Prince Edward Island"),
  CA_QC ("Quebec", "Quebec"),
  CA_SK ("Saskatchewan", "Saskatchewan"),
  CA_YT ("Yukon", "Yukon"),
  CH_AG ("Aargau", "Aargau"),
  CH_AI ("Appenzell Innerrhoden", "Appenzell Innerrhoden"),
  CH_AR ("Appenzell Ausserrhoden", "Appenzell Ausserrhoden"),
  CH_BE ("Bern", "Bern"),
  CH_BL ("Basel-Landschaft", "Basel-Country"),
  CH_BS ("Basel-Stadt", "Basel-City"),
  CH_FR ("Freiburg", "Fribourg"),
  CH_GE ("Genf", "Geneva"),
  CH_GL ("Glarus", "Glarus"),
  CH_GR ("Graubünden", "Graubünden"),
  CH_JU ("Jura", "Jura"),
  CH_LU ("Luzern", "Lucerne"),
  CH_NE ("Neuenburg", "Neuchâtel"),
  CH_NW ("Nidwalden", "Nidwalden"),
  CH_OW ("Obwalden", "Obwalden"),
  CH_SG ("St. Gallen", "St. Gallen"),
  CH_SH ("Schaffhausen", "Schaffhausen"),
  CH_SO ("Solothurn", "Solothurn"),
  CH_SZ ("Schwyz", "Schwyz"),
  CH_TG ("Thurgau", "Thurgau"),
  CH_TI ("Tessin", "Ticino"),
  CH_UR ("Uri", "Uri"),
  CH_VD ("Waadt", "Vaud"),
  CH_VS ("Wallis", "Valais"),
  CH_ZG ("Zug", "Zug"),
  CH_ZH ("Zürich", "Zürich"),
  DE_B ("Berlin", "Berlin"),
  DE_BB ("Brandenburg", "Brandenburg"),
  DE_BW ("Baden-Württemberg", "Baden-Württemberg"),
  DE_BY ("Bayern", "Bavaria"),
  DE_HB ("Bremen", "Bremen"),
  DE_HE ("Hessen", "Hesse"),
  DE_HH ("Hamburg", "Hamburg"),
  DE_MV ("Mecklenburg-Vorpommern", "Mecklenburg-Vorpommern"),
  DE_NI ("Niedersachsen", "Lower Saxony"),
  DE_NW ("Nordrhein-Westfalen", "North Rhine-Westphalia"),
  DE_RP ("Rheinland-Pfalz", "Rhineland-Palatinate"),
  DE_SH ("Schleswig-Holstein", "Schleswig-Holstein"),
  DE_SL ("Saarland", "Saarland"),
  DE_SN ("Sachsen", "Saxony"),
  DE_ST ("Sachsen-Anhalt", "Saxony-Anhalt"),
  DE_TH ("Thüringen", "Thuringia"),
  FR_BR ("Bas-Rhin", "Bas-Rhin"),
  FR_GU ("Guadeloupe", "Guadeloupe"),
  FR_GY ("Guyana", "Guyane"),
  FR_HR ("Haut-Rhin", "Haut-Rhin"),
  FR_LR ("La Reunion", "La Reunion"),
  FR_MA ("Martinique", "Martinique"),
  FR_MO ("Moselle", "Moselle"),
  IN_AN ("Andaman und Nicobar Islands", "Andaman and Nicobar Islands"),
  IN_AP ("Andhra Pradesh", "Andhra Pradesh"),
  IN_AR ("Arunachal Pradesh", "Arunachal Pradesh"),
  IN_AS ("Assam", "Assam"),
  IN_BR ("Bihar", "Bihar"),
  IN_CH ("Chandigarh", "Chandigarh"),
  IN_CT ("Chhattisgarh", "Chhattisgarh"),
  IN_DD ("Daman und Diu", "Daman and Diu"),
  IN_DL ("Delhi", "Delhi"),
  IN_DN ("Dadra und Nagar Haveli", "Dadra and Nagar Haveli"),
  IN_GA ("Goa", "Goa"),
  IN_GJ ("Gujarat", "Gujarat"),
  IN_HP ("Himachal Pradesh", "Himachal Pradesh"),
  IN_HR ("Haryana", "Haryana"),
  IN_JH ("Jharkhand", "Jharkhand"),
  IN_JK ("Jammu und Kashmir", "Jammu and Kashmir"),
  IN_KA ("Karnataka", "Karnataka"),
  IN_KL ("Kerala", "Kerala"),
  IN_LD ("Lakshadweep", "Lakshadweep"),
  IN_MH ("Maharashtra", "Maharashtra"),
  IN_ML ("Meghalaya", "Meghalaya"),
  IN_MN ("Manipur", "Manipur"),
  IN_MP ("Madhya Pradesh", "Madhya Pradesh"),
  IN_MZ ("Mizoram", "Mizoram"),
  IN_NL ("Nagaland", "Nagaland"),
  IN_OR ("Orissa", "Orissa"),
  IN_PB ("Punjab", "Punjab"),
  IN_PY ("Puducherry", "Puducherry"),
  IN_RJ ("Rajasthan", "Rajasthan"),
  IN_SK ("Sikkim", "Sikkim"),
  IN_TN ("Tamil Nadu", "Tamil Nadu"),
  IN_TR ("Tripura", "Tripura"),
  IN_UL ("Uttarakhand", "Uttarakhand"),
  IN_UP ("Uttar Pradesh", "Uttar Pradesh"),
  IN_WB ("West Bengal", "West Bengal"),
  IT_BZ ("Südtirol", "South Tyrol"),
  MD_CH ("Chişinău", "Chişinău"),
  UK_AL ("Alderney", "Alderney"),
  UK_EN ("England", "England"),
  UK_GU ("Guernsey", "Guernsey"),
  UK_IM ("Isle of Man", "Isle of Man"),
  UK_JE ("Jersey", "Jersey"),
  UK_NI ("Nordirland", "Northern Ireland"),
  UK_SC ("Schottland", "Scotland"),
  UK_WA ("Wales", "Wales"),
  US_AK ("Alaska", "Alaska"),
  US_AL ("Alabama", "Alabama"),
  US_AR ("Arkansas", "Arkansas"),
  US_AZ ("Arizona", "Arizona"),
  US_BE ("Berkeley", "Berkeley"),
  US_CA ("Kalifornien", "California"),
  US_CO ("Colorado", "Colorado"),
  US_CT ("Connecticut", "Connecticut"),
  US_DC ("District of Columbia", "District of Columbia"),
  US_DE ("Delaware", "Delaware"),
  US_FL ("Florida", "Florida"),
  US_GA ("Georgia", "Georgia"),
  US_HI ("Hawaii", "Hawaii"),
  US_IA ("Iowa", "Iowa"),
  US_ID ("Idaho", "Idaho"),
  US_IL ("Illinois", "Illinois"),
  US_IN ("Indiana", "Indiana"),
  US_KS ("Kansas", "Kansas"),
  US_KY ("Kentucky", "Kentucky"),
  US_LA ("Louisiana", "Louisiana"),
  US_MA ("Massachusetts", "Massachusetts"),
  US_MD ("Maryland", "Maryland"),
  US_ME ("Maine", "Maine"),
  US_MI ("Michigan", "Michigan"),
  US_MN ("Minnesota", "Minnesota"),
  US_MO ("Missouri", "Missouri"),
  US_MS ("Mississippi", "Mississippi"),
  US_MT ("Montana", "Montana"),
  US_NC_CE ("Charlotte", "Charlotte"),
  US_NC_ME ("Mecklenburg", "Mecklenburg"),
  US_NC ("North Carolina", "North Carolina"),
  US_ND ("North Dakota", "North Dakota"),
  US_NE ("Nebraska", "Nebraska"),
  US_NH ("New Hampshire", "New Hampshire"),
  US_NJ ("New Jersey", "New Jersey"),
  US_NM ("New Mexico", "New Mexico"),
  US_NV ("Nevada", "Nevada"),
  US_NY_NYC ("New York City", "New York City"),
  US_NY ("New York", "New York"),
  US_OH ("Ohio", "Ohio"),
  US_OK ("Oklahoma", "Oklahoma"),
  US_OR ("Oregon", "Oregon"),
  US_PA ("Pennsylvania", "Pennsylvania"),
  US_RI ("Rhode Island", "Rhode Island"),
  US_SC ("South Carolina", "South Carolina"),
  US_SD ("South Dakota", "South Dakota"),
  US_TN ("Tennessee", "Tennessee"),
  US_TX ("Texas", "Texas"),
  US_UT ("Utah", "Utah"),
  US_VA ("Virginia", "Virginia"),
  US_VT ("Vermont", "Vermont"),
  US_WA ("Washington", "Washington"),
  US_WI ("Wisconsin", "Wisconsin"),
  US_WV ("West Virginia", "West Virginia"),
  US_WY ("Wyoming", "Wyoming");

  private final IMultilingualText m_aTP;

  private ECountrySubName (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    m_aTP = TextHelper.create_DE_EN (sDE, sEN);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
