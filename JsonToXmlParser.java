package converter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonToXmlParser {

    public String parseToXml(String input) {
        String element = getElementFromJson(input);
        String elementValue = getElementValueFromJson(input, element);
        Map<String, String> attributes = getAttributesFromJson(input);
        return generateXml(element, elementValue, attributes);
    }

    private String getElementFromJson(String input) {
        input = input.replaceAll("\\s+", "");
        Pattern elementPattern = Pattern.compile("\\{\".+?\"");
        Matcher elementMatcher = elementPattern.matcher(input);
        if (elementMatcher.find()) {
            String element = input.substring(elementMatcher.start(), elementMatcher.end());
            return element.replaceAll("[\"{]", "");
        }
        return null;
    }

    private String getElementValueFromJson(String input, String element) {
        String[] array = input.split("[:,]");
        if (array.length == 2) {
            return array[1].replaceAll("[\"}]", "").trim();
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].contains("#" + element)) {
                String returnVal = array[i + 1].replaceAll("[\"}]", "").trim();
                return returnVal.equals("null") ? null : returnVal;
            }
        }
        return null;
    }

    private Map<String, String> getAttributesFromJson(String input) {
        String[] array = input.split("[:,]");
        Map<String, String> attributes = new HashMap<>();

        if (array.length == 2) {
            return attributes;
        }


        for (int i = 0; i < array.length; i++) {
            String str = array[i].replaceAll("[\"{}]", "").trim();
            if (str.startsWith("@")) {
                attributes.put(str.substring(1), array[i + 1].replaceAll("[\"{}]", "").trim());
            }
        }
        return attributes;
    }

    private String generateXml(String element, String elementValue, Map<String, String> attributes) {
        if (attributes.isEmpty()) {
            return String.format("<%s>%s</%s>", element, elementValue, element);
        }

        StringBuilder sb = new StringBuilder("<");
        sb.append(element).append(" ");
        for (String attribute : attributes.keySet()) {
            sb.append(attribute).append(" = ").append("\"").append(attributes.get(attribute)).append("\" ");
        }
        if (elementValue == null) {
            sb.append("/>");
        } else {
            sb.append(">").append(elementValue).append("</").append(element).append(">");
        }
        return sb.toString();
    }
}
