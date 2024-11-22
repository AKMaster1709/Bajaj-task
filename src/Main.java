import org.json.JSONObject;

import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Step 1: Parse command-line arguments
        if (args.length != 2) {
            System.err.println("Usage: java -jar <jarfile> <roll_number> <path_to_json_file>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s+", ""); // Normalize roll number
        String jsonFilePath = args[1];

        try {
            // Step 2: Parse JSON file
            JSONObject jsonObject = new JSONObject(new FileReader(jsonFilePath));

            // Step 3: Traverse JSON to find the first "destination" key
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Step 4: Generate a random alphanumeric string
            String randomString = generateRandomString(8);

            // Step 5: Generate MD5 hash of concatenated string
            String concatenatedValue = rollNumber + destinationValue + randomString;
            String md5Hash = computeMD5Hash(concatenatedValue);

            // Step 6 & 7: Output the result in the required format
            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // Traverse the JSON object to find the first instance of the key "destination"
    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (key.equals("destination")) {
                return value.toString();
            }

            if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null; // Key not found
    }

    // Generate a random alphanumeric string of the given length
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    // Compute the MD5 hash of a given string
    private static String computeMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
