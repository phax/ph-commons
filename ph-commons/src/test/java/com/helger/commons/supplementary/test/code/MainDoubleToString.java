/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.supplementary.test.code;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.Locale;
import java.util.Locale.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Based on
 * http://www.onjava.com/pub/a/onjava/2000/12/15/formatting_doubles.html
 *
 * @author Philip Helger
 */
public final class MainDoubleToString
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainDoubleToString.class);

  private static final char NO_PREFIX_OR_SUFFIX = '\uFFFF';
  // Hardcode some byte arrays to make them quickly available
  private static final char [] NEGATIVE_INFINITY = { '-', 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
  private static final char [] POSITIVE_INFINITY = { 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
  private static final char [] DOUBLE_ZERO = { '0', '.', '0' };
  private static final char [] DOUBLE_ZERO2 = { '0', '.', '0', '0' };
  private static final char [] DOUBLE_ZERO0 = { '0', '.' };
  private static final char [] DOT_ZERO = { '.', '0' };
  private static final char [] INFINITY = { 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
  private static final char [] NaN = { 'N', 'a', 'N' };
  private static final char [] [] ZEROS = { {},
                                            { '0' },
                                            { '0', '0' },
                                            { '0', '0', '0' },
                                            { '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' },
                                            { '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0',
                                              '0' }, };

  private static final char [] charForDigit = { '0',
                                                '1',
                                                '2',
                                                '3',
                                                '4',
                                                '5',
                                                '6',
                                                '7',
                                                '8',
                                                '9',
                                                'a',
                                                'b',
                                                'c',
                                                'd',
                                                'e',
                                                'f',
                                                'g',
                                                'h',
                                                'i',
                                                'j',
                                                'k',
                                                'l',
                                                'm',
                                                'n',
                                                'o',
                                                'p',
                                                'q',
                                                'r',
                                                's',
                                                't',
                                                'u',
                                                'v',
                                                'w',
                                                'x',
                                                'y',
                                                'z' };

  // And required double related constants.
  private static final long DOUBLE_SIGN_MASK = 0x8000000000000000L;
  private static final long DOUBLE_EXP_MASK = 0x7ff0000000000000L;
  // private static final long DOUBLE_FRACT_MASK = ~(DOUBLE_SIGN_MASK |
  // DOUBLE_EXP_MASK);
  private static final int DOUBLE_EXP_SHIFT = 52;
  private static final int DOUBLE_EXP_BIAS = 1023;

  private static final double [] TENTH_POWERS = { 1e-323D,
                                                  1e-322D,
                                                  1e-321D,
                                                  1e-320D,
                                                  1e-319D,
                                                  1e-318D,
                                                  1e-317D,
                                                  1e-316D,
                                                  1e-315D,
                                                  1e-314D,
                                                  1e-313D,
                                                  1e-312D,
                                                  1e-311D,
                                                  1e-310D,
                                                  1e-309D,
                                                  1e-308D,
                                                  1e-307D,
                                                  1e-306D,
                                                  1e-305D,
                                                  1e-304D,
                                                  1e-303D,
                                                  1e-302D,
                                                  1e-301D,
                                                  1e-300D,
                                                  1e-299D,
                                                  1e-298D,
                                                  1e-297D,
                                                  1e-296D,
                                                  1e-295D,
                                                  1e-294D,
                                                  1e-293D,
                                                  1e-292D,
                                                  1e-291D,
                                                  1e-290D,
                                                  1e-289D,
                                                  1e-288D,
                                                  1e-287D,
                                                  1e-286D,
                                                  1e-285D,
                                                  1e-284D,
                                                  1e-283D,
                                                  1e-282D,
                                                  1e-281D,
                                                  1e-280D,
                                                  1e-279D,
                                                  1e-278D,
                                                  1e-277D,
                                                  1e-276D,
                                                  1e-275D,
                                                  1e-274D,
                                                  1e-273D,
                                                  1e-272D,
                                                  1e-271D,
                                                  1e-270D,
                                                  1e-269D,
                                                  1e-268D,
                                                  1e-267D,
                                                  1e-266D,
                                                  1e-265D,
                                                  1e-264D,
                                                  1e-263D,
                                                  1e-262D,
                                                  1e-261D,
                                                  1e-260D,
                                                  1e-259D,
                                                  1e-258D,
                                                  1e-257D,
                                                  1e-256D,
                                                  1e-255D,
                                                  1e-254D,
                                                  1e-253D,
                                                  1e-252D,
                                                  1e-251D,
                                                  1e-250D,
                                                  1e-249D,
                                                  1e-248D,
                                                  1e-247D,
                                                  1e-246D,
                                                  1e-245D,
                                                  1e-244D,
                                                  1e-243D,
                                                  1e-242D,
                                                  1e-241D,
                                                  1e-240D,
                                                  1e-239D,
                                                  1e-238D,
                                                  1e-237D,
                                                  1e-236D,
                                                  1e-235D,
                                                  1e-234D,
                                                  1e-233D,
                                                  1e-232D,
                                                  1e-231D,
                                                  1e-230D,
                                                  1e-229D,
                                                  1e-228D,
                                                  1e-227D,
                                                  1e-226D,
                                                  1e-225D,
                                                  1e-224D,
                                                  1e-223D,
                                                  1e-222D,
                                                  1e-221D,
                                                  1e-220D,
                                                  1e-219D,
                                                  1e-218D,
                                                  1e-217D,
                                                  1e-216D,
                                                  1e-215D,
                                                  1e-214D,
                                                  1e-213D,
                                                  1e-212D,
                                                  1e-211D,
                                                  1e-210D,
                                                  1e-209D,
                                                  1e-208D,
                                                  1e-207D,
                                                  1e-206D,
                                                  1e-205D,
                                                  1e-204D,
                                                  1e-203D,
                                                  1e-202D,
                                                  1e-201D,
                                                  1e-200D,
                                                  1e-199D,
                                                  1e-198D,
                                                  1e-197D,
                                                  1e-196D,
                                                  1e-195D,
                                                  1e-194D,
                                                  1e-193D,
                                                  1e-192D,
                                                  1e-191D,
                                                  1e-190D,
                                                  1e-189D,
                                                  1e-188D,
                                                  1e-187D,
                                                  1e-186D,
                                                  1e-185D,
                                                  1e-184D,
                                                  1e-183D,
                                                  1e-182D,
                                                  1e-181D,
                                                  1e-180D,
                                                  1e-179D,
                                                  1e-178D,
                                                  1e-177D,
                                                  1e-176D,
                                                  1e-175D,
                                                  1e-174D,
                                                  1e-173D,
                                                  1e-172D,
                                                  1e-171D,
                                                  1e-170D,
                                                  1e-169D,
                                                  1e-168D,
                                                  1e-167D,
                                                  1e-166D,
                                                  1e-165D,
                                                  1e-164D,
                                                  1e-163D,
                                                  1e-162D,
                                                  1e-161D,
                                                  1e-160D,
                                                  1e-159D,
                                                  1e-158D,
                                                  1e-157D,
                                                  1e-156D,
                                                  1e-155D,
                                                  1e-154D,
                                                  1e-153D,
                                                  1e-152D,
                                                  1e-151D,
                                                  1e-150D,
                                                  1e-149D,
                                                  1e-148D,
                                                  1e-147D,
                                                  1e-146D,
                                                  1e-145D,
                                                  1e-144D,
                                                  1e-143D,
                                                  1e-142D,
                                                  1e-141D,
                                                  1e-140D,
                                                  1e-139D,
                                                  1e-138D,
                                                  1e-137D,
                                                  1e-136D,
                                                  1e-135D,
                                                  1e-134D,
                                                  1e-133D,
                                                  1e-132D,
                                                  1e-131D,
                                                  1e-130D,
                                                  1e-129D,
                                                  1e-128D,
                                                  1e-127D,
                                                  1e-126D,
                                                  1e-125D,
                                                  1e-124D,
                                                  1e-123D,
                                                  1e-122D,
                                                  1e-121D,
                                                  1e-120D,
                                                  1e-119D,
                                                  1e-118D,
                                                  1e-117D,
                                                  1e-116D,
                                                  1e-115D,
                                                  1e-114D,
                                                  1e-113D,
                                                  1e-112D,
                                                  1e-111D,
                                                  1e-110D,
                                                  1e-109D,
                                                  1e-108D,
                                                  1e-107D,
                                                  1e-106D,
                                                  1e-105D,
                                                  1e-104D,
                                                  1e-103D,
                                                  1e-102D,
                                                  1e-101D,
                                                  1e-100D,
                                                  1e-99D,
                                                  1e-98D,
                                                  1e-97D,
                                                  1e-96D,
                                                  1e-95D,
                                                  1e-94D,
                                                  1e-93D,
                                                  1e-92D,
                                                  1e-91D,
                                                  1e-90D,
                                                  1e-89D,
                                                  1e-88D,
                                                  1e-87D,
                                                  1e-86D,
                                                  1e-85D,
                                                  1e-84D,
                                                  1e-83D,
                                                  1e-82D,
                                                  1e-81D,
                                                  1e-80D,
                                                  1e-79D,
                                                  1e-78D,
                                                  1e-77D,
                                                  1e-76D,
                                                  1e-75D,
                                                  1e-74D,
                                                  1e-73D,
                                                  1e-72D,
                                                  1e-71D,
                                                  1e-70D,
                                                  1e-69D,
                                                  1e-68D,
                                                  1e-67D,
                                                  1e-66D,
                                                  1e-65D,
                                                  1e-64D,
                                                  1e-63D,
                                                  1e-62D,
                                                  1e-61D,
                                                  1e-60D,
                                                  1e-59D,
                                                  1e-58D,
                                                  1e-57D,
                                                  1e-56D,
                                                  1e-55D,
                                                  1e-54D,
                                                  1e-53D,
                                                  1e-52D,
                                                  1e-51D,
                                                  1e-50D,
                                                  1e-49D,
                                                  1e-48D,
                                                  1e-47D,
                                                  1e-46D,
                                                  1e-45D,
                                                  1e-44D,
                                                  1e-43D,
                                                  1e-42D,
                                                  1e-41D,
                                                  1e-40D,
                                                  1e-39D,
                                                  1e-38D,
                                                  1e-37D,
                                                  1e-36D,
                                                  1e-35D,
                                                  1e-34D,
                                                  1e-33D,
                                                  1e-32D,
                                                  1e-31D,
                                                  1e-30D,
                                                  1e-29D,
                                                  1e-28D,
                                                  1e-27D,
                                                  1e-26D,
                                                  1e-25D,
                                                  1e-24D,
                                                  1e-23D,
                                                  1e-22D,
                                                  1e-21D,
                                                  1e-20D,
                                                  1e-19D,
                                                  1e-18D,
                                                  1e-17D,
                                                  1e-16D,
                                                  1e-15D,
                                                  1e-14D,
                                                  1e-13D,
                                                  1e-12D,
                                                  1e-11D,
                                                  1e-10D,
                                                  1e-9D,
                                                  1e-8D,
                                                  1e-7D,
                                                  1e-6D,
                                                  1e-5D,
                                                  1e-4D,
                                                  1e-3D,
                                                  1e-2D,
                                                  1e-1D,
                                                  1e0D,
                                                  1e1D,
                                                  1e2D,
                                                  1e3D,
                                                  1e4D,
                                                  1e5D,
                                                  1e6D,
                                                  1e7D,
                                                  1e8D,
                                                  1e9D,
                                                  1e10D,
                                                  1e11D,
                                                  1e12D,
                                                  1e13D,
                                                  1e14D,
                                                  1e15D,
                                                  1e16D,
                                                  1e17D,
                                                  1e18D,
                                                  1e19D,
                                                  1e20D,
                                                  1e21D,
                                                  1e22D,
                                                  1e23D,
                                                  1e24D,
                                                  1e25D,
                                                  1e26D,
                                                  1e27D,
                                                  1e28D,
                                                  1e29D,
                                                  1e30D,
                                                  1e31D,
                                                  1e32D,
                                                  1e33D,
                                                  1e34D,
                                                  1e35D,
                                                  1e36D,
                                                  1e37D,
                                                  1e38D,
                                                  1e39D,
                                                  1e40D,
                                                  1e41D,
                                                  1e42D,
                                                  1e43D,
                                                  1e44D,
                                                  1e45D,
                                                  1e46D,
                                                  1e47D,
                                                  1e48D,
                                                  1e49D,
                                                  1e50D,
                                                  1e51D,
                                                  1e52D,
                                                  1e53D,
                                                  1e54D,
                                                  1e55D,
                                                  1e56D,
                                                  1e57D,
                                                  1e58D,
                                                  1e59D,
                                                  1e60D,
                                                  1e61D,
                                                  1e62D,
                                                  1e63D,
                                                  1e64D,
                                                  1e65D,
                                                  1e66D,
                                                  1e67D,
                                                  1e68D,
                                                  1e69D,
                                                  1e70D,
                                                  1e71D,
                                                  1e72D,
                                                  1e73D,
                                                  1e74D,
                                                  1e75D,
                                                  1e76D,
                                                  1e77D,
                                                  1e78D,
                                                  1e79D,
                                                  1e80D,
                                                  1e81D,
                                                  1e82D,
                                                  1e83D,
                                                  1e84D,
                                                  1e85D,
                                                  1e86D,
                                                  1e87D,
                                                  1e88D,
                                                  1e89D,
                                                  1e90D,
                                                  1e91D,
                                                  1e92D,
                                                  1e93D,
                                                  1e94D,
                                                  1e95D,
                                                  1e96D,
                                                  1e97D,
                                                  1e98D,
                                                  1e99D,
                                                  1e100D,
                                                  1e101D,
                                                  1e102D,
                                                  1e103D,
                                                  1e104D,
                                                  1e105D,
                                                  1e106D,
                                                  1e107D,
                                                  1e108D,
                                                  1e109D,
                                                  1e110D,
                                                  1e111D,
                                                  1e112D,
                                                  1e113D,
                                                  1e114D,
                                                  1e115D,
                                                  1e116D,
                                                  1e117D,
                                                  1e118D,
                                                  1e119D,
                                                  1e120D,
                                                  1e121D,
                                                  1e122D,
                                                  1e123D,
                                                  1e124D,
                                                  1e125D,
                                                  1e126D,
                                                  1e127D,
                                                  1e128D,
                                                  1e129D,
                                                  1e130D,
                                                  1e131D,
                                                  1e132D,
                                                  1e133D,
                                                  1e134D,
                                                  1e135D,
                                                  1e136D,
                                                  1e137D,
                                                  1e138D,
                                                  1e139D,
                                                  1e140D,
                                                  1e141D,
                                                  1e142D,
                                                  1e143D,
                                                  1e144D,
                                                  1e145D,
                                                  1e146D,
                                                  1e147D,
                                                  1e148D,
                                                  1e149D,
                                                  1e150D,
                                                  1e151D,
                                                  1e152D,
                                                  1e153D,
                                                  1e154D,
                                                  1e155D,
                                                  1e156D,
                                                  1e157D,
                                                  1e158D,
                                                  1e159D,
                                                  1e160D,
                                                  1e161D,
                                                  1e162D,
                                                  1e163D,
                                                  1e164D,
                                                  1e165D,
                                                  1e166D,
                                                  1e167D,
                                                  1e168D,
                                                  1e169D,
                                                  1e170D,
                                                  1e171D,
                                                  1e172D,
                                                  1e173D,
                                                  1e174D,
                                                  1e175D,
                                                  1e176D,
                                                  1e177D,
                                                  1e178D,
                                                  1e179D,
                                                  1e180D,
                                                  1e181D,
                                                  1e182D,
                                                  1e183D,
                                                  1e184D,
                                                  1e185D,
                                                  1e186D,
                                                  1e187D,
                                                  1e188D,
                                                  1e189D,
                                                  1e190D,
                                                  1e191D,
                                                  1e192D,
                                                  1e193D,
                                                  1e194D,
                                                  1e195D,
                                                  1e196D,
                                                  1e197D,
                                                  1e198D,
                                                  1e199D,
                                                  1e200D,
                                                  1e201D,
                                                  1e202D,
                                                  1e203D,
                                                  1e204D,
                                                  1e205D,
                                                  1e206D,
                                                  1e207D,
                                                  1e208D,
                                                  1e209D,
                                                  1e210D,
                                                  1e211D,
                                                  1e212D,
                                                  1e213D,
                                                  1e214D,
                                                  1e215D,
                                                  1e216D,
                                                  1e217D,
                                                  1e218D,
                                                  1e219D,
                                                  1e220D,
                                                  1e221D,
                                                  1e222D,
                                                  1e223D,
                                                  1e224D,
                                                  1e225D,
                                                  1e226D,
                                                  1e227D,
                                                  1e228D,
                                                  1e229D,
                                                  1e230D,
                                                  1e231D,
                                                  1e232D,
                                                  1e233D,
                                                  1e234D,
                                                  1e235D,
                                                  1e236D,
                                                  1e237D,
                                                  1e238D,
                                                  1e239D,
                                                  1e240D,
                                                  1e241D,
                                                  1e242D,
                                                  1e243D,
                                                  1e244D,
                                                  1e245D,
                                                  1e246D,
                                                  1e247D,
                                                  1e248D,
                                                  1e249D,
                                                  1e250D,
                                                  1e251D,
                                                  1e252D,
                                                  1e253D,
                                                  1e254D,
                                                  1e255D,
                                                  1e256D,
                                                  1e257D,
                                                  1e258D,
                                                  1e259D,
                                                  1e260D,
                                                  1e261D,
                                                  1e262D,
                                                  1e263D,
                                                  1e264D,
                                                  1e265D,
                                                  1e266D,
                                                  1e267D,
                                                  1e268D,
                                                  1e269D,
                                                  1e270D,
                                                  1e271D,
                                                  1e272D,
                                                  1e273D,
                                                  1e274D,
                                                  1e275D,
                                                  1e276D,
                                                  1e277D,
                                                  1e278D,
                                                  1e279D,
                                                  1e280D,
                                                  1e281D,
                                                  1e282D,
                                                  1e283D,
                                                  1e284D,
                                                  1e285D,
                                                  1e286D,
                                                  1e287D,
                                                  1e288D,
                                                  1e289D,
                                                  1e290D,
                                                  1e291D,
                                                  1e292D,
                                                  1e293D,
                                                  1e294D,
                                                  1e295D,
                                                  1e296D,
                                                  1e297D,
                                                  1e298D,
                                                  1e299D,
                                                  1e300D,
                                                  1e301D,
                                                  1e302D,
                                                  1e303D,
                                                  1e304D,
                                                  1e305D,
                                                  1e306D,
                                                  1e307D,
                                                  1e308D };

  public void appendFormatted (final StringBuilder s,
                               final double pd,
                               final int numFractDigits,
                               final char cDecimalPoint,
                               final char cThousandsSeparator,
                               final int nNumDigitsSeparated,
                               final char cNegativePrefix,
                               final char cNegativeSuffix)
  {
    double d = pd;
    // First check for the special cases, +/-infinity, Not-a-number and -0.0
    if (d == Double.NEGATIVE_INFINITY)
    {
      // d == -Infinity
      if (cNegativePrefix != NO_PREFIX_OR_SUFFIX)
        s.append (cNegativePrefix);
      s.append (INFINITY);
      if (cNegativeSuffix != NO_PREFIX_OR_SUFFIX)
        s.append (cNegativeSuffix);
    }
    else
      if (d == Double.POSITIVE_INFINITY)
        // d == Infinity
        s.append (INFINITY);
      else
        if (d != d)
          // d == NaN
          s.append (NaN);
        else
          if (d == 0.0)
          {
            if ((Double.doubleToLongBits (d) & DOUBLE_SIGN_MASK) != 0)
            {
              // d == -0.0
              if (cNegativePrefix != NO_PREFIX_OR_SUFFIX)
                s.append (cNegativePrefix);
              s.append ('0').append (cDecimalPoint).append (ZEROS[numFractDigits]);
              if (cNegativeSuffix != NO_PREFIX_OR_SUFFIX)
                s.append (cNegativeSuffix);
            }
            else
              // d == 0.0
              s.append ('0').append (cDecimalPoint).append (ZEROS[numFractDigits]);
          }
          else
          {
            // convert to a positive format, and record whether we have a
            // negative
            // number so that we know later whether to add the negativeSuffix
            boolean negative = false;
            if (d < 0)
            {
              // Even if the number is negative, we only need to set the
              // negative flag if there is a printable negativeSuffix
              if (cNegativeSuffix != NO_PREFIX_OR_SUFFIX)
                negative = true;
              if (cNegativePrefix != NO_PREFIX_OR_SUFFIX)
                s.append (cNegativePrefix);
              d = -d;
            }
            // Find the magnitude. This is basically the exponent in normal
            // form.
            final int magnitude = _magnitude (d);

            // First off, if the number is too small for the given format, we
            // only print 0.0..., which makes this real quick
            if ((magnitude + numFractDigits) < 0)
            {
              _appendNearlyZeroNumber (s, d, magnitude, numFractDigits, cDecimalPoint);
              if (negative)
                s.append (cNegativeSuffix);
              return;
            }
            long l;
            // Now scale the double to the biggest long value we need
            // We need to handle the smallest magnitudes differently because of
            // rounding errors

            // This test is unlikely to ever be true. It would require
            // numFractDigits
            // to be 305 or more, which is pretty unlikely.
            if (magnitude < -305)
              l = (long) ((d * 1E18) / TENTH_POWERS[magnitude + 324]);
            else
              l = (long) (d / TENTH_POWERS[magnitude + 323 - 17]);

            // And round up if necessary. Add one to the numFractDigits digit if
            // the
            // numFractDigits+1 digit is 5 or greater. It is useful to know that
            // given a long, l, the nth digit is obtained using the formula
            // nthDigit = (l/(tenthPower(l)/l_tenthPowers[n-1]))%10;

            final long l_tenthPower = _tenthPower (l);
            // The numFractDigits+1 digit of the double is the
            // numFractDigits+1+magnitude digit of the long.
            // We only need worry about digits within the long. Very large
            // numbers are
            // not rounded because all the digits after the decimal points are 0
            // anyway
            if (numFractDigits + magnitude + 1 < l_tenthPowers.length)
            {
              final long digit = (l / (l_tenthPower / l_tenthPowers[numFractDigits + magnitude + 1])) % 10;
              if (digit >= 5)
              {
                l += l_tenthPower / l_tenthPowers[numFractDigits + magnitude];
              }
            }
            // And now we just print out our long, with the decimal point
            // character
            // inserted in the right place, using as many places as we wanted.
            appendAsDouble (s,
                            l,
                            l_tenthPower,
                            magnitude,
                            numFractDigits,
                            cDecimalPoint,
                            cThousandsSeparator,
                            nNumDigitsSeparated,
                            cNegativePrefix,
                            cNegativeSuffix);

            // Finally, append the negativeSuffix if necessary
            if (negative)
              s.append (cNegativeSuffix);
          }
  }

  public void appendAsDouble (final StringBuilder s,
                              final long pl,
                              final long pl_mag,
                              final int pd_magnitude,
                              final int pnumFractDigits,
                              final char decimalPoint,
                              final char thousandsSeparator,
                              final int numDigitsSeparated,
                              @SuppressWarnings ("unused") final char cNegativePrefix,
                              @SuppressWarnings ("unused") final char cNegativeSuffix)
  {
    long l = pl;
    long l_mag = pl_mag;
    int d_magnitude = pd_magnitude;
    int numFractDigits = pnumFractDigits;
    // If the magnitude is negative, we have a 0.xxx number
    if (d_magnitude < 0)
    {
      s.append ('0').append (decimalPoint).append (ZEROS[-d_magnitude - 1]);
      // And just print successive digits until we have reached numFractDigits
      // First decrement numFractDigits by the number of digits already printed
      numFractDigits += d_magnitude;

      // get the magnitude of l
      long c;
      while (numFractDigits-- >= 0)
      {
        // Get the leading character (e.g. '62345/10000 = 6' using
        // integer-divide)
        c = l / l_mag;
        // Append the digit character for this digit (e.g. number is 6, so
        // character is '6')
        s.append (charForDigit[(int) c]);
        // Multiply by the leading digit by the magnitude so that we can
        // eliminate the leading digit
        // (e.g. 6 * 10000 = 60000)
        c *= l_mag;
        // and eliminate the leading digit (e.g. 62345-60000 = 2345)
        if (c <= l)
          l -= c;
        // Decrease the magnitude by 10, and repeat the loop.
        l_mag = l_mag / 10;
      }
    }
    else
    {
      // Just keep printing until magnitude is 0
      long c;
      while (d_magnitude-- >= 0)
      {
        if (l_mag == 0)
        {
          s.append ('0');
          continue;
        }
        // Get the leading character (e.g. '62345/10000 = 6' using
        // integer-divide)
        c = l / l_mag;
        // Append the digit character for this digit (e.g. number is 6, so
        // character is '6')
        s.append (charForDigit[(int) c]);

        // Don't forget about the thousands separator
        if (d_magnitude % numDigitsSeparated == (numDigitsSeparated - 1))
          s.append (thousandsSeparator);

        // Multiply by the leading digit by the magnitude so that we can
        // eliminate the leading digit
        // (e.g. 6 * 10000 = 60000)
        c *= l_mag;
        // and eliminate the leading digit (e.g. 62345-60000 = 2345)
        if (c <= l)
          l -= c;
        // Decrease the magnitude by 10, and repeat the loop.
        l_mag = l_mag / 10;
      }
      s.append (decimalPoint);
      if (l_mag == 0)
        s.append (ZEROS[numFractDigits]);
      else
      {
        while (numFractDigits-- > 0)
        {
          if (l_mag == 0)
          {
            s.append ('0');
            continue;
          }
          // Get the leading character (e.g. '62345/10000 = 6' using
          // integer-divide)
          c = l / l_mag;
          // Append the digit character for this digit (e.g. number is 6, so
          // character is '6')
          s.append (charForDigit[(int) c]);
          // Multiply by the leading digit by the magnitude so that we can
          // eliminate the leading digit
          // (e.g. 6 * 10000 = 60000)
          c *= l_mag;
          // and eliminate the leading digit (e.g. 62345-60000 = 2345)
          if (c <= l)
            l -= c;
          // Decrease the magnitude by 10, and repeat the loop.
          l_mag = l_mag / 10;
        }
      }
    }
  }

  private void _appendNearlyZeroNumber (final StringBuilder s,
                                        final double dValue,
                                        final int nValueMagnitude,
                                        final int nNumFractDigits,
                                        final char cDecimalPoint)
  {
    if (nValueMagnitude + nNumFractDigits == -1)
    {
      // Possibly too small, depends on whether the top digit is 5 or greater
      // So we have to scale to get the leading digit
      int i;
      if (nValueMagnitude < -305)
        // Probably not necessary. Who is going to print 305 places?
        i = (int) ((dValue * 1E19) / TENTH_POWERS[nValueMagnitude + 324 + 18]);
      else
        i = (int) (dValue / TENTH_POWERS[nValueMagnitude + 323]);

      if (i >= 5)
      {
        // Not too small, we get to round up
        s.append ('0').append (cDecimalPoint).append (ZEROS[nNumFractDigits - 1]);
        s.append ('1');
      }
      else
      {
        // Definitely too small. Just print zeros
        s.append ('0').append (cDecimalPoint).append (ZEROS[nNumFractDigits]);
      }
    }
    else
    {
      // Definitely too small
      s.append ('0').append (cDecimalPoint).append (ZEROS[nNumFractDigits]);
    }
  }

  /**
   * Assumes i is positive. Returns the magnitude of i in base 10.
   */
  private static long _tenthPower (final long i)
  {
    if (i < 10L)
      return 1;
    if (i < 100L)
      return 10L;
    if (i < 1000L)
      return 100L;
    if (i < 10000L)
      return 1000L;
    if (i < 100000L)
      return 10000L;
    if (i < 1000000L)
      return 100000L;
    if (i < 10000000L)
      return 1000000L;
    if (i < 100000000L)
      return 10000000L;
    if (i < 1000000000L)
      return 100000000L;
    if (i < 10000000000L)
      return 1000000000L;
    if (i < 100000000000L)
      return 10000000000L;
    if (i < 1000000000000L)
      return 100000000000L;
    if (i < 10000000000000L)
      return 1000000000000L;
    if (i < 100000000000000L)
      return 10000000000000L;
    if (i < 1000000000000000L)
      return 100000000000000L;
    if (i < 10000000000000000L)
      return 1000000000000000L;
    if (i < 100000000000000000L)
      return 10000000000000000L;
    if (i < 1000000000000000000L)
      return 100000000000000000L;
    return 1000000000000000000L;
  }

  private static int _magnitude (final double d)
  {
    // It works. What else can I say.
    final long doubleToLongBits = Double.doubleToLongBits (d);
    // 0.301029995663981 = log10(2)
    int magnitude = (int) ((((doubleToLongBits & DOUBLE_EXP_MASK) >> DOUBLE_EXP_SHIFT) - DOUBLE_EXP_BIAS) *
                           0.301029995663981);

    if (magnitude < -323)
      magnitude = -323;
    else
      if (magnitude > 308)
        magnitude = 308;

    if (d >= TENTH_POWERS[magnitude + 323])
    {
      while (magnitude < 309 && d >= TENTH_POWERS[magnitude + 323])
        magnitude++;
      magnitude--;
    }
    else
    {
      while (magnitude > -324 && d < TENTH_POWERS[magnitude + 323])
        magnitude--;
    }
    return magnitude;
  }

  private static final long [] l_tenthPowers = { 1,
                                                 10L,
                                                 100L,
                                                 1000L,
                                                 10000L,
                                                 100000L,
                                                 1000000L,
                                                 10000000L,
                                                 100000000L,
                                                 1000000000L,
                                                 10000000000L,
                                                 100000000000L,
                                                 1000000000000L,
                                                 10000000000000L,
                                                 100000000000000L,
                                                 1000000000000000L,
                                                 10000000000000000L,
                                                 100000000000000000L,
                                                 1000000000000000000L, };

  public static long getNthDigit (final long l, final int n)
  {
    return (l / (_tenthPower (l) / l_tenthPowers[n - 1])) % 10;
  }

  public static void main (final String [] args)
  {
    final double [] ds = { 100D,
                           234000.567D,
                           2340000.56789D,
                           23400000.56789D,
                           234000000.56789D,
                           567.89023D,
                           -1.234D,
                           0.2D,
                           0.03D,
                           0.00235D,
                           999,
                           900 - 0.000456D,
                           -0.000543D,
                           -0.0000456D,
                           -0.0000543D,
                           Double.MIN_VALUE + 1,
                           Double.MAX_VALUE,
                           Float.MIN_VALUE,
                           Float.MAX_VALUE,
                           Long.MIN_VALUE,
                           Long.MAX_VALUE,
                           Integer.MIN_VALUE,
                           Integer.MAX_VALUE };

    final int repeat = 10000;
    // int repeat = 1;

    _main_adj (repeat * 5, "doubles", ds);
    _main_adj (repeat * 5, "doubles", ds);
  }

  @SuppressFBWarnings ("UC_USELESS_OBJECT")
  private static void _main_adj (final int repeat, final String name, final double [] arr)
  {
    long time1;
    final MainDoubleToString a = new MainDoubleToString ();

    LOGGER.info ("The " + name);
    LOGGER.info ("    " + Arrays.toString (arr));
    LOGGER.info ("are appended to a StringBuilder one by one " + repeat + " times.");
    Runtime.getRuntime ().gc ();

    LOGGER.info ("Starting test");
    time1 = System.currentTimeMillis ();
    StringBuilder s1 = new StringBuilder ();
    for (int i = repeat; i > 0; i--)
      for (int j = arr.length - 1; j >= 0; j--)
        a.append (s1, arr[j]);
    time1 = System.currentTimeMillis () - time1;
    LOGGER.info ("  The append        took " + time1 + " milliseconds");
    s1 = null;
    Runtime.getRuntime ().gc ();

    LOGGER.info ("Starting test");
    time1 = System.currentTimeMillis ();
    StringBuilder s2 = new StringBuilder ();
    for (int i = repeat; i > 0; i--)
      for (int j = arr.length - 1; j >= 0; j--)
        a.appendFormatted (s2, arr[j], 4, ',', '.', 3, '-', NO_PREFIX_OR_SUFFIX);
    time1 = System.currentTimeMillis () - time1;
    LOGGER.info ("  The format append took " + time1 + " milliseconds");
    s2 = null;
    Runtime.getRuntime ().gc ();

    LOGGER.info ("Starting test");
    StringBuilder s3 = new StringBuilder ();
    time1 = System.currentTimeMillis ();
    for (int i = repeat; i > 0; i--)
      for (int j = arr.length - 1; j >= 0; j--)
        s3.append (arr[j]);
    time1 = System.currentTimeMillis () - time1;
    LOGGER.info ("  The StringBuilder took " + time1 + " milliseconds");
    s3 = null;
    Runtime.getRuntime ().gc ();

    StringBuffer sb1 = new StringBuffer ();
    LOGGER.info ("Starting test");
    time1 = System.currentTimeMillis ();
    final DecimalFormat format = new DecimalFormat ("#,##0.0000",
                                                    DecimalFormatSymbols.getInstance (Locale.getDefault (Category.FORMAT)));
    final FieldPosition f = new FieldPosition (0);
    for (int i = repeat; i > 0; i--)
      for (int j = arr.length - 1; j >= 0; j--)
        format.format (arr[j], sb1, f);
    time1 = System.currentTimeMillis () - time1;
    LOGGER.info ("  The DecimalFormat took " + time1 + " milliseconds");
    sb1 = null;
    Runtime.getRuntime ().gc ();

    s1 = new StringBuilder ();
    for (int j = arr.length - 1; j >= 0; j--)
    {
      if (s1.length () > 0)
        s1.append (", ");
      a.append (s1, arr[j]);
    }
    s2 = new StringBuilder ();
    for (int j = arr.length - 1; j >= 0; j--)
    {
      if (s2.length () > 0)
        s2.append (", ");
      a.appendFormatted (s2, arr[j], 4, ',', '.', 3, '-', NO_PREFIX_OR_SUFFIX);
    }
    s3 = new StringBuilder ();
    time1 = System.currentTimeMillis ();
    for (int j = arr.length - 1; j >= 0; j--)
    {
      if (s3.length () > 0)
        s3.append (", ");
      s3.append (arr[j]);
    }
    sb1 = new StringBuffer ();
    for (int j = arr.length - 1; j >= 0; j--)
    {
      if (sb1.length () > 0)
        sb1.append (", ");
      format.format (arr[j], sb1, f);
    }
    LOGGER.info ("    " + s1);
    LOGGER.info ("    " + s2);
    LOGGER.info ("    " + s3);
    LOGGER.info ("    " + sb1);
    LOGGER.info ("");
  }

  public void append (final StringBuilder s, final double pd)
  {
    double d = pd;
    if (d == Double.NEGATIVE_INFINITY)
      s.append (NEGATIVE_INFINITY);
    else
      if (d == Double.POSITIVE_INFINITY)
        s.append (POSITIVE_INFINITY);
      else
        if (d != d)
          s.append (NaN);
        else
          if (d == 0.0)
          {
            if ((Double.doubleToLongBits (d) & DOUBLE_SIGN_MASK) != 0)
              s.append ('-');
            s.append (DOUBLE_ZERO);
          }
          else
          {
            if (d < 0)
            {
              s.append ('-');
              d = -d;
            }
            if (d >= 0.001 && d < 0.01)
            {
              long i = (long) (d * 1E20);
              i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
              s.append (DOUBLE_ZERO2);
              _appendFractDigits (s, i, -1);
            }
            else
              if (d >= 0.01 && d < 0.1)
              {
                long i = (long) (d * 1E19);
                i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                s.append (DOUBLE_ZERO);
                _appendFractDigits (s, i, -1);
              }
              else
                if (d >= 0.1 && d < 1)
                {
                  long i = (long) (d * 1E18);
                  i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                  s.append (DOUBLE_ZERO0);
                  _appendFractDigits (s, i, -1);
                }
                else
                  if (d >= 1 && d < 10)
                  {
                    long i = (long) (d * 1E17);
                    i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                    _appendFractDigits (s, i, 1);
                  }
                  else
                    if (d >= 10 && d < 100)
                    {
                      long i = (long) (d * 1E16);
                      i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                      _appendFractDigits (s, i, 2);
                    }
                    else
                      if (d >= 100 && d < 1000)
                      {
                        long i = (long) (d * 1E15);
                        i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                        _appendFractDigits (s, i, 3);
                      }
                      else
                        if (d >= 1000 && d < 10000)
                        {
                          long i = (long) (d * 1E14);
                          i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                          _appendFractDigits (s, i, 4);
                        }
                        else
                          if (d >= 10000 && d < 100000)
                          {
                            long i = (long) (d * 1E13);
                            i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                            _appendFractDigits (s, i, 5);
                          }
                          else
                            if (d >= 100000 && d < 1000000)
                            {
                              long i = (long) (d * 1E12);
                              i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                              _appendFractDigits (s, i, 6);
                            }
                            else
                              if (d >= 1000000 && d < 10000000)
                              {
                                long i = (long) (d * 1E11);
                                i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                                _appendFractDigits (s, i, 7);
                              }
                              else
                              {
                                final int magnitude = _magnitude (d);
                                long i;
                                if (magnitude < -305)
                                  i = (long) (d * 1E18 / TENTH_POWERS[magnitude + 324]);
                                else
                                  i = (long) (d / TENTH_POWERS[magnitude + 323 - 17]);
                                i = i % 100 >= 50 ? (i / 100) + 1 : i / 100;
                                _appendFractDigits (s, i, 1);
                                s.append ('E');
                                append (s, magnitude);
                              }
          }
  }

  public void append (final StringBuilder s, final int pi)
  {
    int i = pi;
    if (i < 0)
    {
      if (i == Integer.MIN_VALUE)
      {
        // cannot make this positive due to integer overflow
        s.append ("-2147483648");
        return;
      }
      s.append ('-');
      i = -i;
    }
    int c;
    if (i < 10)
    {
      // one digit
      s.append (charForDigit[i]);
    }
    else
      if (i < 100)
      {
        // two digits
        s.append (charForDigit[i / 10]);
        s.append (charForDigit[i % 10]);
      }
      else
        if (i < 1000)
        {
          // three digits
          s.append (charForDigit[i / 100]);
          s.append (charForDigit[(c = i % 100) / 10]);
          s.append (charForDigit[c % 10]);
        }
        else
          if (i < 10000)
          {
            // four digits
            s.append (charForDigit[i / 1000]);
            s.append (charForDigit[(c = i % 1000) / 100]);
            s.append (charForDigit[(c %= 100) / 10]);
            s.append (charForDigit[c % 10]);
          }
          else
            if (i < 100000)
            {
              // five digits
              s.append (charForDigit[i / 10000]);
              s.append (charForDigit[(c = i % 10000) / 1000]);
              s.append (charForDigit[(c %= 1000) / 100]);
              s.append (charForDigit[(c %= 100) / 10]);
              s.append (charForDigit[c % 10]);
            }
            else
              if (i < 1000000)
              {
                // six digits
                s.append (charForDigit[i / 100000]);
                s.append (charForDigit[(c = i % 100000) / 10000]);
                s.append (charForDigit[(c %= 10000) / 1000]);
                s.append (charForDigit[(c %= 1000) / 100]);
                s.append (charForDigit[(c %= 100) / 10]);
                s.append (charForDigit[c % 10]);
              }
              else
                if (i < 10000000)
                {
                  // seven digits
                  s.append (charForDigit[i / 1000000]);
                  s.append (charForDigit[(c = i % 1000000) / 100000]);
                  s.append (charForDigit[(c %= 100000) / 10000]);
                  s.append (charForDigit[(c %= 10000) / 1000]);
                  s.append (charForDigit[(c %= 1000) / 100]);
                  s.append (charForDigit[(c %= 100) / 10]);
                  s.append (charForDigit[c % 10]);
                }
                else
                  if (i < 100000000)
                  {
                    // eight digits
                    s.append (charForDigit[i / 10000000]);
                    s.append (charForDigit[(c = i % 10000000) / 1000000]);
                    s.append (charForDigit[(c %= 1000000) / 100000]);
                    s.append (charForDigit[(c %= 100000) / 10000]);
                    s.append (charForDigit[(c %= 10000) / 1000]);
                    s.append (charForDigit[(c %= 1000) / 100]);
                    s.append (charForDigit[(c %= 100) / 10]);
                    s.append (charForDigit[c % 10]);
                  }
                  else
                    if (i < 1000000000)
                    {
                      // nine digits
                      s.append (charForDigit[i / 100000000]);
                      s.append (charForDigit[(c = i % 100000000) / 10000000]);
                      s.append (charForDigit[(c %= 10000000) / 1000000]);
                      s.append (charForDigit[(c %= 1000000) / 100000]);
                      s.append (charForDigit[(c %= 100000) / 10000]);
                      s.append (charForDigit[(c %= 10000) / 1000]);
                      s.append (charForDigit[(c %= 1000) / 100]);
                      s.append (charForDigit[(c %= 100) / 10]);
                      s.append (charForDigit[c % 10]);
                    }
                    else
                    {
                      // ten digits
                      s.append (charForDigit[i / 1000000000]);
                      s.append (charForDigit[(c = i % 1000000000) / 100000000]);
                      s.append (charForDigit[(c %= 100000000) / 10000000]);
                      s.append (charForDigit[(c %= 10000000) / 1000000]);
                      s.append (charForDigit[(c %= 1000000) / 100000]);
                      s.append (charForDigit[(c %= 100000) / 10000]);
                      s.append (charForDigit[(c %= 10000) / 1000]);
                      s.append (charForDigit[(c %= 1000) / 100]);
                      s.append (charForDigit[(c %= 100) / 10]);
                      s.append (charForDigit[c % 10]);
                    }
  }

  private static void _appendFractDigits (final StringBuilder s, final long pi, final int pdecimalOffset)
  {
    long i = pi;
    int decimalOffset = pdecimalOffset;
    long mag = _tenthPower (i);
    long c;
    while (i > 0)
    {
      c = i / mag;
      s.append (charForDigit[(int) c]);
      decimalOffset--;
      if (decimalOffset == 0)
        s.append ('.');
      c *= mag;
      if (c <= i)
        i -= c;
      mag = mag / 10;
    }
    if (i != 0)
      s.append (charForDigit[(int) i]);
    else
      if (decimalOffset > 0)
      {
        s.append (ZEROS[decimalOffset]);
        decimalOffset = 1;
      }

    decimalOffset--;
    if (decimalOffset == 0)
      s.append (DOT_ZERO);
    else
      if (decimalOffset == -1)
        s.append ('0');
  }
}
