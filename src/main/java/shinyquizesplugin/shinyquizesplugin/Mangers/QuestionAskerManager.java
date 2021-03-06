package shinyquizesplugin.shinyquizesplugin.Mangers;

import org.bukkit.ChatColor;
import shinyquizesplugin.Languages.LanguageManager;
import shinyquizesplugin.shinyquizesplugin.Mangers.Messengers.ServerCommunicator;
import shinyquizesplugin.shinyquizesplugin.Quiz.ActiveQuizInformation;
import shinyquizesplugin.shinyquizesplugin.Quiz.QuestionManager;
import shinyquizesplugin.shinyquizesplugin.Quiz.Questions.questionGetters.RandomQuestionManager;

import static shinyquizesplugin.shinyquizesplugin.ShinyQuizesPlugin.PLUGIN;

public class QuestionAskerManager {


    private static int previousQuestion = -1;


    private static int minimumPlayers;
    private static int percentChance;
    public static void start(){
        ActiveQuizInformation.cancelQuestion();
        PLUGIN.getServer().getScheduler().cancelTasks(PLUGIN);

        int delay = ConfigManager.getConfig().getInt("DelayBetweenQuestions");
        percentChance = ConfigManager.getConfig().getInt("PercentChanceForQuestion");
        minimumPlayers = ConfigManager.getConfig().getInt("minimumNumberOfPlayersForQuestions");

        if(ConfigManager.getConfig().getBoolean("enableRandomQuestions")) {
            int cd = ConfigManager.getConfig().getInt("RandomQuestionAnnouncementTimer");
            int announcementDelay = delay - cd;
            if(ConfigManager.getConfig().getBoolean("enableRandomQuestionAnnouncement") && enoughPlayers()) PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> announceQuestion(cd), 20L * announcementDelay);
            PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> askRandomQuestionWithRepeat(delay, enoughPlayers()), 20L *delay);
        }
    }


    private static void askRandomQuestionWithRepeat(int delay, boolean send){

        if(send && !ActiveQuizInformation.isActive() && enoughPlayers()) {
            askRandomQuestion();
        } else {
            ServerCommunicator.sendDebugMessage("Skipped a question.");
        }

        boolean sendNextQuestion = sendNextQuestion(percentChance);

        if(ConfigManager.getConfig().getBoolean("enableRandomQuestionAnnouncement") && enoughPlayers()){
            int cd = ConfigManager.getConfig().getInt("RandomQuestionAnnouncementTimer");
            int announcementDelay = delay - cd;
            if(announcementDelay > 0 && sendNextQuestion) PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> announceQuestion(cd), 20L * announcementDelay);
        }

        PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> askRandomQuestionWithRepeat(delay, sendNextQuestion), 20L *delay);
    }

    public static void askRandomQuestion(){
        QuestionManager.createQuestion(RandomQuestionManager.generateRandomQuestion());
    }

    public static void announceQuestion(int delay){
        ServerCommunicator.sendChatMessage(getQuestionAnnouncementString(delay));
    }

    private static boolean sendNextQuestion(int chance){
        int randomNumber = (int)(Math.random()*100) +1;

        return randomNumber <= chance;
    }

    private static String getQuestionAnnouncementString(int time){
        String color = ConfigManager.getConfig().getString("HighlightedWordColor");
        String str = LanguageManager.getLanguage().get("questionAnnouncement");
        return java.text.MessageFormat.format(str, color+time+ ChatColor.WHITE);
    }


    private static boolean enoughPlayers(){
        int players = PLUGIN.getServer().getOnlinePlayers().size();
        return players >= minimumPlayers;
    }

}
