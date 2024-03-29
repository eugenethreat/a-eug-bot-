package com.aeugbot.discordmavenized.controller;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aeugbot.discordmavenized.model.Message;
import com.aeugbot.discordmavenized.view.UI;

import music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 *
 * @author Eugene
 */
public class ViewChanger extends ListenerAdapter {

    private final String fileName;
    private List<Message> messages;
    private final UI UI;

    public ViewChanger(String f, UI u) {
        fileName = f;
        UI = u;
    }

    public String getFilename() {
        return fileName;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        File logJson = new File(fileName);
        /*
        this is going to be used in a future iteration of the bot, where 
        bot output will be filtered or colored. 
         */
        messages = new ArrayList<>();
        //netbeans tells me to use <> instead of <Message> for some reason?...

        try {
            FileReader logReader = new FileReader(logJson);
            JsonReader logToJsonReader = new JsonReader(logReader);
            logToJsonReader.setLenient(true);

            //need to tak the channel, the user, and the message...
            while (logToJsonReader.peek() != JsonToken.END_DOCUMENT) {

                logToJsonReader.beginObject();

                String placeHolder1 = logToJsonReader.nextName();
                String userString = logToJsonReader.nextString();
                String placeHolder2 = logToJsonReader.nextName();
                String channelString = logToJsonReader.nextString();
                String placeHolder3 = logToJsonReader.nextName();
                String messageString = logToJsonReader.nextString();
                //String placeHolder4 = logToJsonReader.nextName();
                //String messageString = logToJsonReader.nextString();

                /*
                    each of the placeholders holds the value of the key associated with the values; 
                    this is to ensure that logToJsonReader doesn't stall on one of the keys 
                    when it's expecting a value. 
                    (also spent a lot of time stumped on this too) 
                 */
                Message newMessage = new Message(channelString, userString, messageString);

                messages.add(newMessage);
                logToJsonReader.endObject();

                //closes the reader object to prevent memory issues.
            }

            logToJsonReader.close();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        /*
        the following code updates the UIPanel (center/top) and leftPanel (most requested)
         */
        JTextArea outputField = UI.getUIText();
        JTextField leftField = UI.getLeftText();

        int iterator = messages.size() - 1;
        if (iterator == -1) {
            ++iterator; //this is so you don't have to have an extra ++ somewhere if you start with a non "!"-denoated input.
            outputField.setText("waiting ...");
        } else {
            Message currentMessage = messages.get((messages.size() - 1));
            String currentUser = currentMessage.getUser();

            if (currentMessage.toString().contains("help")) {
                outputField.append("\n" + currentMessage + "(responded with !help)");
                MessageChannel channel = event.getChannel();
                //String help = "current commands: !ping, !snail, !admin, !help, !play";
                /*
                bug -- this message repeats 5 infinitely? 
                 */

                String help = "some arbitrary output";
                channel.sendMessage(help).queue();

            } else {
                GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

                VoiceChannel voiceChannel = memberVoiceState.getChannel();
                Guild guild = event.getGuild();
                AudioManager adm = guild.getAudioManager();

                PlayerManager manager = PlayerManager.getInstance();

                if (currentMessage.toString().contains("!snail")) {
                    outputField.append("\n" + currentMessage + "(responded with lindsey jordan is my queen)");
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("lindsey jordan is my queen!").queue();

                } else if (currentMessage.toString().contains("!abuse")) {
                    outputField.append("\n" + currentMessage + "(responded with admin abuse never felt so g ood)");
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("admin abuse never felt so good!").queue();

                } else if (currentMessage.toString().contains("!ping")) {
                    outputField.append("\n" + currentMessage + "(responded with a pong)");
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("pong!").queue();

                } else if (currentMessage.toString().contains("!play") && voiceChannel != null) {
                    
                    String[] query = currentMessage.getMessage().split(" ", 2);

                    //connecting to voice channel
                    adm.setSendingHandler(manager.getGuildMusicManager(guild).getSendHandler());
                    adm.openAudioConnection(voiceChannel);
                    //opens the connection to the current voice channel 

                    outputField.append("\n" + currentMessage + "responded with (!now playing: " + currentMessage.getMessage() + ")");
                    /*
                     the message is taken care of in the loadAndPlay method
                    */

                    manager.loadAndPlay((TextChannel) event.getChannel(), query[1]);
                    manager.getGuildMusicManager(event.getGuild()).player.setVolume(100);

                    //https://www.youtube.com/watch?v=fH0NQzXlzyQ -- sometimes 
                } else if (currentMessage.toString().contains("!stop")) {

                    guild = event.getGuild();
                    adm = guild.getAudioManager();

                    adm.closeAudioConnection();
                    //closes the connection to the current voice channel 

                    outputField.append("\n" + currentMessage + "responded with (!now playing: " + currentMessage.getMessage() + ")");
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("playback stopped!").queue();

                } else {
                    //outputField.append("\n" + currentMessage.toString());
                    //makes sure bot-output isn't output to the jtextarea.

                    /*
                    need to figure out what is causing the messages to repeat 

                    all mesaages that dont contain an exclamation point repeat 
                    5 times.
                     */
                }
            }

            String mostUsedCommand = mostUsedCommand(messages);
            leftField.setText(mostUsedCommand);

        }
        /*
                bug - since it only adds if the thing has a !, if the first 
                message you input in the text channel doesn't have a !, the 
                program crashes with an ArrayIndexOutOfBounds exception.
        
                possible solutions -- dummy variable written at the 
                beginning of every json? 
                
                using "iter" is a small workaround, I don't know if it works 
                in every scenario               
         */
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String mostUsedCommand(List<Message> messages) {

        ArrayList<String> iteratedFrequency = new ArrayList<>();
        ArrayList<Integer> freqHolder = new ArrayList<>();
        ArrayList<String> msgHolder = new ArrayList<>();

        for (Message currentMessage : messages) {
            String text = currentMessage.getMessage();
            if (text.contains("!play")) {
                text = "!play";
                iteratedFrequency.add(text);
            } else {
                iteratedFrequency.add(text);

            }

            int occurances = Collections.frequency(iteratedFrequency, text);
            //I'm kinda insane 
            msgHolder.add(text);
            freqHolder.add(occurances);
            //the values of msgHolder and holder will correspond.
        }

        /*
        how would I return the index of the largest value?
         
        an array that holds the frequencies 
        an array that holds the words 
        
        find the biggest value in freq 
        get the index of that largest value 
        
        get the word at that index 
         */
        int occurancesMostOccured = Collections.max(freqHolder);
        int indexMostOccured = freqHolder.indexOf(occurancesMostOccured);

        String mostOccuredWord = msgHolder.get(indexMostOccured) + " (" + occurancesMostOccured + ")";

        return mostOccuredWord;
    }

}
