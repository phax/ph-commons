package com.helger.commons.error;

import java.io.Serializable;

import com.helger.commons.error.level.IHasErrorLevels;
import com.helger.commons.errorlist.IErrorBase;
import com.helger.commons.lang.IHasSize;

/**
 * Base list
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IErrorBaseList <IMPLTYPE extends IErrorBase <IMPLTYPE>>
                                extends IHasErrorLevels <IMPLTYPE>, IHasSize, Serializable
{}
