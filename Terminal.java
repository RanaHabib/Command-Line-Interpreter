package com.company;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

public class Terminal {

    String currentWorkingDirectory;

    public Terminal() {
        this.currentWorkingDirectory = System.getProperty("user.dir");
    }

    public void clear(){
        for (int i = 0; i < 300*80; ++i) System.out.println();
    }

    public void getArgs(String funcName) {
        if(funcName.equalsIgnoreCase("cd"))
            System.out.println("Directory name");
        else if (funcName.equalsIgnoreCase("cp")) {
            System.out.println("Arg1: Source path");
            System.out.println("Arg2: Destination path");}
        else if (funcName.equalsIgnoreCase("mv")) {
            System.out.println("Arg1: Source path");
            System.out.println("Arg2: Destination path");}
        else if (funcName.equalsIgnoreCase("rm")) {
            System.out.println(" Source path");
        }
        else if (funcName.equalsIgnoreCase("rmdir")) {
            System.out.println("Directory path ");
        }
        else{
            System.out.println("Command doesn't have arguments");
        }
    }

    public void cd(String directoryName){
        try {
            directoryName = removeQuotes(directoryName);

            File file = new File(directoryName);
            if(directoryName.equals("..")){
                String sep = "\\\\";
                int lengthOfPath = this.currentWorkingDirectory.split(sep).length;

                String previousPath = this.currentWorkingDirectory.split(sep)[0];

                for (int i = 1; i < lengthOfPath-1; i++) {
                    previousPath += "\\"+ this.currentWorkingDirectory.split(sep)[i];
                }

                this.currentWorkingDirectory = previousPath;
            }
            else if((file.isAbsolute()) || (file.isDirectory() && directoryName.endsWith(":"))){
                this.currentWorkingDirectory = directoryName;
            }
            else{
                File f = new File(this.currentWorkingDirectory+"\\"+directoryName);
                if(f.exists()){
                    this.currentWorkingDirectory += "\\" + directoryName;
                }
                else {
                    System.out.println("Cannot find path");
                }
            }
        } catch (InvalidPathException | NullPointerException ex) {
            System.out.println("Invalid path");
        }
    }

    public String ls() throws IOException {
        String lsContents = "";
        File file = null;
        String[] filePaths;


        file = new File(currentWorkingDirectory);
        filePaths = file.list();

        for(String path:filePaths) {
            System.out.println(path);
            lsContents += path + "\n";
        }

        return lsContents;
    }

    public void cp(String sourcePath, String destinationPath) throws IOException {
        String source = removeQuotes(sourcePath);
        String dest = removeQuotes(destinationPath);

        source = getPath(source);
        dest = getPath(dest);

        int lengthOfPath = source.split("\\\\").length;
        String lastFileName = source.split("\\\\")[lengthOfPath-1];
        String outputPath = dest + "\\" + lastFileName;

        File isSrc = new File(source);
        File isDest = new File(dest);

        if(isSrc.isDirectory()){
            Path path = Paths.get(outputPath);
            Files.createDirectories(path);
        }
        else if(isSrc.exists() && isDest.exists()){
            File file = new File(source);
            FileInputStream fin =  new FileInputStream(file);

            byte fileContent[] = new byte[(int)file.length()];

            fin.read(fileContent);

            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(fileContent);

            fin.close();
            fos.close();
        }
        else{
            System.out.println("File not found");
        }
    }

    public void mv(String sourcePath, String destinationPath) throws IOException {
        sourcePath = removeQuotes(sourcePath);
        destinationPath = removeQuotes(destinationPath);

        sourcePath = getPath(sourcePath);
        destinationPath = getPath(destinationPath);

        File dest = new File(destinationPath);
        if(dest.isDirectory()){
            cp(sourcePath, destinationPath);
            rm(sourcePath);
        }
        else{
            File src = new File(sourcePath);
            File newName = new File(destinationPath);
            src.renameTo(newName);
        }
    }

    public void rm(String sourcePath){
        sourcePath = removeQuotes(sourcePath);
        sourcePath = getPath(sourcePath);

        File file = new File(sourcePath);

        if (file.isDirectory()){
            System.out.println("rm: cannot remove '" + sourcePath + "': Is a directory");
        }
        else {
            file.delete();
        }
    }

