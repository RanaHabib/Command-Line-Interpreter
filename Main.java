package com.company;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static ArrayList<String> splitCommands(String command, String splitByChar){
        ArrayList<String> splittedCommand = new ArrayList<>();

        for (int i = 0; i < command.split(splitByChar).length; i++) {
            splittedCommand.add( command.split(splitByChar)[i]);
        }
        return splittedCommand;
    }

    public static void main(String[] args) throws IOException {

        Parser parser = new Parser();

        while(true){
            System.out.print("PS " + parser.currentWorkingDirectory() + "> ");
            Scanner input = new Scanner(System.in);
            String cmd = input.nextLine();
            ArrayList<String> pipeCommand = new ArrayList<>();
            ArrayList<String> command = new ArrayList<>();

            pipeCommand = splitCommands(cmd, " \\| ");

            for (int i = 0; i < pipeCommand.size(); i++) {
                command = splitCommands(pipeCommand.get(i), " ");
                parser.parse(command);
            }
        }
    }

}
