package com.mytest;

import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    private Set<String> userIDDetails; //Cities

    private Map<String, String> userCityDetails; //k - ID v- UserName

    private static DataFileProcessor dataFileProcessor;

    public DataFileProcessor(){

    }

    private DataFileProcessor(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
       // processedUserData = new HashMap<>(50);
        userIDDetails = new TreeSet<>();
        userCityDetails = new TreeMap<>();
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

                if(!userLine.equals(format))
                    processData(userLine, format);
            }
        }catch (IOException e){
            //e.printStackTrace();
            System.err.println("An Error occurred processing the file, Exiting Program...");
            System.exit(0);
        }
        return getDataFileProcessor();
    }


    /*
     * processes the data for format one (F1) and format two (F2)
     */
    private void processData(final String userLine, final String format) {
        String[] data = new String[0];

        if(format.equals(FORMAT_ONE))
            data = userLine.split(",");

        if(format.equals(FORMAT_TWO)) {
            data = userLine.split(";");
            data[1] = data[1].trim(); //remove whitespace in front and behind city name so it stands uniquely
            data[2] = new StringBuilder(data[2].trim()).deleteCharAt(ID_LENGTH - 1).toString(); //removing '-' in F2 ID's
        }
        data[0] = data[0].substring(2);//removing 'D ' in front of all data lines

        if(getParameter_1().equals(ID))
            processForID(data);

        if(getParameter_1().equals(CITY))
            processForCity(data);

    }

    /*
    *   Process D -Line for parameter ID
     */
    private void processForID(String[] data){
        if(data[2].equals(getParameter_2()))//For Id's
            userIDDetails.add(data[1]);
    }

    /*
    *   Process D-line for parameter CITY
     */
    private void processForCity(String[] data){
        if(data[1].equals(getParameter_2())){//For Cities
            if(!userCityDetails.containsKey(data[2]))
                userCityDetails.put(data[2], data[0]);
        }
    }


    /*
     * Adds each user to the map for retrieval
     */
//    private void addUserToMap(final String[] data){
//
//        if(processedUserData.containsKey(data[2]))
//            processedUserData.get(data[2]).addUserCity(data[1]);
//        else
//            processedUserData.put(data[2], User.addUserData(data[0].trim(), data[1], data[2]));
//
//    }

    /*
     * Gets the output data based on the given parameters
     */
    void getOutputData(){
//        if(getParameter_1().equals(CITY)){
//
//            processedUserData.values()
//                    .forEach(user ->{
//                        if(user.getUserCities().contains(getParameter_2()))
//                            System.out.format("%s,%s%n", user.getUserName(), user.getUserId());
//                    });
//        }
//
//        if(getParameter_1().equals(ID)){
//
//            processedUserData.get(getParameter_2())
//                    .getUserCities()
//                    .forEach(System.out::println);
//        }

        if(getParameter_1().equals(ID))
            userIDDetails.forEach(System.out::println);

        if(getParameter_1().equals(CITY))
            userCityDetails.forEach((k, v) -> System.out.format("%s,%s%n", v, k));

    }
}
