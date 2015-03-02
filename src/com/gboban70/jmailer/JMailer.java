package com.gboban70.jmailer;
/**
 *
 * @author Goran Boban
 * @since 2015-02-22
 * @version v0.0.4
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
import java.util.Iterator;
import java.util.Properties;
//import javax.mail.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.*;

public class JMailer {
    private Properties properties = null;
    private MimeMessage message = null;
    private boolean verbose = false;

    public JMailer(){
        this.initialize();
    }

    private void initialize(){
        this.properties = System.getProperties();
        Session session = Session.getDefaultInstance(this.properties);
        this.message = new MimeMessage(session);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     *
     * Following methods are shortcuts for configuring MimeMessage and properties.
     * in future, JMailer may be subclassed from MimeMessage containing Properties and Transport...
     *
     */
    public void setHost(String host){
        if(host == null || host == ""){
            host =" localhost";
        }
        this.properties.setProperty("mail.smtp.host", host);
    }

    public void setPort(int port){
        if(port<=0){
            port = 25;
        }
        this.properties.setProperty("mail.smtp.port", Integer.toString(port));
    }

    public void setFrom(String from) throws MessagingException{
        if(from == null || from == ""){
            try{
                from = this.properties.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName();
            }catch(UnknownHostException e){
                from = this.properties.getProperty("user.name") + "@localhost";
            }
        }
        this.message.setFrom(from);
    }

    public void setSubject(String subject) throws MessagingException{
        if(subject == null){
            subject = "";
        }
        this.message.setSubject(subject);
    }

    public void addRecipientTo(String recipient) throws MessagingException{
        this.message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    }

    public void addRecipientCc(String recipient) throws MessagingException{
        this.message.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient));
    }

    public void addRecipientBcc(String recipient) throws MessagingException{
        this.message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
    }

    protected void _basic_addRecipients(Message.RecipientType recipientType, ArrayList<String> recipients) throws MessagingException{
        Iterator<String> recipientIterator = recipients.iterator();
        while(recipientIterator.hasNext()){
            String recipient = recipientIterator.next();
            message.addRecipient(recipientType, new InternetAddress(recipient));
        }
    }

    public void addRecipientsTo(ArrayList<String> recipients) throws MessagingException{
        this._basic_addRecipients(Message.RecipientType.TO, recipients);
    }

    public void addRecipientsCc(ArrayList<String> recipients) throws MessagingException{
        this._basic_addRecipients(Message.RecipientType.CC, recipients);
    }

    public void addRecipientsBcc(ArrayList<String> recipients) throws MessagingException{
        this._basic_addRecipients(Message.RecipientType.BCC, recipients);
    }

    public void setBody(String text) throws MessagingException{
        this.message.setText(text);
    }

    /**
     * send()
     * @throws MessagingException
     *
     * main method for sending message
     */
    public void send() throws MessagingException{
        Transport.send(this.message);
    }
}
