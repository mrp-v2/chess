package model;

import java.util.Collection;

public record GamesResponse(Collection<GameData> games) implements JsonSerializable {
}
