/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeugbot.discordmavenized.model;

public class Message {

    private final String channel;
    private final String user;
    private final String message;
    
    //private boolean fromBot = false;

    public Message(String c, String u, String m) {
        channel = c;
        user = u;
        message = m;
    }

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Channel: " + channel + " User: " + user + " Text: " + message;
    }
    /*
    public void setFromBot(boolean b){
        fromBot = b;
    }*/

}
