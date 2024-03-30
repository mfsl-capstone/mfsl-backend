package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.model.Draft;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.enums.DraftStatus;
import capstone.mfslbackend.repository.DraftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class DraftService {
    private final DraftRepository draftRepository;
    private final TransactionService transactionService;
    private final PlayerService playerService;
    private final FantasyLeagueService fantasyLeagueService;
    private static final int DRAFT_TURN_TIME = 30;
    private static final int MAX_ROUNDS = 15;
    public DraftService(DraftRepository draftRepository, TransactionService transactionService, PlayerService playerService, FantasyLeagueService fantasyLeagueService) {
        this.draftRepository = draftRepository;
        this.transactionService = transactionService;
        this.playerService = playerService;
        this.fantasyLeagueService = fantasyLeagueService;
    }
    public Draft getDraft(long fantasyLeagueId) {
        FantasyLeague fantasyLeague = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        Draft d = fantasyLeague.getDraft();

        if (d.getStatus().equals(DraftStatus.NOT_STARTED) && LocalDateTime.now().isAfter(d.getDraftDate())) {
            d.setStatus(DraftStatus.IN_PROGRESS);
            List<FantasyTeam> fantasyTeams = new ArrayList(fantasyLeague.getFantasyTeams());
            int i = 1;
            Random r = new Random();
            while (fantasyTeams.size() > 0) {
                int index = r.nextInt(1, fantasyTeams.size());
                FantasyTeam ft = fantasyTeams.remove(index);
                ft.setOrderNumber(i);
                i++;
            }
            d.setFantasyTeam(d.getFantasyLeague().getFantasyTeams().stream()
                    .filter(fantasyTeam -> fantasyTeam.getOrderNumber() == 1)
                    .findFirst().orElseThrow(() -> new Error400("No fantasy team with order 1 set")));
            d.setTimePlayerStarted(LocalDateTime.now());
            d.setDirection("asc");
            d.setRound(1);
        }
        if (d.getTimePlayerStarted().plusSeconds(DRAFT_TURN_TIME).isBefore(LocalDateTime.now())) {
            Transaction t = null;
            while (t == null) {
                try {
                    t = draftPlayer(fantasyLeagueId, d.getFantasyTeam().getId(), playerService.getRandomPlayer().getPlayerId());
                } catch (Exception e) {
                    t = null;
                }
            }
        }
        return draftRepository.save(d);
    }

    public Transaction draftPlayer(long fantasyLeagueId, long fantasyTeamId, long playerId) {
        Draft d = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId).getDraft();
        Set<Transaction> transactions;
        if (d.getTransactions() != null) {
            transactions = d.getTransactions();
        } else {
            transactions = new HashSet<>();
        }


        if (!d.getStatus().equals(DraftStatus.IN_PROGRESS)) {
            throw new Error400("Draft is not in progress");
        }
        if (d.getFantasyTeam().getId() != fantasyTeamId) {
            throw new Error400("It is not your turn to draft");
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

        d.setTimePlayerStarted(LocalDateTime.now());
        draftRepository.save(d);

        if (d.getRound() > MAX_ROUNDS) {
            d.setStatus(DraftStatus.COMPLETED);
            fantasyLeagueService.createFantasyLeagueSchedule(fantasyLeagueId);
        }

        return t;
    }

}
