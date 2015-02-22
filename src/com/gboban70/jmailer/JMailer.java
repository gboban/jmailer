package com.gboban70.jmailer;
/**
 * Created by goran on 2/22/15.
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class JMailer {
    private Properties properties = null;
    private MimeMessage message = null;
    private Session session = null;
    private boolean verbose = false;

    private ArrayList<String> arguments = null;

    public JMailer(ArrayList<String> arguments){
        this.setArguments(arguments);
    }

    public JMailer(String[] args){
        this.setArguments(args);
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public void setArguments(String[] args) {
        if(this.arguments == null){
            this.arguments = new ArrayList<String>();
        }

        if(this.arguments.isEmpty() == false){
            this.arguments.clear();
        }

        for(int i = 0; i < args.length; ++i){
            this.arguments.add(args[i]);
        }
    }

    private ArrayList<String> getOptionParams(String option){
        int index = this.arguments.indexOf(option);
        if(index == -1){
            return null;
        }

        ArrayList<String> result = new ArrayList<String>();
        for(int i = index + 1; i < this.arguments.toArray().length; ++i){
            String argument = this.arguments.get(i);

            // check if element is next option
            if(argument.charAt(0) == '-'){
                break;
            }

            result.add(argument);
        }
        return result;
    }

    private MimeMessage getMessage() throws MessagingException{
        this.properties = System.getProperties();
        this.session = Session.getDefaultInstance(this.properties);
        this.message = new MimeMessage(this.session);

        /* VERBOSE (-v) */
        this.verbose = (this.arguments.indexOf("-v") != -1);

        /* TIMEOUT (-t) */
        int index = this.arguments.indexOf("-t");
        if(index != -1){
            throw new MessagingException("JMailer: switch is not used: -t");
        }

        /* HOST (-h) */
        index = this.arguments.indexOf("-h");
        String host = null;
        if(index != -1){
            host = this.arguments.get(index+1);
            if(host.charAt(0)=='-') host = null;
        }
        if(host != null){
            this.properties.setProperty("mail.smtp.host", host);
        }

        /* PORT (-p) */
        index = this.arguments.indexOf("-p");
        String strPort = null;
        if(index != -1){
            strPort = this.arguments.get(index+1);
            if(strPort.charAt(0)=='-') strPort = null;
        }
        if(strPort != null){
            int port = Integer.parseInt(strPort);
            this.properties.setProperty("mail.smtp.host", Integer.toString(port));
        }

        /* FROM (-from) */
        String from = null;
        index = this.arguments.indexOf("-from");
        if(index != -1){
            from = this.arguments.get(index+1);
            if(from.charAt(0)=='-') from = null;
        }else{
            try{
                from = this.properties.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName();
            }catch(UnknownHostException e){
                from = this.properties.getProperty("user.name") + "@localhost";
            }
        }
        if(from == null){
            throw new MessagingException("JMailer: switch requires argument: -from");
        }
        this.message.setFrom(from);

        /* SUBJECT (-s) */
        String subject = null;
        index = this.arguments.indexOf("-s");
        if(index != -1){
            subject = this.arguments.get(index+1);
            if(from.charAt(0)=='-') subject = null;

            if(subject == null){
                throw new MessagingException("JMailer: switch requires argument: -s");
            }
        }else{
            throw new MessagingException("JMailer: missing switch: -s");
        }

        this.message.setSubject(subject);

        /* TO (-to) */
        index = this.arguments.indexOf("-to");
        if(index == -1){
            throw new MessagingException("JMailer: missing switch: -to");
        }else{
            ArrayList<String> toParams = this.getOptionParams("-to");
            boolean hasTo = false;
            while(toParams.iterator().hasNext()){
                hasTo = true;
                String to = toParams.iterator().next();
                this.message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            }

            if(!hasTo){
                throw new MessagingException("JMailer: switch requires argument: -to");
            }
        }

        /* CC (-cc) */
        index = this.arguments.indexOf("-cc");
        if(index != -1){
            ArrayList<String> ccParams = this.getOptionParams("-cc");
            boolean hasCc = false;
            while(ccParams.iterator().hasNext()){
                hasCc = true;
                String cc = ccParams.iterator().next();
                this.message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(cc));
            }

            if(!hasCc){
                throw new MessagingException("JMailer: switch requires argument: -cc");
            }
        }

        /* BCC (-bcc) */
        index = this.arguments.indexOf("-bcc");
        if(index != -1){
            ArrayList<String> bccParams = this.getOptionParams("-bcc");
            boolean hasBcc = false;
            while(bccParams.iterator().hasNext()){
                hasBcc = true;
                String bcc = bccParams.iterator().next();
                this.message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(bcc));
            }

            if(!hasBcc){
                throw new MessagingException("JMailer: switch requires argument: -bcc");
            }
        }

        String body = "";
        /* READ FROM STDIN (-i) */
        index = this.arguments.indexOf("-i");
        if(index != -1){
            body = "Reading from stdin";
        }

        /* FILE (-file) */
        index = this.arguments.indexOf("-file");
        if(index != -1){
            body = "Reading from file";
        }

        this.message.setText(body);

        return this.message;
    }

    public void send() throws MessagingException{
        MimeMessage m = this.getMessage();
        Transport.send(m);
    }
}
