package com.application;

import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String... args){
        /*
         * @Author Victor Jo
         */

        long startTime  = System.currentTimeMillis();
        System.out.printf("Start Time: %d MilliSeconds", startTime);
        System.out.println();

        if(args.length == 0) {
            System.err.println("No Arguments Provided, Exiting Program...");
            return;
        }

        if(args.length == 3) {

            String input = args[0];
            String parameter_1 = args[1];
            String parameter_2 = args[2];

            DataFileProcessor.setProcessingParameters(input, parameter_1, parameter_2)
                    .processDataFile()
                    .getOutputData();

            long stopTime  = System.currentTimeMillis() - startTime;

            System.out.println();
            System.out.printf("Stop Time: %d Seconds", TimeUnit.MILLISECONDS.toSeconds(stopTime));
            System.out.println();

        }else {
            System.err.println("Wrong Argument, Exiting Program...");
        }

    }
}
