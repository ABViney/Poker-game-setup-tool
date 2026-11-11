package buzz.blaize;


import java.util.Objects;

public class PokerBot {
    private AuthToken token;
    private BotType botType;

    public PokerBot(AuthToken token, BotType botType) {
        this.token = token;
        this.botType = botType;
    }

    public String getId() { return token.getId(); }
    public String getName() { return token.getName(); }
    public AuthToken getToken() { return token; }

    public BotType getBotType() {
        return botType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PokerBot pokerBot)) return false;
        return Objects.equals(token, pokerBot.token) && botType == pokerBot.botType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, botType);
    }
}
