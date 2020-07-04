package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String input = readInput();
        ConversionApp app = new ConversionApp(input);
        String output = app.sourceIsXml() ? app.getJson() : app.getXml();
        System.out.println(output);
    }

    private static String readInput() throws IOException {
//        final Scanner scanner = new Scanner(System.in);
        return Files.readString(Paths.get("test.txt"));
    }
}

