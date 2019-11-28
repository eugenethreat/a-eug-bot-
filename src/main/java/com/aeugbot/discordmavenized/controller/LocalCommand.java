/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeugbot.discordmavenized.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aeugbot.discordmavenized.model.Message;
import com.aeugbot.discordmavenized.view.UI;
import com.aeugbot.discordmavenized.model.BotModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.Instant;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 *
 * @author eugene
 */
public class LocalCommand {

    private final BotModel BotModel;
    private final UI UI;
    private final ViewChanger vc;

    private List<Message> messages;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final HashMap<String, String> localMap = new HashMap<>();

    public LocalCommand(BotModel passedBotModel, UI passedUI, ViewChanger passedVc) {

        BotModel = passedBotModel;
        UI = passedUI;
        vc = passedVc;

        /*
        saving input from the jtextfield middleField as a string/message; 
        once it's being treated as a messsage I can just use it 
        in either of the controller methods;
        
         */
        JTextField middleField = UI.getMiddleText();
        middleField.requestFocusInWindow();
        middleField.setFocusable(true);

        middleField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                JTextArea outputField = UI.getUIText();

                String textFromMiddle = middleField.getText();

                if (textFromMiddle != null && e.getKeyCode() == 10) {
                    /*the listener will only activate is 
                    the enter key is pressed and something is there*/

                    if (textFromMiddle.contains("!")) {
                        Message currentMessage = new Message("Local", "Local", textFromMiddle);

                        try {
                            fileWriting(currentMessage);

                        } catch (IOException ex) {
                            Logger.getLogger(LocalCommand.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (currentMessage.toString().contains("meowzers")) {
                            //who the fuck is going to request meowzers
                            //placeholder so that commands can execute 
                            /*
                    the logic that handles the bot's response; eventually, once 
                    the functionality for playing music is implented (on my own)
                    this will print out to the jtexfield in amore robust manner. 
                             */
                        }
                        if (currentMessage.toString().contains("!snail")) {
                            outputField.append("\n" + currentMessage + "(responded with mail)");

                        }
                        if (currentMessage.toString().contains("!admin")) {
                            outputField.append("\n" + currentMessage + "(responded with !admin)");

                        }
                        if (currentMessage.toString().contains("!ping")) {
                            outputField.append("\n" + currentMessage + "(responded with a pong)");

                        }
                        /*if (currentMessage.toString().contains("help")) {
                outputField.append("\n" + currentMessage + "(responded with !help)");
                MessageChannel channel = event.getChannel();
                String help = "current commands: ! ping, ! snail, ! admin, ! help, ! play";
                channel.sendMessage(help).queue();
                
            } */

                        if (currentMessage.toString().contains("!play")) {

                        }
                        /*

                        /*
                            I think I'm going to have to access messages with the json name like I do 
                            in the other two methods; that seems like the best way to make 
                            sure that it gets updated, unfortunately. 
                            
                            i dunnno.
                        
                            implementing most used: 
                                grabbing the array from the json (need to write local commands
                                to the json as well ... ) 
                        
                                messages; making a new array every time 
                                copying the code from ViewChanger 
                        
                                theoretically, should work 

                         */
                    }
                }
            } //closing braces for the listener

            @Override
            public void keyReleased(KeyEvent e) {
                /*
                nothing needed here since bot only needs to wait for enter keypress.
                 */
            }
        });

    }

    public void fileWriting(Message passedMessage) throws IOException {
        /*
        writing contents of the local commands to the shared json 
         */
        messages = new ArrayList<>(); //List<Message> global variable 

        String fileName = vc.getFilename();
        try (FileWriter jsonWriter = new FileWriter(fileName, true)) {
            String messageObjToJson = gson.toJson(passedMessage);
            jsonWriter.write(messageObjToJson);
        }

        try {
            File logJson = new File(fileName);

            FileReader logReader = new FileReader(logJson);
            JsonReader logToJsonReader = new JsonReader(logReader);
            logToJsonReader.setLenient(true);

            JTextField leftField = UI.getLeftText();

            //need to tak the channel, the user, and the message...
            while (logToJsonReader.peek() != JsonToken.END_DOCUMENT) {

                logToJsonReader.beginObject();

                String placeHolder1 = logToJsonReader.nextName();
                String userString = logToJsonReader.nextString();
                String placeHolder2 = logToJsonReader.nextName();
                String channelString = logToJsonReader.nextString();
                String placeHolder3 = logToJsonReader.nextName();
                String messageString = logToJsonReader.nextString();
                /*
                    each of the placeholders holds the value of the key associated with the values; 
                    this is to ensure that logToJsonReader doesn't stall on one of the keys 
                    when it's expecting a value. 
                    (also spent a lot of time stumped on this too) 
                 */

                Message newMessage = new Message(channelString, userString, messageString);
                messages.add(newMessage);

                logToJsonReader.endObject();
                logToJsonReader.close();
                //closes the reader object to prevent memory issues.

                mapping(messages); //adds the local commands to localMap

                String mostUsedCommand = vc.mostUsedCommand(messages);
                leftField.setText(mostUsedCommand); //updates mostUsedCommand, if necessary.

            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void mapping(List<Message> messages) {
        for (Message message : messages) {
            String msgContents = message.getMessage();
            Clock clock = Clock.systemDefaultZone();
            Instant instant = clock.instant();
            String time = instant.toString();
            localMap.put(time + " " + message.toString(), msgContents);
        }
        System.out.println(localMap);

    }
}
