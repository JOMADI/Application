package com.mytest;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFileProcessor {

    private static final String TAG = "com.mytest.DataFileProcessor";
    private String dataFile;
    private String parameter_1; //This can be 'ID' or 'CITY'
    private String parameter_2; //This can be user's ID or CITY name

    private static final String FORMAT_ONE = "F1";
    private static final String FORMAT_TWO = "F2";

    private final Pattern FORMAT_ONE_REGEX = Pattern.compile("[D\\w]([^ ][^,]*)");
    private final Pattern FORMAT_TWO_REGEX = Pattern.compile("[D\\w]([^ ][^;]*)");

    private static final String ID = "ID";
    private static final String CITY = "CITY";

    private static final int ID_LENGTH = 9;

    private Map<String, User> processedUserData;

    private static DataFileProcessor dataFileProcessor;

    public DataFileProcessor(){

    }

    private DataFileProcessor(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        processedUserData = new HashMap<>(40);

        setParameter_1(parameter_1);

        if(parameter_1.equals(ID) && parameter_2.length() != ID_LENGTH)
            setParameter_2(String.join("", parameter_2.split("-")));
        else
            setParameter_2(parameter_2);

        setDataFile(dataFile);
    }

    static DataFileProcessor setProcessingParameters(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        dataFileProcessor = new DataFileProcessor(dataFile, parameter_1, parameter_2);
        return dataFileProcessor;

    }

    private static DataFileProcessor getDataFileProcessor() {
        return dataFileProcessor;
    }

    String getDataFile() {
        return dataFile;
    }

    private void setDataFile(final String dataFile) {
        this.dataFile = dataFile;
    }

    private String getParameter_1() {
        return parameter_1;
    }

    private void setParameter_1(final String parameter_1) {
        this.parameter_1 = parameter_1;
    }

    private String getParameter_2() {
        return parameter_2;
    }

    private void setParameter_2(final String parameter_2) {
        this.parameter_2 = parameter_2;
    }

    DataFileProcessor processDataFile(){

        try(BufferedReader input = new BufferedReader(new FileReader(getDataFile()))){
            String userLine;
            String format = "";
            while ((userLine = input.readLine()) != null){

                if(userLine.equals(FORMAT_ONE))
                    format = FORMAT_ONE;

                if(userLine.equals(FORMAT_TWO))
                    format = FORMAT_TWO;

                if((format.equals(FORMAT_ONE)) && !userLine.equals(FORMAT_ONE))
                    processFormatOneData(userLine);

                if((format.equals(FORMAT_TWO)) && !userLine.equals(FORMAT_TWO))
                    processFormatTwoData(userLine);
            }
        }catch (IOException | NullPointerException e){
            System.err.println("File Not Found, Exiting Program...");
        }

        return getDataFileProcessor();
    }

    public   String[] matchDataPattern(final Pattern regex, final String userLine){
        Matcher matcher = regex.matcher(userLine);
        String[] data = new String[3]; //data[0] - user's name
        int counter = 0;               //data[1] - city name
        while (matcher.find()){        //data[2] - user's id
            data[counter++] = matcher.group(0).trim();
        }
        return data;
    }

    private void processFormatOneData(final String userLine) {
        String[] data = matchDataPattern(FORMAT_ONE_REGEX, userLine);
        addUserToMap(data);
    }

    private void processFormatTwoData(final String userLine) {
        String[] data = matchDataPattern(FORMAT_TWO_REGEX, userLine);
        data[2] = String.join("", data[2].split("-"));
        addUserToMap(data);
    }

    private void addUserToMap(final String[] data){
        User user = User.addUserData(data[0], data[1], data[2]);

        if(processedUserData.containsKey(user.getUserId())){
            User exitingUser = processedUserData.get(user.getUserId());
            exitingUser.addUserCity(data[1]);
            processedUserData.put(exitingUser.getUserId(), exitingUser);

        }else{
            processedUserData.put(user.getUserId(), user);
        }
    }

    void getOutputData(){
        if(getParameter_1().equals(CITY)){
            processedUserData.values()
                    .stream()
                    .filter(user -> user
                    .getUserCities()
                    .contains(getParameter_2()))
                    .forEach(user -> System.out.format("%s,%s%n",
                            user.getUserName(), user.getUserId()));
        }

        if(getParameter_1().equals(ID)){
            processedUserData.get(getParameter_2())
                    .getUserCities()
                    .spliterator()
                    .forEachRemaining(System.out::println);
        }
    }
}
