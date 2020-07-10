package converter;

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
            json = new XmlToJsonParser().parseToJson(input);
        } else {
            json = input;
            xml = new JsonToXmlParser().parseToXml(input);
        }
    }
}