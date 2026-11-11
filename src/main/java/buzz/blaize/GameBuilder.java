package buzz.blaize;

import javax.annotation.Nullable;
import java.util.*;

/// Multiple commands may be issued that must be tracked alongside each other.
///
///
public class GameBuilder {
    public static final int MAX_BOTS = 8;

    /// 8 keys, for a max of 8 players (bots) in one game
    private static List<Pair<Boolean, AuthToken>> botKeys = new ArrayList<>(8);
    static {
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("103", "naje", "bahms.58LQaSByKuNJhj7B.YesZCbNTG7mVgp44DTFe4I2on2UZyIguN49VBi81CC8"))); // 103, naje
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("104", "ear", "bahms.pGuH2qRnQ--NwLNV.8qmQJk8sek27G-_6-VZ6I_OZ2SEgqI-CGo_kPCSWONk"))); // 104, ear
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("105", "flow", "bahms.DkNHlglZi0ltjGAV.JDOYIRg2yT-0Y96YwN3bDtOh-Ds_eG9RRCsvWiMXSIs"))); // 105, flow
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("110", "der", "bahms.rI-1Wjw1PQPWyqd2.EDcLkXXyHCWtLpMy2hqisxlk5Fwr9_IwsuPzKqZ3S88"))); // 110, der
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("106", "jane", "bahms.RHGz6ETXDWxKdO_R.pIKmg--V7NX-BnoPpuT71pi0NzFw6MgtpIX2rWzv_nQ"))); // 106, jane
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("107", "red", "bahms.Dq0akyrdSJx8a1Jx.MexJPy42D_65zjzgnTuPO2AvEABwKyKGa80S-ZCz4eY"))); // 107, red
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("108", "rae", "bahms.aeGwNz4gn_NBk-gX.j2EWqnPA29s1SZ5LlolhJnZ89ZTsl_ufznNXYfJfvuc"))); // 108, rae
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("109", "wolf", "bahms.ij66RZCqhEa5VsSF.741lwcgeYPnEFgmcCXdS6En7ibB0lMM1o1nCWyJmfIY"))); // 109, wolf
    }
    @Nullable
    private static AuthToken allocateAuthToken() {
        for (Pair<Boolean, AuthToken> botKey : botKeys) {
            if (botKey.getKey().booleanValue() == false) {
                botKey.setKey(new Boolean(true));
                return botKey.getValue();
            }
        }
        return null;
    }
    private static void releaseAuthToken(AuthToken token) {
        for (Pair<Boolean, AuthToken> botKey : botKeys) {
            if (botKey.getValue().equals(token)) {
                botKey.setKey(new Boolean(false));
                return;
            }
        }
    }


    // Token used for communicating with poker server. Token is the same between jane's and wolf's apis.
    private String authToken;

    // The game ID for this game. This ID is valid until the game is completed.
    @Nullable
    private String gameId;
    @Nullable
    public String getGameId() { return this.gameId; }

    // True if the game is started
    private boolean gameInProgress = false;
    public boolean isGameInProgress() { return this.gameInProgress; }

    // True if the game is completed or stopped.
    private boolean gameCompleted = false;
    public boolean isGameCompleted() { return this.gameCompleted; }

    // The list of bots participating in this game. Any in this list are populated when the game is created.
    private Set<PokerBot> bots = new HashSet<>();
    public int getNumberOfBots() { return bots.size(); }

    public GameBuilder(String authToken) {
        this.authToken = authToken;
    }

    public void addBot(BotType botType) {
        if (bots.size() == 8) {
            throw new ArrayIndexOutOfBoundsException("Cannot add any more bots to this game.");
        }
        AuthToken token = GameBuilder.allocateAuthToken();
        bots.add(new PokerBot(token, botType));
    }

    public void removeBot(PokerBot pokerBot) {
        if (bots.contains(pokerBot)) {
            bots.remove(pokerBot);
            GameBuilder.releaseAuthToken(pokerBot.getToken());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Game ID
        sb.append("Game ID: " + ((gameId != null) ? gameId : "None"));
        sb.append("\n");

        // Participating bots
        sb.append("Selected bots:");
        sb.append("\n");
        final String format = "%-5s %-10s %-20s%n";
        sb.append(String.format(format, "#", "Name", "Bot Type"));
        sb.append("-------------------------------\n");
        if (bots.isEmpty()) {
            sb.append("None");
        } else {
            int i = 1;
            for(PokerBot bot : bots) {
                sb.append(String.format(format, i++, bot.getName(), bot.getBotType()));
            }
        }

        if (gameInProgress) {
            // If the game is in progress
            sb.append("Game currently in progress");
        } else if (gameCompleted) {
            // if the game can be started
            sb.append("Game is completed.");
        }

        return sb.toString();
    }
}
