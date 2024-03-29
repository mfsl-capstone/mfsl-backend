package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Draft;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.enums.DraftStatus;
import capstone.mfslbackend.repository.DraftRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class DraftService {
    private final DraftRepository draftRepository;
    private final TransactionService transactionService;
    private final PlayerService playerService;
    private final FantasyLeagueService fantasyLeagueService;
    public DraftService(DraftRepository draftRepository, TransactionService transactionService, PlayerService playerService, FantasyLeagueService fantasyLeagueService) {
        this.draftRepository = draftRepository;
        this.transactionService = transactionService;
        this.playerService = playerService;
        this.fantasyLeagueService = fantasyLeagueService;
    }
    public Draft getDraft(long fantasyLeagueId) {

        Draft d = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId).getDraft();

        if (d.getStatus().equals(DraftStatus.NOT_STARTED) && LocalDateTime.now().isAfter(d.getDraftDate())) {
            d.setStatus(DraftStatus.IN_PROGRESS);
            d.setFantasyTeam(d.getFantasyLeague().getFantasyTeams().stream()
                    .filter(fantasyTeam -> fantasyTeam.getOrderNumber() == 1)
                    .findFirst().orElseThrow(() -> new Error400("No fantasy team with order 1 set")));
            System.out.println(d.getFantasyTeam().getTeamName());
            d.setTimePlayerStarted(LocalDateTime.now());
            d.setDirection("asc");
            d.setRound(1);
        }

        if (d.getTimePlayerStarted().plusSeconds(30).isBefore(LocalDateTime.now())) {
            Transaction t = null;
            while (t == null) {
                try {
                    t = draftPlayer(fantasyLeagueId, d.getFantasyTeam().getId(), playerService.getRandomPlayer().getPlayerId());
                } catch (Exception e) {
                    t = null;
                }
            }
        }

        return d;
    }

    public Transaction draftPlayer(long fantasyLeagueId, long fantasyTeamId, long playerId) {
        Draft d = getDraft(fantasyLeagueId);
        Set<Transaction> transactions = d.getTransactions();

        if (d.getFantasyTeam().getId() != fantasyTeamId) {
            throw new Error400("It is not your turn to draft");
        }
        System.out.println(d.getStatus());
        if (!d.getStatus().equals(DraftStatus.IN_PROGRESS)) {
            throw new Error400("Draft is not in progress");
        }
        Transaction t = transactionService.draftTransaction(fantasyTeamId, playerId);
        transactions.add(t);
        d.setTransactions(transactions);

        if (d.getDirection().equals("asc")) {
            d.setFantasyTeam(d.getFantasyLeague().getFantasyTeams().stream()
                    .filter(fantasyTeam -> fantasyTeam.getOrderNumber() == d.getFantasyTeam().getOrderNumber() + 1)
                    .findFirst()
                    .orElseGet(() -> {
                        d.setDirection("desc");
                        d.setRound(d.getRound() + 1);
                        return d.getFantasyTeam();
                    })
            );
        } else {
            d.setFantasyTeam(d.getFantasyLeague().getFantasyTeams().stream()
                    .filter(fantasyTeam -> fantasyTeam.getOrderNumber() == d.getFantasyTeam().getOrderNumber() - 1)
                    .findFirst()
                    .orElseGet(() -> {
                        d.setDirection("asc");
                        d.setRound(d.getRound() + 1);
                        return d.getFantasyTeam();
                    })
            );
        }

        if (d.getRound() == 16) {
            d.setStatus(DraftStatus.COMPLETED);
        }
        d.setTimePlayerStarted(LocalDateTime.now());
        return t;
    }

}
