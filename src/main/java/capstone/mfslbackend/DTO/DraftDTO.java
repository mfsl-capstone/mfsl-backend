package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Draft;
import capstone.mfslbackend.model.enums.DraftStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
public class DraftDTO {
    private LocalDateTime draftDate;
    private LocalDateTime timePlayerStarted;
    private int round;
    private String direction;
    private DraftStatus status;
    private FantasyLeagueDTO fantasyLeague;
    private FantasyTeamWithNoTransactionsNoLeagueDTO fantasyTeam;
    private List<TransactionDTO> transactions;
    public DraftDTO from(Draft draft) {
        this.draftDate = draft.getDraftDate();
        this.timePlayerStarted = draft.getTimePlayerStarted();
        this.round = draft.getRound();
        this.direction = draft.getDirection();
        this.status = draft.getStatus();
        this.fantasyLeague = new FantasyLeagueDTO().from(draft.getFantasyLeague());
        this.fantasyTeam = new FantasyTeamWithNoTransactionsNoLeagueDTO().from(draft.getFantasyTeam());
        this.transactions = draft.getTransactions().stream()
                .map(t -> new TransactionDTO().from(t))
                .sorted(Comparator.comparing(TransactionDTO::getDate))
                .toList();
        return this;
    }
}