    public void mkdir(String file){
        file = removeQuotes(file);
        file = getPath(file);
        try {
            Path path = Paths.get(file);
            Files.createDirectories(path);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void rmdir(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                rmdir(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public File cat(ArrayList<String> input) throws IOException {
        File file = null;
        if(input.get(1).equals(">>") || input.get(1).equals(">")){
            String path = removeQuotes(input.get(2));

            String fullPath = getPath(path);

            file = new File(fullPath);

            Scanner scan = new Scanner(System.in);
            String inputs;

            FileWriter myWriter = null;
            if(input.get(1).equals(">>")){
                myWriter = new FileWriter(fullPath, true);
            }
            else{
                myWriter = new FileWriter(fullPath);
            }

            myWriter.write("\n");
            while(scan.hasNext()) {

                inputs = scan.nextLine();
                myWriter.write(inputs + "\n");
            }
            myWriter.close();

        }
        else{
            try {
                String path = input.get(1);

                if(input.get(1).startsWith("\"") && input.get(1).endsWith("\"")){
                    path = input.get(1).substring(1,input.get(1).length()-1);
                }

                path = getPath(path);

                file = new File(path);
                Scanner read = new Scanner(file);

                while (read.hasNextLine()) {
                    String data = read.nextLine();
                    System.out.println(data);
                }
                read.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
                e.printStackTrace();
            }
        }
        return file;
    }

    public String pwd() {
        System.out.println(currentWorkingDirectory);
        return currentWorkingDirectory;
    }

    public void more(String path) throws IOException {
        path = removeQuotes(path);
        path = getPath(path);

        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        Scanner scan = new Scanner(System.in);

        for (int i = 0; i < 18; i++) {
            System.out.println(line);
            line = reader.readLine();
        }

        String input = scan.nextLine();
        while (line != null && input.isEmpty()) {
            System.out.println(line);
            line = reader.readLine();
            input = scan.nextLine();
        }
    }

    public String date() {
        Date date=java.util.Calendar.getInstance().getTime();
        System.out.println(date);

        return date.toString();
    }

    public File help() {
        File file = null;
        try {
            file = new File("help.txt");
            Scanner read = new Scanner(file);

            while (read.hasNextLine()) {
                String data = read.nextLine();
                System.out.println(data);
            }

            read.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return file;
    }

    public String getPath(String filename){
        File file = new File(filename);

        if(file.isAbsolute() || (file.isDirectory() && filename.endsWith(":"))){
            return filename;
        }
        else {
            return currentWorkingDirectory + "\\" + filename;
        }
    }

    public String removeQuotes(String path){
        if(path.startsWith("\"") && path.endsWith("\"")){
            path = path.substring(1,path.length()-1);
        }
        while(path.endsWith("\\") || path.endsWith("/")){
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public void outputOperator(boolean isSingle,String command, String args, String textFile) throws IOException {

        textFile = removeQuotes(textFile);
        textFile = getPath(textFile);

        File file = null;
        String output = null;

        ArrayList<String> catCommand = new ArrayList<>();

        if(command.equals("help")){
            file = help();
        }
        else if(command.equals("ls")){
            output = ls();
        }
        else if(command.equals("date")){
            output = date();
        }
        else if(command.equals("pwd")){
            output = pwd();
        }
//        else if(command.equals("cat")){
//            args = removeQuotes(args);
//            args = getPath(args);
//            catCommand.add(command);
//            catCommand.add(args);
//            file = cat(catCommand);
//        }

        if(file!=null){
            readAndWriteFile(file, textFile, isSingle);
        }
        else if(output!=null){
            writeStringToFile(output, textFile, isSingle);
        }

    }

    public void readAndWriteFile(File readFile, String writeToPath, boolean isSingle) throws IOException {
        Scanner read = new Scanner(readFile);
        FileWriter myWriter = null;

        if(isSingle){
            myWriter = new FileWriter(writeToPath);

        }else{
            myWriter = new FileWriter(writeToPath, true);

        }

        while (read.hasNextLine()) {
            String data = read.nextLine();
            myWriter.write(data + "\n");
        }
        read.close();
        myWriter.close();
    }

    public void writeStringToFile(String writeText, String writeToPath, boolean isSingle) throws IOException {
        FileWriter myWriter;

        if(isSingle){
            myWriter = new FileWriter(writeToPath);

        }else{
            myWriter = new FileWriter(writeToPath, true);

        }

        myWriter.write(writeText);
        myWriter.close();
    }

    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }
}
