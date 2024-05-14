package model;

import java.util.Collection;

public record GamesData(Collection<GameData> games) implements IJsonSerializable {
}
