package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.FantasyLeaguePlayer;
import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import capstone.mfslbackend.repository.FantasyWeekRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final UserService userService;
    private final PlayerService playerService;
    private final FantasyTeamRepository fantasyTeamRepository;

    private final FantasyTeamService fantasyTeamService;


    private final FantasyWeekRepository fantasyWeekRepository;

    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository, UserService userService,
                                FantasyTeamRepository fantasyTeamRepository, FantasyTeamService fantasyTeamService, PlayerService playerService, FantasyWeekRepository fantasyWeekRepository) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.userService = userService;
        this.fantasyTeamService = fantasyTeamService;
        this.playerService = playerService;
        this.fantasyTeamRepository = fantasyTeamRepository;
        this.fantasyWeekRepository = fantasyWeekRepository;
    }

    public FantasyLeague createFantasyLeague(String leagueName) {
        FantasyLeague fantasyLeague = new FantasyLeague();
        fantasyLeague.setLeagueName(leagueName);
        return fantasyLeagueRepository.save(fantasyLeague);
    }

    public FantasyLeague getFantasyLeagueById(Long fantasyLeagueId) {
        return fantasyLeagueRepository.findById(fantasyLeagueId)
                .orElseThrow(() -> new Error404("Fantasy League with id " + fantasyLeagueId + " not found"));
    }

    public List<FantasyLeague> getFantasyLeagueByName(String fantasyLeagueName) {
        String name = "%" + fantasyLeagueName + "%";
        return fantasyLeagueRepository.findFantasyLeagueByLeagueNameLikeIgnoreCase(name);
    }

    public FantasyLeague joinFantasyLeague(String username, Long leagueId, String teamName) {
        User user = userService.getUser(username);
        FantasyLeague league = getFantasyLeagueById(leagueId);
        FantasyTeam fantasyTeam = new FantasyTeam();
        fantasyTeam.setTeamName(teamName);

//        block users from registering two teams in a league
        for (FantasyTeam team : league.getFantasyTeams()) {
            if (team.getUser().equals(user)) {
                throw new Error400("User already has a team in this league");
            }
        }

        fantasyTeam.setUser(user);
        fantasyTeam.setFantasyLeague(league);
        fantasyTeamRepository.save(fantasyTeam);

//        we can assume league is present since we already got it with the same id
        return fantasyLeagueRepository.findById(leagueId).get();
    }

    public List<Player> getTakenPlayersByFantasyLeagueId(Long fantasyLeagueId) {
        FantasyLeague fantasyLeague = getFantasyLeagueById(fantasyLeagueId);

        List<Player> players = new ArrayList<>();
        fantasyLeague.getFantasyTeams().forEach(fantasyTeam -> players.addAll(fantasyTeam.getPlayers()));
        return players;
    }

    public Optional<FantasyTeam> getFantasyTeamOfTakenPlayer(Long fantasyLeagueId, Long playerId) {
        FantasyLeague fantasyLeague = getFantasyLeagueById(fantasyLeagueId);

        return fantasyLeague.getFantasyTeams().stream()
                .filter(fantasyTeam -> fantasyTeam.getPlayers().stream()
                        .anyMatch(player -> player.getPlayerId().equals(playerId)))
                .findFirst();
    }

    public List<FantasyLeaguePlayer> getFantasyLeaguePlayers(Long leagueId, List<Map<String, String>> filters, String sortDirection, String sortField, Boolean noTaken, int limit, int offset) {

        List<Player> players = getTakenPlayersByFantasyLeagueId(leagueId);

        return playerService.getPlayers(players, filters, sortDirection, sortField, noTaken, limit, offset).stream()
                .map(player -> new FantasyLeaguePlayer(player, players.contains(player)))
                .toList();
    }


    public List<FantasyWeek> createFantasyLeagueSchedule(Long leagueId) {
        List<FantasyTeam> teams = fantasyTeamService.getFantasyTeamsByLeagueId(leagueId);
        int numTeams = teams.size();
        boolean hasGhostTeam = false;

        //schedule builder

        if (numTeams % 2 != 0) {
            FantasyTeam ghostTeam = new FantasyTeam();
            ghostTeam.setId(-1L);
            teams.add(ghostTeam);
            numTeams++;
            hasGhostTeam = true;
        }

        List<List<Long>> schedule = new ArrayList<>();
        List<Long> matches = new ArrayList<>();
        // Each week
        for (int week = 0; week < numTeams - 1; week++) {

            // Each match in a week
            for (int match = 0; match < numTeams / 2; match++) {
                int homeTeam = (week + match) % (numTeams - 1);
                int awayTeam = (numTeams - 1 - match + week) % (numTeams - 1);

                if (match == 0) {
                    awayTeam = numTeams - 1;
                }

                Long homeTeamId = teams.get(homeTeam).getId();
                Long awayTeamId = teams.get(awayTeam).getId();


                // Exclude matches involving the ghost team
                if (!hasGhostTeam || (homeTeamId != -1L && awayTeamId != -1L)) {
                    matches.add(homeTeamId);
                    matches.add(awayTeamId);
                }
            }
            schedule.add(matches);

        }

        // Create the weeks
            for (int w = 0; w < schedule.size(); w++) {
                List<Long> games = schedule.get(w);
                FantasyWeek fantasyWeek = new FantasyWeek();
                fantasyWeek.setWeekNumber(w + 1);

                // Iterate through each match in the week
                for (int matchIndex = 0; matchIndex < matches.size(); matchIndex += 2) {
                    Long homeTeamId = matches.get(matchIndex);
                    Long awayTeamId = matches.get(matchIndex + 1);

                    FantasyTeam teamA = fantasyTeamRepository.findFantasyTeamById(homeTeamId);
                    FantasyTeam teamB = fantasyTeamRepository.findFantasyTeamById(awayTeamId);

                    Set<FantasyWeek> weeksA = teamA.getFantasyWeeksA();
                    Set<FantasyWeek> weeksB = teamB.getFantasyWeeksB();

                    fantasyWeek.setFantasyTeamA(teamA);
                    fantasyWeek.setFantasyTeamB(teamB);

                    weeksA.add(fantasyWeek);
                    weeksB.add(fantasyWeek);
                    teamA.setFantasyWeeksA(weeksA);
                    teamB.setFantasyWeeksB(weeksB);

                    teamA.setOrderNumber(matchIndex);
                    teamB.setOrderNumber(matchIndex + 1);
                    teamB.setOpponentNumber(Math.toIntExact(homeTeamId));
                    teamA.setOpponentNumber(Math.toIntExact(awayTeamId));

                }

                fantasyWeekRepository.save(fantasyWeek);
            }

            return getFantasyWeeksByLeagueId(leagueId);
        }


