package in.co.tsmith.miniso;

public class AppConfigSettings {

    static final String ClientValidator = "MOB45831-E9SO-47B1-916C-4MIS6FAFETTS";
    static final String TAG = "MSOTAG";
    static final int WSTimeOutValueVerySmall = 20000;
    static final int WSTimeOutValueSmall = 30000;
    static final int WSTimeOutValueMedium = 45000;
    //	 static final int WSTimeOutValueHigh = 120000;
    static final int WSTimeOutValueHigh = 300000;

    static final String DeviceSettingsURL ="http://vansales.tsmithindia.com/MobSODeviceSettingsService01.asmx";//url for downloading device settings

    public static final String WSNAMESPACE = "http://tempuri.org/";

    //This Field can Take Values MOBSO or SFASO
    static final String APPFlag = "SFASO";

    //	 static final boolean IS_OFFLINE_ENABLED = false; //Commented by 1165 on 25-02-2020
    static final boolean IS_OFFLINE_ENABLED = true;
}
