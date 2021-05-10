package com.github.menglim.mutils;

import com.github.menglim.mutils.model.CurrencyModel;

import java.util.ArrayList;
import java.util.List;

public class CurrencyUtils {

    private static List<CurrencyModel> list = null;
    private static CurrencyUtils _instance;

    public static CurrencyUtils getInstance() {
        if (_instance == null) _instance = new CurrencyUtils();
        return _instance;
    }

    public CurrencyModel findByDigitalCode(int currencyCode) {
        return list.stream().filter(x -> x.getDigitalCode() == currencyCode).findFirst().orElse(null);
    }

    public CurrencyModel findByCurrencyCode(String currencyCode) {
        return list.stream().filter(x -> x.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().orElse(null);
    }

    public List<CurrencyModel> getAllCurrencies() {
        return list;
    }

    public int findDigitalCurrencyCode(String currencyCode) {
        CurrencyModel model = list.stream().filter(x -> x.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().orElse(null);
        if (model != null) {
            return model.getDigitalCode();
        }
        return 0;
    }

    public String findCurrencyCode(int digitalCurrencyCode) {
        CurrencyModel model = list.stream().filter(x -> x.getDigitalCode() == digitalCurrencyCode).findFirst().orElse(null);
        if (model != null) {
            return model.getCurrencyCode();
        }
        return null;
    }

    public CurrencyUtils() {
        if (list == null) {
            list = new ArrayList<>();
            list.add(new CurrencyModel(784, "AED", "UAE Dirham", "UAE", "د.إ"));
            list.add(new CurrencyModel(971, "AFN", "Afghani", "Afghanistan", "Af"));
            list.add(new CurrencyModel(8, "ALL", "Lek", "Albania", "L"));
            list.add(new CurrencyModel(51, "AMD", "Armenian Dram", "Armenia", "Դ"));
            list.add(new CurrencyModel(973, "AOA", "Kwanza", "Angola", "Kz"));
            list.add(new CurrencyModel(32, "ARS", "Argentine Peso", "Argentina", "$"));
            list.add(new CurrencyModel(36, "AUD", "Australian Dollar", "Australia", "$"));
            list.add(new CurrencyModel(36, "AUD", "Australian Dollar", "Kiribati", "$"));
            list.add(new CurrencyModel(36, "AUD", "Australian Dollar", "Coconut Islands", "$"));
            list.add(new CurrencyModel(36, "AUD", "Australian Dollar", "Nauru", "$"));
            list.add(new CurrencyModel(36, "AUD", "Australian Dollar", "Tuvalu", "$"));
            list.add(new CurrencyModel(533, "AWG", "Aruban Guilder/Florin", "Aruba", "ƒ"));
            list.add(new CurrencyModel(944, "AZN", "Azerbaijanian Manat", "Azerbaijan", "ман"));
            list.add(new CurrencyModel(977, "BAM", "Konvertibilna Marka", "Bosnia and Herzegovina", "КМ"));
            list.add(new CurrencyModel(52, "BBD", "Barbados Dollar", "Barbados", "$"));
            list.add(new CurrencyModel(50, "BDT", "Taka", "Bangladesh", "৳"));
            list.add(new CurrencyModel(975, "BGN", "Bulgarian Lev", "Bulgaria", "лв"));
            list.add(new CurrencyModel(48, "BHD", "Bahraini Dinar", "Bahrain", "ب.د"));
            list.add(new CurrencyModel(108, "BIF", "Burundi Franc", "Burundi", "₣"));
            list.add(new CurrencyModel(60, "BMD", "Bermudian Dollar", "Bermuda", "$"));
            list.add(new CurrencyModel(96, "BND", "Brunei Dollar", "Brunei", "$"));
            list.add(new CurrencyModel(96, "BND", "Brunei Dollar", "Singapore", "$"));
            list.add(new CurrencyModel(68, "BOB", "Boliviano", "Bolivia", "Bs."));
            list.add(new CurrencyModel(986, "BRL", "Brazilian Real", "Brazil", "R$"));
            list.add(new CurrencyModel(44, "BSD", "Bahamian Dollar", "Bahamas", "$"));
            list.add(new CurrencyModel(64, "BTN", "Ngultrum", "Bhutan", ""));
            list.add(new CurrencyModel(72, "BWP", "Pula", "Botswana", "P"));
            list.add(new CurrencyModel(933, "BYN", "Belarusian Ruble", "Belarus", "Br"));
            list.add(new CurrencyModel(84, "BZD", "Belize Dollar", "Belize", "$"));
            list.add(new CurrencyModel(124, "CAD", "Canadian Dollar", "Canada", "$"));
            list.add(new CurrencyModel(976, "CDF", "Congolese Franc", "Congo (Kinshasa)", "₣"));
            list.add(new CurrencyModel(756, "CHF", "Swiss Franc", "Lichtenstein", "₣"));
            list.add(new CurrencyModel(756, "CHF", "Swiss Franc", "Switzerland", "₣"));
            list.add(new CurrencyModel(152, "CLP", "Chilean Peso", "Chile", "$"));
            list.add(new CurrencyModel(156, "CNY", "Yuan", "China", "¥"));
            list.add(new CurrencyModel(170, "COP", "Colombian Peso", "Colombia", "$"));
            list.add(new CurrencyModel(188, "CRC", "Costa Rican Colon", "Costa Rica", "₡"));
            list.add(new CurrencyModel(192, "CUP", "Cuban Peso", "Cuba", "$"));
            list.add(new CurrencyModel(132, "CVE", "Cape Verde Escudo", "Cape Verde", "$"));
            list.add(new CurrencyModel(203, "CZK", "Czech Koruna", "Czech Republic", "Kč"));
            list.add(new CurrencyModel(262, "DJF", "Djibouti Franc", "Djibouti", "₣"));
            list.add(new CurrencyModel(208, "DKK", "Danish Krone", "Denmark", "kr"));
            list.add(new CurrencyModel(214, "DOP", "Dominican Peso", "Dominican Republic", "$"));
            list.add(new CurrencyModel(12, "DZD", "Algerian Dinar", "Algeria", "د.ج"));
            list.add(new CurrencyModel(818, "EGP", "Egyptian Pound", "Egypt", "£"));
            list.add(new CurrencyModel(232, "ERN", "Nakfa", "Eritrea", "Nfk"));
            list.add(new CurrencyModel(230, "ETB", "Ethiopian Birr", "Ethiopia", ""));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Akrotiri and Dhekelia", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Andorra", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Austria", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Belgium", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Cyprus", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Estonia", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Finland", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "France", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Germany", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Greece", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Ireland", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Italy", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Kosovo", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Latvia", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Lithuania", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Luxembourg", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Malta", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Monaco", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Montenegro", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Netherlands", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Portugal", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "San-Marino", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Slovakia", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Slovenia", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Spain", "€"));
            list.add(new CurrencyModel(978, "EUR", "Euro", "Vatican", "€"));
            list.add(new CurrencyModel(242, "FJD", "Fiji Dollar", "Fiji", "$"));
            list.add(new CurrencyModel(238, "FKP", "Falkland Islands Pound", "Falkland Islands", "£"));
            list.add(new CurrencyModel(826, "GBP", "Pound Sterling", "Alderney", "£"));
            list.add(new CurrencyModel(826, "GBP", "Pound Sterling", "British Indian Ocean Territory", "£"));
            list.add(new CurrencyModel(826, "GBP", "Pound Sterling", "Great Britain", "£"));
            list.add(new CurrencyModel(826, "GBP", "Pound Sterling", "Isle of Maine", "£"));
            list.add(new CurrencyModel(981, "GEL", "Lari", "Georgia", "ლ"));
            list.add(new CurrencyModel(981, "GEL", "Lari", "South Ossetia", "ლ"));
            list.add(new CurrencyModel(936, "GHS", "Cedi", "Ghana", "₵"));
            list.add(new CurrencyModel(292, "GIP", "Gibraltar Pound", "Gibraltar", "£"));
            list.add(new CurrencyModel(270, "GMD", "Dalasi", "Gambia", "D"));
            list.add(new CurrencyModel(324, "GNF", "Guinea Franc", "Guinea", "₣"));
            list.add(new CurrencyModel(320, "GTQ", "Quetzal", "Guatemala", "Q"));
            list.add(new CurrencyModel(328, "GYD", "Guyana Dollar", "Guyana", "$"));
            list.add(new CurrencyModel(344, "HKD", "Hong Kong Dollar", "Hong Kong", "$"));
            list.add(new CurrencyModel(340, "HNL", "Lempira", "Honduras", "L"));
            list.add(new CurrencyModel(191, "HRK", "Croatian Kuna", "Croatia", "Kn"));
            list.add(new CurrencyModel(332, "HTG", "Gourde", "Haiti", "G"));
            list.add(new CurrencyModel(348, "HUF", "Forint", "Hungary", "Ft"));
            list.add(new CurrencyModel(360, "IDR", "Rupiah", "Indonesia", "Rp"));
            list.add(new CurrencyModel(376, "ILS", "New Israeli Shekel", "Israel", "₪"));
            list.add(new CurrencyModel(376, "ILS", "New Israeli Shekel", "Palestine", "₪"));
            list.add(new CurrencyModel(356, "INR", "Indian Rupee", "Bhutan", "₹"));
            list.add(new CurrencyModel(356, "INR", "Indian Rupee", "India", "₹"));
            list.add(new CurrencyModel(368, "IQD", "Iraqi Dinar", "Iraq", "ع.د"));
            list.add(new CurrencyModel(364, "IRR", "Iranian Rial", "Iran", "﷼"));
            list.add(new CurrencyModel(352, "ISK", "Iceland Krona", "Iceland", "Kr"));
            list.add(new CurrencyModel(388, "JMD", "Jamaican Dollar", "Jamaica", "$"));
            list.add(new CurrencyModel(400, "JOD", "Jordanian Dinar", "Jordan", "د.ا"));
            list.add(new CurrencyModel(392, "JPY", "Yen", "Japan", "¥"));
            list.add(new CurrencyModel(404, "KES", "Kenyan Shilling", "Kenya", "Sh"));
            list.add(new CurrencyModel(417, "KGS", "Som", "Kyrgyzstan", ""));
            list.add(new CurrencyModel(116, "KHR", "Riel", "Cambodia", "៛"));
            list.add(new CurrencyModel(408, "KPW", "North Korean Won", "North Korea", "₩"));
            list.add(new CurrencyModel(410, "KRW", "South Korean Won", "South Korea", "₩"));
            list.add(new CurrencyModel(414, "KWD", "Kuwaiti Dinar", "Kuwait", "د.ك"));
            list.add(new CurrencyModel(136, "KYD", "Cayman Islands Dollar", "Cayman Islands", "$"));
            list.add(new CurrencyModel(398, "KZT", "Tenge", "Kazakhstan", "〒"));
            list.add(new CurrencyModel(418, "LAK", "Kip", "Laos", "₭"));
            list.add(new CurrencyModel(422, "LBP", "Lebanese Pound", "Lebanon", "ل.ل"));
            list.add(new CurrencyModel(144, "LKR", "Sri Lanka Rupee", "Sri Lanka", "Rs"));
            list.add(new CurrencyModel(430, "LRD", "Liberian Dollar", "Liberia", "$"));
            list.add(new CurrencyModel(426, "LSL", "Loti", "Lesotho", "L"));
            list.add(new CurrencyModel(434, "LYD", "Libyan Dinar", "Libya", "ل.د"));
            list.add(new CurrencyModel(504, "MAD", "Moroccan Dirham", "Morocco", "د.م."));
            list.add(new CurrencyModel(498, "MDL", "Moldovan Leu", "Moldova", "L"));
            list.add(new CurrencyModel(969, "MGA", "Malagasy Ariary", "Madagascar", ""));
            list.add(new CurrencyModel(807, "MKD", "Denar", "Macedonia", "ден"));
            list.add(new CurrencyModel(104, "MMK", "Kyat", "Myanmar (Burma)", "K"));
            list.add(new CurrencyModel(496, "MNT", "Tugrik", "Mongolia", "₮"));
            list.add(new CurrencyModel(446, "MOP", "Pataca", "Macao", "P"));
            list.add(new CurrencyModel(929, "MRU", "Ouguiya", "Mauritania", "UM"));
            list.add(new CurrencyModel(480, "MUR", "Mauritius Rupee", "Mauritius", "₨"));
            list.add(new CurrencyModel(462, "MVR", "Rufiyaa", "Maldives", "ރ."));
            list.add(new CurrencyModel(454, "MWK", "Kwacha", "Malawi", "MK"));
            list.add(new CurrencyModel(484, "MXN", "Mexican Peso", "Mexico", "$"));
            list.add(new CurrencyModel(458, "MYR", "Malaysian Ringgit", "Malaysia", "RM"));
            list.add(new CurrencyModel(943, "MZN", "Metical", "Mozambique", "MTn"));
            list.add(new CurrencyModel(516, "NAD", "Namibia Dollar", "Namibia", "$"));
            list.add(new CurrencyModel(566, "NGN", "Naira", "Nigeria", "₦"));
            list.add(new CurrencyModel(558, "NIO", "Cordoba Oro", "Nicaragua", "C$"));
            list.add(new CurrencyModel(578, "NOK", "Norwegian Krone", "Norway", "kr"));
            list.add(new CurrencyModel(524, "NPR", "Nepalese Rupee", "Nepal", "₨"));
            list.add(new CurrencyModel(554, "NZD", "New Zealand Dollar", "Cook Islands", "$"));
            list.add(new CurrencyModel(554, "NZD", "New Zealand Dollar", "New Zealand", "$"));
            list.add(new CurrencyModel(554, "NZD", "New Zealand Dollar", "Niue", "$"));
            list.add(new CurrencyModel(554, "NZD", "New Zealand Dollar", "Pitcairn Island", "$"));
            list.add(new CurrencyModel(512, "OMR", "Rial Omani", "Oman", "ر.ع."));
            list.add(new CurrencyModel(590, "PAB", "Balboa", "Panama", "B/."));
            list.add(new CurrencyModel(604, "PEN", "Nuevo Sol", "Peru", "S/."));
            list.add(new CurrencyModel(598, "PGK", "Kina", "Papua New Guinea", "K"));
            list.add(new CurrencyModel(608, "PHP", "Philippine Peso", "Philippines", "₱"));
            list.add(new CurrencyModel(586, "PKR", "Pakistan Rupee", "Pakistan", "₨"));
            list.add(new CurrencyModel(985, "PLN", "PZloty", "Poland", "zł"));
            list.add(new CurrencyModel(600, "PYG", "Guarani", "Paraguay", "₲"));
            list.add(new CurrencyModel(634, "QAR", "Qatari Rial", "Qatar", "ر.ق"));
            list.add(new CurrencyModel(946, "RON", "Leu", "Romania", "L"));
            list.add(new CurrencyModel(941, "RSD", "Serbian Dinar", "Kosovo", "din"));
            list.add(new CurrencyModel(941, "RSD", "Serbian Dinar", "Serbia", "din"));
            list.add(new CurrencyModel(643, "RUB", "Russian Ruble", "Russia", "р."));
            list.add(new CurrencyModel(643, "RUB", "Russian Ruble", "South Ossetia", "р."));
            list.add(new CurrencyModel(646, "RWF", "Rwanda Franc", "Rwanda", "₣"));
            list.add(new CurrencyModel(682, "SAR", "Saudi Riyal", "Saudi Arabia", "ر.س"));
            list.add(new CurrencyModel(90, "SBD", "Solomon Islands Dollar", "Solomon Islands", "$"));
            list.add(new CurrencyModel(690, "SCR", "Seychelles Rupee", "Seychelles", "₨"));
            list.add(new CurrencyModel(938, "SDG", "Sudanese Pound", "Sudan", "£"));
            list.add(new CurrencyModel(752, "SEK", "Swedish Krona", "Sweden", "kr"));
            list.add(new CurrencyModel(702, "SGD", "Singapore Dollar", "Brunei", "$"));
            list.add(new CurrencyModel(702, "SGD", "Singapore Dollar", "Singapore", "$"));
            list.add(new CurrencyModel(654, "SHP", "Saint Helena Pound", "Ascension Island", "£"));
            list.add(new CurrencyModel(654, "SHP", "Saint Helena Pound", "Saint Helena", "£"));
            list.add(new CurrencyModel(654, "SHP", "Saint Helena Pound", "Tristan da Cunha", "£"));
            list.add(new CurrencyModel(694, "SLL", "Leone", "Sierra Leone", "Le"));
            list.add(new CurrencyModel(706, "SOS", "Somali Shilling", "Somalia", "Sh"));
            list.add(new CurrencyModel(968, "SRD", "Suriname Dollar", "Suriname", "$"));
            list.add(new CurrencyModel(930, "STN", "Dobra", "Sao Tome and Principe", "Db"));
            list.add(new CurrencyModel(760, "SYP", "Syrian Pound", "Syria", "ل.س"));
            list.add(new CurrencyModel(748, "SZL", "Lilangeni", "Swaziland", "L"));
            list.add(new CurrencyModel(764, "THB", "Baht", "Thailand", "฿"));
            list.add(new CurrencyModel(972, "TJS", "Somoni", "Tajikistan", "ЅМ"));
            list.add(new CurrencyModel(934, "TMT", "Manat", "Turkmenistan", "m"));
            list.add(new CurrencyModel(788, "TND", "Tunisian Dinar", "Tunisia", "د.ت"));
            list.add(new CurrencyModel(776, "TOP", "Pa’anga", "Tonga", "T$"));
            list.add(new CurrencyModel(949, "TRY", "Turkish Lira", "North Cyprus", "₤"));
            list.add(new CurrencyModel(949, "TRY", "Turkish Lira", "Turkey", "₤"));
            list.add(new CurrencyModel(780, "TTD", "Trinidad and Tobago Dollar", "Trinidad and Tobago", "$"));
            list.add(new CurrencyModel(901, "TWD", "Taiwan Dollar", "Taiwan", "$"));
            list.add(new CurrencyModel(834, "TZS", "Tanzanian Shilling", "Tanzania", "Sh"));
            list.add(new CurrencyModel(980, "UAH", "Hryvnia", "Ukraine", "₴"));
            list.add(new CurrencyModel(800, "UGX", "Uganda Shilling", "Uganda", "Sh"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "American Samoa", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "British Indian Ocean Territory", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "British Virgin Islands", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Guam", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Haiti", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Marshall Islands", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Micronesia", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Northern Mariana Islands", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Pacific Remote islands", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Palau", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Panama", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Puerto Rico", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "Turks and Caicos Islands", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "United States of America", "$"));
            list.add(new CurrencyModel(840, "USD", "US Dollar", "US Virgin Islands", "$"));
            list.add(new CurrencyModel(858, "UYU", "Peso Uruguayo", "Uruguay", "$"));
            list.add(new CurrencyModel(860, "UZS", "Uzbekistan Sum", "Uzbekistan", ""));
            list.add(new CurrencyModel(937, "VEF", "Bolivar Fuerte", "Venezuela", "Bs F"));
            list.add(new CurrencyModel(704, "VND", "Dong", "Vietnam", "₫"));
            list.add(new CurrencyModel(548, "VUV", "Vatu", "Vanuatu", "Vt"));
            list.add(new CurrencyModel(882, "WST", "Tala", "Samoa", "T"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Benin", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Burkina Faso", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Cameroon", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Central African Republic", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Chad", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Congo (Brazzaville)", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Côte d'Ivoire", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Equatorial Guinea", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Gabon", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Guinea-Bissau", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Mali", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Niger", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Senegal", "₣"));
            list.add(new CurrencyModel(950, "XAF", "CFA Franc BCEAO", "Togo", "₣"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Anguilla", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Antigua and Barbuda", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Dominica", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Grenada", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Montserrat", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Saint Kitts and Nevis", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Saint Lucia", "$"));
            list.add(new CurrencyModel(951, "XCD", "East Caribbean Dollar", "Saint Vincent and Grenadine", "$"));
            list.add(new CurrencyModel(953, "XPF", "CFP Franc", "French Polynesia", "₣"));
            list.add(new CurrencyModel(953, "XPF", "CFP Franc", "New Caledonia", "₣"));
            list.add(new CurrencyModel(953, "XPF", "CFP Franc", "Wallis and Futuna", "₣"));
            list.add(new CurrencyModel(886, "YER", "Yemeni Rial", "Yemen", "﷼"));
            list.add(new CurrencyModel(710, "ZAR", "Rand", "Lesotho", "R"));
            list.add(new CurrencyModel(710, "ZAR", "Rand", "Namibia", "R"));
            list.add(new CurrencyModel(710, "ZAR", "Rand", "South Africa", "R"));
            list.add(new CurrencyModel(967, "ZMW", "Zambian Kwacha", "Zambia", "ZK"));
            list.add(new CurrencyModel(932, "ZWL", "Zimbabwe Dollar", "Zimbabwe", "$"));
        }
    }
}
