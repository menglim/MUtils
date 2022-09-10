package com.github.menglim.mutils;

import java.util.Optional;

public class CoreConstants {
    public static final String URL_SECURED = "true_";
    public static final String URL_NOT_SECURE = "false_";
    public static final int BUTTONS_TO_SHOW = 15;
    public static final int INITIAL_PAGE = 0;
    public static final int INITIAL_PAGE_SIZE = 10;
    public static final int[] PAGE_SIZES = {5, 10, 15, 30, 60, 100, 200, 300, 500, 1000};

    public static final String HTTP_HEADER_AUTHORIZATION_PREFIX = "Bearer ";
    public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    public static final String HTTP_HEADER_DEVICE_TOKEN = "ClientId";
    public static final String JSON_PRODUCE_MINE_TYPE = "application/json";
    public static final String JWT_PAYLOAD_STRING = "Payload";
    public static final String HOLIDAY_DATE_FORMAT = "dd MMMM yyyy";
    public static final String KEY_APP_ID = "appId";
    public static final String KEY_ACCESS_TOKEN = "accessToken";
    public static final String KEY_CHECKSUM = "verifyToken";
    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String LEAVE_DATE_FORMAT = "dd MMM yyyy (a)";

    public enum OTPMethod {
        SMS,
        Email,
        SMS_And_Email;

//        @Override
//        public String toString() {
//            String name = super.toString().replaceAll("_", " ");
//            return StringUtils.capitalize(name);
//        }
    }

    public enum OTPRequired {
        Not_Required,
        On_New_Device_Only,
        On_Every_Login;

//        @Override
//        public String toString() {
//            String name = super.toString().replaceAll("_", " ");
//            return StringUtils.capitalize(name);
//        }
    }

    public enum Gender {
        NotSet,
        Male,
        Female,
        Other
    }

    public enum MaritalStatus {
        NotSet,
        Single,
        Married,
        Widowed,
        Separated,
        Divorced
    }

    public enum CompanyType {
        Individual,
        Company
    }

    public enum BICMobileActivationType {
        AccountNo,
        ATMCardNo
    }

    public enum EmploymentType {
        NotSet,
        FullTime,
        PartTime,
        Intern,
        Contractor
    }

    public enum TShirtSize {
        NotSet,
        XXS,
        XS,
        S,
        M,
        L,
        XL,
        XXL,
        XXXL
    }

    public enum Title {
        NotSet,
        Mr,
        Mrs,
        Ms,
        Miss
    }

    public enum LoginType {
        Mobile,
        Facebook,
        GooglePlus
    }

    public enum DeviceType {
        Android,
        iOS,
        Browser;

        public static CoreConstants.DeviceType[] getDeviceType(Optional<String[]> stringOptional) {
            if (!stringOptional.isPresent()) return DeviceType.values();
            return getDeviceType(stringOptional.orElse(new String[]{"1"}));
        }

        public static CoreConstants.DeviceType[] getDeviceType(String[] values) {
            DeviceType[] reValue = new DeviceType[values.length];
            for (int i = 0; i < values.length; i++) {
                reValue[i] = DeviceType.values()[Integer.parseInt(values[i])];
            }
            return reValue;
        }
    }

    public enum Status {
        Disabled,
        Enabled,
        Pending,
        Deleted;

        public static CoreConstants.Status[] getStatus(Optional<String[]> stringOptional) {
            return getStatus(stringOptional.orElse(new String[]{"1"}));
        }

        public static CoreConstants.Status[] getStatus(String[] values) {
            Status[] reValue = new Status[values.length];
            for (int i = 0; i < values.length; i++) {
                reValue[i] = Status.values()[Integer.parseInt(values[i])];
            }
            return reValue;
        }
    }

    public enum AdminUserSource {
        Database,
        ActiveDirectory
    }

    public enum AttachmentType {
        LOCATION,
        ROOM,
        EMPLOYEE_PROFILE_PICTURE,
        BANNER,
        ICON,
        THUMBNAIL,
        PRODUCT,
        CATEGORY,
        PURCHASE_ORDER,
        SALE_ORDER,
        CUSTOMER_PROFILE_PICTURE,
        INVOICE,
        RECEIPT,
        SUPPLIER,
        DELIVERY_AGENT,
        VEHICLE,
        LEAVE_REQUEST,
        COMPANY_LOGO,
        COMPANY,
        CUSTOMER_NOTICE,
        ANNOUNCEMENT,
    }

    public enum StockType {
        IN,
        OUT,
        ADJ,
        RET
    }

    public enum LinkTileColorCode {
        lime,
        green,
        emerald,
        blue,
        teal,
        cyan,
        cobalt,
        indigo,
        violet,
        pink,
        magenta,
        crimson,
        red,
        orange,
        amber,
        yellow,
        brown,
        olive,
        steel,
        mauve,
        taupe,
        gray,
        grayBlue,
    }

