// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

// <GraphToIanaSnippet>
package com.example.graphtutorial;

import java.time.ZoneId;
import java.util.HashMap;

// Basic lookup for mapping Windows time zone identifiers to
// IANA identifiers
// Mappings taken from
// https://github.com/unicode-org/cldr/blob/master/common/supplemental/windowsZones.xml
public class GraphToIana {
    private static final HashMap<String, String> timeZoneIdMap = new HashMap<String, String>();
    static {
        timeZoneIdMap.put("Dateline Standard Time", "Etc/GMT+12");
        timeZoneIdMap.put("UTC-11", "Etc/GMT+11");
        timeZoneIdMap.put("Aleutian Standard Time", "America/Adak");
        timeZoneIdMap.put("Hawaiian Standard Time", "Pacific/Honolulu");
        timeZoneIdMap.put("Marquesas Standard Time", "Pacific/Marquesas");
        timeZoneIdMap.put("Alaskan Standard Time", "America/Anchorage");
        timeZoneIdMap.put("UTC-09", "Etc/GMT+9");
        timeZoneIdMap.put("Pacific Standard Time (Mexico)", "America/Tijuana");
        timeZoneIdMap.put("UTC-08", "Etc/GMT+8");
        timeZoneIdMap.put("Pacific Standard Time", "America/Los_Angeles");
        timeZoneIdMap.put("US Mountain Standard Time", "America/Phoenix");
        timeZoneIdMap.put("Mountain Standard Time (Mexico)", "America/Chihuahua");
        timeZoneIdMap.put("Mountain Standard Time", "America/Denver");
        timeZoneIdMap.put("Central America Standard Time", "America/Guatemala");
        timeZoneIdMap.put("Central Standard Time", "America/Chicago");
        timeZoneIdMap.put("Easter Island Standard Time", "Pacific/Easter");
        timeZoneIdMap.put("Central Standard Time (Mexico)", "America/Mexico_City");
        timeZoneIdMap.put("Canada Central Standard Time", "America/Regina");
        timeZoneIdMap.put("SA Pacific Standard Time", "America/Bogota");
        timeZoneIdMap.put("Eastern Standard Time (Mexico)", "America/Cancun");
        timeZoneIdMap.put("Eastern Standard Time", "America/New_York");
        timeZoneIdMap.put("Haiti Standard Time", "America/Port-au-Prince");
        timeZoneIdMap.put("Cuba Standard Time", "America/Havana");
        timeZoneIdMap.put("US Eastern Standard Time", "America/Indianapolis");
        timeZoneIdMap.put("Turks And Caicos Standard Time", "America/Grand_Turk");
        timeZoneIdMap.put("Paraguay Standard Time", "America/Asuncion");
        timeZoneIdMap.put("Atlantic Standard Time", "America/Halifax");
        timeZoneIdMap.put("Venezuela Standard Time", "America/Caracas");
        timeZoneIdMap.put("Central Brazilian Standard Time", "America/Cuiaba");
        timeZoneIdMap.put("SA Western Standard Time", "America/La_Paz");
        timeZoneIdMap.put("Pacific SA Standard Time", "America/Santiago");
        timeZoneIdMap.put("Newfoundland Standard Time", "America/St_Johns");
        timeZoneIdMap.put("Tocantins Standard Time", "America/Araguaina");
        timeZoneIdMap.put("E. South America Standard Time", "America/Sao_Paulo");
        timeZoneIdMap.put("SA Eastern Standard Time", "America/Cayenne");
        timeZoneIdMap.put("Argentina Standard Time", "America/Buenos_Aires");
        timeZoneIdMap.put("Greenland Standard Time", "America/Godthab");
        timeZoneIdMap.put("Montevideo Standard Time", "America/Montevideo");
        timeZoneIdMap.put("Magallanes Standard Time", "America/Punta_Arenas");
        timeZoneIdMap.put("Saint Pierre Standard Time", "America/Miquelon");
        timeZoneIdMap.put("Bahia Standard Time", "America/Bahia");
        timeZoneIdMap.put("UTC-02", "Etc/GMT+2");
        timeZoneIdMap.put("Azores Standard Time", "Atlantic/Azores");
        timeZoneIdMap.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
        timeZoneIdMap.put("UTC", "Etc/GMT");
        timeZoneIdMap.put("GMT Standard Time", "Europe/London");
        timeZoneIdMap.put("Greenwich Standard Time", "Atlantic/Reykjavik");
        timeZoneIdMap.put("Sao Tome Standard Time", "Africa/Sao_Tome");
        timeZoneIdMap.put("Morocco Standard Time", "Africa/Casablanca");
        timeZoneIdMap.put("W. Europe Standard Time", "Europe/Berlin");
        timeZoneIdMap.put("Central Europe Standard Time", "Europe/Budapest");
        timeZoneIdMap.put("Romance Standard Time", "Europe/Paris");
        timeZoneIdMap.put("Central European Standard Time", "Europe/Warsaw");
        timeZoneIdMap.put("W. Central Africa Standard Time", "Africa/Lagos");
        timeZoneIdMap.put("Jordan Standard Time", "Asia/Amman");
        timeZoneIdMap.put("GTB Standard Time", "Europe/Bucharest");
        timeZoneIdMap.put("Middle East Standard Time", "Asia/Beirut");
        timeZoneIdMap.put("Egypt Standard Time", "Africa/Cairo");
        timeZoneIdMap.put("E. Europe Standard Time", "Europe/Chisinau");
        timeZoneIdMap.put("Syria Standard Time", "Asia/Damascus");
        timeZoneIdMap.put("West Bank Standard Time", "Asia/Hebron");
        timeZoneIdMap.put("South Africa Standard Time", "Africa/Johannesburg");
        timeZoneIdMap.put("FLE Standard Time", "Europe/Kiev");
        timeZoneIdMap.put("Israel Standard Time", "Asia/Jerusalem");
        timeZoneIdMap.put("Kaliningrad Standard Time", "Europe/Kaliningrad");
        timeZoneIdMap.put("Sudan Standard Time", "Africa/Khartoum");
        timeZoneIdMap.put("Libya Standard Time", "Africa/Tripoli");
        timeZoneIdMap.put("Namibia Standard Time", "Africa/Windhoek");
        timeZoneIdMap.put("Arabic Standard Time", "Asia/Baghdad");
        timeZoneIdMap.put("Turkey Standard Time", "Europe/Istanbul");
        timeZoneIdMap.put("Arab Standard Time", "Asia/Riyadh");
        timeZoneIdMap.put("Belarus Standard Time", "Europe/Minsk");
        timeZoneIdMap.put("Russian Standard Time", "Europe/Moscow");
        timeZoneIdMap.put("E. Africa Standard Time", "Africa/Nairobi");
        timeZoneIdMap.put("Iran Standard Time", "Asia/Tehran");
        timeZoneIdMap.put("Arabian Standard Time", "Asia/Dubai");
        timeZoneIdMap.put("Astrakhan Standard Time", "Europe/Astrakhan");
        timeZoneIdMap.put("Azerbaijan Standard Time", "Asia/Baku");
        timeZoneIdMap.put("Russia Time Zone 3", "Europe/Samara");
        timeZoneIdMap.put("Mauritius Standard Time", "Indian/Mauritius");
        timeZoneIdMap.put("Saratov Standard Time", "Europe/Saratov");
        timeZoneIdMap.put("Georgian Standard Time", "Asia/Tbilisi");
        timeZoneIdMap.put("Volgograd Standard Time", "Europe/Volgograd");
        timeZoneIdMap.put("Caucasus Standard Time", "Asia/Yerevan");
        timeZoneIdMap.put("Afghanistan Standard Time", "Asia/Kabul");
        timeZoneIdMap.put("West Asia Standard Time", "Asia/Tashkent");
        timeZoneIdMap.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
        timeZoneIdMap.put("Pakistan Standard Time", "Asia/Karachi");
        timeZoneIdMap.put("Qyzylorda Standard Time", "Asia/Qyzylorda");
        timeZoneIdMap.put("India Standard Time", "Asia/Calcutta");
        timeZoneIdMap.put("Sri Lanka Standard Time", "Asia/Colombo");
        timeZoneIdMap.put("Nepal Standard Time", "Asia/Katmandu");
        timeZoneIdMap.put("Central Asia Standard Time", "Asia/Almaty");
        timeZoneIdMap.put("Bangladesh Standard Time", "Asia/Dhaka");
        timeZoneIdMap.put("Omsk Standard Time", "Asia/Omsk");
        timeZoneIdMap.put("Myanmar Standard Time", "Asia/Rangoon");
        timeZoneIdMap.put("SE Asia Standard Time", "Asia/Bangkok");
        timeZoneIdMap.put("Altai Standard Time", "Asia/Barnaul");
        timeZoneIdMap.put("W. Mongolia Standard Time", "Asia/Hovd");
        timeZoneIdMap.put("North Asia Standard Time", "Asia/Krasnoyarsk");
        timeZoneIdMap.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
        timeZoneIdMap.put("Tomsk Standard Time", "Asia/Tomsk");
        timeZoneIdMap.put("China Standard Time", "Asia/Shanghai");
        timeZoneIdMap.put("North Asia East Standard Time", "Asia/Irkutsk");
        timeZoneIdMap.put("Singapore Standard Time", "Asia/Singapore");
        timeZoneIdMap.put("W. Australia Standard Time", "Australia/Perth");
        timeZoneIdMap.put("Taipei Standard Time", "Asia/Taipei");
        timeZoneIdMap.put("Ulaanbaatar Standard Time", "Asia/Ulaanbaatar");
        timeZoneIdMap.put("Aus Central W. Standard Time", "Australia/Eucla");
        timeZoneIdMap.put("Transbaikal Standard Time", "Asia/Chita");
        timeZoneIdMap.put("Tokyo Standard Time", "Asia/Tokyo");
        timeZoneIdMap.put("North Korea Standard Time", "Asia/Pyongyang");
        timeZoneIdMap.put("Korea Standard Time", "Asia/Seoul");
        timeZoneIdMap.put("Yakutsk Standard Time", "Asia/Yakutsk");
        timeZoneIdMap.put("Cen. Australia Standard Time", "Australia/Adelaide");
        timeZoneIdMap.put("AUS Central Standard Time", "Australia/Darwin");
        timeZoneIdMap.put("E. Australia Standard Time", "Australia/Brisbane");
        timeZoneIdMap.put("AUS Eastern Standard Time", "Australia/Sydney");
        timeZoneIdMap.put("West Pacific Standard Time", "Pacific/Port_Moresby");
        timeZoneIdMap.put("Tasmania Standard Time", "Australia/Hobart");
        timeZoneIdMap.put("Vladivostok Standard Time", "Asia/Vladivostok");
        timeZoneIdMap.put("Lord Howe Standard Time", "Australia/Lord_Howe");
        timeZoneIdMap.put("Bougainville Standard Time", "Pacific/Bougainville");
        timeZoneIdMap.put("Russia Time Zone 10", "Asia/Srednekolymsk");
        timeZoneIdMap.put("Magadan Standard Time", "Asia/Magadan");
        timeZoneIdMap.put("Norfolk Standard Time", "Pacific/Norfolk");
        timeZoneIdMap.put("Sakhalin Standard Time", "Asia/Sakhalin");
        timeZoneIdMap.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
        timeZoneIdMap.put("Russia Time Zone 11", "Asia/Kamchatka");
        timeZoneIdMap.put("New Zealand Standard Time", "Pacific/Auckland");
        timeZoneIdMap.put("UTC+12", "Etc/GMT-12");
        timeZoneIdMap.put("Fiji Standard Time", "Pacific/Fiji");
        timeZoneIdMap.put("Chatham Islands Standard Time", "Pacific/Chatham");
        timeZoneIdMap.put("UTC+13", "Etc/GMT-13");
        timeZoneIdMap.put("Tonga Standard Time", "Pacific/Tongatapu");
        timeZoneIdMap.put("Samoa Standard Time", "Pacific/Apia");
        timeZoneIdMap.put("Line Islands Standard Time", "Pacific/Kiritimati");
    }

    public static String getIanaFromWindows(String windowsTimeZone) {
        String iana = timeZoneIdMap.get(windowsTimeZone);

        // If a mapping was not found, assume the value passed
        // was already an IANA identifier
        return (iana == null) ? windowsTimeZone : iana;
    }

    public static ZoneId getZoneIdFromWindows(String windowsTimeZone) {
        String timeZoneId = getIanaFromWindows(windowsTimeZone);

        return ZoneId.of(timeZoneId);
    }
}
// </GraphToIanaSnippet>
