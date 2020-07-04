package converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConversionApp {
    private final String input;
    private String json = null;
    private String xml = null;

    public ConversionApp(String input) {
        this.input = input.trim();
        parse(input);
    }

    public boolean sourceIsXml() {
        return input.startsWith("<");
    }

    public String getJson() {
        return json;
    }

    public String getXml() {
        return xml;
    }

    private void parse(String input) {
        if (sourceIsXml()) {
            xml = input;
            json = parseToJson(input);
        } else {
            json = input;
            xml = parseToXml(input);
        }
    }

    private String parseToXml(String input) {
        String element = getElementFromJson(input);
        String elementValue = getElementValueFromJson(input, element);
        Map<String, String> attributes = getAttributesFromJson(input);
        return generateXml(element, elementValue, attributes);
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

    private String parseToJson(String input) {
        String element = getElementFromXml(input);
        String elementValue = getElementValueFromXml(input, element);
        Map<String, String> attributes = getAttributesFromXml(input, element);
        return generateJson(element, elementValue, attributes);
    }

    private String generateJson(String element, String elementValue, Map<String, String> attributes) {
        StringBuilder sb = new StringBuilder("{ \"");
        sb.append(element).append("\" : ");
        if (attributes.isEmpty()) {
            sb.append("\"").append(elementValue).append("\" }");
        } else {
            sb.append("{ ");
            for (String attribute : attributes.keySet()) {
                sb.append("\"@").append(attribute).append("\" : ").append("\"").append(attributes.get(attribute))
                        .append("\", ");
            }
            if (elementValue == null) {
                sb.append("\"#").append(element).append("\" : ").append(elementValue).append(" } }");
            } else {
                sb.append("\"#").append(element).append("\" : \"").append(elementValue)
                        .append("\"").append(" } }");
            }
        }
        return sb.toString();
    }

    private Map<String, String> getAttributesFromXml(String input, String element) {
        Map<String, String> attributes = new HashMap<>();

        Pattern pattern = Pattern.compile("<" + element + " " + "(.+?)" + "[>/]");
        Matcher matcher = pattern.matcher(input);
        String attributesString = null;
        if (matcher.find()) {
            attributesString = matcher.group(1);
        }
        if (attributesString == null) {
            return attributes; // empty return
        }

        attributesString = attributesString.replaceAll("[\"=]", "");
        String[] attributesArray = attributesString.split("\\s+");
        for (int i = 0; i < attributesArray.length - 1; i = i + 2) {
            attributes.put(attributesArray[i], attributesArray[i + 1]);
        }
        return attributes;
    }

    private String getElementValueFromXml(String input, String element) {
        Pattern pattern = Pattern.compile("<" + element + ">(.+)</" + element + ">");
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("<" + element + " " + ".+>(.+)</" + element + ">");
        matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    private String getElementFromXml(String input) {
        Pattern elementPattern = Pattern.compile("<.+?[\\s+>]");
        Matcher elementMatcher = elementPattern.matcher(input);
        if (elementMatcher.find()) {
            return input.substring(1, elementMatcher.end() - 1);
        }
        return null;
    }
}