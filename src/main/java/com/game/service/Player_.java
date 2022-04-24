package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(Player.class)
public abstract class Player_ {

    public static volatile SingularAttribute<Player, Long> id;
    public static volatile SingularAttribute<Player, String> name;
    public static volatile SingularAttribute<Player, String> title;
    public static volatile SingularAttribute<Player, Race> race;
    public static volatile SingularAttribute<Player, Profession> profession;
    public static volatile SingularAttribute<Player, Integer> experience;
    public static volatile SingularAttribute<Player, Integer> level;
    public static volatile SingularAttribute<Player, Integer> untilNextLevel;
    public static volatile SingularAttribute<Player, Date> birthday;
    public static volatile SingularAttribute<Player, Boolean> banned;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String RACE = "race";
    public static final String PROFESSION = "profession";
    public static final String EXPERIENCE = "experience";
    public static final String LEVEL = "level";
    public static final String UNTIL_NEXT_LEVEL = "untilNextLevel";
    public static final String BIRTHDAY =  "birthday";
    public static final String BANNED = "banned";
}
