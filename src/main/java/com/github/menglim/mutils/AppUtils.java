package com.github.menglim.mutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.menglim.mutils.annotation.CSVField;
import com.github.menglim.mutils.annotation.ExcelField;
import com.github.menglim.mutils.model.CSVModel;
import com.github.menglim.mutils.model.ExcelFieldModel;
import com.github.menglim.mutils.model.KeyValue;
import com.github.windpapi4j.InitializationFailedException;
import com.github.windpapi4j.WinAPICallFailedException;
import com.github.windpapi4j.WinDPAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.security.bc.BCSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.*;
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
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class AppUtils {

    public int UNIREST_TIMEOUT = 10000;

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

    public String getUUID(boolean withoutSymbol) {
        UUID uuid = UUID.randomUUID();
        String value = uuid.toString();
        if (withoutSymbol) {
            value = StringUtils.replace(value, "-", "");
        }
        return value;
    }

    public String getUUID() {
        return this.getUUID(false);
    }

    public String getUUIDWithHashCode() {
        UUID uuid = UUID.randomUUID();
        return uuid + "@" + System.identityHashCode(uuid);
    }

    public String getMimeType(String filename) {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        String mimeType = fileTypeMap.getContentType(filename);
        return mimeType;
    }

    public int getRandom(int from, int to) {
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
        return getDate("yyyyMMddHHmmssS");
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
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
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
        for (int i = 0; i < Objects.requireNonNull(node).getChildNodes().getLength(); i++) {
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
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
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
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
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


    public String sql(Object value) {
        Class clazz = value.getClass();
        if (clazz == Integer.class || clazz == Double.class || clazz == Float.class || clazz == Long.class) {
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

    public InputStream post(String url, CoreConstants.ContentType contentType, String body) throws Exception {
        try {
            //log.info(body);
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

    //Pending Testing Update library sshj-0.27.0 to 0.31.0
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
                    log.info(localFilename + " uploaded successfully");
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

    public String toDate(String value, CoreConstants.FormatDate fromFormatDate, String toFormatDate) {
        String separator = getFirstSpecialSymbol(value);
        return toDate(value, fromFormatDate, separator, toFormatDate);
    }

    public String toDate(String value, CoreConstants.FormatDate fromFormatDate, String fromDateSeparator, String toFormatDate) {
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

    public String getTimeAgoBtw2Date(Date fromDate, Date toDate, int level, boolean abb) {
        return TimeAgo.toRelative(fromDate, toDate, level, abb);
    }

    public String nextString(String string) {
        if (string.length() == 1) {
            if (string.equals("z"))
                return "a1";
            else if (string.equals("Z"))
                return "a1";
            else {
                String nextValue = (char) (string.charAt(0) + 1) + "";
                while (AppUtils.getInstance().containsSpecialCharacter(nextValue)) {
                    nextValue = (char) (nextValue.charAt(0) + 1) + "";
                }
                return nextValue;
            }
        }
        if (string.charAt(string.length() - 1) != 'z') {
            String nextValue = (char) (string.charAt(string.length() - 1) + 1) + "";
            while (AppUtils.getInstance().containsSpecialCharacter(nextValue)) {
                nextValue = (char) (nextValue.charAt(0) + 1) + "";
            }
            return string.substring(0, string.length() - 1) + nextValue;
        }
        return nextString(string.substring(0, string.length() - 1)) + "1";
    }

    private boolean containsSpecialCharacterForNextString(String s) {
        if (AppUtils.getInstance().isNull(s)) {
            return true;
        }
        return s != null && s.matches("[^A-Z1-9 ]");
    }

    public String nextStringCapitalOnly(String string) {
        if (string.length() == 1) {
            if (string.equals("z"))
                return "A1";
            else if (string.equals("Z"))
                return "A1";
            else {
                String nextValue = (char) (string.charAt(0) + 1) + "";
                while (containsSpecialCharacterForNextString(nextValue)) {
                    nextValue = (char) (nextValue.charAt(0) + 1) + "";
                }
                return nextValue;
            }
        }
        if (string.charAt(string.length() - 1) != 'Z') {
            String nextValue = (char) (string.charAt(string.length() - 1) + 1) + "";
            while (containsSpecialCharacterForNextString(nextValue)) {
                nextValue = (char) (nextValue.charAt(0) + 1) + "";
            }
            return string.substring(0, string.length() - 1) + nextValue;
        }
        return nextStringCapitalOnly(string.substring(0, string.length() - 1)) + "1";
    }


    public Object toObject(String jsonData, TypeReference reference) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Object object = mapper.readValue(jsonData, reference);
            return object;
        } catch (JsonProcessingException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public void copyFolderToSmbFolder(String localFolder, String host, String username, String domainName, String password, String remoteNetworkPath) {
        SmbConfig cfg = SmbConfig.builder().
                withMultiProtocolNegotiate(true).
                withSecurityProvider(new BCSecurityProvider()).
                build();
        SMBClient client = new SMBClient(cfg);

        try (Connection connection = client.connect(host)) {
            log.info("connected SMB Host " + host);
            AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domainName);
            Session session = connection.authenticate(ac);
            log.info("SMB is authenticated");
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "/", File.separator);
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "\\", File.separator);
            String shareDisk = splitGetAtFirst(remoteNetworkPath, File.separator);
            log.info("ShareDisk Name => " + shareDisk);
            try (DiskShare share = (DiskShare) session.connectShare(shareDisk)) {
                String remoteDirectory = remoteNetworkPath.substring(shareDisk.length());
                String[] subFolders = StringUtils.split(remoteDirectory, File.separator);
                remoteDirectory = "";
                for (String aSubFolder : subFolders) {
                    remoteDirectory = remoteDirectory + aSubFolder;
                    if (AppUtils.getInstance().nonNull(remoteDirectory)) {
                        if (!share.folderExists(remoteDirectory)) {
                            share.mkdir(remoteDirectory);
                        }
                        remoteDirectory = remoteDirectory + File.separator;
                    }
                }

                List<File> files = listFiles(localFolder);
                for (File file : files) {
                    if (file.isDirectory()) {
                        remoteDirectory = remoteNetworkPath.substring(shareDisk.length());
                        String subFoldersToBeCreated = file.getAbsolutePath().substring(localFolder.length());
                        if (!share.folderExists(remoteDirectory + File.separator + subFoldersToBeCreated)) {
                            share.mkdir(remoteDirectory + File.separator + subFoldersToBeCreated);
                        }
                    } else {
                        remoteDirectory = remoteNetworkPath.substring(shareDisk.length()) + File.separator + file.getAbsolutePath().substring(localFolder.length() + 1);
                        remoteDirectory = StringUtils.replace(remoteDirectory, "/", File.separator);
                        remoteDirectory = StringUtils.replace(remoteDirectory, "\\", File.separator);
                        com.hierynomus.smbj.share.File output = null;
                        if (!share.fileExists(remoteDirectory)) {
                            output = share.openFile(remoteDirectory,
                                    new HashSet<>(Arrays.asList(AccessMask.MAXIMUM_ALLOWED)),
                                    new HashSet<>(Arrays.asList(FileAttributes.FILE_ATTRIBUTE_SYSTEM, FileAttributes.FILE_ATTRIBUTE_NORMAL)),
                                    SMB2ShareAccess.ALL,
                                    SMB2CreateDisposition.FILE_CREATE,
                                    new HashSet<>(Arrays.asList(SMB2CreateOptions.FILE_DIRECTORY_FILE)));
                            OutputStream os = output.getOutputStream();
                            os.write(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                            os.flush();
                            os.close();
                            output.close();

                            log.info(file.getName() + " has been uploaded to " + remoteDirectory);
                        } else {
                            log.info(file.getName() + " already existed in " + remoteDirectory);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean uploadFileViaSmb(String host, String username, String domainName, String password, String remoteNetworkPath, String... localFullPathFileNames) {
        boolean result = false;
        SmbConfig cfg = SmbConfig.builder().
                withMultiProtocolNegotiate(true).
                withSecurityProvider(new BCSecurityProvider()).
                build();
        SMBClient client = new SMBClient(cfg);

        try (Connection connection = client.connect(host)) {
            log.info("connected SMB Host " + host);
            AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domainName);
            Session session = connection.authenticate(ac);
            log.info("Authenticated");
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "/", File.separator);
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "\\", File.separator);
            String shareDisk = splitGetAtFirst(remoteNetworkPath, File.separator);
            log.info("ShareDisk Name => " + shareDisk);
            try (DiskShare share = (DiskShare) session.connectShare(shareDisk)) {
                String remoteDirectory = StringUtils.replaceOnce(remoteNetworkPath, shareDisk, "");
                String[] subFolders = StringUtils.split(remoteDirectory, File.separator);
                remoteDirectory = "";
                for (String aSubFolder : subFolders) {
                    if (AppUtils.getInstance().nonNull(aSubFolder)) {
                        remoteDirectory = remoteDirectory + File.separator + aSubFolder;
                        boolean folderExisted = share.folderExists(remoteDirectory);
                        log.info("RemoteDirectory " + remoteDirectory + " existed " + folderExisted);
                        if (!folderExisted) {
                            share.mkdir(remoteDirectory);
                            log.info(remoteDirectory + " is created");
                        }
                    }
                }
                for (String localFullPathFileName : localFullPathFileNames) {
                    File fileToBeUpload = new File(localFullPathFileName);
                    com.hierynomus.smbj.share.File output = null;
                    if (fileToBeUpload != null) {
                        if (!share.fileExists(remoteDirectory + File.separator + fileToBeUpload.getName())) {
                            output = share.openFile(remoteDirectory + File.separator + fileToBeUpload.getName(),
                                    new HashSet<>(Arrays.asList(AccessMask.MAXIMUM_ALLOWED)),
                                    new HashSet<>(Arrays.asList(FileAttributes.FILE_ATTRIBUTE_SYSTEM, FileAttributes.FILE_ATTRIBUTE_NORMAL)),
                                    SMB2ShareAccess.ALL,
                                    SMB2CreateDisposition.FILE_CREATE,
                                    new HashSet<>(Arrays.asList(SMB2CreateOptions.FILE_DIRECTORY_FILE)));
                            OutputStream os = output.getOutputStream();
                            os.write(Files.readAllBytes(Paths.get(localFullPathFileName)));
                            os.flush();
                            os.close();
                            output.close();
                            log.info(fileToBeUpload.getName() + " has been uploaded to " + remoteNetworkPath);
                            result = true;
                        } else {
                            log.info(fileToBeUpload.getName() + " already existed in " + remoteNetworkPath);
                        }
                    } else {
                        log.error(localFullPathFileName + " does not exist");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<File> listFiles(String directoryName) {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        assert fList != null;
        List<File> resultList = new ArrayList<>(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                //System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listFiles(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    public String encryptAES(String plainText, String keyPhrase) throws Exception {
        if (keyPhrase.length() != 16) {
            throw new Exception("Key must be in 16 digits");
        }
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return AppUtils.getInstance().toBase64(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptAES(byte[] data, String keyPhrase) throws Exception {
        if (keyPhrase.length() != 16) {
            throw new Exception("Key must be in 16 digits");
        }
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptAES(byte[] data, String keyPhrase) {
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptAES(String encryptedBase64Text, String keyPhrase) {
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] encryptedText = AppUtils.getInstance().fromBase64ToByte(encryptedBase64Text);
            String decrypted = new String(cipher.doFinal(encryptedText));
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getAccountNoFormat(String accountNo) {
        return StringUtils.isNotEmpty(accountNo) ? accountNo.substring(0, 5) + " "
                + accountNo.substring(5, 7) + " "
                + accountNo.substring(7, 13) + " "
                + accountNo.substring(13, 15) : null;
    }

    public List<String> getListOfString(String key, String value) {
        if (AppUtils.getInstance().nonNull(value)) {
            if (value.contains(",")) {
                List<String> result = Stream.of(value.split(",", -1))
                        .collect(Collectors.toList());
                return result;
            }
            return Collections.singletonList(value);
        }
        return new ArrayList<>();
    }

    public double roundKHRAmount(long khrAmount) {
        int last2Digit = getLast2Digit(khrAmount);
        if (last2Digit >= 50) {
            return khrAmount + (100 - last2Digit);
        }
        return khrAmount - last2Digit;
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
            if (result.length() > separator.length()) {
                return result.substring(0, result.length() - separator.length());
            }
            return result;
        } else {
            return result;
        }
    }

    public Date toDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        return date;
    }

    public Date toDate(int year, int month, int day, int hour, int minute) {
        return toDate(year, month, day, hour, minute, 0);
    }

    public Date toDate(int year, int month, int day, int hour) {
        return toDate(year, month, day, hour, 0, 0);
    }

    public Date toDate(int year, int month, int day) {
        return toDate(year, month, day, 0, 0, 0);
    }

    private int getLast2Digit(long amount) {
        String doubleAsString = String.valueOf(amount);
        if (doubleAsString.contains(".")) {
            doubleAsString = doubleAsString.split("\\.")[0];
        }
        if (doubleAsString.length() >= 2) {
            doubleAsString = StringUtils.right(doubleAsString, 2);
        }
        return Integer.parseInt(doubleAsString);
    }

    public String postXml(String url, String xmlRequest) {
        HashMap<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Content-Type", "text/xml");
        xmlRequest = StringUtils.normalizeSpace(xmlRequest);
        return processToServer(HttpMethod.POST, xmlRequest, url, headerParameters);
    }

    public <T> T getJson(String url, Class<T> outputClazz, KeyValue<String, String>... additionalHeaderParameters) {
        String responseBody = getJson(url, additionalHeaderParameters);
        if (AppUtils.getInstance().nonNull(responseBody))
            return (T) AppUtils.getInstance().toObject(responseBody, outputClazz);
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getJsonAuthorization(String url, Class<T> outputClazz, String bearerToken) {
        log.info("GET" + " to " + url);
        HashMap<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Content-Type", "application/json");
        headerParameters.put("Authorization", "Bearer " + bearerToken);
        String jsonResponse = null;
        try {
            HttpResponse<Object> response = Unirest.get(url).headers(headerParameters).asObject(String.class);
            assert response != null;
            jsonResponse = response.getBody().toString();
            if (response.getStatus() != 200) {
                log.error("HttpStatus = " + response.getStatus() + " with raw response => " + jsonResponse);
            } else {
                log.info("HttpStatus = " + response.getStatus());
            }
        } catch (UnirestException var7) {
            var7.printStackTrace();
        }
        if (AppUtils.getInstance().nonNull(jsonResponse))
            return (T) AppUtils.getInstance().toObject(jsonResponse, outputClazz);
        else {
            return null;
        }
    }

    public <T> T getJson(String url, TypeReference<T> outputClazz, KeyValue<String, String>... additionalHeaderParameters) {
        String responseBody = getJson(url, additionalHeaderParameters);
        if (AppUtils.getInstance().nonNull(responseBody))
            return (T) AppUtils.getInstance().toObject(responseBody, outputClazz);
        else {
            return null;
        }
    }

    public <T> T postJson(String url, HashMap<String, Object> body, TypeReference<T> outputClazz, KeyValue<String, String>... additionalHeaderParameters) {
        String responseBody = postJson(url, body, additionalHeaderParameters);
        if (AppUtils.getInstance().nonNull(responseBody))
            return (T) AppUtils.getInstance().toObject(responseBody, outputClazz);
        else {
            return null;
        }
    }

    public <T> T postJson(String url, String jsonBody, TypeReference<T> outputClazz, KeyValue<String, String>... additionalHeaderParameters) {
        String responseBody = postJson(url, jsonBody, additionalHeaderParameters);
        if (AppUtils.getInstance().nonNull(responseBody))
            return (T) AppUtils.getInstance().toObject(responseBody, outputClazz);
        else {
            return null;
        }
    }

    public String getJson(String url, KeyValue<String, String>... additionalHeaderParameters) {
        HashMap<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Content-Type", "application/json");
        if (additionalHeaderParameters != null) {
            for (int i = 0; i < additionalHeaderParameters.length; i++) {
                KeyValue<String, String> aValue = additionalHeaderParameters[i];
                if (aValue != null) {
                    if (AppUtils.getInstance().nonNull(aValue.getKey())) {
                        headerParameters.put(aValue.getKey(), aValue.getValue());
                    }
                }
            }
        }
        return processToServer(HttpMethod.GET, null, url, headerParameters);
    }

    public <T> T postJson(String url, HashMap<String, Object> requestBody, Class<T> outputClazz, KeyValue<String, String>... additionalHeaderParameters) {
        String requestBodyAsString = AppUtils.getInstance().toJsonString(requestBody, false);
        String value = postJson(url, requestBodyAsString, additionalHeaderParameters);
        if (AppUtils.getInstance().nonNull(value))
            return (T) AppUtils.getInstance().toObject(value, outputClazz);
        return null;
    }

    public String postJson(String url, HashMap<String, Object> requestBody, KeyValue<String, String>... additionalHeaderParameters) {
        String requestBodyAsString = AppUtils.getInstance().toJsonString(requestBody, false);
        return postJson(url, requestBodyAsString, additionalHeaderParameters);
    }

    public <T> T deleteJson(String url, Class<T> outputClazz) {
        String value = processToServer(HttpMethod.DELETE, null, url, null);
        if (AppUtils.getInstance().nonNull(value))
            return (T) AppUtils.getInstance().toObject(value, outputClazz);
        return null;
    }

    public String postJson(String url, String jsonBody, KeyValue<String, String>... additionalHeaderParameters) {
        HashMap<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Content-Type", "application/json");
        if (additionalHeaderParameters != null) {
            for (int i = 0; i < additionalHeaderParameters.length; i++) {
                KeyValue<String, String> aValue = additionalHeaderParameters[i];
                if (aValue != null) {
                    if (AppUtils.getInstance().nonNull(aValue.getKey())) {
                        headerParameters.put(aValue.getKey(), aValue.getValue());
                    }
                }
            }
        }
        return processToServer(HttpMethod.POST, jsonBody, url, headerParameters);
    }

    public String postText(String url, String jsonBody, KeyValue<String, String>... additionalHeaderParameters) {
        HashMap<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Content-Type", "text/plain");
        if (additionalHeaderParameters != null) {
            for (int i = 0; i < additionalHeaderParameters.length; i++) {
                KeyValue<String, String> aValue = additionalHeaderParameters[i];
                if (aValue != null) {
                    if (AppUtils.getInstance().nonNull(aValue.getKey())) {
                        headerParameters.put(aValue.getKey(), aValue.getValue());
                    }
                }
            }
        }
        return processToServer(HttpMethod.POST, jsonBody, url, headerParameters);
    }

    public String processToServer(HttpMethod httpMethod, String payload, String url, HashMap<String, String> headerParameters) {
        String urlForLog = url;
        try {
            Unirest.config().reset();
            Unirest.config().connectTimeout(UNIREST_TIMEOUT);
            Unirest.config().socketTimeout(UNIREST_TIMEOUT);
            Unirest.config().verifySsl(false);
            HttpResponse<String> response = null;

            urlForLog = urlForLog.replaceAll("[paygo24.com/api/pre_pay?sid=]@[\\s\\S]*$", "=******");
            if (HttpMethod.GET.equals(httpMethod)) {
                log.info(httpMethod.name() + " to " + urlForLog);
                response = Unirest.get(url).headers(headerParameters).asString();

            } else if (HttpMethod.PUT.equals(httpMethod)) {
                String logPayloadPut = payload.replaceFirst("(?s)<web:cm_password[^>]*>.*?</web:cm_password>", "<web:cm_password>*****</web:cm_password>");
                logPayloadPut = logPayloadPut.replaceFirst("(?s)<cm_password[^>]*>.*?</cm_password>", "<cm_password>*****</cm_password>");
                log.info(httpMethod.name() + " to " + urlForLog + " with body => " + logPayloadPut);
                response = Unirest.put(url).headers(headerParameters).body(payload).asString();
            } else if (HttpMethod.HEAD.equals(httpMethod)) {
            } else if (HttpMethod.POST.equals(httpMethod)) {
                String logPayload = payload.replaceFirst("(?s)<web:cm_password[^>]*>.*?</web:cm_password>", "<web:cm_password>*****</web:cm_password>");
                logPayload = logPayload.replaceFirst("(?s)<cm_password[^>]*>.*?</cm_password>", "<cm_password>*****</cm_password>");
                log.info(httpMethod.name() + " to " + urlForLog + " with body => " + logPayload);
                response = Unirest.post(url).headers(headerParameters).body(payload).asString();
            } else if (HttpMethod.PATCH.equals(httpMethod)) {
            } else if (HttpMethod.DELETE.equals(httpMethod)) {
                log.info(httpMethod.name() + " to " + urlForLog);
                response = Unirest.delete(url).asString();
            } else if (HttpMethod.OPTIONS.equals(httpMethod)) {
            }
            if (response == null) {
                log.error("response is NULL");
                return null;
            }
            String jsonResponse = response.getBody();
            String jsonResponseForLog = jsonResponse;
            if (AppUtils.getInstance().nonNull(jsonResponseForLog)) {
                jsonResponseForLog = jsonResponseForLog.replaceAll("(?s)<tran:Specific[^>]*>.*?</tran:Specific>", "<tran:Specific><tran:CreateVirtualCard Cvv2=\"*\"/></tran:Specific>");
            }
            if (response.getStatus() != 200) {
                log.error("HttpStatus = " + response.getStatus() + " with raw response => " + jsonResponseForLog);
            } else {
                log.info("HttpStatus = " + response.getStatus() + " with raw response => " + jsonResponseForLog);
            }
            return jsonResponse;
        } catch (UnirestException var7) {
            var7.printStackTrace();
        }
        return null;
    }

    public BigDecimal toDecimal(String value) {
        if (AppUtils.getInstance().isNull(value)) return BigDecimal.ZERO;
        BigDecimal bigDecimal = new BigDecimal(value);
        return toDecimal(bigDecimal);
    }

    public BigDecimal toDecimal(BigDecimal value) {
        return value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal toDecimal(Double value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return toDecimal(bigDecimal);
    }

    public String formatCurrency(BigDecimal value, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(value);
    }

    public String formatCurrency(BigDecimal value) {
        return formatCurrency(value, "###0.00");
    }

    public String splitGetAtEnd(String value, String separator) {
        String[] tmp = StringUtils.split(value, separator);
        if (tmp.length > 0) {
            return tmp[tmp.length - 1];
        }
        return value;
    }

    public String splitGetAtFirst(String value, String separator) {
        String[] tmp = StringUtils.split(value, separator);
        if (tmp.length > 0) {
            return tmp[0];
        }
        return value;
    }

    public String split(String value, String separator, int index, String defaultValue) {
        if (value.contains(separator)) {
            String[] tmp = StringUtils.split(value, separator);
            if (tmp.length > index) {
                return tmp[index];
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String split(String value, String separator, int indexAtFirst, int indexAtEnd, String defaultValue) {
        StringBuilder sb = new StringBuilder();
        if (value.contains(separator)) {
            String[] tmp = StringUtils.split(value, separator);
            if (tmp.length > indexAtFirst) {
                sb.append(tmp[indexAtFirst]);
            }
            if (tmp.length > indexAtEnd) {
                sb.append(tmp[indexAtEnd]);
            }
            return sb.toString();
        }
        return defaultValue;
    }

    public String toFastAmount(String currency, BigDecimal value) {
        if (currency.equalsIgnoreCase("KHR")) {
            if (value.toString().contains(".")) {
                return StringUtils.split(value.toString(), ".")[0];
            }
            return formatCurrency(value, "0");
        } else {
//            if (value.toString().contains(".")) {
//                long lastDecimalValue = Long.parseLong(splitGetAtEnd(value.toString(), "."));
//                if (lastDecimalValue == 0) {
//                    return formatCurrency(value, "0");
//                } else if (lastDecimalValue < 10) {
//                    return formatCurrency(value, "0.0");
//                } else {
//                    formatCurrency(value, "0.00");
//                }
//            }
            return formatCurrency(value, "0.00");
        }
    }

    public String toFastAmount(String currency, BigDecimal value, String format) {
        if (currency.equalsIgnoreCase("KHR")) {
            if (value.toString().contains(".")) {
                return formatCurrency(BigDecimal.valueOf(Long.parseLong(StringUtils.split(value.toString(), ".")[0])), format);
            }
            return formatCurrency(value, format);
        } else {
//            if (value.toString().contains(".")) {
//                long lastDecimalValue = Long.parseLong(splitGetAtEnd(value.toString(), "."));
//                if (lastDecimalValue == 0) {
//                    return formatCurrency(value, "0");
//                } else if (lastDecimalValue < 10) {
//                    return formatCurrency(value, "0.0");
//                } else {
//                    formatCurrency(value, "0.00");
//                }
//            }
            return formatCurrency(value, "0.00");
        }
    }

    private String sqlString(String value) {
        return "'" + value + "'";
    }

    public String sql(String... values) {
        StringBuilder result = new StringBuilder();
        for (Object value : values) {
            result.append(sqlString(String.valueOf(value)));
            result.append(",");
        }
        if (result.toString().endsWith(",")) {
            return result.substring(0, result.length() - 1);
        }
        return result.toString();
    }

    public String sqlNumber(String... values) {
        StringBuilder result = new StringBuilder();
        for (Object value : values) {
            result.append(value);
            result.append(",");
        }
        if (result.toString().endsWith(",")) {
            return result.substring(0, result.length() - 1);
        }
        return result.toString();
    }

    public String sql(Object... values) {
        StringBuilder result = new StringBuilder();
        for (Object value : values) {
            Class clazz = value.getClass();
            if (clazz == Integer.class || clazz == Double.class || clazz == Float.class) {
                result.append(value);
            } else if (clazz == String.class) {
                result.append(sqlString(String.valueOf(value)));
            }
            result.append(",");
        }
        if (result.toString().endsWith(",")) {
            return result.substring(0, result.length() - 1);
        }
        return result.toString();
    }

    public String inputString(String label, String defaultValue) {
        String value = "";
        while (AppUtils.getInstance().isNull(value)) {
            System.out.print(label);
            Scanner input = new Scanner(System.in);
            value = input.nextLine();
            if (AppUtils.getInstance().isNull(value)) {
                value = defaultValue;
            }
        }
        return value;
    }

    public int inputInteger(String label, int defaultValue) {
        String value = inputString(label, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }

    private String readLine(String message, String defaultPassword) throws IOException {
        if (System.console() != null) {
            String value = System.console().readLine(message);
            if (AppUtils.getInstance().isNull(value)) {
                return defaultPassword;
            }
            return value;
        }
        System.out.print(message);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String value = reader.readLine();
        if (AppUtils.getInstance().isNull(value)) {
            return defaultPassword;
        }
        return value;
    }

    public String inputPassword(String message, String defaultPassword)
            throws IOException {
        if (System.console() != null) {
            String value = String.valueOf(System.console().readPassword(message));
            if (AppUtils.getInstance().isNull(value)) {
                return defaultPassword;
            }
            return value;
        }
        return String.valueOf(readLine(message, defaultPassword).toCharArray());
    }

    public boolean inputConfirm(String label) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(label);
            String value = scanner.nextLine();
            if (value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
                return false;
            } else if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
                return true;
            }
        }
    }

    public String encryptWindowDataProtectionAPI(String rawString) {
        if (WinDPAPI.isPlatformSupported()) {
            WinDPAPI winDPAPI = null;
            try {
                winDPAPI = WinDPAPI.newInstance(WinDPAPI.CryptProtectFlag.CRYPTPROTECT_LOCAL_MACHINE);
                String charsetName = "UTF-8";
                byte[] cipherTextBytes = winDPAPI.protectData(rawString.getBytes(charsetName));
                String encryptedText = AppUtils.getInstance().toBase64(cipherTextBytes);
                return encryptedText;
            } catch (InitializationFailedException | UnsupportedEncodingException | WinAPICallFailedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("ERROR: platform not supported");
        }
        return null;
    }

    public byte[] encryptWindowDataProtectionAPI(byte[] data) {
        if (WinDPAPI.isPlatformSupported()) {
            WinDPAPI winDPAPI = null;
            try {
                winDPAPI = WinDPAPI.newInstance(WinDPAPI.CryptProtectFlag.CRYPTPROTECT_LOCAL_MACHINE);
                byte[] cipherTextBytes = winDPAPI.protectData(data);
                return cipherTextBytes;
            } catch (InitializationFailedException | WinAPICallFailedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("ERROR: platform not supported");
        }
        return null;
    }

    public String decryptWindowDataProtectionAPI(String encryptedText) {
        if (WinDPAPI.isPlatformSupported()) {
            WinDPAPI winDPAPI = null;
            try {
                winDPAPI = WinDPAPI.newInstance(WinDPAPI.CryptProtectFlag.CRYPTPROTECT_LOCAL_MACHINE);
                String charsetName = "UTF-8";
                byte[] cipherTextBytes = winDPAPI.unprotectData(AppUtils.getInstance().fromBase64ToByte(encryptedText));
                String decryptedText = new String(cipherTextBytes, charsetName);
                return decryptedText;
            } catch (InitializationFailedException | UnsupportedEncodingException | WinAPICallFailedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("ERROR: platform not supported");
        }
        return null;
    }

    public byte[] decryptWindowDataProtectionAPI(byte[] data) {
        if (WinDPAPI.isPlatformSupported()) {
            WinDPAPI winDPAPI = null;
            try {
                winDPAPI = WinDPAPI.newInstance(WinDPAPI.CryptProtectFlag.CRYPTPROTECT_LOCAL_MACHINE);
                byte[] cipherTextBytes = winDPAPI.unprotectData(data);
                return cipherTextBytes;
            } catch (InitializationFailedException | WinAPICallFailedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("ERROR: platform not supported");
        }
        return null;
    }

    public byte[] fromObjectToByteArray(Object serializableObject) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(serializableObject);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public Object fromByteArrayToObject(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public String getExecutableFilePath(Class clazz) throws UnsupportedEncodingException {
        String path = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath() + ".conf";
        return URLDecoder.decode(path, "UTF-8");
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    public boolean sendEmail(String host,
                             int port,
                             boolean authenticationEnable,
                             SendEmailSecurityOption sslOption,
                             String username,
                             String password,
                             @NonNull String fromEmailAddress,
                             @NonNull String toEmailAddressCommaOption,
                             String ccEmailAddressCommaOption,
                             @NonNull String subject,
                             @NonNull String body,
                             List<String> attachmentRelativePath) {

        log.info("Sending Email via " + host + ":" + port + " with " + username + " auth: " + authenticationEnable + " ssl: " + sslOption);
        Properties prop = new Properties();
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", String.valueOf(port));
        prop.put("mail.smtp.auth", String.valueOf(authenticationEnable));
        prop.put("mail.smtp.ssl.trust", "*");

        switch (sslOption) {
            case SSL:
                prop.put("mail.smtp.ssl.enable", "true");
                break;
            case None:
                break;
            case TTSL:
                prop.put("mail.smtp.starttls.enable", "true");
                break;
            case TTSLv12:
                prop.put("mail.smtp.starttls.enable", "true");
                prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
                break;
        }
        return sendEmail(prop, username, password, fromEmailAddress, toEmailAddressCommaOption, ccEmailAddressCommaOption, null, subject, body, attachmentRelativePath);
    }

    public boolean sendEmail(String host,
                             int port,
                             boolean authenticationEnable,
                             SendEmailSecurityOption sslOption,
                             String username,
                             String password,
                             @NonNull String fromEmailAddress,
                             @NonNull String toEmailAddressCommaOption,
                             String ccEmailAddressCommaOption,
                             String bccEmailAddressCommaOption,
                             @NonNull String subject,
                             @NonNull String body,
                             List<String> attachmentRelativePath) {

        log.info("Sending Email via " + host + ":" + port + " with " + username + " auth: " + authenticationEnable + " ssl: " + sslOption);
        Properties prop = new Properties();
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", String.valueOf(port));
        prop.put("mail.smtp.auth", String.valueOf(authenticationEnable));
        prop.put("mail.smtp.ssl.trust", "*");

        switch (sslOption) {
            case SSL:
                prop.put("mail.smtp.ssl.enable", "true");
                break;
            case None:
                break;
            case TTSL:
                prop.put("mail.smtp.starttls.enable", "true");
                break;
            case TTSLv12:
                prop.put("mail.smtp.starttls.enable", "true");
                prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
                break;
        }
        return sendEmail(prop, username, password, fromEmailAddress, toEmailAddressCommaOption, ccEmailAddressCommaOption, bccEmailAddressCommaOption, subject, body, attachmentRelativePath);
    }

    public boolean sendEmail(Properties properties,
                             String username,
                             String password,
                             @NonNull String fromEmailAddress,
                             @NonNull String toEmailAddressCommaOption,
                             String ccEmailAddressCommaOption,
                             String bccEmailAddressCommaOption,
                             @NonNull String subject,
                             @NonNull String body,
                             List<String> attachmentRelativePath) {

        javax.mail.Session session = javax.mail.Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });
        Message msg = new MimeMessage(session);
        try {

            msg.setFrom(new InternetAddress(fromEmailAddress));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddressCommaOption, false));
            if (AppUtils.getInstance().nonNull(ccEmailAddressCommaOption)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmailAddressCommaOption, false));
            }
            if (AppUtils.getInstance().nonNull(bccEmailAddressCommaOption)) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccEmailAddressCommaOption, false));
            }

            msg.setSubject(subject);
            MimeBodyPart text = new MimeBodyPart();
            text.setDataHandler(new DataHandler(new HTMLDataSource(body)));

            List<MimeBodyPart> attachments = null;
            if (AppUtils.getInstance().nonNull(attachmentRelativePath)) {
                for (int i = 0; i < attachmentRelativePath.size(); i++) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(attachmentRelativePath.get(i));
                    try {
                        attachment.setDataHandler(new DataHandler(fileDataSource));
                        attachment.setFileName(fileDataSource.getName());
                        if (attachments == null) attachments = new ArrayList<>();
                        attachments.add(attachment);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(text);
            if (attachments != null) {
                if (attachments.size() > 0) {
                    attachments.forEach(mimeBodyPart -> {
                        try {
                            multipart.addBodyPart(mimeBodyPart);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            msg.setContent(multipart);
            msg.setSentDate(new Date());
            Transport.send(msg);
            log.info("Email has been sent successfully");
            return true;
        } catch (MessagingException e) {
            log.error("Email sending failed because => " + e.getLocalizedMessage());
            log.error(e.getMessage(), e.fillInStackTrace());
            e.printStackTrace();
            return false;
        }
    }

    static class HTMLDataSource implements DataSource {

        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (html == null) throw new IOException("html message is null!");
            return new ByteArrayInputStream(html.getBytes());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        @Override
        public String getContentType() {
            return "text/html; charset=utf-8";
        }

        @Override
        public String getName() {
            return "HTMLDataSource";
        }
    }
}
