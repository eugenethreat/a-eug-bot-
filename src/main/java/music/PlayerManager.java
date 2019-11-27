/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 *
 * @author eugene
 */
public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager apm;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.apm = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(apm);
        AudioSourceManagers.registerLocalSource(apm);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(apm);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        apm.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack at) {
                channel.sendMessage("adding to queue: " + (at.getInfo().title)).queue();
                play(musicManager, at); //ez clap 
                /*
                so this the class that takes care of actually PLAYING the audio.
                */

            }

            @Override
            public void playlistLoaded(AudioPlaylist ap) {
                AudioTrack firstTrack = ap.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = ap.getTracks().get(0);
                }

                channel.sendMessage("adding to queue: " + firstTrack.getInfo().title + "first track ....." + ap.getName()).queue();
                play(musicManager, firstTrack);

            }

            @Override
            public void noMatches() {
                channel.sendMessage("nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException fe) {
                channel.sendMessage("could not play " + fe.getMessage()).queue();
            }
        });

    }

    private void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
        /*
        what is a thread?
         */
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return apm;
    }

}
