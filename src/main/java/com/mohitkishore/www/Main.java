package com.mohitkishore.www;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    private static final String PRODUCTS = "products";
    private static final String VARIANTS = "variants";
    private static final String PRICE = "price";
    private static final String API_END_POINT_BASE_URL = "http://shopicruit.myshopify.com/products.json?";
    private static final String PAGE_QUERY = "page=";
    private static double totalPrice = 0.0;


    private static String getJSONString(String buildUrl) throws Exception {
        //String to hold value of the returned data
        String resultJsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(buildUrl);

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                System.out.println("Input stream is null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            resultJsonStr = buffer.toString();
        } catch (IOException e) {
            // If the code didn't successfully get the data, there's no point need to parse it
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
        return resultJsonStr;
    }

    private static void getJSONArray(JSONObject resultJson) {

        JSONArray productsArray = resultJson.getJSONArray(PRODUCTS);

        System.out.println("Number of products : " + productsArray.length());
        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject prodJson = productsArray.getJSONObject(i);
            //System.out.println(prodJson.toString());
            JSONArray variants = prodJson.getJSONArray(VARIANTS);
            for (int j = 0; j < variants.length(); j++) {
                JSONObject variantObj = variants.getJSONObject(j);
                String price = variantObj.getString(PRICE);
                double pr = Double.parseDouble(price);
                totalPrice = totalPrice + pr;
            }

        }
    }

    public static void main(String[] args) {

        JSONObject emptyObj = new JSONObject();
        JSONArray emptyProductsArray = new JSONArray();
        emptyObj.put("products", emptyProductsArray);
        try {

            int pageNum = 1;
            StringBuilder urlStringBuilder = new StringBuilder(API_END_POINT_BASE_URL);
            urlStringBuilder.append(PAGE_QUERY);
            String urlString = urlStringBuilder.toString() + pageNum;
            System.out.println(urlString);
            JSONObject returnedValue = new JSONObject(Main.getJSONString(urlString));
            Main.getJSONArray(returnedValue);

            while(hasProducts(emptyObj, returnedValue)){
                pageNum++;
                urlString = urlStringBuilder.toString() + pageNum;
                System.out.println(urlString);
                returnedValue = new JSONObject(Main.getJSONString(urlString));
                Main.getJSONArray(returnedValue);
            };
            System.out.printf("Total cost to buy all watches = %.4f\n", totalPrice);
        } catch (Exception e) {
            System.out.println("Oh-oh error");
            e.printStackTrace();
        }
    }

    private static boolean hasProducts(JSONObject emptyObj, JSONObject returnedValue) {
        boolean returnVal = false;
        if (emptyObj.toString().trim().equals(returnedValue.toString().trim())) {
            // products array is empty
            return false;
        } else {
            // DUDDDDE products exists.
            return true;
        }
    }
}
