package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.junit.jupiter.api.Test;

class CheckinMessageParserTests {
  @Test
  void testParse() {
    CheckinMessageParser parser =
        new CheckinMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final OffsetDateTime returnDate = transactionDate.plusMinutes(5);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String returnDateString = formatter.format(returnDate);
    final Checkin checkin = parser.parse(
        "Y" + transactionDateString + returnDateString
        + "APcirc_desk|ABSomeBook|AC|CHAutographed|"
        + "AOuniversity_id|BIN|");

    assertEquals(TRUE, checkin.getNoBlock());
    assertEquals(transactionDate, checkin.getTransactionDate());
    assertEquals(returnDate, checkin.getReturnDate());
    assertEquals("circ_desk", checkin.getCurrentLocation());
    assertEquals("university_id", checkin.getInstitutionId());
    assertEquals("SomeBook", checkin.getItemIdentifier());
    assertEquals("", checkin.getTerminalPassword());
    assertEquals("Autographed", checkin.getItemProperties());
    assertEquals(FALSE, checkin.getCancel());
  }
}
