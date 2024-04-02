package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.FantasyTeamLineup;
import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FantasyTeamService {
    private static final int STARTING_XI_PLAYERS = 11;
    private static final int PLAYERS = 15;
    private static final int MIN_DEF = 3;
    private static final int MAX_DEF = 5;
    private static final int MIN_MID = 2;
    private static final int MAX_MID = 5;
    private static final int MIN_ATT = 1;
    private static final int MAX_ATT = 4;
    private final FantasyTeamRepository fantasyTeamRepository;
    public FantasyTeamService(FantasyTeamRepository fantasyTeamRepository) {
        this.fantasyTeamRepository = fantasyTeamRepository;
    }
    public FantasyTeam getFantasyTeam(Long teamId) throws Error404 {
        return fantasyTeamRepository.findById(teamId)
                .orElseThrow(() -> new Error404("Fantasy Team with id " + teamId + " not found"));
    }

    public FantasyTeamLineup getFantasyTeamLineup(Long fantasyTeamId) throws Error404 {
        FantasyTeam fantasyTeam = getFantasyTeam(fantasyTeamId);
        if (CollectionUtils.isEmpty(fantasyTeam.getPlayers())) {
            return null;
        }
        return new FantasyTeamLineup(fantasyTeam.getPlayers(), fantasyTeam.getPlayerIdsInOrder());
    }

    public FantasyTeamLineup setFantasyTeamLineup(Long fantasyTeamId, String lineup) throws Error404, Error400 {
        FantasyTeam team = getFantasyTeam(fantasyTeamId);

        String[] playerIds = lineup.split(" ");

        if (playerIds.length != PLAYERS) {
            throw new Error400("Lineup must have 15 players");
        }

        if (getPlayer(playerIds[0], team.getPlayers()) == null
                || !getPlayer(playerIds[0], team.getPlayers()).getPosition().equals("Goalkeeper")) {
            throw new Error400("First player must be a goalkeeper");
        }
        int defCount = 0;
        int midCount = 0;
        int attCount = 0;
        for (int i = 1; i < STARTING_XI_PLAYERS; i++) {
            Player p0 = getPlayer(playerIds[i - 1], team.getPlayers());
            Player p1 = getPlayer(playerIds[i], team.getPlayers());
            if (p1 == null || p0 == null) {
                throw new Error400("Lineup must have more than 11 players");
            }
            switch (p1.getPosition()) {
                case "Goalkeeper" -> throw new Error400("Goalkeeper cannot be in any position other than the first");
                case "Defender" -> {
                    defCount++;
                    if (!p0.getPosition().equals("Goalkeeper") && !p0.getPosition().equals("Defender")) {
                        throw new Error400("Defender cannot be in any position other than the first or after another defender");
                    }
                }
                case "Midfielder" -> {
                    midCount++;
                    if (!p0.getPosition().equals("Defender") && !p0.getPosition().equals("Midfielder")) {
                        throw new Error400("Midfielder cannot be in any position other than after a defender or another midfielder");
                    }
                }
                case "Attacker" -> {
                    attCount++;
                    if (!p0.getPosition().equals("Midfielder") && !p0.getPosition().equals("Attacker")) {
                        throw new Error400("Attacker cannot be in any position other than after a midfielder or another attacker");
                    }
                }
                default -> throw new Error400("Invalid position for player with id " + p1.getPlayerId());
            }
        }
        if (defCount > MAX_DEF || defCount < MIN_DEF) {
            throw new Error400("There can only be " + MIN_DEF + "-" + MAX_DEF + " defenders in a lineup");
        }
        if (midCount > MAX_MID || midCount < MIN_MID) {
            throw new Error400("There can only be " + MIN_MID + "-" + MAX_MID + " midfielders in a lineup");
        }
        if (attCount > MAX_ATT || attCount < MIN_ATT) {
            throw new Error400("There can only be " + MIN_ATT + "-" + MAX_ATT + " attackers in a lineup");
        }

        team.setPlayerIdsInOrder(lineup);
        fantasyTeamRepository.save(team);
        return new FantasyTeamLineup(team.getPlayers(), lineup);
    }

    public Player getPlayer(String playerId, Set<Player> players) throws Error404 {
        for (Player player : players) {
            if (player.getPlayerId().equals(Long.parseLong(playerId))) {
                return player;
            }
        }
        throw new Error404("Player with id " + playerId + " from lineup not found in the team");
    }


}
