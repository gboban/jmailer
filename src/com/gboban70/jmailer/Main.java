package com.gboban70.jmailer;

import javax.mail.MessagingException;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Goran Boban
 * @since 2015-02-22
 * @version v0.1.0
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

    private static String readBody(InputStream in) throws IOException {

        BufferedInputStream reader = new BufferedInputStream(in);
        byte[] buffer = new byte[reader.available()];
        reader.read(buffer, 0, reader.available());
        return new String(buffer);
    }

    public static void main(String[] args) throws MessagingException, IOException, Exception {
        JMailer mailer = new JMailer();

        CmdLineArguments clargs = new CmdLineArguments(args);

        /**
         * process arguments
         */
         /* VERBOSE (-v) */
        mailer.setVerbose(clargs.hasArgument("-v"));

        /* TIMEOUT (-t) */
        if(clargs.hasArgument("-t")){
            throw new Exception("JMailer: switch is not used: -t");
        }

        /* HOST (-h) */
        try{
            String host = clargs.getSignleArgument("-h");
            mailer.setHost(host);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            System.out.println("No host specified (-h) - using default SMTP host: " + mailer.getHost());
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            System.out.println("Missing host name after '-h' option - using default SMTP host" + mailer.getHost());
        }

        /* PORT (-p) */
        try{
            String port = clargs.getSignleArgument("-p");
            mailer.setPort(Integer.parseInt(port));
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            System.out.println("No port specified (-p) - using default SMTP port: " + mailer.getPort());
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            System.out.println("Missing port number after '-p' option - using default SMTP port" + mailer.getPort());
        }

        /* FROM (-from) */
        try{
            String from = clargs.getSignleArgument("-from");
            mailer.setFrom(from);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            System.out.println("No from option sepcified (-from) - using default from: " + mailer.getFrom());
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            System.out.println("Missing from address after '-from' option - using default from" + mailer.getFrom());
        }

        try{
            String subject = clargs.getSignleArgument("-s");
            mailer.setSubject(subject);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            System.out.println("No subject option sepcified (-s) - sending e-mail without subject line!");
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            System.out.println("Missing subject after '-s' option -  - sending e-mail without subject line!");
        }

        /* TO (-to) */
        try{
            ArrayList<String> toArguments = clargs.getArguments("-to");
            mailer.addRecipientsTo(toArguments);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            throw new Exception("No to option sepcified (-to)!");

        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            throw new Exception("Missing arguments after '-to' option!");
        }

        /* CC (-cc) */
        try{
            ArrayList<String> ccArguments = clargs.getArguments("-cc");
            mailer.addRecipientsCc(ccArguments);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            // this is not an errorous condition - should be eventually logged
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            throw new Exception("Missing arguments after '-cc' option!");
        }

        /* BCC (-bcc) */
        try{
            ArrayList<String> bccArguments = clargs.getArguments("-bcc");
            mailer.addRecipientsBcc(bccArguments);
        }catch(CmdLineArguments.CmdLineNoSuchArgumentException clex){
            // this is not an errorous condition - should be eventually logged
        }catch(CmdLineArguments.CmdLineMissingArgumentException clex){
            throw new Exception("Missing arguments after '-bcc' option!");
        }

        String body = "";
        /* READ FROM STDIN (-i) */
        if(clargs.hasArgument("-i")){
            body = readBody(System.in);
            mailer.setBody(body);
        }

        /* FILE (-file) */
        if(clargs.hasArgument("-file")){
            String fileName = clargs.getSignleArgument("-file");
            FileInputStream fis = new FileInputStream(fileName);
            body = readBody(fis);
            mailer.setBody(body);
        }

        mailer.send();
    }
}
