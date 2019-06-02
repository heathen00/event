package com.ht.wfp3.message;

import java.util.List;

/**
 * The message description. The description contains a human readable string that supports i18n with
 * a defined limited length. The message string is optionally parameterized. The message string is
 * specified explicitly before the message subsystem startup including mark-up indicating where
 * parameters are inserted. The parameters are applied to the message using the standard Java
 * Formatter functionality.
 * 
 * @author nickl
 *
 */
public interface Description extends UniqueComponent<Description> {

  /**
   * Returns the message description text after all specified formatting has been applied, if any
   * formatting information exists.
   * 
   * @return A string text message with any Description formatting applied. If the Description
   *         contains no formatting, then this method will return the same string as
   *         "getUnformattedText()".
   */
  String getFormattedText();

  /**
   * Returns the optional list of parameters that will be used to format the description.
   * 
   * @return A list of parameters that will be applied to the description text when it is formatted.
   *         If the description text is not parameterized and contains no formatting, then an empty
   *         list is returned.
   */
  List<? extends Object> getFormattedTextParameters();

  /**
   * Returns the unformatted Description text, thus, if the Description text contains formatting
   * mark-up, the mark-up will be present in this text.
   * 
   * @return a string containing the message text. If the Description text contains formatting
   *         mark-up it will be present in this string. If the Description text does not contain any
   *         formatting mark-up, then this string will be the same as returned by the method
   *         "getFormattedText()".
   */
  String getUnformattedText();
}