package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.TransactionStatus;
import capstone.mfslbackend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final int MAX_PLAYERS = 15;

    private static final int PLAYERS_IN_STARTING_LINEUP = 11;

    private static final int PLAYERS_IN_BENCH = 4;
    private static final int MIN_GK = 1;
    private static final int DRAFT_MIN_DEF = 4;
    private static final int MAX_DEF = 5;
    private static final int DRAFT_MIN_MID = 4;
    private static final int MAX_MID = 5;
    private static final int DRAFT_MIN_FWD = 2;
    private static final int MAX_ATT = 3;
    private static final int TOTAL_DEF = 9;
    private static final int TOTAL_MID = 9;
    private static final int TOTAL_ATT = 7;

    private static final int GK_ORDER = 1;
    private static final int DEF_ORDER = 2;
    private static final int MID_ORDER = 3;
    private static final int ATT_ORDER = 4;
    private static final int DEFAULT_ORDER = 5;



    private final TransactionRepository transactionRepository;
    private final FantasyTeamService fantasyTeamService;
    private final FantasyLeagueService fantasyLeagueService;
    private final PlayerService playerService;

    public TransactionService(TransactionRepository transactionRepository, FantasyTeamService fantasyTeamService,
                              PlayerService playerService, FantasyLeagueService fantasyLeagueService) {
        this.transactionRepository = transactionRepository;
        this.fantasyTeamService = fantasyTeamService;
        this.playerService = playerService;
        this.fantasyLeagueService = fantasyLeagueService;

    }
    public Transaction createTransaction(Long fantasyTeamId, Long incomingPlayerId, Long outgoingPlayerId) {
        Transaction transaction = new Transaction();

        transaction.setDate(LocalDate.now());

        FantasyTeam proposingFantasyTeam = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        transaction.setProposingFantasyTeam(proposingFantasyTeam);

        Optional<Player> playerOut = proposingFantasyTeam.getPlayers().stream().filter(player -> player.getPlayerId().equals(outgoingPlayerId)).findFirst();
        if (playerOut.isPresent()) {
            transaction.setPlayerOut(playerOut.get());
        } else {
            throw new Error404("Player with id " + outgoingPlayerId + " not found in fantasy team with id " + fantasyTeamId);
        }

        Optional<FantasyTeam> takenTeamOptional = fantasyLeagueService.getFantasyTeamOfTakenPlayer(proposingFantasyTeam.getFantasyLeague().getId(), incomingPlayerId);
        if (takenTeamOptional.isPresent()) {
            FantasyTeam takenTeam = takenTeamOptional.get();
            transaction.setReceivingFantasyTeam(takenTeam);
        }

        Player playerIn = playerService.getPlayerById(incomingPlayerId);
        Player outgoingPlayer = playerService.getPlayerById(outgoingPlayerId);
        boolean isSubstituted = false;

        if (transaction.getReceivingFantasyTeam() == null) { //it means it is a free agent
            if (validTransaction(transaction, proposingFantasyTeam, playerIn, outgoingPlayer, isSubstituted)) { //if valid, transaction is accepted
                if (!isSubstituted) {
                    String replacement = proposingFantasyTeam.getPlayerIdsInOrder().replace(outgoingPlayerId.toString(), incomingPlayerId.toString());
                    proposingFantasyTeam.setPlayerIdsInOrder(replacement);
                    transaction.setPlayerIn(playerIn);
                    Set<Player> currentPlayers = proposingFantasyTeam.getPlayers();
                    currentPlayers.remove(outgoingPlayer);
                    currentPlayers.add(playerIn);
                    proposingFantasyTeam.setPlayers(currentPlayers);
                }
                transaction.setStatus(TransactionStatus.ACCEPTED);
            } else {
                transaction.setStatus(TransactionStatus.REJECTED);
            }

        } else {
            transaction.setStatus(TransactionStatus.PROPOSED);
        }
        transaction.setPlayerIn(playerIn);
        return transactionRepository.save(transaction);
    }

    public Transaction draftTransaction(Long fantasyTeamId, Long incomingPlayerId) {
        Transaction transaction = new Transaction();

        transaction.setDate(LocalDate.now());

        FantasyTeam proposingFantasyTeam = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        transaction.setProposingFantasyTeam(proposingFantasyTeam);

        Optional<FantasyTeam> takenTeamOptional = fantasyLeagueService.getFantasyTeamOfTakenPlayer(proposingFantasyTeam.getFantasyLeague().getId(), incomingPlayerId);
        if (takenTeamOptional.isPresent()) {
            throw new Error400("Player with id " + incomingPlayerId + " is already in a fantasy team");
        }

        Player playerIn = playerService.getPlayerById(incomingPlayerId);
        transaction.setPlayerIn(playerIn);

//      check if fantasy team has enough players in each position
        Set<Player> players = proposingFantasyTeam.getPlayers();
        players.add(playerIn);

        if (players.size() > MAX_PLAYERS) {
            throw new Error400("Fantasy team with id " + fantasyTeamId + " already has the maximum number of players");
        }

        int missingGkCount = MIN_GK;
        int missingDefCount = DRAFT_MIN_DEF;
        int missingMidCount = DRAFT_MIN_MID;
        int missingFwdCount = DRAFT_MIN_FWD;
        for (Player player : players) {
            switch (player.getPosition()) {
                case "Goalkeeper" -> {
                    if (missingGkCount > 0) {
                        missingGkCount--;
                    }
                }
                case "Defender" -> {
                    if (missingDefCount > 0) {
                        missingDefCount--;
                    }
                }
                case "Midfielder" -> {
                    if (missingMidCount > 0) {
                        missingMidCount--;
                    }
                }
                case "Attacker" -> {
                    if (missingFwdCount > 0) {
                        missingFwdCount--;
                    }
                }
                default -> throw new Error400("Invalid position for player with id " + player.getPlayerId());
            }
        }
        int missingPlayerCount = missingGkCount + missingDefCount + missingMidCount + missingFwdCount;

        if (missingPlayerCount > (MAX_PLAYERS - players.size())) {
            String missingPositions = "";
            if (missingGkCount > 0) {
                missingPositions += missingGkCount + " Goalkeepers ";
            }
            if (missingDefCount > 0) {
                missingPositions += missingDefCount + " Defenders ";
            }
            if (missingMidCount > 0) {
                missingPositions += missingMidCount + " Midfielders ";
            }
            if (missingFwdCount > 0) {
                missingPositions += missingFwdCount + " Attackers ";
            }
            throw new Error400("Fantasy team with id " + fantasyTeamId + " does not have enough players in the following positions: " + missingPositions);
        }

//        add player to the team immediately
        proposingFantasyTeam.setPlayers(players);
        String lineup = proposingFantasyTeam.getPlayerIdsInOrder() + " " + playerIn.getPlayerId();
        if (players.size() == MAX_PLAYERS) {
            lineup = "";
            List<Player> gks = players.stream().filter(player -> player.getPosition().equals("Goalkeeper")).toList();
            List<Player> defs = players.stream().filter(player -> player.getPosition().equals("Defender")).toList();
            List<Player> mids = players.stream().filter(player -> player.getPosition().equals("Midfielder")).toList();
            List<Player> fwds = players.stream().filter(player -> player.getPosition().equals("Attacker")).toList();
            lineup += gks.get(0).getPlayerId() + " ";
            lineup += defs.subList(0, DRAFT_MIN_DEF).stream().map(player -> player.getPlayerId().toString())
                    .collect(Collectors.joining(" ")) + " ";
            lineup += mids.subList(0, DRAFT_MIN_MID).stream().map(player -> player.getPlayerId().toString())
                    .collect(Collectors.joining(" ")) + " ";
            lineup += fwds.subList(0, DRAFT_MIN_FWD).stream().map(player -> player.getPlayerId().toString())
                    .collect(Collectors.joining(" ")) + " ";
            if (gks.subList(1, gks.size()).size() > 0) {
                lineup += gks.subList(1, gks.size()).stream().map(player -> player.getPlayerId().toString())
                        .collect(Collectors.joining(" ")) + " ";
            }
            if (defs.subList(DRAFT_MIN_DEF, defs.size()).size() > 0) {
                lineup += defs.subList(DRAFT_MIN_DEF, defs.size()).stream().map(player -> player.getPlayerId().toString())
                        .collect(Collectors.joining(" ")) + " ";
            }
            if (mids.subList(DRAFT_MIN_MID, mids.size()).size() > 0) {
                lineup += mids.subList(DRAFT_MIN_MID, mids.size()).stream().map(player -> player.getPlayerId().toString())
                        .collect(Collectors.joining(" ")) + " ";
            }
            if (fwds.subList(DRAFT_MIN_FWD, fwds.size()).size() > 0) {
                lineup += fwds.subList(DRAFT_MIN_FWD, fwds.size()).stream().map(player -> player.getPlayerId().toString())
                        .collect(Collectors.joining(" "));
            }
            lineup = lineup.trim();
        }
        proposingFantasyTeam.setPlayerIdsInOrder(lineup);
        transaction.setStatus(TransactionStatus.ACCEPTED);
        return transactionRepository.save(transaction);
    }



    public Transaction acceptTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);

        transaction.setStatus(TransactionStatus.ACCEPTED);
        Set<Player> proposingPlayers = transaction.getProposingFantasyTeam().getPlayers();
        proposingPlayers.remove(transaction.getPlayerOut());
        proposingPlayers.add(transaction.getPlayerIn());

