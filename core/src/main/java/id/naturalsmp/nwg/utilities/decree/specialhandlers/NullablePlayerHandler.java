package id.naturalsmp.nwg.utilities.decree.specialhandlers;

import id.naturalsmp.nwg.utilities.decree.exceptions.DecreeParsingException;
import id.naturalsmp.nwg.utilities.decree.handlers.PlayerHandler;
import org.bukkit.entity.Player;

public class NullablePlayerHandler extends PlayerHandler {

    @Override
    public Player parse(String in, boolean force) throws DecreeParsingException {
        return getPossibilities(in).stream().filter((i) -> toString(i).equalsIgnoreCase(in)).findFirst().orElse(null);
    }
}
