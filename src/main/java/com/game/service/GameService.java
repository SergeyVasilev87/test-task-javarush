package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.*;
import com.game.repository.GameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    private Object castType(Class fieldType, String value) {
        if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf(fieldType, value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (fieldType.isAssignableFrom(Date.class)) {
            return new Date(Long.parseLong(value));
        }
        return null;
    }

    private Specification<Player> createSpecification(Filter filter) {
        switch (filter.getOperator()) {
            case LIKE:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(filter.getField()), "%"+filter.getValue()+"%");
            case EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(filter.getField()), castType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            case GREATER_THAN:
                return (root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get(filter.getField()), (Number) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            case LESS_THAN:
                return (root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get(filter.getField()), (Number) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            case AFTER_THAN:
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(filter.getField()), (Date) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            case BEFORE_THAN:
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(filter.getField()), (Date) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            default:
                throw new RuntimeException("no operator");
        }
    }
    private Specification<Player> getAllSpecification(List<Filter> filters) {
        Specification<Player> specification = Specification.where(createSpecification(filters.remove(0)));
        for (Filter filter : filters) {
            specification = specification.and(createSpecification(filter));
        }
        return specification;
    }

    public Page<Player> getAllPlayers (List<Filter> filters, Integer pageNumber, Integer pageSize, PlayerOrder order) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Player> allSpecification = getAllSpecification(filters);
        return gameRepository.findAll(allSpecification, pageRequest);
    }


    public Integer getCount(List<Filter> filters) {
        if (!filters.isEmpty()) {
            Specification<Player> allSpecification = getAllSpecification(filters);
            int count = (int) gameRepository.count(allSpecification);
            return count;
        }
        else
            return (int) gameRepository.count();
    }

    public Player getPlayer(Long id) {
        Player player = gameRepository.findById(id).orElseThrow(() -> new RuntimeException("no player"));
        return player;
    }

    public Player createPlayer(PlayerDto playerDto) {
        Player player = new Player();
        player.setName(playerDto.getName());
        player.setTitle(playerDto.getTitle());
        player.setRace(playerDto.getRace());
        player.setProfession(playerDto.getProfession());
        player.setExperience(playerDto.getExperience());
        int level = (int) ((Math.sqrt(2500 + 200 * playerDto.getExperience()) - 50) / 100);
        player.setLevel(level);
        int untilNextLevel = 50*(level+1)*(level+2) - playerDto.getExperience();
        player.setUntilNextLevel(untilNextLevel);
        player.setBirthday(new Date(playerDto.getBirthday()));
        player.setBanned(playerDto.getBanned() != null && playerDto.getBanned());

        return gameRepository.save(player);
    }

    public Player updatePlayer(Long id, PlayerDto playerDto) {
        Player player = gameRepository.findById(id).orElseThrow(() -> new RuntimeException("no player"));
        player.setId(id);
        if (playerDto.getName() != null || playerDto.getTitle() != null || playerDto.getRace() != null ||
                playerDto.getProfession() != null || playerDto.getBirthday() != null ||
                playerDto.getBanned() != null || playerDto.getExperience() != null) {

            if (playerDto.getBirthday() != null && (
                    playerDto.getBirthday() < new Date(2000 - 1900, 0, 1).getTime() ||
                    playerDto.getBirthday() > new Date(3000 - 1900, 11, 31).getTime())) {
                throw new RuntimeException("bad");
            }
            if (playerDto.getExperience() != null && (
                    playerDto.getExperience() < 0 || playerDto.getExperience() > 10_000_000)) {
                throw new RuntimeException("bad");
            }
            if (playerDto.getName() != null) {
                player.setName(playerDto.getName());
            }
            if (playerDto.getTitle() != null) {
                player.setTitle(playerDto.getTitle());
            }
            if (playerDto.getRace() != null) {
                player.setRace(playerDto.getRace());
            }
            if (playerDto.getProfession() != null) {
                player.setProfession(playerDto.getProfession());
            }
            if (playerDto.getBirthday() != null) {
                player.setBirthday(new Date(playerDto.getBirthday()));
            }
            if (playerDto.getBanned() != null) {
                player.setBanned(playerDto.getBanned());
            }
            if (playerDto.getExperience() != null) {
                player.setExperience(playerDto.getExperience());

                int level = (int) ((Math.sqrt(2500 + 200 * playerDto.getExperience()) - 50) / 100);
                player.setLevel(level);
                int untilNextLevel = 50*(level+1)*(level+2) - playerDto.getExperience();
                player.setUntilNextLevel(untilNextLevel);
            }
            return gameRepository.save(player);
        } else {
            return player;
        }
    }

    public void deletePlayer(Long id) {
        gameRepository.findById(id).orElseThrow(() -> new RuntimeException("no player"));
        gameRepository.deleteById(id);
    }
}