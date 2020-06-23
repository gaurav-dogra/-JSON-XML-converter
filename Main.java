package converter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        ConversionApp app = new ConversionApp();
        Scanner scanner = new Scanner(System.in);

        String output = app.convert(scanner.nextLine());
        System.out.println(output);
    }
}
