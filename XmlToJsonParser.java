package converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlToJsonParser {

    public String parseToJson(String input) {
        Pattern elementPattern = Pattern.compile("</?(.+?)>");
        Matcher elementMatcher = elementPattern.matcher(input);
        List<String> pathElements = new ArrayList<>();

        while (elementMatcher.find()) {
            String[] matchArray = elementMatcher.group(1).split("\\s+");
            String match = matchArray[0];
            if (pathElements.contains(match)) {
                pathElements.remove(match);
            } else {
                pathElements.add(match);
                System.out.println("Element:");
                printPath(pathElements);
            }
            if (elementMatcher.group(0).startsWith("</")) {
                pathElements.remove(match);
            }
        }
        return null;
    }

    private void printPath(List<String> pathElements) {
        System.out.print("\npath = ");
        for (int i = 0; i < pathElements.size(); i++) {
            System.out.print(pathElements.get(i));
            if (i != pathElements.size() -1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    private String getElementFromXml(String input) {
        Pattern elementPattern = Pattern.compile("<.+?[\\s+>]");
        Matcher elementMatcher = elementPattern.matcher(input);
        if (elementMatcher.find()) {
            return input.substring(1, elementMatcher.end() - 1);
        }
        return null;
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
}
