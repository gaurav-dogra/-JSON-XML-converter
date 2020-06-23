package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionApp {

    public String convert(String inputString) throws Exception {
        inputString = inputString.trim();
        IConvert converter;

        if (inputString.charAt(0) == '<') {
            converter = this::xmlToJson;

        } else if (inputString.charAt(0) == '{') {
            converter = this::jsonToXml;
        } else {
            throw new Exception("Unable to parse input String");
        }

        return converter.convert(inputString);
    }

    private String jsonToXml(String input) {

        Pattern pattern = Pattern.compile("\".+?\"");
        Matcher matcher = pattern.matcher(input);
        String key = null;
        String value = null;

        if (matcher.find()) {
            key = input.substring(matcher.start() + 1, matcher.end() - 1);
        }

        if (matcher.find()) {
            value = input.substring(matcher.start() + 1, matcher.end() - 1);
        }

        if (value == null && key != null) {
            return "<" + key + "/>";
        } else {

            return "<" + key + ">" + value + "</" + key + ">";
        }
    }

    private String xmlToJson(String input) {

        Pattern pattern = Pattern.compile("<.*?>");
        Matcher firstMatcher = pattern.matcher(input);
        String key = null;
        if (firstMatcher.find()) {
            key = input.substring(firstMatcher.start() + 1, firstMatcher.end() - 1);
        }
        pattern = Pattern.compile("</.*>");
        Matcher secondMatcher = pattern.matcher(input);
        String value = null;
        if (secondMatcher.find()) {
            value = input.substring(firstMatcher.end(), secondMatcher.start());
        }

        if (value == null && key != null) {
            key = key.substring(0, key.length() - 1);
            return "{\"" + key + "\": " + null + " }";
        } else {
            return "{\"" + key + "\":\"" + value + "\"}";
        }

    }
}