    public enum LinkTileColorType {
        Light,
        Normal,
        Dark
    }

    public enum BillerFieldType {
        InputText,
        List
    }

    public enum BillerDataType {
        Text,
        Number,
        USDAmount,
        KHRAmount,
        Email,
        MobileNo,
        List
    }

    public enum MultipleDeviceSingSessionType {
        OverridePreviousSession,
        LoginNotAllowed
    }

    public enum BannerType {
        DashboardBanner,
        FirstPageBanner
    }

    public enum LanguageCodeP {
        en,
        km
    }

    public enum LocatorType {
        ATM,
        Branch,
        BICValue,
        Merchant,
        Agent
    }

    public enum RefreshTokenType {
        PIN,
        Biometric
    }

    public enum FavoriteType {
        Transfer,
        Payment
    }

    public enum NotificationType {
        AdvanceNotice,
        Transaction
    }

    public enum WorkDay {
        No,
        Full,
        Half
    }

    public enum PriorityType {
        Low,
        Medium,
        High,
        Critical
    }

    public enum LeaveStatus {
        Plan,
        Pending,
        Relieved,
        Approved,
        NotRelieved,
        Rejected,
        PendingCancel,
        Cancelled;

        public static CoreConstants.LeaveStatus[] getLeaveStatus(Optional<String[]> stringOptional) {
            return getLeaveStatus(stringOptional.orElse(new String[]{"1"}));
        }

        public static CoreConstants.LeaveStatus[] getLeaveStatus(String[] values) {
            LeaveStatus[] reValue = new LeaveStatus[values.length];
            for (int i = 0; i < values.length; i++) {
                if (!values[i].isEmpty())
                    reValue[i] = LeaveStatus.values()[Integer.parseInt(values[i])];
            }
            return reValue;
        }
    }

//    public enum SCREEN_IDENTIFIER {
//        STAY_IN_SCREEN,
//        SCREEN_INPUT_OTP,
//        SCREEN_LANDING_SCREEN,
//        SCREEN_NEXT
//    }


    //    public static class StatusModel {
//        private int code;
//        private String statusName;
//
//        public StatusModel() {
//        }
//
//        public StatusModel(int code, String statusName) {
//            this.code = code;
//            this.statusName = statusName;
//        }
//
//        public int getCode() {
//            return code;
//        }
//
//        public void setCode(int code) {
//            this.code = code;
//        }
//
//        public String getStatusName() {
//            return statusName;
//        }
//
//        public void setStatusName(String statusName) {
//            this.statusName = statusName;
//        }
//    }
    public enum PromotionType {
        Link,
        Content
    }

    public enum ChannelType {
        ATM("A"),
        Branch("B"),
        InternetBanking("I");

        private String channelType;

        ChannelType(String channelType) {
            this.channelType = channelType;
        }

        public String getChannelType() {
            return channelType;
        }
    }

    public enum CurrencyCode {
        USD,
        KHR
    }

    public enum ReportExportType {
        PDF,
        XLXS,
        CSV
    }

    public enum TransactionStatus {
        Success,
        Pending,
        Rejected,
        Cancelled
    }

    public enum TransactionPosition {
        Debit,
        Credit,
        Pending,
    }

    public enum KeyboardType {
        Integer,
        Decimal,
        Text
    }

    public enum TermConditionType {
        TermOfUse,
        Registration,
        SplitBill
    }

    public enum OTPGenerationMethod {
        FixedValue,
        RandomValue
    }

    public enum ProductImportType {
        Local,
        Abroad
    }

    public enum CustomerType {
        Individual,
        Company
    }

    public enum ContactType {
        MobileNo,
        HomeNo,
        Email,
        Fax
    }

    public enum AddressType {
        Permanent,
        Temporary,
        Present
    }

    public enum SupplierType {
        Individual,
        Company
    }

    public enum EmploymentStatus {
        Active,
        Resigned,
        Terminated,
        OnLeave,
        NotSet;

        public static CoreConstants.EmploymentStatus[] getEmploymentStatus(Optional<String[]> stringOptional) {
            return getEmploymentStatus(stringOptional.orElse(new String[]{"1"}));
        }

        public static CoreConstants.EmploymentStatus[] getEmploymentStatus(String[] values) {
            EmploymentStatus[] reValue = new EmploymentStatus[values.length];
            for (int i = 0; i < values.length; i++) {
                reValue[i] = EmploymentStatus.values()[Integer.parseInt(values[i])];
            }
            return reValue;
        }
    }

    public enum ContentType {
        JSON,
        XML
    }

    public enum FormatDate {
        DDMMYYYY,
        MMDDYYYY,
        YYYYMMDD,
    }

    public enum IncrementType {
        NumberOnly,
        CapitalOnly,
        All
    }
}
