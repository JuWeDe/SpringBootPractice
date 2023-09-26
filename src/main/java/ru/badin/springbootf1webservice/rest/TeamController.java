package ru.badin.springbootf1webservice.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.badin.springbootf1webservice.Services.CarService;
import ru.badin.springbootf1webservice.Services.RacerService;
import ru.badin.springbootf1webservice.Services.TeamService;
import ru.badin.springbootf1webservice.model.Car;
import ru.badin.springbootf1webservice.model.Racer;
import ru.badin.springbootf1webservice.model.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    private final RacerService racerService;
    private final CarService carService;

    @Autowired
    public TeamController(TeamService teamService, RacerService racerService, CarService carService) {
        this.teamService = teamService;
        this.racerService = racerService;
        this.carService = carService;
    }
    @PostMapping
        public Team addTeam(@RequestBody Map<String, String> body) {
           Team team = new Team();
            String name = body.get("name");
            Double points = Double.valueOf(body.get("points"));
            String teamPrinciple = body.get("teamPrinciple");
           team.setTeamPrinciple(teamPrinciple);
           team.setName(name);
           team.setPoints(points);
           return teamService.createTeam(team);
        }
    @PutMapping("/{id}")
    public Team updateTeam(@PathVariable Long id, @RequestBody Team updatedTeam) {
        Team team = teamService.getTeamById(id);
        if (team != null) {
            team.setName(updatedTeam.getName());
            team.setPoints(updatedTeam.getPoints());
            team.setTeamPrinciple(updatedTeam.getTeamPrinciple());
            List<Racer> racers = updatedTeam.getRacers();
            if (racers != null) {
                for (Racer racer : racers) {
                    Racer existingRacer = racerService.getRacerById(racer.getId());
                    if (existingRacer != null) {
                        existingRacer.setTeam(team);
                        racerService.updateRacer(existingRacer.getId(), existingRacer);
                    }
                }
                team.setRacers(racers);
            }
            List<Car> cars = updatedTeam.getCars();
            if (cars != null) {
                for (Car car : cars) {
                    Car existingCar = carService.getCarById(car.getId());
                    if (existingCar != null) {
                        existingCar.setTeam(team);
                        carService.updateCar(existingCar.getId(), existingCar);
                    }
                }
                team.setCars(cars);
            }
            return teamService.updateTeam(id, team);
        }
        return null;
    }

    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/hal")
    public ResponseEntity<Map<String, Object>> getTeams (@RequestParam(defaultValue = "0") int index,
                                                       @RequestParam(defaultValue = "25") int count) {
        Page<Team> teams = teamService.getTeams(index, count);

        List<Team> teamList = teams.getContent();
        Map<String, Object> response = new HashMap<>();
        int total = teams.getTotalPages();
        response.put("items", teamList);
        response.put("count", teamList.size());
        response.put("total", teams.getTotalElements());
        response.put("index", index);

        if (index < total) {
            response.put("next", "/teams/hal?index=" + (index + count));
            response.put("final", "/teams/hal?index=" + (total - 1) * count + "&count=" + count);
        }

        if (index > 0) {
            response.put("prev", "/teams/hal?index=" + (index - count));
            response.put("first", "/teams/hal?index=0");
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public Team getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }
}