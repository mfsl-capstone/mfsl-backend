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

@Service
public class TransactionService {
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
    public Transaction createTransaction(Long fantasyTeamId, Long incomingPlayerId, Long outgoingPlayerId) throws Error404 {
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
        transaction.setPlayerIn(playerIn);

        transaction.setStatus(TransactionStatus.PROPOSED);

        return transactionRepository.save(transaction);
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

//        check if fantasy team has enough players in each position
        Set<Player> players = proposingFantasyTeam.getPlayers();

        int missingGkCount = 1;
        int missingDefCount = 4;
        int missingMidCount = 4;
        int missingFwdCount = 2;
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
            }
        }
        int missingPlayerCount = missingGkCount + missingDefCount + missingMidCount + missingFwdCount;

        if (missingPlayerCount > (15 - players.size())) {
            String missingPositions = "";
            if (missingGkCount > 0) {
                missingPositions += "Goalkeeper ";
            }
            if (missingDefCount > 0) {
                missingPositions += "Defender ";
            }
            if (missingMidCount > 0) {
                missingPositions += "Midfielder ";
            }
            if (missingFwdCount > 0) {
                missingPositions += "Attacker ";
            }
            throw new Error400("Fantasy team with id " + fantasyTeamId + " does not have enough players in the following positions: " + missingPositions);
        }

//        add player to the team immediately
        players.add(playerIn);
        proposingFantasyTeam.setPlayers(players);
        String lineup = proposingFantasyTeam.getPlayerIdsInOrder();
        if (lineup != null && !lineup.isEmpty()) {
            lineup += " ";
        }
        lineup += playerIn.getPlayerId();
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
        if (!approveTeam(proposingPlayers, transaction.getProposingFantasyTeam().getId())) {
            transaction.setStatus(TransactionStatus.REJECTED);
            return transaction;
        }

        if (transaction.getReceivingFantasyTeam() != null) {
            Set<Player> receivingPlayers = transaction.getReceivingFantasyTeam().getPlayers();
            receivingPlayers.add(transaction.getPlayerOut());
            receivingPlayers.remove(transaction.getPlayerIn());

            if (!approveTeam(receivingPlayers, transaction.getReceivingFantasyTeam().getId())) {
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

    public Boolean approveTeam(Set<Player> players, Long teamId) {
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
            }
        }
        return gkCount > 1 && defCount > 4 && midCount > 4 && fwdCount > 2;
    }
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new Error404("Transaction with id " + id + " not found"));
    }
}
