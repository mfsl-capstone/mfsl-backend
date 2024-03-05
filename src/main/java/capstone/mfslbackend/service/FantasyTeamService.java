package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.FantasyTeamLineup;
import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FantasyTeamService {

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
        return new FantasyTeamLineup(fantasyTeam.getPlayers(), fantasyTeam.getPlayerIdsInOrder());
    }

    public FantasyTeamLineup setFantasyTeamLineup(Long fantasyTeamId, String lineup) throws Error404, Error400 {
        FantasyTeam team = getFantasyTeam(fantasyTeamId);

        String[] playerIds = lineup.split(" ");

        if (getPlayer(playerIds[0], team.getPlayers()) == null ||
                !getPlayer(playerIds[0], team.getPlayers()).getPosition().equals("Goalkeeper")) {
            throw new Error400("First player must be a goalkeeper");
        }
        int midCount = 0;
        for (int i = 1; i < 11; i++) {
            Player p0 = getPlayer(playerIds[i-1], team.getPlayers());
            Player p1 = getPlayer(playerIds[i], team.getPlayers());
            if (p1 == null || p0 == null) {
                throw new Error400("Lineup must have more than 11 players");
            }
            switch (p1.getPosition()) {
                case "Goalkeeper" -> throw new Error400("Goalkeeper cannot be in any position other than the first");
                case "Defender" -> {
                    if (!p0.getPosition().equals("Goalkeeper") && !p0.getPosition().equals("Defender")) {
                        throw new Error400("Defender cannot be in any position other than the first or after another defender");
                    }
                    if (i > 5) {
                        throw new Error400("There can only be 5 defenders in a lineup");
                    }
                }
                case "Midfielder" -> {
                    midCount++;
                    if (!p0.getPosition().equals("Defender") && !p0.getPosition().equals("Midfielder")) {
                        throw new Error400("Midfielder cannot be in any position other than after a defender or another midfielder");
                    }
                    if (i < 4 || i > 9) {
                        throw new Error400("There can only be 3-5 midfielders in a lineup");
                    }
                }
                case "Attacker" -> {
                    if (!p0.getPosition().equals("Midfielder") && !p0.getPosition().equals("Attacker")) {
                        throw new Error400("Attacker cannot be in any position other than after a midfielder or another attacker");
                    }
                    if (i < 7) {
                        throw new Error400("There can only be 1-4 attackers in a lineup");
                    }
                }
            }
        }
        if (midCount > 5) {
            throw new Error400("There can only be 3-5 midfielders in a lineup");
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
