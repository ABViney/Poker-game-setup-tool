package buzz.blaize;

import buzz.blaize.bahms_poker.api.GameApi;
import buzz.blaize.bahms_poker.model.GameActiveGameDTO;
import buzz.blaize.bahms_poker.model.GameNewGameConfigDTO;
import buzz.blaize.bahms_poker.model.JustResponseMessageString;
import buzz.blaize.wws_poker.api.BotsApi;
import buzz.blaize.wws_poker.model.AgentType;
import buzz.blaize.wws_poker.model.BulkJoinRequest;
import buzz.blaize.wws_poker.model.JoinRequest;

import java.net.http.HttpClient;
import java.nio.CharBuffer;
import java.util.*;
import java.util.stream.IntStream;

public class Main {

    static private Scanner scanner = new Scanner(System.in);

    static private List<PokerBot> bots = Arrays.asList(
            new PokerBot(AuthToken.allocateAuthToken(), BotType.ADAPTIVE),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.BAYESIAN),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.LIMPER),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.GEARSHIFT),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.BLUFFER),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.SHORTSTACK),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.MINRAISER),
            new PokerBot(AuthToken.allocateAuthToken(), BotType.POSAWARE)
    );

    static void main() {
        try {
            // Token used to create the game
            final String vinIsSoAwsomeUwU = "bahms.hguVUkyjJyE5gvlL.J0vYraHLeKc_79FyNyrSOYo8cf1IGqG9RiXXZLCKXaI";

            MainLoop:
            while (true) {

                int numOfPlayers = getNumericUserInput("Enter number of players: ", IntStream.range(0, 9).toArray());
                int numOfBots = getNumericUserInput("Enter number of bots: ", IntStream.range(0, 9).toArray());

                if (numOfPlayers + numOfBots > 8) {
                    System.out.println("Too many participants. Ensure the sum of players and bots doesn't exceed 8.\n");
                    continue MainLoop;
                }

                /** Creating a game */
                String gameId;

                // Poker server API requires an Authorization header set with the token
                buzz.blaize.bahms_poker.invoker.ApiClient bahmsApiClient = new buzz.blaize.bahms_poker.invoker.ApiClient();
                bahmsApiClient.setRequestInterceptor(r -> r.header("Authorization", "Bearer " + vinIsSoAwsomeUwU));

                GameApi gameApi = new GameApi(bahmsApiClient);

                GameNewGameConfigDTO newGameConfigDTO = new GameNewGameConfigDTO();
                newGameConfigDTO
                        .autoStartsHands(false)
                        .bigBlind(100)
                        .smallBlind(50)
                        .playerCount(numOfPlayers + numOfBots)
                        .startingChips(Map.ofEntries(
                                Map.entry("10", 10),
                                Map.entry("50", 5),
                                Map.entry("100", 5),
                                Map.entry("500", 1),
                                Map.entry("1000", 4)
                        ))
                        .chipDenominations(Arrays.asList(
                                10,
                                50,
                                100,
                                500,
                                1000
                        ))
                ;

                JustResponseMessageString gamePostResponse = gameApi.gamePost(newGameConfigDTO);
                gameId = gamePostResponse.getData();
                System.out.println();
                System.out.println("Game ID: " + gameId);
                System.out.println("Open Tabletop Simulator and listen to this instance.");

                if (numOfBots > 0) {
                    Collections.shuffle(bots);

                    buzz.blaize.wws_poker.invoker.ApiClient wwsApiClient = new buzz.blaize.wws_poker.invoker.ApiClient();
                    wwsApiClient.updateBaseUri("https://pokerbot.wolfwithsword.com/");
                    BotsApi botsApi = new BotsApi(wwsApiClient);

                    getUserNextLine("Press Enter when ready for bots to join.");
                    for (int i = 0; i < numOfBots; i++) {
                        PokerBot bot = bots.get(i);
                        botsApi.joinJoinPost(new JoinRequest()
                                .gameId(gameId)
                                .userId(bot.getId())
                                .token(bot.getToken().getToken())
                                .agent(AgentType.fromValue(bot.getBotType().toString())));
                    }

                }

                getUserNextLine("Once all players have joined, press Enter to start.\n");

                if (gameId == null) {
                    System.out.println("No game ID exists. Something horrible went wrong!");
                    return;
                }
                gameApi.gameGameIdStartedPost(gameId, null);

                boolean gameInProgress = true;
                // I can get notified here when the game ends by seeing if this game is still active.
                while (gameApi.gameGet().stream().anyMatch(gagDTO -> gagDTO.getId() != null && gagDTO.getId().equals(gameId))) {
                    Thread.sleep(1000);
                }

                System.out.println("Game has finished.");
            }

//            App app = new App();
//            app.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Terminal based app

        // Output how to use the app
        // do the same if "help"

//        System.out.println("Poker-bot game setup");
//        String auth_token = new String(System.console().readPassword("Enter auth token: "));

        // --new-game <auth_token> [-p player_name -b bot-type ... (up to 8)]
        // if no players/bot-types are provided, generate random lobby
        // output game-id
    }

    static private char getCharUserInput(String prompt, char... validCharacters) {
        System.out.print(prompt);
        while (true) {
            String input = scanner.nextLine();
            if (input.length() == 1
                && IntStream.range(0, validCharacters.length).anyMatch(i -> validCharacters[i] == input.toCharArray()[0])
            ) {
                return input.toCharArray()[0];
            }

            // No char[] overload in streams api. this just checks if the input is in the validCharacters array
            System.out.println("Invalid input: Acceptable options are "
                    + Arrays.toString(
                            IntStream.range(0, validCharacters.length)
                            .mapToObj(i -> new String("'" + validCharacters[i] + "'"))
                            .toArray()
                    ) + ". You provided: " + input + ".\nTry again: "
            );
        }
    }

    static private int getNumericUserInput(String prompt, int... validNumbers) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine();
                int inputVal = Integer.parseInt(input);

                if (IntStream.range(0, validNumbers.length).anyMatch(i -> validNumbers[i] == inputVal))
                    return inputVal;
                else throw new NumberFormatException("Invalid option selected");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: Acceptable options are "
                        + Arrays.toString(
                                Arrays.stream(validNumbers).mapToObj(String::valueOf).toArray()
                        )
                        + ". You provided: input"
                );
            }
        }
    }

    static private String getUserNextLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
