package com.example.android_project_3_multithread;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class httpGetThread extends Thread {
    public enum CONTENT_TYPE{
        JSON, PNG
    }
    private CONTENT_TYPE content;
    private URL url;
    public JSONObject json;

    public httpGetThread(String _url, CONTENT_TYPE _content) {
        try {
            url = new URL(_url);
        } catch (MalformedURLException e) {
            System.out.println("PLSSSS!!!!!!!!!!!_____________++++++++++++++++++!!!!!!!!!!!!!!!!!!!++++++++++");
            e.printStackTrace();
        }
        content = _content;
    }
    public void run(){
        try{
            switch(content){
                case JSON:
                        getJSON();
                    break;
                case PNG: getPNG(); break;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    private void getJSON() throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        if(con.getResponseCode()>=200 && con.getResponseCode()<300){
            json = (JSONObject) con.getContent();
        }
    }
    private void getPNG(){

    }
}
