package com.mytest;

public class Application {

    public static void main(String... args){

        if(args.length == 0) {
            System.err.println("No Arguments Provided, Exiting Program...");
            return;
        }

        if(args.length == 3) {

            String input = args[0];
            String parameter_1 = args[1];
            String parameter_2 = args[2];

            System.out.println();

            DataFileProcessor.setProcessingParameters(input, parameter_1, parameter_2)
                    .processDataFile()
                    .getOutputData();

        }else {
            System.err.println("Wrong Argument, Exiting Program...");
        }

    }
}
