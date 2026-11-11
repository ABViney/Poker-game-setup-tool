package buzz.blaize;

import java.io.IOException;
import java.io.Reader;
import java.lang.classfile.Label;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private Scanner scanner;

    /**
     *
     * @throws IOException if an error occurred while mounting the terminal
     */

    public App() {
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException, NoSuchFieldException {
        System.out.println("Starting up");
        String authToken = requestAuthToken();
        System.out.println("Auth token provided.");

        GameBuilder gameBuilder = new GameBuilder(authToken);

        // While the app is running, the user can
        boolean shouldQuit = false;
        while (!shouldQuit) {
            System.out.println("Current game configuration:");
            System.out.println(gameBuilder);

            // Available actions are derived from the state of the GameBuilder
            List<Pair<Character, Action>> options = getOptions(gameBuilder);
            for (Pair<Character, Action> option : options) {
                System.out.println(String.format("[%s]: %s", option.getKey(), option.getValue().getLabel()));
            }

            // Maps don't guarantee retrieval order, but they are much easier for matching key/val.
            Map<Character, Action> actionMap = options.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));

            boolean validSelection = false;
            while (!validSelection) {

                char character;
                Action action = null;

                while (!actionMap.containsKey(new Character(character = scanner.next().toCharArray()[0])));

                action = actionMap.get(character);

                executeAction(action, gameBuilder);
            }

//            lineReader.zeroOut();
            // 1. Add/remove bots
            // 2. Create game
            // 3. Start game
            // 4? Stop game
            // q? Quit - Stop game if in progress
        }


    }

    private void executeAction(Action action, GameBuilder gameBuilder) {
        switch (action) {
            case Action.AddBot:
                addBots(gameBuilder);
                break;
            case Action.RemoveBot:
                break;
            case Action.CreateGame:
                break;
            case Action.StartGame:
                break;
            case Action.StopGame:
                break;
            case Action.Quit:
                break;
            default:
                throw new IllegalArgumentException("Unrecognized Action [" + action.toString() + "]");
        }
    }

    private void addBots(GameBuilder gameBuilder) {
        List<Pair<Character, BotType>> botTypeOptions = getBotTypeOptions();
        Map<Character, BotType> botTypeSelectionMap = botTypeOptions.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        System.out.println("Select a bot type: ");
        for (Pair<Character, BotType> option : botTypeOptions) {
            System.out.println(String.format("[%s]: %s", option.getKey(), option.getValue()));
        }
        System.out.println("[0]: Back");

        char inputCharacter;
        boolean backOut = false;
        boolean isAddingBot = true;
        while (!backOut && isAddingBot) {
            while (isAddingBot) {
                inputCharacter = new Character(scanner.next().toCharArray()[0]);

                // Exit this menu
                if (inputCharacter == '0') {
                    backOut = true;
                    break;
                }
                // If a selection is made, add that bot
                else if (botTypeSelectionMap.containsKey(new Character(inputCharacter))) {
                    BotType botType = botTypeSelectionMap.get(inputCharacter);
                    gameBuilder.addBot(botType);
                    System.out.println("Added bot [" + botType.toString() + "]");
                    if (gameBuilder.getNumberOfBots() >= GameBuilder.MAX_BOTS) {
                        backOut = true;
                        break;
                    }
                    else System.out.println("Select another bot or press [0] to exit.");
                }
            }
        }
    }

    /**
     * Generate a list of actions that can be taken regarding the current state of the game builder.
     * @param gameBuilder
     * @return A list of inputs and their associated Action
     */
    private List<Pair<Character, Action>> getOptions(GameBuilder gameBuilder) {
        List<Pair<Character, Action>> actions = new ArrayList<>();

        // Ansii numerics are sequential and there won't be more than a few options
        char index = '1';

        // Create/Start/Stop game
        if (gameBuilder.getGameId() != null || gameBuilder.isGameCompleted()) {
            actions.add(new Pair<>(new Character(index++), Action.CreateGame));
        } else if (gameBuilder.isGameInProgress()) {
            actions.add(new Pair<>(new Character(index++), Action.StopGame));
        } else {
            actions.add(new Pair<>(new Character(index++), Action.StartGame));
        }

        if (!gameBuilder.isGameInProgress()) {
            int numOfBots = gameBuilder.getNumberOfBots();
            if (numOfBots < 8) {
                actions.add(new Pair<>(new Character(index++), Action.AddBot));
            }
            if (numOfBots > 0) {
                actions.add(new Pair<>(new Character(index++), Action.RemoveBot));
            }
        }

        actions.add(new Pair<>(new Character('q'), Action.Quit));

        return actions;
    }

    /**
     * Request the user input an authentication token required for making requests to the Pokerbot API.
     * @return The auth token for this instance.
     */
    private String requestAuthToken() {
        final String pokerControlPanelUrl = "https://bahms.org/assets/poker-admin/?auth_success=true#game_list";
        System.out.println("Go to\n" + pokerControlPanelUrl + "\nto create an auth token if you do not have one already.");

        String authToken = "";
        do {
            // Note: JetBrains w/ Gradle makes it difficult to hook into system console. JLine has a workaround for this,
            // but it behaves oddly in the IDEA terminal. The mask "works" but it's wonky. Use only in final build.
//             String input = lineReader.readLine("Enter your auth token: ", '*');
            System.out.println("Enter your auth token: ");
            String input = scanner.next();

            if (!input.startsWith("bahms.")) {
                System.out.println("Invalid token provided: Token must start with \"bahms.\"");
                System.out.println("Received: " + input);
            } else if (input.length() != 66) {
                System.out.println("Invalid token length: Tokens are 66 characters long [input is " + input.length() + " characters]" );
                System.out.println("Received: " + input);
            } else {
                authToken = input;
                System.out.println("This token is probably valid.");
                break;
            }

        } while(true);
        System.out.println("Made it here");
        return authToken;
    }

    private List<Pair<Character, BotType>> getBotTypeOptions() {
        List<Pair<Character, BotType>> botTypeOptions = new ArrayList<>();
        char index = 'a';
        for (BotType bot : BotType.values()) {
            botTypeOptions.add(new Pair<>(new Character(index++), bot));
        }
        return botTypeOptions;
    }
}
