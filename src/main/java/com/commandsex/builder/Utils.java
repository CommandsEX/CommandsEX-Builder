package com.commandsex.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Utils {

    public static String readUrl(String address){
        BufferedReader reader = null;
        try {
            URL url = new URL(address);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1){
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static URL getLatestJenkinsDownload(){
        String latestJobAddress = getLatestJenkinsJobAddress();
        String json = readUrl(latestJobAddress + "api/json");
        JsonElement jsonElement = new JsonParser().parse(json);
        String filename = jsonElement.getAsJsonObject().getAsJsonArray("artifacts").get(0).getAsJsonObject().getAsJsonPrimitive("relativePath").getAsString();
        try {
            URL url = new URL(latestJobAddress + "artifact/" + filename);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getLatestJenkinsJobAddress(){
        String json = readUrl("http://ci.keir-nellyer.co.uk/job/CommandsEX/api/json");
        JsonElement jsonElement = new JsonParser().parse(json);
        String lastSuccessfulUrl = jsonElement.getAsJsonObject().getAsJsonObject("lastSuccessfulBuild").getAsJsonPrimitive("url").getAsString();
        return lastSuccessfulUrl;
    }

    public static void closeWindow(JFrame jFrame){
        WindowEvent wev = new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }

    /**
     * <p>Retrieve a list of filepaths from a given directory within a jar
     * file. If filtered results are needed, you can supply a |filter|
     * regular expression which will match each entry.
     *
     * @author http://www.m0interactive.com/archives/2009/04/29/how_to_get_the_list_of_files_in_a_directory_inside_a_jar_file.html
     * @param jarLocation The jar file
     * @param filter to filter the results within a regular expression.
     * @return a list of files within the jar |file|
     */
    public static List<String> getJarFileListing(File jarLocation, String filter) {
        List<String> files = new ArrayList<String>();
        if (jarLocation == null) {
            return files; // Empty.
        }

        // Lets stream the jar file
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream(new FileInputStream(jarLocation));
            JarEntry jarEntry;

            // Iterate the jar entries within that jar. Then make sure it follows the
            // filter given from the user.
            do {
                jarEntry = jarInputStream.getNextJarEntry();
                if (jarEntry != null) {
                    String fileName = jarEntry.getName();

                    // The filter could be null or has a matching regular expression.
                    if (filter == null || fileName.matches(filter)) {
                        files.add(fileName);
                    }
                }
            }
            while (jarEntry != null);
            jarInputStream.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to get Jar input stream from '" + jarLocation + "'", ioe);
        }
        return files;
    }

    /**
     * Reads the first line of an InputStream and returns it
     * @param inputStream The InputStream to read
     * @return The first line of an InputStream
     */
    public static String readFirstLine(InputStream inputStream){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine().trim();
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
