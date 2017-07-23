package com.example.notificationtesting;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by esajee on 12/13/2016.
 */

public class FetchData {

    public static JSONObject requestWebService(String serviceUrl, String params,String link){//,Boolean withcookies,String header) {

        serviceUrl = "http://monarch.esajee.com:8082/traccar/rest/" + serviceUrl + "?1=1&" + params;

        Log.i("service", serviceUrl);
        disableConnectionReuseIfNecessary();
Context cxt = null;
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);

            urlConnection = (HttpURLConnection)
                    urlToRequest.openConnection();

            Log.i("Response",urlConnection.getResponseCode()+"");
            if(urlConnection.getResponseCode()==500)
            {
                FirebaseAuth.getInstance().signOut();
                JSONObject test = new JSONObject();
                return test;
            }
            else
            {
                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                JSONObject test = new JSONObject(getResponseText(in));

                //urlConnection.getResponseCode();
                Log.i("Response",urlConnection.getResponseCode()+"");

                Log.i("test", test.toString());
                return test;
            }


        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
            // response body is no valid JSON string
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public JSONObject signIn(String params) {

        JSONObject serviceResult = new JSONObject();
        try {
            /*serviceResult = requestWebService(
                    "signIn", "data=" + URLEncoder.encode(params, "UTF-8"),true,"");*/

            Log.i("resp", serviceResult.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceResult;
    }

    public JSONObject signUp(String params) {

        JSONObject serviceResult = new JSONObject();
        try {
           /* serviceResult = requestWebService(
                "signUp", "data=" + URLEncoder.encode(params, "UTF-8"),true,"");*/

        Log.i("resp", serviceResult.toString());


    } catch (Exception e) {
        e.printStackTrace();
    }

        return serviceResult;
    }

    public boolean updateData(String params) {

        try {

         /*   JSONObject serviceResult = requestWebService(
                    "updateComplaint", params,true,"");*/

           /* Log.i("resp", serviceResult.toString());

            if (serviceResult.optString("error2").equals("null")) {
                return true;
            } else {
                return false;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addData(String params) {

        try {

            /*JSONObject serviceResult = requestWebService(
                    "addComplaint", params,true,"");*/

            /*Log.i("resp", serviceResult.toString());

            if (serviceResult.optString("error2").equals("null")) {
                return true;
            } else {
                return false;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public JSONObject findAllItems(String header) {

//        List<DummyContent.DummyItem> foundItems = new ArrayList<DummyContent.DummyItem>();

        try {


            /*JSONObject serviceResult = requestWebService(
                    "getAllDevices", "",false,header);
            //Log.i("dfwfd",serviceResult.toString());
            return serviceResult;*/


        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
            return null;
        }

        return null;//DummyContent.ITEMS;
    }


}
