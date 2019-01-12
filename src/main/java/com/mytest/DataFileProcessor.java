package com.mytest;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DataFileProcessor {

    private static final String TAG = "com.mytest.DataFileProcessor";
    private String dataFile;
    private String parameter_1; //This can be 'ID' or 'CITY'
    private String parameter_2; //This can be user's ID or CITY name

    private static final String FORMAT_ONE = "F1";
    private static final String FORMAT_TWO = "F2";

    private static final String ID = "ID";
    private static final String CITY = "CITY";

    private static final int ID_LENGTH = 9;

    private Map<String, User> processedUserData;

    private static DataFileProcessor dataFileProcessor;

    public DataFileProcessor(){

    }

    private DataFileProcessor(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        processedUserData = new TreeMap<>();

        setParameter_1(parameter_1.toUpperCase());

        if(parameter_1.equals(ID) && parameter_2.length() != ID_LENGTH)
            setParameter_2(parameter_2.replace("-", ""));
        else
            setParameter_2(parameter_2);

        setDataFile(dataFile);
    }

    /*
     *Sets the arguments for processing the file and producing the required output
     */
    static DataFileProcessor setProcessingParameters(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        dataFileProcessor = new DataFileProcessor(dataFile, parameter_1, parameter_2);
        return dataFileProcessor;

    }

    /*
     * Gets the dataFileProcessor instance to enable it call a public member method,
     * giving it a functional programming style feel.
     */
    private static DataFileProcessor getDataFileProcessor() {
        return dataFileProcessor;
    }

    /*
     *gets the value for datafile which is to be processed
     * although its private, this is left here incase it might be useful
     */
    private String getDataFile() {
        return dataFile;
    }

    /*
     *Sets the value for input data file to be processed
     */
    private void setDataFile(final String dataFile) {
        this.dataFile = dataFile;
    }

    /*
     *gets the value for parameter 1 which is the search key
     * although its private, this is left here incase it might be useful
     */
    private String getParameter_1() {
        return parameter_1;
    }

    /*
     *Sets the value for parameter 1 which is the operation type
     */
    private void setParameter_1(final String parameter_1) {
        this.parameter_1 = parameter_1;
    }

    /*
     *gets the value for parameter 2 which is the search key
     * although its private, this is left here incase it might be useful
     */
    private String getParameter_2() {
        return parameter_2;
    }

    /*
     *Sets the value for parameter 2 which is the search key
     */
    private void setParameter_2(final String parameter_2) {
        this.parameter_2 = parameter_2;
    }

    /*
     *processes the data file line by line based on the last seen format line
     */
    DataFileProcessor processDataFile(){

        try(BufferedReader input = Files.newBufferedReader(Paths.get(getDataFile()), StandardCharsets.UTF_8)){
            String userLine;
            String format = "";
            while((userLine = input.readLine()) != null) {

                format = userLine.equals(FORMAT_ONE) ? FORMAT_ONE : userLine.equals(FORMAT_TWO) ? FORMAT_TWO : format;

                if(!userLine.equals(format)) {
                    //System.out.println(userLine);
                   processData(userLine, format);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("An Error occurred processing the file, Exiting Program...");
            System.exit(0);
        }

       // processedUserData.forEach((s, user) -> System.out.format("%s %s%n", s, user));
        return getDataFileProcessor();
    }

    DataFileProcessor processDataFileFast(){
        File file = new File(getDataFile());
        try(Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)){
            final String[] format = {""};
            lines.forEach(userLine ->{
                format[0] = userLine.equals(FORMAT_ONE) ? FORMAT_ONE : userLine.equals(FORMAT_TWO) ? FORMAT_TWO : format[0];

                if(!userLine.equals(format[0])) {
                    //System.out.println(userLine);
                    processData(userLine, format[0]);
                }
            });

//            byte [] fileBytes = Files.readAllBytes(file.toPath());
//            char singleChar;
//            StringBuilder builder = new StringBuilder();
//            String format = "";
//            for(byte b : fileBytes) {
//                singleChar = (char) b;
//                if(singleChar == '\n' || singleChar == '\r'){
//                  //  out.printf("Content: %s%n", builder.toString());
//                    String userLine = builder.toString();
//                    if(userLine.equals(FORMAT_ONE)) {
//                        format = FORMAT_ONE;
//                      //  System.out.println(format);
//                    }
//
//                    if(userLine.equals(FORMAT_TWO)) {
//                        format = FORMAT_TWO;
//                       // System.out.println(format);
//                    }
//
//                    if(!userLine.equals(format)) {
//                       // System.out.println(userLine);
//                        processData(userLine, format);
//                    }
//
//                    builder = new StringBuilder();
//                }else{
//                    builder.append(singleChar);
//                }
//                //System.out.print(singleChar);
//            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return getDataFileProcessor();
    }

    /*
     * processes the data for format one (F1) and format two (F2)
     */
    private void processData(final String userLine, final String format) {
        String[] data = new String[0];

        if(format.equals(FORMAT_ONE)) {
            data = userLine.split(",");
        }

        if(format.equals(FORMAT_TWO)) {
            data = userLine.split(";");
            data[2] = data[2].replace("-", "");
        }

        data[0] = data[0].replaceFirst("D ", "");


        addUserToMap(data);
    }

    /*
     * Adds each user to the map for retrieval
     */
    private void addUserToMap(final String[] data){

        if(processedUserData.containsKey(data[2]))
            processedUserData.get(data[2]).addUserCity(data[1]);
        else
            processedUserData.put(data[2], User.addUserData(data[0], data[1], data[2]));

    }

    /*
     * Gets the output data based on the given parameters
     */
    void getOutputData(){
        if(getParameter_1().equals(CITY)){

            processedUserData.values()
                    .stream()
                    .filter(user -> user.getUserCities()
                            .contains(getParameter_2()))
                    .forEach(user -> System.out.format("%s,%s%n",
                            user.getUserName(), user.getUserId()));
        }

        if(getParameter_1().equals(ID)){

            processedUserData.get(getParameter_2())
                    .getUserCities()
                    .forEach(System.out::println);
        }
    }
}
