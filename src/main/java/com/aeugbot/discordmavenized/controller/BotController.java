package com.aeugbot.discordmavenized.controller;

import java.util.List;
import javax.security.auth.login.LoginException;

import com.aeugbot.discordmavenized.model.Message;
import com.aeugbot.discordmavenized.view.UI;
import com.aeugbot.discordmavenized.model.BotModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

public class BotController {

    BotModel BotModel;
    UI UI;

    ProgramListener ml;
    ViewChanger vc;
    
    

    public BotController() throws FileNotFoundException {

        try {
            BotModel = new BotModel();
        } catch (LoginException | InterruptedException le) {
            System.out.println(le.getMessage());
        }
        UI = new UI();
        UI.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                File daJson = new File(ml.getFileName());
                daJson.delete();
                e.getWindow().dispose();
            }
        });


        ProgramListener();
        ViewChanger();
        LocalCommand(BotModel, UI, vc);

    }

    private void ProgramListener() {
        BotModel.getJDA().addEventListener(ml = new ProgramListener());

    }

    private void ViewChanger() {
        System.out.println("json name " + ml.getFileName());
        //name of the json where logs are stored ...

        BotModel.getJDA().addEventListener(vc = new ViewChanger(ml.getFileName(), UI));

    }
    
    private void LocalCommand(BotModel bm, UI UI, ViewChanger vc){        
        LocalCommand lc = new LocalCommand(bm,UI,vc);
        
    }

    /*so now I need a listener attached to UIPanel that whenever it detects a message,
        it grabs the messages and displays them on the textfield.
    
        (this is all in ViewChanger.java )
     */
    public List<Message> getMessages() {
        return vc.getMessages();
    }

}
