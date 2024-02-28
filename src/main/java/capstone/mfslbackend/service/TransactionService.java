package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.TransactionStatus;
import capstone.mfslbackend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
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
    public Transaction createTransaction(Long fantasyTeamId, Long incomingPlayerId, Long outgoingPlayerId) {
        Transaction transaction = new Transaction();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        transaction.setDate(LocalDateTime.now());

        Optional<FantasyTeam> proposingFantasyTeamOptional = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        FantasyTeam proposingFantasyTeam;
        if (proposingFantasyTeamOptional.isPresent()) {
            proposingFantasyTeam = proposingFantasyTeamOptional.get();
            transaction.setProposingFantasyTeam(proposingFantasyTeam);
        } else {
            log.error("Fantasy team with id {} not found", fantasyTeamId);
            return null;
        }


        Optional<Player> playerOut = proposingFantasyTeam.getPlayers().stream().filter(player -> player.getPlayerId().equals(outgoingPlayerId)).findFirst();
        if (playerOut.isPresent()) {
            transaction.setPlayerOut(playerOut.get());
        } else {
            log.error("Player with id {} not found", outgoingPlayerId);
            return null;
        }

        Optional<FantasyTeam> takenTeamOptional = fantasyLeagueService.getFantasyTeamOfTakenPlayer(proposingFantasyTeam.getFantasyLeague().getId(), incomingPlayerId);
        FantasyTeam takenTeam;
        if (takenTeamOptional.isPresent()) {
            takenTeam = takenTeamOptional.get();
            transaction.setReceivingFantasyTeam(takenTeam);
        }

        Optional<Player> playerIn = playerService.getPlayerById(incomingPlayerId);
        if (playerIn.isPresent()) {
            transaction.setPlayerIn(playerIn.get());
        } else {
            log.error("Player with id {} not found", incomingPlayerId);
            return null;
        }

        transaction.setStatus(TransactionStatus.PROPOSED);

        return transactionRepository.save(transaction);
    }

    public Transaction acceptTransaction(Long transactionId) {
        Optional<Transaction> transactionOptional = getTransactionById(transactionId);
        if (transactionOptional.isEmpty()) {
            log.error("Transaction with id {} not found", transactionId);
            return null;
        }
        Transaction transaction = transactionOptional.get();
        transaction.setStatus(TransactionStatus.ACCEPTED);
        Set<Player> proposingPlayers = transaction.getProposingFantasyTeam().getPlayers();
        proposingPlayers.remove(transaction.getPlayerOut());
        proposingPlayers.add(transaction.getPlayerIn());
        transaction.getProposingFantasyTeam().setPlayers(proposingPlayers);

        if (transaction.getReceivingFantasyTeam() != null) {
            Set<Player> receivingPlayers = transaction.getReceivingFantasyTeam().getPlayers();
            receivingPlayers.add(transaction.getPlayerOut());
            receivingPlayers.remove(transaction.getPlayerIn());
            transaction.getReceivingFantasyTeam().setPlayers(receivingPlayers);
        }
        return transactionRepository.save(transaction);
    }
    public Transaction rejectTransaction(Long transactionId) {
        Optional<Transaction> transactionOptional = getTransactionById(transactionId);
        if (transactionOptional.isEmpty()) {
            log.error("Transaction with id {} not found", transactionId);
            return null;
        }
        Transaction transaction = transactionOptional.get();
        transaction.setStatus(TransactionStatus.REJECTED);
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
