/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.flights;

/*

#	Element	Mandatory	Size	Sample	Remark
1	Format Code	M	1	M	Always “M”
2	Number of legs encoded	M	1
3	Passenger Name	M	20
4	Electronic Ticket Indicator	M	2	E
5	Operating carrier PNR Code	M	7
6	Origin IATA Code	M	3	FRA	Airport Code
7	Destination IATA Code	M	3	SIN	Airport Code
8	Operating carrier IATA Code	M	3	LH	Airline
9	Flight Number	M	5	3456
10	Date of Flight	M	3	280	Julian Date
11	Compartment Code	M	1	B	First, Business, Economy
12	Seat Number	M	4	25A
13	Check-in Sequence Number	M	5	0012
14	Passenger Status	M	1	00
15	Size of optional Block	M	2	5D	hexadecimal
16	Start Version Number		1	>	Always “>”
17	Version Number		1	5
18	Field Size of follow ing structured message		2
19	Passenger Description		1
20	Source of check-in		1
21	Source of Boarding Pass Issuance		1
22	Date of Issue of Boarding Pass (Julian Date)		4
23	Document Type		1
24	Airline Designator of boarding pass issuer		3
25	Baggage Tag Licence Plate Number 1		13
26	Baggage Tag Licence Plate Number 2		13
27	Baggage Tag Licence Plate Number 3		13
28	Field Size of follow ing structured message		2
29	Airline Numeric Code		3
30	Document Form/Serial Number		10
 */

import com.android.launcher3.Utilities;
import java.io.Serializable;
import kotlin.jvm.functions.Function2;

public class Flight implements Serializable  {
    public String legs;
    public String formattedName;
    public String electronic;
    public String pnr;
    public String origIata;
    public String destIata;
    public String opIata;
    public String flightNumb;
    public String julDay;
    public char compCode;
    public String seatNumb;
    public String checkInSeqNumb;
    public String passengerStat;
    public String optBlkSize;
    /*
     * begin optional flight data fields
     */
    public char stVerNumb;
    public String passengerDescription;
    public String chkInSrc;
    public String brdPassInc;
    public String docIssue;
    public String docType;
    public String airDesigBrdPassIssuer;
    public String bgTagLcPltNum1;
    public String bgTagLcPltNum2;
    public String bgTagLcPltNum3;
    public String fldSizeFlwStctrdMsg;
    public String airNumCd;
    public String docFormSerialNumber;

    public enum Types {
        M(1, true, ((flight, s) -> {
            if (!s.equals("M")) {
                Utilities.error("mandatory flight barcode check failed");
            }
            return null;
        })), LEGS(1, true, (flight, string) -> {
            flight.legs = string;
            return null;
        }), PSGNAME(20, true, (flight, s) -> {
            flight.formattedName = s;
            return null;
        }), ELECTRONIC(1, true, ((flight, s) -> {
            flight.electronic = s;
            return null;
        })), PNR(7, true, ((flight, s) -> {
            flight.pnr = s;
            return null;
        })), ORIGIATA(3, true, (flight, s) -> {
            flight.origIata = s;
            return null;
        }), DESTGIATA(3, true, (flight, s) -> {
            flight.destIata = s;
            return null;
        }), OPIATA(3, true, (flight, s) -> {
            flight.opIata = s;
            return null;
        }), FLGHTNUM(5, true, (flight, s) -> {
            flight.flightNumb = s;
            return null;
        }), JULDAY(3, true, (flight, s) -> {
            flight.julDay = s;
            return null;
        }), COMPCODE(1, true, (flight, s) -> {
            flight.compCode = s.toUpperCase().charAt(0);
            return null;
        }), SEATNUMB(4, true, (flight, s) -> {
            flight.seatNumb = s.toUpperCase();
            return null;
        }), CHECKINSEQNUMB(5, true, (flight, s) -> {
            flight.checkInSeqNumb = s;
            return null;
        }), PSGSTAT(1, true, (flight, s) -> {
            flight.passengerStat = s;
            return null;
        });

        Types(int size, boolean mandatory, Function2<Flight, String, Void> addToFlight) {
            this.size = size;
            this.mandatory = mandatory;
            this.addToFlight = addToFlight;
        }

        public int size;
        public boolean mandatory;
        public Function2<Flight, String, Void> addToFlight;
    }
}
