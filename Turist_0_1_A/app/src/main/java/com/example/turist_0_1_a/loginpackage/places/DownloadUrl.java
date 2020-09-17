package com.example.turist_0_1_a.loginpackage.places;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {

    public String readUrl(String u) throws IOException{

        String data="";//string gol ce va contine raspunsul api in format json
        InputStream input = null;
        HttpURLConnection urlConnection = null;//Conexiune Http

        try {
            URL url = new URL(u);//url setat cu url creat in ListActivity
            urlConnection = (HttpURLConnection) url.openConnection();//se deschide conexiunea cu url setat
            urlConnection.connect();//se conecteaza la conexiune

            input = urlConnection.getInputStream();//se seteaza input stream
            BufferedReader b = new BufferedReader(new InputStreamReader(input));//se seteaza un buffer reader
            StringBuffer s = new StringBuffer();

            String read = "";
            while((read=b.readLine())!=null){//se citeste linie cu linie pana cand s-a citit tot  mesajul json
                s.append(read);//se adauga in String Buffer
            }

            data = s.toString();//casteaza din string buffer in string mesajul json
            b.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            input.close();//inchide inputStrea
            urlConnection.disconnect();//deconecteaza de la conexiune
        }

        return data;
    }

}