public List<FantasyWeek> getFantasyLeagueMatchups(Long leagueId, int weekNumber) {

        List<FantasyWeek> schedule = getFantasyWeeksByLeagueId(leagueId);
        List<FantasyWeek> leagueMatchups = new ArrayList<>();

        if (weekNumber < 1 || weekNumber > schedule.size()) {
            for (FantasyWeek fantasyW : schedule) {
                int newWeekNumber = fantasyW.getWeekNumber() % schedule.size();
                if (newWeekNumber == 0) {
                    newWeekNumber = schedule.size(); // If modulo is 0, set week number to the maximum
                }
                fantasyW.setWeekNumber(newWeekNumber);
                fantasyWeekRepository.save(fantasyW);
            }
        }

        for (FantasyWeek fantasyW : schedule) {
            if (fantasyW.getWeekNumber() == weekNumber) {
                leagueMatchups.add(fantasyW);
            }
        }

        return leagueMatchups;
    }

    public List<FantasyWeek> getFantasyWeeksByLeagueId(Long leagueId) {
        List<FantasyTeam> teams = fantasyTeamRepository.findFantasyTeamsByFantasyLeagueId(leagueId);
        Set<FantasyWeek> weeks = new HashSet<>();
        for (FantasyTeam team : teams) {
            weeks.addAll(team.getFantasyWeeksA());
            weeks.addAll(team.getFantasyWeeksB());
        }
        return new ArrayList<>(weeks);
    }

}
