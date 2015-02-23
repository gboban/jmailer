package com.gboban70.jmailer;

import javax.mail.MessagingException;
import java.util.ArrayList;

/**
 * Created by goran on 2/22/15.
 *
 * version 0.0.4
 */
/*
*	Copyright (C) 2015  Goran Boban
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/

public class Main {

    public static ArrayList<String> getStringArrayList(String[] args) {

        ArrayList<String> result = new ArrayList<String>();

        for(int i = 0; i < args.length; ++i){
            result.add(args[i]);
        }

        return result;
    }

    private static ArrayList<String> getOptionParams(String option, ArrayList<String> arguments){
        int index = arguments.indexOf(option);
        if(index == -1){
            return null;
        }

        ArrayList<String> result = new ArrayList<String>();
        for(int i = index + 1; i < arguments.toArray().length; ++i){
            String argument = arguments.get(i);

            // check if element is next option
            if(argument.charAt(0) == '-'){
                break;
            }

            result.add(argument);
        }
        return result;
    }

    public static void main(String[] args) throws MessagingException {
        JMailer mailer = new JMailer();
       /**
        * get arguments as ArrayList of strings for convenience (contains(), indexOf()...)
        */
        ArrayList<String> arguments = getStringArrayList(args);

        /**
         * process switches
         */
                /* VERBOSE (-v) */
        mailer.setVerbose(arguments.indexOf("-v") != -1);

        /* TIMEOUT (-t) */
        int index = arguments.indexOf("-t");
        if(index != -1){
            throw new MessagingException("JMailer: switch is not used: -t");
        }

        /* HOST (-h) */
        index = arguments.indexOf("-h");
        String host = null;
        if(index != -1){
            host = arguments.get(index+1);
            if(host.charAt(0)=='-') host = null;
        }
        if(host != null){
            mailer.setHost(host);
        }

        /* PORT (-p) */
        index = arguments.indexOf("-p");
        String strPort = null;
        if(index != -1){
            strPort = arguments.get(index+1);
            if(strPort.charAt(0)=='-') strPort = null;
        }
        if(strPort != null){
            int port = Integer.parseInt(strPort);
            mailer.setPort(port);
        }

        /* FROM (-from) */
        String from = null;
        index = arguments.indexOf("-from");
        if(index != -1){
            from = arguments.get(index+1);
            if(from.charAt(0)=='-') from = null;
        }
        mailer.setFrom(from);

        /* SUBJECT (-s) */
        String subject = null;
        index = arguments.indexOf("-s");
        if(index != -1){
            subject = arguments.get(index+1);
            if(from.charAt(0)=='-') subject = null;

            if(subject == null){
                throw new MessagingException("JMailer: switch requires argument: -s");
            }
        }else{
            throw new MessagingException("JMailer: missing switch: -s");
        }

        mailer.setSubject(subject);

        /* TO (-to) */
        index = arguments.indexOf("-to");
        if(index == -1){
            throw new MessagingException("JMailer: missing switch: -to");
        }else{
            ArrayList<String> toParams = getOptionParams("-to", arguments);
            boolean hasTo = !toParams.isEmpty();
            mailer.addRecipientsTo(toParams);

            if(!hasTo){
                throw new MessagingException("JMailer: switch requires argument: -to");
            }
        }

        /* CC (-cc) */
        index = arguments.indexOf("-cc");
        if(index != -1){
            ArrayList<String> ccParams = getOptionParams("-cc", arguments);
            boolean hasCc = !ccParams.isEmpty();
            mailer.addRecipientsCc(ccParams);

            if(!hasCc){
                throw new MessagingException("JMailer: switch requires argument: -cc");
            }
        }

        /* BCC (-bcc) */
        index = arguments.indexOf("-bcc");
        if(index != -1){
            ArrayList<String> bccParams = getOptionParams("-bcc", arguments);
            boolean hasBcc = !bccParams.isEmpty();
            mailer.addRecipientsBcc(bccParams);

            if(!hasBcc){
                throw new MessagingException("JMailer: switch requires argument: -bcc");
            }
        }

        String body = "";
        /* READ FROM STDIN (-i) */
        index = arguments.indexOf("-i");
        if(index != -1){
            body = "Reading from stdin";
        }

        /* FILE (-file) */
        index = arguments.indexOf("-file");
        if(index != -1){
            body = "Reading from file";
        }

        mailer.setBody(body);

        mailer.send();
    }
}
