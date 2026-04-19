package id.naturalsmp.NaturalWorldGen.util.decree.specialhandlers;

import id.naturalsmp.NaturalWorldGen.util.decree.exceptions.DecreeParsingException;
import id.naturalsmp.NaturalWorldGen.util.decree.handlers.PlayerHandler;
import org.bukkit.entity.Player;

public class NullablePlayerHandler extends PlayerHandler {

    @Override
    public Player parse(String in, boolean force) throws DecreeParsingException {
        return getPossibilities(in).stream().filter((i) -> toString(i).equalsIgnoreCase(in)).findFirst().orElse(null);
    }
}
