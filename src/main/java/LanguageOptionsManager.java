import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageOptionsManager {
    public static enum LanguageOptionsEnum
    {
        EN, AR, ZH_CN, NL, FR, DE, HI, IT, JA, KO, PT, RU, ES, SV, TR, VI
    }

    private static HashMap<LanguageOptionsEnum, String> languageMap = new HashMap<>();
    static
    {
        languageMap.put(LanguageOptionsEnum.EN, "en");
        languageMap.put(LanguageOptionsEnum.AR, "ar");
        languageMap.put(LanguageOptionsEnum.ZH_CN, "zh-CN");
        languageMap.put(LanguageOptionsEnum.NL, "nl");
        languageMap.put(LanguageOptionsEnum.FR, "fr");
        languageMap.put(LanguageOptionsEnum.DE, "de");
        languageMap.put(LanguageOptionsEnum.HI, "hi");
        languageMap.put(LanguageOptionsEnum.IT, "it");
        languageMap.put(LanguageOptionsEnum.JA, "ja");
        languageMap.put(LanguageOptionsEnum.KO, "ko");
        languageMap.put(LanguageOptionsEnum.PT, "pt");
        languageMap.put(LanguageOptionsEnum.RU, "ru");
        languageMap.put(LanguageOptionsEnum.ES, "es");
        languageMap.put(LanguageOptionsEnum.SV, "sv");
        languageMap.put(LanguageOptionsEnum.TR, "tr");
        languageMap.put(LanguageOptionsEnum.VI, "vi");
    }

    private static HashMap<String, LanguageOptionsEnum> languageReadableStringMap = new HashMap<>();
    static
    {
        languageReadableStringMap.put("English", LanguageOptionsEnum.EN);
        languageReadableStringMap.put("Arabic", LanguageOptionsEnum.AR);
        languageReadableStringMap.put("Chinese (Simplified)", LanguageOptionsEnum.ZH_CN);
        languageReadableStringMap.put("Dutch", LanguageOptionsEnum.NL);
        languageReadableStringMap.put("French", LanguageOptionsEnum.FR);
        languageReadableStringMap.put("German", LanguageOptionsEnum.DE);
        languageReadableStringMap.put("Hindi", LanguageOptionsEnum.HI);
        languageReadableStringMap.put("Italian", LanguageOptionsEnum.IT);
        languageReadableStringMap.put("Japanese", LanguageOptionsEnum.JA);
        languageReadableStringMap.put("Korean", LanguageOptionsEnum.KO);
        languageReadableStringMap.put("Portuguese", LanguageOptionsEnum.PT);
        languageReadableStringMap.put("Russian", LanguageOptionsEnum.RU);
        languageReadableStringMap.put("Spanish", LanguageOptionsEnum.ES);
        languageReadableStringMap.put("Swedish", LanguageOptionsEnum.SV);
        languageReadableStringMap.put("Turkish", LanguageOptionsEnum.TR);
        languageReadableStringMap.put("Vietnamese", LanguageOptionsEnum.VI);
    }

    public static String getAbbreviatedLanguage(String lang)
    {
        LanguageOptionsEnum languageOptionsEnum = languageReadableStringMap.get(lang);
        return languageMap.get(languageOptionsEnum);
    }

    public static List<String> getAllSupportedLanguage()
    {
        return languageReadableStringMap.keySet().stream().map(lang -> lang.toString()).collect(Collectors.toList());
    }

}


