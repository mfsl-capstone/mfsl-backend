package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.FantasyTeamLineup;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class FantasyTeamService {

    private final FantasyTeamRepository fantasyTeamRepository;
    public FantasyTeamService(FantasyTeamRepository fantasyTeamRepository) {
        this.fantasyTeamRepository = fantasyTeamRepository;
    }
    public Optional<FantasyTeam> getFantasyTeam(Long teamId) {
        Optional<FantasyTeam> fantasyTeam = fantasyTeamRepository.findById(teamId);
        if (fantasyTeam.isEmpty()) {
            log.warn("could not find team with id {}", teamId);
        }
        return fantasyTeam;
    }

    public FantasyTeam createFantasyTeam(String teamName) {
        if (StringUtils.isEmpty(teamName)) {
            log.warn("team name cannot be empty");
        }
        FantasyTeam fantasyTeam = new FantasyTeam();
        fantasyTeam.setTeamName(teamName);
        fantasyTeamRepository.save(fantasyTeam);
        return fantasyTeam;
    }

    public FantasyTeamLineup getFantasyTeamLineup(Long fantasyTeamId) {
        Optional<FantasyTeam> fantasyTeam = getFantasyTeam(fantasyTeamId);
        return fantasyTeam.map(team -> new FantasyTeamLineup(team.getPlayers(), team.getPlayerIdsInOrder())).orElse(null);
    }

    public FantasyTeamLineup setFantasyTeamLineup(Long fantasyTeamId, String lineup) {
        Optional<FantasyTeam> fantasyTeam = getFantasyTeam(fantasyTeamId);
        if (fantasyTeam.isEmpty()) {
            return null;
        }
        FantasyTeam team = fantasyTeam.get();
        String[] playerIds = lineup.split(" ");


        if (getPlayer(playerIds[0], team.getPlayers()) == null ||
                !getPlayer(playerIds[0], team.getPlayers()).getPosition().equals("Goalkeeper")) {
            return null;
        }
        int midCount = 0;
        for (int i = 1; i < 11; i++) {
            Player p0 = getPlayer(playerIds[i-1], team.getPlayers());
            Player p1 = getPlayer(playerIds[i], team.getPlayers());
            if (p1 == null || p0 == null) {
                return null;
            }
            switch (p1.getPosition()) {
                case "Goalkeeper":
                    return null;
                case "Defender":
                    if (!p0.getPosition().equals("Goalkeeper") && !p0.getPosition().equals("Defender")) {
                        return null;
                    }
                    if (i > 5) {
                        return null;
                    }
                    break;
                case "Midfielder":
                    midCount++;
                    if (!p0.getPosition().equals("Defender") && !p0.getPosition().equals("Midfielder")) {
                        return null;
                    }
                    if (i < 4 || i > 9) {
                        return null;
                    }
                    break;
                case "Attacker":
                    if (!p0.getPosition().equals("Midfielder") && !p0.getPosition().equals("Attacker")) {
                        return null;
                    }
                    if (i < 7) {
                        return null;
                    }
                    break;
            }
        }
        if (midCount > 5) {
            return null;
        }

        team.setPlayerIdsInOrder(lineup);
        fantasyTeamRepository.save(team);
        return new FantasyTeamLineup(team.getPlayers(), lineup);
    }

    public Player getPlayer(String playerId, Set<Player> players) {
        for (Player player : players) {
            if (player.getPlayerId().equals(Long.parseLong(playerId))) {
                return player;
            }
        }
        return null;
    }
}
