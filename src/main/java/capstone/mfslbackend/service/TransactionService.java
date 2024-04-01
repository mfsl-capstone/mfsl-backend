package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.TransactionStatus;
import capstone.mfslbackend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final int MAX_PLAYERS = 15;
    private static final int PLAYERS_IN_STARTING_LINEUP = 11;
    private static final int MIN_GK = 1;
    private static final int DRAFT_MIN_DEF = 4;
    private static final int MIN_DEF = 3;
    private static final int MAX_DEF = 5;
    private static final int DRAFT_MIN_MID = 4;
    private static final int MIN_MID = 3;
    private static final int MAX_MID = 5;
    private static final int DRAFT_MIN_FWD = 2;
    private static final int MIN_FWD = 1;
    private static final int MAX_FWD = 3;
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

        transaction.setDate(LocalDateTime.now());

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
        transaction.setPlayerIn(playerIn);

        if (transaction.getReceivingFantasyTeam() != null) {
            transaction.setStatus(TransactionStatus.PROPOSED);
            return transactionRepository.save(transaction);
        }

        //it means it is a free agent
        if (substitutePlayer(playerIn, outgoingPlayer, proposingFantasyTeam)) {
            transaction.setStatus(TransactionStatus.ACCEPTED);
            return transactionRepository.save(transaction);
        } else {
            transaction.setStatus(TransactionStatus.REJECTED);
            transactionRepository.save(transaction);
            throw new Error400("Fantasy team with id " + fantasyTeamId + " was unable to swap player: " + outgoingPlayerId + " with player: " + incomingPlayerId + " due to lineup restrictions");
        }
    }

    public Transaction draftTransaction(Long fantasyTeamId, Long incomingPlayerId) {
        Transaction transaction = new Transaction();

        transaction.setDate(LocalDateTime.now());

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

        if (transaction.getStatus() != TransactionStatus.PROPOSED) {
            throw new Error400("Transaction with id " + transactionId + " is not in proposed state");
        }
        FantasyTeam proposingFantasyTeam = transaction.getProposingFantasyTeam();
        FantasyTeam receivingFantasyTeam = transaction.getReceivingFantasyTeam();
        FantasyTeam proposingFantasyTeamCopy = new FantasyTeam(proposingFantasyTeam);
        FantasyTeam receivingFantasyTeamCopy = new FantasyTeam(receivingFantasyTeam);

        if (substitutePlayer(transaction.getPlayerIn(), transaction.getPlayerOut(), proposingFantasyTeamCopy)
                && substitutePlayer(transaction.getPlayerOut(), transaction.getPlayerIn(), receivingFantasyTeamCopy)) {
            proposingFantasyTeam.setPlayers(proposingFantasyTeamCopy.getPlayers());
            proposingFantasyTeam.setPlayerIdsInOrder(proposingFantasyTeamCopy.getPlayerIdsInOrder());
            receivingFantasyTeam.setPlayers(receivingFantasyTeamCopy.getPlayers());
            receivingFantasyTeam.setPlayerIdsInOrder(receivingFantasyTeamCopy.getPlayerIdsInOrder());
            transaction.setStatus(TransactionStatus.ACCEPTED);
        } else {
            transaction.setStatus(TransactionStatus.REJECTED);
        }
        return transactionRepository.save(transaction);
    }
    public Transaction rejectTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setHasBeenNotified(false);
        return transactionRepository.save(transaction);
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

    /*
     * This method substitutes the incoming player on the first eligible bench player in the fantasy team
     */
    private Boolean substitutePlayer(Player playerIn, Player playerOut, FantasyTeam proposingFantasyTeam) {
//        we can assume playerIn and playerOut have different positions
//        check if lineup is valid if we were to substitute playerOut with playerIn
        String[] newLineup = proposingFantasyTeam.getPlayerIdsInOrder().split(" ");
        List<Player> players = new ArrayList<>();
        for (String playerId : newLineup) {
            players.add(playerService.getPlayerById(Long.parseLong(playerId)));
        }

        int index = players.indexOf(playerOut);
        players.add(index, playerIn);
        players.remove(playerOut);

        if (index >= PLAYERS_IN_STARTING_LINEUP || playerIn.getPosition().equals(playerOut.getPosition())) {
            proposingFantasyTeam.setPlayers(new HashSet<>(players));
            proposingFantasyTeam.setPlayerIdsInOrder(players.stream().map(player -> player.getPlayerId().toString()).collect(Collectors.joining(" ")));
            return true;
        }

        List<Player> sortedStartingLineup = players.subList(0, PLAYERS_IN_STARTING_LINEUP);
        sortedStartingLineup.sort(Comparator.comparingInt(p -> getPositionOrder(p.getPosition())));
        sortedStartingLineup.addAll(players.subList(PLAYERS_IN_STARTING_LINEUP, MAX_PLAYERS));
        if (validStartingXI(sortedStartingLineup)) {
            proposingFantasyTeam.setPlayers(new HashSet<>(sortedStartingLineup));
            proposingFantasyTeam.setPlayerIdsInOrder(sortedStartingLineup.stream().map(player -> player.getPlayerId().toString()).collect(Collectors.joining(" ")));
            return true;
        }

        List<Player> playersCopy = new ArrayList<>(players);
        for (Player benchPlayer: playersCopy.subList(PLAYERS_IN_STARTING_LINEUP, MAX_PLAYERS)) {
            sortedStartingLineup = new ArrayList<>(playersCopy.subList(0, PLAYERS_IN_STARTING_LINEUP));
            index = sortedStartingLineup.indexOf(playerIn);
            sortedStartingLineup.add(index, benchPlayer);
            sortedStartingLineup.remove(playerIn);
            sortedStartingLineup.sort(Comparator.comparingInt(p -> getPositionOrder(p.getPosition())));

            List<Player> bench = new ArrayList<>(playersCopy.subList(PLAYERS_IN_STARTING_LINEUP, MAX_PLAYERS));
            index = bench.indexOf(benchPlayer);
            bench.add(index, playerIn);
            bench.remove(benchPlayer);

            sortedStartingLineup.addAll(bench);

            if (validStartingXI(sortedStartingLineup)) {
                proposingFantasyTeam.setPlayers(new HashSet<>(sortedStartingLineup));
                proposingFantasyTeam.setPlayerIdsInOrder(sortedStartingLineup.stream().map(player -> player.getPlayerId().toString()).collect(Collectors.joining(" ")));
                return true;
            }
        }
        return false;
    }

    public Boolean validStartingXI(List<Player> players) {
        int gkCount = 0;
        int defCount = 0;
        int midCount = 0;
        int fwdCount = 0;
        for (Player player : players.subList(0, PLAYERS_IN_STARTING_LINEUP)) {
            switch (player.getPosition()) {
                case "Goalkeeper" -> gkCount++;
                case "Defender" -> defCount++;
                case "Midfielder" -> midCount++;
                case "Attacker" -> fwdCount++;
                default -> throw new Error400("Invalid position for player with id " + player.getPlayerId());
            }
        }
        return gkCount == MIN_GK && defCount >= MIN_DEF && defCount <= MAX_DEF && midCount >= MIN_MID && midCount <= MAX_MID && fwdCount >= MIN_FWD && fwdCount <= MAX_FWD;
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


    public List<Player> isValid(Long fantasyTeamId, Long incomingPlayerId) {
        FantasyTeam fantasyTeam = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        FantasyTeam fantasyTeamCopy;
        List<Player> validPlayers = new ArrayList<>();
        for (Player player: fantasyTeam.getPlayers()) {
            fantasyTeamCopy = new FantasyTeam(fantasyTeam);
            if (substitutePlayer(playerService.getPlayerById(incomingPlayerId), player, fantasyTeamCopy)) {
                validPlayers.add(player);
            }
        }

        return validPlayers;
    }

}
