package com.gboban70.jmailer;

import java.util.ArrayList;

/**
 * Created by goran on 3/2/15.
 * @author Goran Boban
 * @since 2015-03-02
 * @version v0.1.0
 */
public class CmdLineArguments {
    private ArrayList<String> arguments = null;
    /**
     * some exceptions used to signal missing parameters
     */
    public class CmdLineNoSuchArgumentException extends Exception{}

    public class CmdLineMissingArgumentException extends Exception{}

    public CmdLineArguments(String[] args){
        this.arguments = new ArrayList<String>();
        for(int i=0; i < args.length; ++i){
            this.arguments.add(args[i]);
        }
    }

    public boolean hasArgument(String s){
        int index = this.arguments.indexOf(s);
        return (index != -1);
    }

    public String getSignleArgument(String s) throws CmdLineNoSuchArgumentException, CmdLineMissingArgumentException{
        int index = this.arguments.indexOf(s);
        if(index < 0){
            throw new CmdLineNoSuchArgumentException();
        }

        if(index + 1 >= this.arguments.size()){
            throw new CmdLineMissingArgumentException();
        }

        String result = this.arguments.get(index + 1);
        Character first = result.charAt(0);
        if(first == '-'){
            throw new CmdLineMissingArgumentException();
        }

        return result;
    }

    public ArrayList<String> getArguments(String s) throws CmdLineNoSuchArgumentException, CmdLineMissingArgumentException{
        int index = this.arguments.indexOf(s);
        if(index < 0){
            throw new CmdLineNoSuchArgumentException();
        }

        ArrayList<String> result = new ArrayList<String>();
        for(int i = index + 1; i < arguments.size(); ++i){
            String argument = arguments.get(i);

            // check if element is next option
            if(argument.charAt(0) == '-'){
                break;
            }

            result.add(argument);
        }

        if(result.isEmpty()){
            throw new CmdLineMissingArgumentException();
        }
        return result;
    }
}
