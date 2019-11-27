package com.aeugbot.discordmavenized.controller;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import com.aeugbot.discordmavenized.model.Message;

public class ProgramListener extends ListenerAdapter {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Random random = new Random();
    double rand = random.nextInt(100000);
    String jsonName = "json" + String.format("%.2f", rand);
    /*making sure that it's a new json for storing messages; 
        the json only stores messages with the special character, in this case "!".
     */

    String fileName = "src/main/java/com/aeugbot/discordmavenized/" + jsonName;
        //a jank solution 
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        //gson with my father - making a file readable by all classes.
        //saves the message as a json to be read by the view or controller.
        try {
            FileWriter jsonWriter = new FileWriter(fileName, true);

            String channelText = event.getTextChannel().getName();
            String userText = event.getMember().getEffectiveName();
            String messageText = event.getMessage().getContentDisplay();

            Message messageObj = new Message(channelText, userText, messageText);

            if (messageText.contains("!")) {
                String messageObjToJson = gson.toJson(messageObj);
                jsonWriter.write(messageObjToJson);
                jsonWriter.close();
            } //this ensures that only the requests are added (as outlined above).
            else {
                jsonWriter.close();
            }
            /*had a lot of trouble with the json formatting ... spent years getting errors
            before I realized I could not have the prettyprinting format here, otherwise 
            it would error out 
            
            (ie, writer.write("\n").)
             */

        } catch (FileNotFoundException fe) {
            System.out.println(fe.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }

    }

    public String getFileName() {
        return fileName;
    } //so other classes can access the json as well.

}
