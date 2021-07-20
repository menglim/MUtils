package com.github.menglim.mutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.menglim.mutils.annotation.CSVField;
import com.github.menglim.mutils.annotation.ExcelField;
import com.github.menglim.mutils.model.CSVModel;
import com.github.menglim.mutils.model.ExcelFieldModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AppUtils {

    public AppUtils() {

    }

    private static AppUtils instance;

    public static AppUtils getInstance() {
        if (instance == null) instance = new AppUtils();
        return instance;
    }

    public boolean isNull(Object object) {
        if (object instanceof String) {
            String val = ((String) object).trim();
            if (val == null) return true;
            if (val.equalsIgnoreCase("")) return true;
        }
        return Objects.isNull(object);
    }

    public boolean nonNull(Object object) {
        return !isNull(object);
    }

//    public int getPageNumber(Optional<Integer> pageNumber, int defaultValue) {
//        int page = (pageNumber.orElse(0) < 1) ? defaultValue : pageNumber.get() - 1;
//        return page;
//    }

    public int getPageNumber(Optional<Integer> pageNumber) {
        int page = (pageNumber.orElse(0) < 1) ? 0 : pageNumber.get() - 1;
        return page;
    }

    public String getJsonObjectValue(JSONObject jsonObject, String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return getJsonObjectValue(jsonObject, path.split("/"));
    }

    public String getJsonObjectValue(JSONObject jsonObject, String... paths) {
        for (int i = 0; i < paths.length - 1; i++) {
            if (jsonObject != null) {
                if (jsonObject.has(paths[i])) {
                    try {
                        jsonObject = jsonObject.getJSONObject(paths[i]);
                    } catch (JSONException e) {
//                        e.printStackTrace();
                        log.error("Cannot value from JSON " + paths[i] + " => returning NULL");
                    }
                }
            }
        }
        if (jsonObject.has(paths[paths.length - 1])) {
            String obj = null;
            try {
                obj = jsonObject.getString(paths[paths.length - 1]);
            } catch (JSONException e) {
//                e.printStackTrace();
                log.error("Cannot value from JSON " + paths[paths.length - 1] + " => returning NULL");
            }
            return obj;
        }
        return null;
    }

    public String getTimeAgo(Date previousDate, int level, boolean abb) {
        return TimeAgo.toRelative(previousDate, new Date(), level, abb);
    }

    public String[] getUsernamePasswordBasicAuthentication(String authorization) {
        if (nonNull(authorization) && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            return credentials.split(":");
        }
        return null;
    }

    public String toBase64(byte[] data) {
        String dd = Base64.getEncoder().encodeToString(data);
        return dd;
    }

    public String getExtensionFilename(String filename) {
        int pos = filename.lastIndexOf(".");
        String ext = filename.substring(pos + 1);
        return ext;
    }

    public String getFilenameWithoutExtension(String filename) {
        int pos = filename.lastIndexOf(".");
        String filenameWoExtension = filename.substring(0, pos);
        return filenameWoExtension;
    }

    public String genUniqueRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid + "@" + System.identityHashCode(uuid);
    }

    public String getMimeType(String filename) {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        String mimeType = fileTypeMap.getContentType(filename);
        return mimeType;
    }

    public int random(int from, int to) {
        Random rn = new Random();
        return rn.nextInt(to - from + 1) + from;
    }

    public Date getDate(Optional<String> optionalDate) {
        String dateValue = optionalDate.orElse(getDate("dd-MM-yyyy"));
        return getDate(dateValue, "dd-MM-yyyy");
    }

    public String getDate(String format) {
        String currentDateTime = new SimpleDateFormat(format).format(new Date());
        return currentDateTime;
    }

    public String formatDate(Date date, String format) {
        String currentDateTime = new SimpleDateFormat(format).format(date);
        return currentDateTime;
    }

    public String getDate() {
        return getDate("dd-MMM-yyyy HH:mm:ss");
    }

    public String getTimestamp() {
        return getDate("yyyyMMddHHmmss");
    }

    public Date getDate(String dateValue, String formatter) {
        dateValue = dateValue.trim();
        SimpleDateFormat f = new SimpleDateFormat(formatter);
        try {
            Date d = f.parse(dateValue);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public Date getDate(int numOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, numOfMonth);
        return calendar.getTime();
    }

    public String getRandomSecuredString(int length) {
        String rnds = RandomStringUtils.random(length, 0, 0, true, true, null, new SecureRandom());
        return rnds;
    }

    public String toPrettyFormat(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonString);
        return gson.toJson(je);
    }

    public Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public String toBase64(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    public String fromBase64(String base64Text) {
        byte[] data = Base64.getDecoder().decode(base64Text.getBytes());
        return new String(data);
    }

    public byte[] fromBase64ToByte(String base64Text) {
        byte[] data = Base64.getDecoder().decode(base64Text.getBytes());
        return data;
    }

    public int[] toIntArray(String[] values) {
        return Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
    }

    public Long[] toLongArray(String[] values) {
        long[] selectedValues = Arrays.stream(values).mapToLong(Long::parseLong).toArray();
        Long[] returnValues = new Long[selectedValues.length];
        for (int i = 0; i < selectedValues.length; i++) {
            returnValues[i] = selectedValues[i];
        }
        return returnValues;
    }

    public Long toLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    public boolean stringInLength(String value, int min, int max) {
        if (value.length() >= min && value.length() <= max) {
            return true;
        }
        return false;
    }

    public boolean stringInLength(String value, int max) {
        if (value.length() >= 0 && value.length() <= max) {
            return true;
        }
        return false;
    }

    public boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public boolean isMobileNo(String mobileNo) {
        boolean valid;
        if (isNull(mobileNo)) {
            valid = false;
        } else {
            if (stringInLength(mobileNo, 11, 12)) {
                if (mobileNo.contains("+")) {
                    valid = false;
                } else {
                    valid = isNumeric(mobileNo);
                }
            } else valid = false;
        }

        return valid;
    }

    public Long[] toLongArray(Optional<String[]> values) {
        if (values.isPresent() == false) return null;
        return toLongArray(values.get());
    }

    private String stripXSS(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }

    public Date dateAdd(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public Date dateAdd(Long seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, Math.toIntExact(seconds));
        return calendar.getTime();
    }

    public int getInt(String value) {
        return Integer.parseInt(value);
    }

    public double toDouble(String value) {
        return Double.parseDouble(value);
    }

    public Long dateDiff(Date date) {
        Date currentDate = new Date();
        Long diffInMillies = currentDate.getTime() - date.getTime();
        Long inSecond = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return inSecond;
    }

    public Long dateDiff(Date futureDate, Date previousDate) {
        Long diffInMillies = futureDate.getTime() - previousDate.getTime();
        Long inSecond = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return inSecond;
    }

    public String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    public String toJsonString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error at toJsonString because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public String toJsonString(Object object, boolean prettyFormat) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            if (prettyFormat) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                jsonString = mapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            log.error("Error at toJsonString because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public Object toObject(String jsonData, Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            Object object = mapper.readValue(jsonData, clazz);
            return object;
        } catch (JsonProcessingException e) {
            log.error("Error at toJsonString because " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Object toObject(HashMap<String, String> hashMap, Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        Object object = mapper.convertValue(hashMap, clazz);
        return object;
    }

    private String splitByLine(String text, int start, int numberOfLine, String separate, boolean reverse) {
        if (AppUtils.getInstance().isNull(text)) return null;
        if (text.contains(separate)) {
            String[] tmp = text.split(separate);
            StringBuffer result = new StringBuffer();
            if (reverse) {
                if (start < 0) start = 0;
                for (int i = start; i < tmp.length; i++) {
                    result.append(tmp[i] + separate);
                }
            } else {
                if (numberOfLine > tmp.length) numberOfLine = tmp.length;
                for (int i = start; i < numberOfLine; i++) {
                    result.append(tmp[i] + separate);
                }
            }
            return result.toString();
        }
        return text;
    }

    public String splitByLineStart(String text, int numberOfLine, String separate) {
        return splitByLine(text, 0, numberOfLine, separate, false);
    }

    public String splitByLineEnd(String text, int numberOfLine, String separate) {
        int totalLine = text.split(separate).length;
        return splitByLine(text, totalLine - numberOfLine, numberOfLine, separate, true);
    }

    public String splitLineStartAndLineEnd(String text, int numberOfLineStart, int numberOfLineEnd, String separate) {
        if (AppUtils.getInstance().isNull(text)) return null;
        if (text.contains(separate)) {
            String[] tmp = text.split(separate);
            StringBuilder result = new StringBuilder();
            if (tmp.length <= numberOfLineStart + numberOfLineEnd) {
                return text;
            } else {
                if (tmp.length > numberOfLineStart + numberOfLineEnd || tmp.length <= numberOfLineStart + numberOfLineEnd + numberOfLineEnd) {
                    String strStart = splitByLineStart(text, numberOfLineStart, separate);
                    String strEnd = splitByLineEnd(text, numberOfLineEnd, separate);

                    return result.append(strStart).append(".<br/>.<br/>.<br/>").append(strEnd).toString();
                } else {
                    return text;
                }
            }
        }
        return text;
    }

    public String twoDecimalDoubleToString(Double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(d);
    }

    public NodeList getNodes(String xml, String path) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            doc.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile(path).evaluate(doc, XPathConstants.NODESET);
            return nodeList;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNodeValue(Node node, String searchForNodeNameWithPrefix) {
        if (node == null) try {
            throw new Exception("Node cannot be NULL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node eachChildNode = node.getChildNodes().item(i);
            if (eachChildNode != null) {
//                String nodeName = eachChildNode.getNodeName();
//                System.out.println("NodeName => " + nodeName);
                if (eachChildNode.getNodeName().equalsIgnoreCase(searchForNodeNameWithPrefix)) {
                    return eachChildNode.getTextContent();
                }
            }
        }
        return null;
    }

    public String getNodeValue(String xml, String path) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            doc.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.compile(path).evaluate(doc, XPathConstants.NODE);
            if (node != null) {
                return node.getTextContent();
            }
            log.error(path + " not found");
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getNodeAttributeValue(String xml, String path, String attributeKey) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            doc.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.compile(path).evaluate(doc, XPathConstants.NODE);
            if (node == null) {
                log.error(path + " not found");
                return null;
            }
            if (node.getAttributes().getNamedItem(attributeKey) == null) {
                log.error("AttributeKey " + attributeKey + " not found");
                return null;
            }
            return node.getAttributes().getNamedItem(attributeKey).getTextContent();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List excelReader(InputStream inputStream, int sheetIndex, int rowStart, int rowEnd, String password, HashMap<String, String> dataMatching, Class clzz) throws Exception {
        return ExcelFileUtils.builder()
                .inputStream(inputStream)
                .sheetIndex(sheetIndex)
                .rowStart(rowStart)
                .rowEnd(rowEnd)
                .password(password)
                .dataMatching(dataMatching)
                .clzz(clzz)
                .build()
                .excelReader();
    }

    public String maskString(String strText, int start, int end, char maskChar) {

        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            try {
                throw new Exception("End index cannot be greater than start index");
            } catch (Exception e) {
                e.printStackTrace();
            }

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    public String getLastPackageName(Class mainClazz) {
        String[] tmp = mainClazz.getPackage().getName().split("\\.");
        return tmp[tmp.length - 1];
    }

    public boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public List<String> toList(String valuesWithSplitter, String splitter) {
        return Arrays.stream(valuesWithSplitter.split(splitter)).map(String::trim).collect(Collectors.toList());
    }

    private String sqlString(String value) {
        return "'" + value + "'";
    }

    public String sql(Object value) {
        Class clazz = value.getClass();
        if (clazz == Integer.class || clazz == Double.class || clazz == Float.class) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return sqlString(String.valueOf(value));
        }
        return String.valueOf(value);
    }

    /**
     * Compare valid of the date with the current date.
     *
     * @param inputDate input date which smaller than the current date.
     * @param hour      hour of time (ex: 24h) for adding​validity period to input date.
     * @return true if unexpired, false if expired.
     */
    public boolean compareValidDate(Date inputDate, int hour) {
        long MillisecondPerHour = hour * 60 * 60 * 1000L;
        return Math.abs(new Date().getTime() - inputDate.getTime()) > MillisecondPerHour;
    }

//    public Date getEarlyDate(Date date) {
//        date = getDate(formatDate(date, "dd-MMM-yyyy"), "dd-MMM-yyyy");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.set(Calendar.HOUR, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        Date d = cal.getTime();
//        return d;
//    }
//
//    public Date getEODDate(Date date) {
//        date = getEarlyDate(date);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.set(Calendar.HOUR, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.set(Calendar.MILLISECOND, 999);
//        Date d = cal.getTime();
//        return d;
//    }

    public String momentByMonth(Date date) {
        return momentByMonth(date, "Yesterday", "Today", "Tomorrow");
    }

    public String momentByMonth(Date date, String yesterdayText, String todayText, String tomorrowText) {
        ZonedDateTime dt = date.toInstant().atZone(ZoneId.systemDefault());
        DateTimeFormatter MMYYYY_Format = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault());

        // check difference in days from today, considering just the date (ignoring the hours)
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dt.toLocalDate());
        if (days == 0) { // today
//            sb.append("Today ");
            return ((nonNull(todayText) ? todayText : "Today"));
        } else if (days == 1) { // tomorrow
//            sb.append("Tomorrow ");
            return ((nonNull(yesterdayText) ? tomorrowText : "Tomorrow"));
        } else if (days == -1) { // yesterday
//            sb.append("Yesterday ");
            return ((nonNull(yesterdayText) ? yesterdayText : "Yesterday"));
        } else if (days > 0 && days < 7) { // next week
            return (dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        } else if (days < 0 && days > -7) { // last week
//            sb.append("Last ").append(dt.getDayOfWeek().getDisplayName(TextStyle.FULL, CoreConstants.DEFAULT_LOCALE)).append(" ");
            String result = "Last " + dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            result = result + " ";
            return (result);
        } else {
            return dt.format(MMYYYY_Format);
        }
    }


    public String moment(Date date, String yesterdayText, String todayText, String tomorrowText) {
        ZonedDateTime dt = date.toInstant().atZone(ZoneId.systemDefault());
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
//        DateTimeFormatter MDY_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
//        DateTimeFormatter MDY_FORMAT = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm a");
        DateTimeFormatter DAY_OF_MONTH_FORMAT = DateTimeFormatter.ofPattern("d", Locale.getDefault());
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM, yyyy hh:mm a", Locale.getDefault());

        // check difference in days from today, considering just the date (ignoring the hours)
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dt.toLocalDate());
        if (days == 0) { // today
//            sb.append("Today ");
            sb.append((nonNull(todayText) ? todayText : "Today "));
        } else if (days == 1) { // tomorrow
//            sb.append("Tomorrow ");
            sb.append((nonNull(yesterdayText) ? tomorrowText : "Tomorrow "));
        } else if (days == -1) { // yesterday
//            sb.append("Yesterday ");
            sb.append((nonNull(yesterdayText) ? yesterdayText : "Yesterday "));
        } else if (days > 0 && days < 7) { // next week
            sb.append(dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())).append(" ");
        } else if (days < 0 && days > -7) { // last week

//            sb.append("Last ").append(dt.getDayOfWeek().getDisplayName(TextStyle.FULL, CoreConstants.DEFAULT_LOCALE)).append(" ");
            String result = "Last " + dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            result = result + " ";
            sb.append(result);
        }

        if (Math.abs(days) < 7) {  // difference is less than a week, append current time
            sb.append("at ").append(dt.format(HOUR_FORMAT));
        } else { // more than a week of difference
            sb.append(dt.format(DAY_OF_MONTH_FORMAT) + getDayOfMonthSuffix(dt.getDayOfMonth()) + " " + dt.format(DATE_FORMAT));
        }
        return sb.toString();
    }

    private String getDayOfMonthSuffix(int day) {
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * To replace last index of any value
     *
     * @param text        example: http://example.com/foo/bar/1
     * @param indexOf     example: '/'
     * @param replacement example: '*'
     * @return this will return http://example.com/foo/bar/*
     */
    public String replaceLastIndexOf(String text, String indexOf, String replacement) {
        String searchString = text.substring(text.lastIndexOf(indexOf) + 1);
        return replace(text, searchString, replacement);
    }

    public String getLanguageCode(Locale locale) {
        String languageCode = locale.getLanguage();
        if (languageCode.contains("_")) {
            languageCode = languageCode.split("_")[0];
        }
        return languageCode;
    }

    public DayOfWeek getDayOfWeek() {
        return getDayOfWeek(new Date());
    }

    public DayOfWeek getDayOfWeek(Date date) {
        ZonedDateTime dt = date.toInstant().atZone(ZoneId.systemDefault());
        return dt.getDayOfWeek();
    }

    public Date getAtStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getAtEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public InputStream post(String url, Constants.ContentType contentType, String body) throws Exception {
        try {
            log.info(body);
            URL urlConnection = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) urlConnection.openConnection();
            // set required headers
            switch (contentType) {
                case XML:
                    conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
                    break;
                case JSON:
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    break;
            }
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(bodyBytes.length);
            BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(bodyBytes);
            out.close();
            conn.connect();
            int statusCode = conn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream content = new BufferedInputStream(conn.getInputStream());
                return content;
            } else {
                throw new Exception("HTTP Status: " + statusCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ignored) {

        } finally {
            try {
                is.close();
            } catch (IOException ignored) {

            }
        }
        return sb.toString();
    }

    public String toCSVText(Object object) {
        return toCSVText(object, '|');
    }

    public String toCSVHeader(Object object) {
        return toCSVHeader(object, '|');
    }

    public String toCSVHeader(Object object, char separator) {
        StringBuilder builder = new StringBuilder();
        StringBuilder header = new StringBuilder();
        String result = "";
        Field[] fields = object.getClass().getDeclaredFields();
        List<CSVModel> models = new ArrayList<>();
        int order = 0;
        String fieldName = "";
        for (Field field : fields) {
            fieldName = field.getName();
            CSVField annotation = field.getAnnotation(CSVField.class);
            if (annotation != null) {
                if (annotation.ignore()) continue;
                fieldName = annotation.value();
                order = annotation.order();
            }
            models.add(new CSVModel(fieldName, null, order));
        }
        models.sort(Comparator.comparing(CSVModel::getFieldOrder));
        models.forEach(csvModel -> {
            builder.append(csvModel.getFieldName());
            builder.append(separator);
        });
        result = builder.toString().trim();
        if (result.endsWith(String.valueOf(separator))) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public String toCSVText(Object object, char separator) {
        StringBuilder builder = new StringBuilder();
        StringBuilder header = new StringBuilder();
        String result = "";
        Field[] fields = object.getClass().getDeclaredFields();
        String formatDate = null;
        List<CSVModel> models = new ArrayList<>();
        int order = 0;
        String fieldName = "";
        Object fieldValue = null;
        for (Field field : fields) {
            fieldName = field.getName();
            CSVField annotation = field.getAnnotation(CSVField.class);
            if (annotation != null) {
                if (annotation.ignore()) continue;
                fieldName = annotation.value();
                order = annotation.order();
                formatDate = annotation.formatDate();
            }
            fieldValue = getFieldValue(object, field);
            if (field.getType().equals(Date.class)) {
                fieldValue = getFieldDateValue(object, field, formatDate);
            }
            models.add(new CSVModel(fieldName, (fieldValue == null ? "" : fieldValue), order));
        }

        models.sort(Comparator.comparing(CSVModel::getFieldOrder));
        models.forEach(csvModel -> {
            builder.append(csvModel.getFieldValue());
            builder.append(separator);
        });
        result = builder.toString().trim();
        if (result.endsWith(String.valueOf(separator))) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static Boolean isType(Field field, Class classType) {
        return field.getType().equals(classType);
    }

    public static Method getGetMethod(Class clazz, Field field) {
        PropertyDescriptor propertyDescriptor = null;
        try {
            propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        assert propertyDescriptor != null;
        return propertyDescriptor.getReadMethod();
    }

    public String getFieldValue(Object object, Field field) {
//        final String format = isType(field, String.class) ? "%s='%s'" : "%s=%s";
        StringBuilder stringBuilder = new StringBuilder();
        Method getMethod = getGetMethod(object.getClass(), field);
        try {
            Object fieldValue = getMethod.invoke(object);
            if (fieldValue == null) return "";
//            stringBuilder = new StringBuilder(String.format(format, field.getName(), fieldValue));
            stringBuilder = new StringBuilder(String.valueOf(fieldValue));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public String getFieldDateValue(Object object, Field field, String formatDate) {
        Method getMethod = getGetMethod(object.getClass(), field);
        try {
            Date fieldValue = (Date) getMethod.invoke(object);
            return AppUtils.getInstance().formatDate(fieldValue, formatDate);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean uploadFileViaSSH(String host, String username, String password, HashMap<String, String> files) {
        try {
            SSHClient client = new SSHClient();
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(host);
            client.authPassword(username, password);
            SFTPClient sftpClient = client.newSFTPClient();
            files.forEach((localFilename, remoteDirectory) -> {
                if (remoteDirectory.endsWith("/")) {
                    remoteDirectory = remoteDirectory.substring(0, remoteDirectory.length() - 1);
                }
                try {
                    sftpClient.put(localFilename, remoteDirectory + "/" + getFileName(localFilename));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            sftpClient.close();
            client.disconnect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean uploadFileViaSSH(String host, String username, String password, List<String> localFilenames, String remoteDirectory) {
        try {
            if (remoteDirectory.endsWith("/")) {
                remoteDirectory = remoteDirectory.substring(0, remoteDirectory.length() - 1);
            }
            SSHClient client = new SSHClient();
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(host);
            client.authPassword(username, password);
            SFTPClient sftpClient = client.newSFTPClient();
            for (int i = 0; i < localFilenames.size(); i++) {
                sftpClient.put(localFilenames.get(i), remoteDirectory + "/" + getFileName(localFilenames.get(i)));
            }
            sftpClient.close();
            client.disconnect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getFileName(String filename) {
        File file = new File(filename);
        String name = file.getName();
        return name;
    }

    public String toVariableFormat(String normalString) {
        normalString = normalString.replaceAll("[^a-zA-Z0-9]", "");
        return normalString;
    }

    public String firstCharToLowerCase(String str) {
        if (str == null || str.length() == 0)
            return "";

        if (str.length() == 1)
            return str.toLowerCase();

        char[] chArr = str.toCharArray();
        chArr[0] = Character.toLowerCase(chArr[0]);
        return new String(chArr);
    }

    public List<String> getCountries() {
        List<String> list = new ArrayList<>();
        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("en", country);
            String name = locale.getDisplayCountry();
            if (!name.trim().equals(""))
                list.add(name.trim());
        }
        list.sort(String::compareToIgnoreCase);
        return list;
    }

    public HashMap<String, Object> toHashMap(Object object) {
        HashMap<String, Object> result = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        String fieldName = "";
        Object fieldValue = null;
        for (Field field : fields) {
            fieldName = field.getName();
            fieldValue = getFieldValue(object, field);
            result.put(fieldName, fieldValue);
        }
        return result;
    }

    public String getFirstSpecialSymbol(String value) {
        if (value == null) return null;
        if (value.equals("")) return "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (containsSpecialCharacter(String.valueOf(c))) {
                return String.valueOf(c).trim();
            }
        }
        return "";
    }

    public boolean containsSpecialCharacter(String s) {
        return (s != null) && s.matches("[^A-Za-z0-9 ]");
    }

    public String toDate(String value, Constants.FormatDate fromFormatDate, String toFormatDate) {
        String separator = getFirstSpecialSymbol(value);
        return toDate(value, fromFormatDate, separator, toFormatDate);
    }

    public String toDate(String value, Constants.FormatDate fromFormatDate, String fromDateSeparator, String toFormatDate) {
        if (value == null) return null;
        if (value.equals("")) return "";
        String yy = "";
        String mm = "";
        String dd = "";
        switch (fromFormatDate) {
            case DDMMYYYY:
                if (nonNull(fromDateSeparator)) {
                    String[] tmp = value.split(fromDateSeparator);
                    dd = tmp[0];
                    mm = tmp[1];
                    yy = tmp[2];
                } else {
                    dd = value.substring(0, 2);
                    mm = value.substring(2, 4);
                    yy = value.substring(4, 8);
                }
                break;
            case MMDDYYYY:
                if (nonNull(fromDateSeparator)) {
                    String[] tmp = value.split(fromDateSeparator);
                    dd = tmp[1];
                    mm = tmp[0];
                    yy = tmp[2];
                } else {
                    mm = value.substring(0, 2);
                    dd = value.substring(2, 4);
                    yy = value.substring(4, 8);
                }
                break;
            case YYYYMMDD:
                if (nonNull(fromDateSeparator)) {
                    String[] tmp = value.split(fromDateSeparator);
                    dd = tmp[2];
                    mm = tmp[1];
                    yy = tmp[0];
                } else {
                    dd = value.substring(6, 8);
                    mm = value.substring(4, 6);
                    yy = value.substring(0, 4);
                }
                break;
        }

        if (!isNumeric(dd)) {
            try {
                throw new Exception("Invalid DD value should be number => " + dd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isNumeric(mm)) {
            try {
                throw new Exception("Invalid MM value should be number => " + mm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isNumeric(yy)) {
            try {
                throw new Exception("Invalid YY value should be number => " + yy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LocalDate date = LocalDate.of(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(toFormatDate);
        return date.format(formatter);
    }

//    public static void main(String[] args) {
//        String originalValue = "12/14/2021";
//        String value = AppUtils.getInstance().toDate(originalValue, Constants.FormatDate.MMDDYYYY, "/", "yyyy-MMM-dd");
//        System.out.println(originalValue + " => " + value);
//        System.out.println("=> " + AppUtils.getInstance().getFirstSpecialSymbol(originalValue));
//    }

    public <R> byte[] toExcel(List<R> list, String worksheetName) {

        if (list == null) try {
            throw new Exception("List is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert list != null;
        if (list.size() == 0) {
            try {
                throw new Exception("List is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        R r = list.get(0);

        if (AppUtils.getInstance().isNull(worksheetName)) {
            worksheetName = r.getClass().getSimpleName();
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(worksheetName);
            // Header

            int rowIdx = 0;

            Row headerRow = sheet.createRow(0);
            List<String> headers = getColumnHeader(r);
            if (headers.size() == 0) {
                throw new Exception("No column selected");
            }
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers.get(col));
            }

            rowIdx = 1;

            for (R t1 : list) {
                Row row = sheet.createRow(rowIdx++);
                Field[] fields = t1.getClass().getDeclaredFields();
                String formatDate = "yyyy-MM-dd HH:mm:ss";
                List<ExcelFieldModel> models = new ArrayList<>();
                int order = 0;
                String fieldName = "";
                Object fieldValue = null;

                for (Field field : fields) {
                    fieldName = field.getName();
                    ExcelField annotation = field.getAnnotation(ExcelField.class);
                    if (annotation == null) continue;
                    else {
                        if (annotation.ignore()) continue;
                        fieldName = annotation.value();
                        order = annotation.order();
                        formatDate = annotation.formatDate();
                        if (field.getType().equals(Date.class)) {
                            fieldValue = getFieldDateValue(t1, field, formatDate);
                        } else {
                            fieldValue = getFieldValue(t1, field);
                        }
                        models.add(new ExcelFieldModel(fieldName, (fieldValue == null ? "" : fieldValue), order));
                    }
                }
                models.sort(Comparator.comparing(ExcelFieldModel::getFieldOrder));
                for (int col = 0; col < models.size(); col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellValue(String.valueOf(models.get(col).getFieldValue()));
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getColumnHeader(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        List<ExcelFieldModel> models = new ArrayList<>();
        int order = 0;
        String fieldName = "";
        for (Field field : fields) {
            fieldName = field.getName();
            ExcelField annotation = field.getAnnotation(ExcelField.class);
            if (annotation == null) continue;
            else {
                if (annotation.ignore()) continue;
                fieldName = annotation.value();
                order = annotation.order();
            }
            models.add(new ExcelFieldModel(fieldName, null, order));
        }
        models.sort(Comparator.comparing(ExcelFieldModel::getFieldOrder));
        List<String> result = models.stream().map(ExcelFieldModel::getFieldName).collect(Collectors.toList());
        return result;
    }

    private final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private final String[] tens = {
            "",        // 0
            "",        // 1
            "Twenty",  // 2
            "Thirty",  // 3
            "Forty",   // 4
            "Fifty",   // 5
            "Sixty",   // 6
            "Seventy", // 7
            "Eighty",  // 8
            "Ninety"   // 9
    };

    public String toWord(Long n) {
        if (n < 0) {
            return "minus " + toWord(-n);
        }
        if (n < 20) {
            return units[Math.toIntExact(n)];
        }
        if (n < 100) {
            return tens[Math.toIntExact(n / 10)] + ((n % 10 != 0) ? " " : "") + units[Math.toIntExact(n % 10)];
        }
        if (n < 1000) {
            return units[Math.toIntExact(n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + toWord(n % 100);
        }
        if (n < 1000000) {
            return toWord(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + toWord(n % 1000);
        }
        if (n < 1000000000) {
            return toWord(n / 1000000) + " Million" + ((n % 1000000 != 0) ? " " : "") + toWord(n % 1000000);
        }
        return toWord(n / 1000000000) + " Billion" + ((n % 1000000000 != 0) ? " " : "") + toWord(n % 1000000000);
    }

    public String amountToWord(Double n) {
        String amountInString = n.toString();
        String part1 = "";
        String part2 = "";

        String dollas = " Dollar";
        String cents = " Cent";

        if (amountInString.contains(".")) {
            String[] tmp = amountInString.split("\\.");
            part1 = tmp[0];
            part2 = tmp[1];
        }
        if (Long.parseLong(part1) > 1) {
            dollas = " Dollars";
        }
        if (Long.parseLong(part2) > 1) {
            cents = " Cents";
        }

        part1 = toWord(Long.parseLong(part1));
        part2 = toWord(Long.parseLong(part2));

        return part1 + dollas + " and " + part2 + cents;
    }

    public String find(String[] array, String find, String defaultValue) {
        for (String a :
                array) {
            if (a.contentEquals(find)) {
                return a;
            }
        }
        return defaultValue;
    }

    public String find(String[] array, String find) {
        return this.find(array, find, null);
    }

    public int findIndex(String[] array, String find) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].contentEquals(find)) {
                return i;
            }
        }
        return -1;
    }

    public String getAccountFormat(String accountNo, String separator) {
        if (accountNo == null) return null;
        if (accountNo.length() == 15)
            return StringUtils.isNotEmpty(accountNo) ?
                    accountNo.substring(0, 5).concat(separator)
                            .concat(accountNo.substring(5, 7)).concat(separator)
                            .concat(accountNo.substring(7, 13)).concat(separator).concat(accountNo.substring(13, 15))
                    : null;
        if (accountNo.length() == 9) {
            return StringUtils.isNotEmpty(accountNo) ?
                    accountNo.substring(0, 3).concat(separator)
                            .concat(accountNo.substring(3, 6)).concat(separator)
                            .concat(accountNo.substring(6, 9))
                    : null;
        }
        return accountNo;
    }

    public String getAccountFormat(String accountNo) {
        return getAccountFormat(accountNo, StringUtils.SPACE);
    }


    public String innerXml(Node node, boolean includeSelf) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();

        if (includeSelf) {
            lsSerializer.getDomConfig().setParameter("xml-declaration", false);
            StringBuilder sb = new StringBuilder();
            sb.append(lsSerializer.writeToString(node));
            return sb.toString();
        } else {
            NodeList childNodes = node.getChildNodes();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < childNodes.getLength(); i++) {
                sb.append(lsSerializer.writeToString(childNodes.item(i)));
            }
            return sb.toString();
        }
    }

    public String getHostIP() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            String value = ip.toString();
            if (value.contains("/")) {
                String[] tmp = value.split("/");
                return tmp[1];
            }
            return value;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getHostname() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostName();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String contact(@NonNull String... values) {
        return concatWithSeparator(" - ", values);
    }

    public String concatWithSeparator(String separator, @NonNull String... values) {
        String result = "";
        for (String value : values) {
            if (AppUtils.getInstance().nonNull(value)) {
                result = result + value + separator;
            }
        }
        if (AppUtils.getInstance().nonNull(separator)) {
            return result.substring(0, result.length() - separator.length());
        } else {
            return result;
        }
    }

    public String getTimeAgoBtw2Date(Date fromDate, Date toDate, int level, boolean abb) {
        return TimeAgo.toRelative(fromDate, toDate, level, abb);
    }

    public String incrementString(String string) {
        if (string.length() == 1) {
            if (string.equals("z"))
                return "a1";
            else if (string.equals("Z"))
                return "a1";
            else {
                String nextValue = (char) (string.charAt(0) + 1) + "";
                while (containsSpecialCharacter(nextValue)) {
                    nextValue = (char) (nextValue.charAt(0) + 1) + "";
                }
                return nextValue;
            }
        }
        if (string.charAt(string.length() - 1) != 'z') {
            String nextValue = (char) (string.charAt(string.length() - 1) + 1) + "";
            while (containsSpecialCharacter(nextValue)) {
                nextValue = (char) (nextValue.charAt(0) + 1) + "";
            }
            return string.substring(0, string.length() - 1) + nextValue;
        }
        return incrementString(string.substring(0, string.length() - 1)) + "1";
    }


    public Object toObject(String jsonData, TypeReference reference) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Object object = mapper.readValue(jsonData, reference);
            return object;
        } catch (JsonProcessingException var6) {
            var6.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
