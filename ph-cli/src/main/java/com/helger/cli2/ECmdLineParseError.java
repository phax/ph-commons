package com.helger.cli2;

/**
 * Errors that can occur when parsing the command line.
 *
 * @author Philip Helger
 */
public enum ECmdLineParseError
{
  NON_REPEATABLE_OPTION_OCCURS_MORE_THAN_ONCE,
  TOO_LITTLE_REQUIRED_VALUES,
  REQUIRED_OPTION_IS_MISSING,
  ANOTHER_OPTION_OF_GROUP_ALREADY_PRESENT
}
