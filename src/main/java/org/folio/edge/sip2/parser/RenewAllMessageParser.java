package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.RenewAll.builder;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.requests.RenewAll.RenewAllBuilder;

/**
 * Parser for the Renew All message.
 *
 * @author mreno-EBSCO
 *
 */
public class RenewAllMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public RenewAllMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Renew All message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Renew All message.
   */
  public RenewAll parse(String message) {
    final RenewAllBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final ZonedDateTime transactionDate = parseDateTime(messageChars);
    builder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          builder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          builder.patronIdentifier(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          builder.patronPassword(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          builder.terminalPassword(valueString);
          break;
        case BO:
          // fee acknowledged: 1-char, optional field field
          builder.feeAcknowledged(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Renew All field with value {}", valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }
}
