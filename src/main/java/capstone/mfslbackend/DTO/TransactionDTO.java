package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.model.TransactionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private LocalDateTime date;
    private TransactionStatus status;
    private boolean notified;
    private FantasyTeamWithNoTransactionsNoLeagueDTO proposingFantasyTeam;
    private PlayerWithNoStatsDTO playerIn;
    private PlayerWithNoStatsDTO playerOut;
    private FantasyTeamWithNoTransactionsNoLeagueDTO receivingFantasyTeam;
    public TransactionDTO from(Transaction transaction) {
        this.date = transaction.getDate();
        this.status = transaction.getStatus();
        this.notified = transaction.isNotified();
        this.proposingFantasyTeam = new FantasyTeamWithNoTransactionsNoLeagueDTO().from(transaction.getProposingFantasyTeam());
        this.playerIn = new PlayerWithNoStatsDTO().from(transaction.getPlayerIn());
        this.playerOut = new PlayerWithNoStatsDTO().from(transaction.getPlayerOut());
        this.receivingFantasyTeam = new FantasyTeamWithNoTransactionsNoLeagueDTO().from(transaction.getReceivingFantasyTeam());
        return this;
    }
}
