package com.company;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// Interface for parser
public class Parser {
    ArrayList<ArrayList<String>> args;
    ArrayList<String> cmd;
    Terminal terminal = null;

    public Parser() throws IOException {
        this.args = new ArrayList<>();
        terminal = new Terminal();
    }

    public boolean parse(ArrayList<String> input) throws IOException {
        if(input.contains(">") || input.contains(">>") || input.contains("cat")){
            if(input.get(0).equals("cat")){
                if (checkArgSize(2,input)){
                    terminal.cat(input);
                }
            }
            else if(input.get(1).equals(">")){
                terminal.outputOperator(true, input.get(0), null, input.get(2));
            }
            else if(input.get(1).equals(">>")){
                terminal.outputOperator(false, input.get(0), null, input.get(2));
            }
        }
        else if(input.get(0).equals("clear")){
            terminal.clear();
        }
        else if(input.get(0).equals("cd")){
            if(checkArgSize(2, input)){
                terminal.cd(input.get(1));
            }
        }
        else if(input.get(0).equals("ls")){
            terminal.ls();
        }
        else if(input.get(0).equals("cp")){
            if(checkArgSize(3, input)){
                terminal.cp(input.get(1), input.get(2));
            }
        }
        else if(input.get(0).equals("mv")){
            if(checkArgSize(3, input)) {
                terminal.mv(input.get(1), input.get(2));
            }
        }
        else if(input.get(0).equals("rm")){
            if(checkArgSize(2,input)){
                terminal.rm(input.get(1));
            }
        }
        else if(input.get(0).equals("mkdir")){
            if(checkArgSize(2,input)) {
                terminal.mkdir(input.get(1));
            }
        }
        else if(input.get(0).equals("rmdir")){
            if(checkArgSize(2,input)) {
                String filename = input.get(1);
                filename = terminal.removeQuotes(filename);
                filename = terminal.getPath(filename);

                File file = new File(filename);
                terminal.rmdir(file);
            }
        }
        else if(input.get(0).equals("more")){
            if (checkArgSize(2,input)) {
                terminal.more(input.get(1));
            }
        }
        else if(input.get(0).equals("pwd")){
            terminal.pwd();
        }
        else if(input.get(0).equals("date")){
            terminal.date();
        }
        else if(input.get(0).equals("help")){
            terminal.help();
        }
        else if(input.get(0).equals("args")){
            if (checkArgSize(2,input)) {
                terminal.getArgs(input.get(1));
            }
        }
        else if(input.get(0).equals("exit")){
            System.exit(0);
        }
        else {
            System.out.println("Command doesn't exist");
        }
        return true;
    }

    public boolean checkArgSize(int size, ArrayList<String> command){
        return command.size() >= size;
    }

    public ArrayList<ArrayList<String>> getArgs() {
        return args;
    }

    public ArrayList<String> getCmd() {
        return cmd;
    }

    public String currentWorkingDirectory(){
        return terminal.getCurrentWorkingDirectory();
    }
}
