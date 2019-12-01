/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeugbot.discordmavenized.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author Eugene
 */
public class BotModel extends ListenerAdapter {

    private JDA bot;

    public BotModel() throws LoginException, InterruptedException, FileNotFoundException {
        try {
            //   String fileName = "src/mydiscordbot/" + jsonName;

            File keyFile = new File("key.txt");

            Scanner scanner = new Scanner(keyFile);

            String key = scanner.nextLine();
            bot = new JDABuilder(key).build();
            //I spent years here, only to realize the API key was entered wrong ...
            //please don't hijack my bot <3 
            //api key removed 

            bot.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
            System.out.println("Finished Building JDA!");

        } catch (LoginException | InterruptedException le) {
            System.out.println(le.getMessage());
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getMessage());
        }

    }

    public JDA getJDA() {
        return bot;
    }

}