//        decline trade if not enough players in each position
        if (!approveTeam(proposingPlayers)) {
            transaction.setStatus(TransactionStatus.REJECTED);
            return transaction;
        }

        if (transaction.getReceivingFantasyTeam() != null) {
            Set<Player> receivingPlayers = transaction.getReceivingFantasyTeam().getPlayers();
            receivingPlayers.add(transaction.getPlayerOut());
            receivingPlayers.remove(transaction.getPlayerIn());

            if (!approveTeam(receivingPlayers)) {
                transaction.setStatus(TransactionStatus.REJECTED);
                return transaction;
            }
            transaction.getReceivingFantasyTeam().setPlayers(receivingPlayers);
            transaction.getReceivingFantasyTeam().setPlayerIdsInOrder(changeLineupString(transaction.getReceivingFantasyTeam().getPlayerIdsInOrder(),
                    transaction.getPlayerOut().getPlayerId().toString(), transaction.getPlayerIn().getPlayerId().toString()));
        }
        transaction.getProposingFantasyTeam().setPlayers(proposingPlayers);
        transaction.getProposingFantasyTeam().setPlayerIdsInOrder(changeLineupString(transaction.getProposingFantasyTeam().getPlayerIdsInOrder(),
                transaction.getPlayerIn().getPlayerId().toString(), transaction.getPlayerOut().getPlayerId().toString()));
        return transaction;
    }
    public Transaction rejectTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setStatus(TransactionStatus.REJECTED);
        return transaction;
    }
    public String changeLineupString(String lineup, String idIn, String idOut) {
        String[] ids = lineup.split(" ");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(idOut)) {
                ids[i] = idIn;
                break;
            }
        }
        return String.join(" ", ids);
    }

    public Boolean approveTeam(Set<Player> players) {
        int gkCount = 0;
        int defCount = 0;
        int midCount = 0;
        int fwdCount = 0;
        for (Player player : players) {
            switch (player.getPosition()) {
                case "Goalkeeper" -> gkCount++;
                case "Defender" -> defCount++;
                case "Midfielder" -> midCount++;
                case "Attacker" -> fwdCount++;
                default -> throw new Error400("Invalid position for player with id " + player.getPlayerId());
            }
        }
        return gkCount > MIN_GK && defCount > DRAFT_MIN_DEF && midCount > DRAFT_MIN_MID && fwdCount > DRAFT_MIN_FWD;
    }
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new Error404("Transaction with id " + id + " not found"));
    }

    public boolean validTransaction(Transaction transaction, FantasyTeam proposingFantasyTeam, Player ingoingPlayer, Player outgoingPlayer, boolean isSubstituted) throws Error404 {

        String position = ingoingPlayer.getPosition();
        Long fantasyTeamId = proposingFantasyTeam.getId();

        if (proposingFantasyTeam.getPlayerIdsInOrder() == null) {
            throw new Error400("Fantasy team with id " + fantasyTeamId + " does not have any players");
        }

        String[] playerIdsArray = proposingFantasyTeam.getPlayerIdsInOrder().split(" ");

        String[] benchPlayerIds = Arrays.copyOfRange(playerIdsArray, playerIdsArray.length - PLAYERS_IN_BENCH, playerIdsArray.length);

        //CASE 1: Outgoing Player is on Bench
        if (Arrays.asList(benchPlayerIds).contains(outgoingPlayer.getPlayerId().toString())) {
            return true;
        } else if (Arrays.asList(playerIdsArray).contains(outgoingPlayer.getPlayerId().toString()) && outgoingPlayer.getPosition().equals(ingoingPlayer.getPosition())) {
            return true; //CASE 2: Outgoing Player is on starting XI and has same position as incoming player
        } else { //CASE 3: Outgoing Player is on starting XI and has different position as incoming player
            if (checkExcedentPositions(proposingFantasyTeam, position)) { //check if we exceed max number of players in each position
                return true;
            } else { //Check if substitution with bench player is possible, if true then return true
                return substitutePlayer(transaction, outgoingPlayer, proposingFantasyTeam, benchPlayerIds, isSubstituted);
            }

        }
    }

    private boolean checkExcedentPositions(FantasyTeam proposingFantasyTeam, String position) {

        Long fantasyTeamId = proposingFantasyTeam.getId();

        String[] playerIdsArray = proposingFantasyTeam.getPlayerIdsInOrder().split(" ");

        List<Long> playerIdsList = Arrays.stream(playerIdsArray)
                .map(Long::parseLong)
                .toList();

        long totalDefenders = proposingFantasyTeam.getPlayers().stream()
                .filter(player -> player.getPosition().equals("Defender"))
                .count();

        long startingDefenders = playerIdsList.stream()
                .limit(PLAYERS_IN_STARTING_LINEUP)
                .map(playerService::getPlayerById)
                .filter(player -> player != null && player.getPosition().equals("Defender"))
                .count();

        long totalMidfielders = proposingFantasyTeam.getPlayers().stream()
                .filter(player -> player.getPosition().equals("Midfielder"))
                .count();

        long startingMidfielders = playerIdsList.stream()
                .limit(PLAYERS_IN_STARTING_LINEUP)
                .map(playerService::getPlayerById)
                .filter(player -> player != null && player.getPosition().equals("Midfielder"))
                .count();

        long totalAttackers = proposingFantasyTeam.getPlayers().stream()
                .filter(player -> player.getPosition().equals("Attacker"))
                .count();

        long startingAttackers = playerIdsList.stream()
                .limit(PLAYERS_IN_STARTING_LINEUP)
                .map(playerService::getPlayerById)
                .filter(player -> player != null && player.getPosition().equals("Attacker"))
                .count();


        long startingGoalkeepers = playerIdsList.stream()
                .limit(PLAYERS_IN_STARTING_LINEUP)
                .map(playerService::getPlayerById)
                .filter(player -> player != null && player.getPosition().equals("Goalkeeper"))
                .count();

        if (position.equals("Goalkeeper") && startingGoalkeepers > MIN_GK) {
            throw new Error400("Fantasy team with id " + fantasyTeamId + " cannot have more than 1 goalkeeper in the starting lineup");

        } else if (!position.equals("Goalkeeper") && startingGoalkeepers < MIN_GK) {
            throw new Error400("Fantasy team with id " + fantasyTeamId + " needs at least 1 goalkeeper in the starting lineup");

        } else if (position.equals("Defender")) {
            if (totalDefenders >= TOTAL_DEF) {
                throw new Error400("Fantasy team with id " + fantasyTeamId + " cannot have more than 9 defenders in total");
            } else if (startingDefenders >= MAX_DEF) {
                return false;
            }

        } else if (position.equals("Midfielder")) {
            if (totalMidfielders >= TOTAL_MID) {
                throw new Error400("Fantasy team with id " + fantasyTeamId + " cannot have more than 9 midfielders in total");
            } else if (startingMidfielders >= MAX_MID) {
                return false;
            }

        } else if (position.equals("Attacker")) {
            if (totalAttackers >= TOTAL_ATT) {
                throw new Error400("Fantasy team with id " + fantasyTeamId + " cannot have more than 7 attackers in total");
            } else if (startingAttackers >= MAX_ATT) {
                return false;
            }
        }
        return true;
    }

    /*
    * This method substitutes the incoming player on the first eligible bench player in the fantasy team
     */
    private boolean substitutePlayer(Transaction transaction, Player outgoingPlayer, FantasyTeam proposingFantasyTeam, String[] benchPlayerIds, boolean isSubstituted) {
        String part1;
        String part2;
        String part3;
        int indexOutgoing = proposingFantasyTeam.getPlayerIdsInOrder().indexOf(outgoingPlayer.getPlayerId().toString());
        int indexBench;


        for (String benchPlayerId : benchPlayerIds) {
            //check if we exceed max number of players per position if we were to pick that bench player
            if (checkExcedentPositions(proposingFantasyTeam, playerService.getPlayerById(Long.valueOf(benchPlayerId)).getPosition())) {

                indexBench = proposingFantasyTeam.getPlayerIdsInOrder().indexOf(benchPlayerId.toString());
                part1 = proposingFantasyTeam.getPlayerIdsInOrder().substring(0, indexOutgoing);
                part2 = proposingFantasyTeam.getPlayerIdsInOrder().substring(indexOutgoing + outgoingPlayer.getPlayerId().toString().length(), indexBench);
                part3 = proposingFantasyTeam.getPlayerIdsInOrder().substring(indexBench + benchPlayerId.toString().length());

                String newPlayersInOrder = part1 + benchPlayerId.toString() + part2 + outgoingPlayer.getPlayerId().toString() + part3;
                proposingFantasyTeam.setPlayerIdsInOrder(newPlayersInOrder);
                reOrderPlayerIDs(proposingFantasyTeam);
                isSubstituted = true;

                return isSubstituted;
            }
        }
        return false;
    }

    public void reOrderPlayerIDs(FantasyTeam fantasyTeam) {
        String[] playerIds = fantasyTeam.getPlayerIdsInOrder().split(" ");
        Map<String, String> playerPositions = new HashMap<>();
        playerPositions.put("1", "Goalkeeper");
        playerPositions.put("2", "Defender");
        playerPositions.put("3", "Midfielder");
        playerPositions.put("4", "Attacker");

        // Sort the first 11 players
        Arrays.sort(playerIds, 0, PLAYERS_IN_STARTING_LINEUP, Comparator.comparingInt(id -> getPositionOrder(playerPositions.get(id))));

        // Sort the last 4 players
        Arrays.sort(playerIds, PLAYERS_IN_STARTING_LINEUP, MAX_PLAYERS, Comparator.comparingInt(id -> getPositionOrder(playerPositions.get(id))));

        String sortedPlayerIdsInOrder = String.join(" ", playerIds);
        fantasyTeam.setPlayerIdsInOrder(sortedPlayerIdsInOrder);

    }

    private static int getPositionOrder(String position) {
        if (position == null) {
            return DEFAULT_ORDER; // Unknown position
        }
        return switch (position) {
            case "Goalkeeper" -> GK_ORDER;
            case "Defender" -> DEF_ORDER;
            case "Midfielder" -> MID_ORDER;
            case "Attacker" -> ATT_ORDER;
            default -> DEFAULT_ORDER; // Unknown position
        };
    }
}
