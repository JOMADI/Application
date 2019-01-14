package com.application;

import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DataFileProcessor {

    /*
    * @author Victor Jo
     */

    private static final String TAG = "com.application.DataFileProcessor";
    private String dataFile;
    private String parameter_1; //This can be 'ID' or 'CITY'
    private String parameter_2; //This can be user's ID or CITY name

    private static final String FORMAT_ONE = "F1";
    private static final String FORMAT_TWO = "F2";

    private static final String ID = "ID";
    private static final String CITY = "CITY";

    private static final int ID_LENGTH = 9;

    private Set<String> userIDDetails; //contains the cities a specified ID has been to

    private Map<String, String> userCityDetails; //a map of k-ID v-Name of individuals who have visited a specified city

    private static DataFileProcessor dataFileProcessor;

    public DataFileProcessor(){

    }

    private DataFileProcessor(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        userIDDetails = new TreeSet<>();
        userCityDetails = new HashMap<>();
        setParameter_1(parameter_1.toUpperCase());

        if(parameter_1.equals(ID) && parameter_2.length() != ID_LENGTH)
            setParameter_2(parameter_2.replace("-", ""));
        else
            setParameter_2(parameter_2);

        setDataFile(dataFile);
    }

    /*
     * @param dataFile the dataFile for processing
     * @param parameter_1 the search type (ID || CITY)
     * @param parameter_2 the search key (UserId || CityName)
     */
    static DataFileProcessor setProcessingParameters(@NotNull final String dataFile, @NotNull final String parameter_1, @NotNull final String parameter_2){
        dataFileProcessor = new DataFileProcessor(dataFile, parameter_1, parameter_2);
        return dataFileProcessor;

    }

    /*
     * @return dataFileProcessor the initialized instance of this class
     */
    private static DataFileProcessor getDataFileProcessor() {
        return dataFileProcessor;
    }

    /*
     *@return dataFile the dataFile name
     */
    private String getDataFile() {
        return dataFile;
    }

    /*
     *@param dataFile the datFile to set
     */
    private void setDataFile(final String dataFile) {
        this.dataFile = dataFile;
    }

    /*
     *@return parameter_1 the search type
     */
    private String getParameter_1() {
        return parameter_1;
    }

    /*
     *@param parameter_1 Sets the value for parameter 1 which is the operation type
     */
    private void setParameter_1(final String parameter_1) {
        this.parameter_1 = parameter_1;
    }

    /*
     * @return parameter_2 the value of the search key
     */
    private String getParameter_2() {
        return parameter_2;
    }

    /*
     * @param parameter_2 the search Key to set
     */
    private void setParameter_2(final String parameter_2) {
        this.parameter_2 = parameter_2;
    }


    /*
    *   @return userIDDetails the size of the number of cities ID has visited.
     */
    int citiesIdVisited_Size(){ //i prayed to God to reveal to me in dream a better name for this, but i had a dream about peter from family guy instead.
        return userIDDetails.size();
    }
    /*
    * @return userCityDetails the size of the number of individuals who have been to specified city
     */
    int idVisitedCities_Size(){// Yeah, umm this too, sorry about this....
        return userCityDetails.size();
    }

    /*
     *@return dataFileProcessor the instance of the current class handling processing
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
        }catch (FileNotFoundException e){
            System.err.println("File not found, Exiting program...");
            System.exit(0);
        }catch (IOException e){
            System.err.println("An error occurred processing the file, Exiting program...");
            System.exit(0);
        }
        return getDataFileProcessor();
    }

    /*
     * @param userLine each line of data in the dataFile
     * @param format the previously seen format type
     */
    private void processData(final String userLine, final String format) {
        String[] data = new String[0];

        if(format.equals(FORMAT_ONE))
            data = userLine.split(",");

        if(format.equals(FORMAT_TWO)) {
            data = userLine.split(" ; ");
            data[2] = new StringBuilder(data[2]).deleteCharAt(ID_LENGTH - 1).toString(); //removing '-' in F2 ID's
        }
        data[0] = data[0].substring(2);//removing 'D ' in front of all data lines

        if(getParameter_1().equals(ID))
            processForID(data);

        if(getParameter_1().equals(CITY))
            processForCity(data);

    }

    /*
    *  @param data an array containing an individual data(name,city,id)
    *  processing for search type ID
     */
    private void processForID(final String[] data){
        if(data[2].equals(getParameter_2()))//For Id's
            userIDDetails.add(data[1]);
    }

    /*
    *  @param data an array containing an individual data(name,city,id)
    *  processing for search type CITY
     */
    private void processForCity(final String[] data){
        if(data[1].equals(getParameter_2())){//For Cities
            if(!userCityDetails.containsKey(data[2]))
                userCityDetails.put(data[2], data[0]);
        }
    }

    /*
     * Gets the output data based on the given parameters
     */
    void getOutputData(){

        if(getParameter_1().equals(ID)){
            for(String city: userIDDetails)
                System.out.println(city);
        }

        if(getParameter_1().equals(CITY)){
            for(Map.Entry<String, String> user : userCityDetails.entrySet())
                System.out.printf("%s,%s%n", user.getValue(), user.getKey());

        }
    }
}
