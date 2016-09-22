package com.mohitkishore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    private static String getJSONString() throws Exception {

        //String to hold value of the returned data
        String productsJsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String urlString = "http://shopicruit.myshopify.com/products.json?page=1";

        try {

            URL url = new URL(urlString);

            // Create the request , and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            productsJsonStr = buffer.toString();
        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.out.println("Oh-oh error");
                }
            }
        }
        return productsJsonStr;
    }

    public static String[] getJSONArray(String productsJsonStr){

        JSONObject productstJson = new JSONObject(productsJsonStr);
        return null;
    }
    public static void main(String[] args) {

        try {
            System.out.println(Main.getJSONString());
        } catch (Exception e) {
            System.out.println("Oh-oh error");
            e.printStackTrace();
        }
    }
}
