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

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final UserService userService;
    private final PlayerService playerService;
    private final FantasyTeamRepository fantasyTeamRepository;
    private final GameService gameService;
    private final FantasyWeekRepository fantasyWeekRepository;

    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository, UserService userService,
                                FantasyTeamRepository fantasyTeamRepository, GameService gameService, PlayerService playerService, FantasyWeekRepository fantasyWeekRepository) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.userService = userService;
        this.playerService = playerService;
        this.fantasyTeamRepository = fantasyTeamRepository;
        this.fantasyWeekRepository = fantasyWeekRepository;
        this.gameService = gameService;
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

        //block users from registering two teams in a league
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
        FantasyLeague league = getFantasyLeagueById(leagueId);
        List<FantasyTeam> teams = new ArrayList<>(league.getFantasyTeams());

        int numTeams = teams.size();
        boolean hasGhostTeam = false;

        //schedule builder

        if (numTeams % 2 != 0) {

            numTeams++;

        }

        List<List<FantasyTeam>> schedule = new ArrayList<>();
        List<FantasyTeam> matches = new ArrayList<>();
        List<FantasyWeek> fantasyWeeks = new ArrayList<>();

        // Each week
        for (int week = 0; week < numTeams - 1; week++) {
            // Each match in a week
            for (int match = 0; match < numTeams / 2; match++) {
                int homeTeamIndex = (week + match) % (numTeams - 1);
                int awayTeamIndex = (numTeams - 1 - match + week) % (numTeams - 1);

                if (match == 0) {
                    awayTeamIndex = numTeams - 1;
                }
                FantasyTeam homeTeam = teams.get(homeTeamIndex);
                FantasyTeam awayTeam = null;

                if (awayTeamIndex < teams.size()) {
                    awayTeam = teams.get(awayTeamIndex);
                }
                matches.add(homeTeam);
                matches.add(awayTeam);

            }
            schedule.add(new ArrayList<>(matches));

        }

        LocalDate startDate = LocalDate.now();
        DayOfWeek dayOfWeek = startDate.getDayOfWeek();
        int daysUntilTuesday=0;
        if (dayOfWeek != DayOfWeek.TUESDAY) {
            daysUntilTuesday = DayOfWeek.TUESDAY.getValue() - dayOfWeek.getValue();
            if (daysUntilTuesday < 0) {
                daysUntilTuesday += 7;
            }
        }
        startDate = startDate.plusDays(daysUntilTuesday);
        LocalDate endDate = startDate.plusWeeks(1);
        int weekNumber = 1;
        int isOneMonth;

            while (gameService.getGamesBetweenDates(startDate, endDate) != null) {
                for (List<FantasyTeam> weekMatches : schedule) {
                    for (int matchIndex = 0; matchIndex < weekMatches.size(); matchIndex += 2) {
                        FantasyTeam teamA = weekMatches.get(matchIndex);
                        FantasyTeam teamB = weekMatches.get(matchIndex + 1);

                        FantasyWeek fantasyWeek = new FantasyWeek();
                        fantasyWeek.setFantasyTeamA(teamA);
                        fantasyWeek.setFantasyTeamB(teamB);
                        fantasyWeek.setWeekNumber(weekNumber);
                        fantasyWeek.setStartDate(startDate);

                        isOneMonth = 1;

                        while (gameService.getGamesBetweenDates(startDate, endDate).size() < 4 && isOneMonth < 4) {
                            endDate = endDate.plusWeeks(1);
                            isOneMonth++;
                        }

                        fantasyWeek.setEndDate(endDate);

                        fantasyWeekRepository.save(fantasyWeek);
                        fantasyWeeks.add(fantasyWeek);
                    }
                    weekNumber += 1;
                }
            }
            return getFantasyWeeksByLeagueId(leagueId);
        }


        public List<FantasyWeek> getFantasyLeagueMatchups (Long leagueId,int weekNumber){

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

        public List<FantasyWeek> getFantasyWeeksByLeagueId (Long leagueId){
            FantasyLeague league = getFantasyLeagueById(leagueId);
            Set<FantasyTeam> teams = league.getFantasyTeams();
            Set<FantasyWeek> weeks = new HashSet<>();
            for (FantasyTeam team : teams) {
                weeks.addAll(team.getFantasyWeeks());
            }
            return new ArrayList<>(weeks);
        }

    }



