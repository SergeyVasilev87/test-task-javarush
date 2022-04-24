package com.game.controller;

import com.game.entity.*;
import com.game.service.Filter;
import com.game.service.GameService;
import com.game.service.QueryOperator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class MyController {

    private final GameService gameService;

    public MyController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> getAllPlayers(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order, //todo или String
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize
            ) {

        List<Filter> filters = new ArrayList<>();
        if (name != null)
            filters.add(new Filter("name", QueryOperator.LIKE, name));
        if (title != null)
            filters.add(new Filter("title", QueryOperator.LIKE, title));
        if (race != null)
            filters.add(new Filter("race", QueryOperator.EQUALS, race.toString()));
        if (profession != null)
            filters.add(new Filter("profession", QueryOperator.EQUALS, profession.toString()));
        if (after != null)
            filters.add(new Filter("birthday", QueryOperator.AFTER_THAN, String.valueOf(after)));
        if (before != null)
            filters.add(new Filter("birthday", QueryOperator.BEFORE_THAN, String.valueOf(before)));
        if (banned != null)
            filters.add(new Filter("banned", QueryOperator.EQUALS, banned.toString()));
        if (minExperience != null)
            filters.add(new Filter("experience", QueryOperator.GREATER_THAN, minExperience.toString()));
        if (maxExperience != null)
            filters.add(new Filter("experience", QueryOperator.LESS_THAN, maxExperience.toString()));
        if (minLevel != null)
            filters.add(new Filter("level", QueryOperator.GREATER_THAN, minLevel.toString()));
        if (maxLevel != null)
            filters.add(new Filter("level", QueryOperator.LESS_THAN, maxLevel.toString()));

        if (order == null)
            order = PlayerOrder.ID;

        Page<Player> allPlayers = gameService.getAllPlayers(filters, pageNumber, pageSize, order);

        return ResponseEntity.ok(allPlayers.getContent());
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getCount(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) Race race,
                                            @RequestParam(required = false) Profession profession,
                                            @RequestParam(required = false) Long after,
                                            @RequestParam(required = false) Long before,
                                            @RequestParam(required = false) Boolean banned,
                                            @RequestParam(required = false) Integer minExperience,
                                            @RequestParam(required = false) Integer maxExperience,
                                            @RequestParam(required = false) Integer minLevel,
                                            @RequestParam(required = false) Integer maxLevel) {
        List<Filter> filters = new ArrayList<>();
        if (name != null)
            filters.add(new Filter("name", QueryOperator.LIKE, name));
        if (title != null)
            filters.add(new Filter("title", QueryOperator.LIKE, title));
        if (race != null)
            filters.add(new Filter("race", QueryOperator.EQUALS, race.toString()));
        if (profession != null)
            filters.add(new Filter("profession", QueryOperator.EQUALS, profession.toString()));
        if (after != null)
            filters.add(new Filter("birthday", QueryOperator.AFTER_THAN, String.valueOf(after)));
        if (before != null)
            filters.add(new Filter("birthday", QueryOperator.BEFORE_THAN, String.valueOf(before)));
        if (banned != null)
            filters.add(new Filter("banned", QueryOperator.EQUALS, banned.toString()));
        if (minExperience != null)
            filters.add(new Filter("experience", QueryOperator.GREATER_THAN, minExperience.toString()));
        if (maxExperience != null)
            filters.add(new Filter("experience", QueryOperator.LESS_THAN, maxExperience.toString()));
        if (minLevel != null)
            filters.add(new Filter("level", QueryOperator.GREATER_THAN, minLevel.toString()));
        if (maxLevel != null)
            filters.add(new Filter("level", QueryOperator.LESS_THAN, maxLevel.toString()));

        Integer count = gameService.getCount(filters);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/rest/players/{id}") // done
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Player player = gameService.getPlayer(id);
            return ResponseEntity.ok(player);
        } catch (RuntimeException e) {
            return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rest/players/{id}") // done
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody PlayerDto playerDto) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Player player = gameService.updatePlayer(id, playerDto);
            return ResponseEntity.ok(player);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("bad"))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else
                return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rest/players") // done
    public ResponseEntity<Player> createPlayer(@RequestBody PlayerDto playerDto) {
        if (playerDto.getBirthday() == null || playerDto.getExperience() == null ||
        playerDto.getName() == null || playerDto.getProfession() == null ||
        playerDto.getRace() == null || playerDto.getTitle() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (playerDto.getName().length() > 12 || playerDto.getTitle().length() > 30 ||
        playerDto.getName().isEmpty() || playerDto.getExperience() < 0 ||
        playerDto.getExperience() > 10_000_000 || playerDto.getBirthday() < 0 ||
        playerDto.getBirthday() < new Date(2000-1900, 0, 1).getTime() ||
                playerDto.getBirthday() > new Date(3000-1900, 11, 31).getTime()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            Player player = gameService.createPlayer(playerDto);
            return new ResponseEntity<Player>(player, HttpStatus.OK);
        }
    }

    @DeleteMapping("/rest/players/{id}") // done
    public ResponseEntity<Player> deletePlayer (@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            gameService.deletePlayer(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
