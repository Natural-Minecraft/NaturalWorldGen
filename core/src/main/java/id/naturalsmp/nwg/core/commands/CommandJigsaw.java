/*
 * NaturalGenerator is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2022 Arcane Arts (NaturalDev Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package id.naturalsmp.nwg.core.commands;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.core.edit.JigsawEditor;
import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.engine.framework.placer.WorldObjectPlacer;
import id.naturalsmp.nwg.engine.jigsaw.PlannedStructure;
import id.naturalsmp.nwg.engine.object.IrisJigsawPiece;
import id.naturalsmp.nwg.engine.object.IrisJigsawStructure;
import id.naturalsmp.nwg.engine.object.IrisObject;
import id.naturalsmp.nwg.engine.object.IrisPosition;
import id.naturalsmp.nwg.toolbelt.decree.DecreeExecutor;
import id.naturalsmp.nwg.toolbelt.decree.DecreeOrigin;
import id.naturalsmp.nwg.toolbelt.decree.annotations.Decree;
import id.naturalsmp.nwg.toolbelt.decree.annotations.Param;
import id.naturalsmp.nwg.toolbelt.decree.specialhandlers.ObjectHandler;
import id.naturalsmp.nwg.toolbelt.format.C;
import id.naturalsmp.nwg.toolbelt.format.Form;
import id.naturalsmp.nwg.toolbelt.math.RNG;
import id.naturalsmp.nwg.toolbelt.plugin.NaturalDevSender;
import id.naturalsmp.nwg.toolbelt.scheduling.PrecisionStopwatch;

import java.io.File;

@Decree(name = "jigsaw", origin = DecreeOrigin.PLAYER, studio = true, description = "NaturalGenerator jigsaw commands")
public class CommandJigsaw implements DecreeExecutor {
    @Decree(description = "Edit a jigsaw piece")
    public void edit(
            @Param(description = "The jigsaw piece to edit")
            IrisJigsawPiece piece
    ) {
        File dest = piece.getLoadFile();
        new JigsawEditor(player(), piece, IrisData.loadAnyObject(piece.getObject(), data()), dest);
    }

    @Decree(description = "Place a jigsaw structure")
    public void place(
            @Param(description = "The jigsaw structure to place")
            IrisJigsawStructure structure
    ) {
        PrecisionStopwatch p = PrecisionStopwatch.start();
        try {
            var world = world();
            WorldObjectPlacer placer = new WorldObjectPlacer(world);
            PlannedStructure ps = new PlannedStructure(structure, new IrisPosition(player().getLocation().add(0, world.getMinHeight(), 0)), new RNG(), true);
            NaturalDevSender sender = sender();
            sender.sendMessage(C.GREEN + "Generated " + ps.getPieces().size() + " pieces in " + Form.duration(p.getMilliseconds(), 2));
            ps.place(placer, failed -> sender.sendMessage(failed ? C.GREEN + "Placed the structure!" : C.RED + "Failed to place the structure!"));
        } catch (IllegalArgumentException e) {
            sender().sendMessage(C.RED + "Failed to place the structure: " + e.getMessage());
        }
    }

    @Decree(description = "Create a jigsaw piece")
    public void create(
            @Param(description = "The name of the jigsaw piece")
            String piece,
            @Param(description = "The project to add the jigsaw piece to")
            String project,
            @Param(description = "The object to use for this piece", customHandler = ObjectHandler.class)
            String object
    ) {
        IrisObject o = IrisData.loadAnyObject(object, data());

        if (object == null) {
            sender().sendMessage(C.RED + "Failed to find existing object");
            return;
        }

        File dest = NaturalGenerator.instance.getDataFile("packs", project, "jigsaw-pieces", piece + ".json");
        new JigsawEditor(player(), null, o, dest);
        sender().sendMessage(C.GRAY + "* Right Click blocks to make them connectors");
        sender().sendMessage(C.GRAY + "* Right Click connectors to orient them");
        sender().sendMessage(C.GRAY + "* Shift + Right Click connectors to remove them");
        sender().sendMessage(C.GREEN + "Remember to use /naturalworldgen jigsaw save");
    }

    @Decree(description = "Exit the current jigsaw editor")
    public void exit() {
        JigsawEditor editor = JigsawEditor.editors.get(player());

        if (editor == null) {
            sender().sendMessage(C.GOLD + "You don't have any pieces open to exit!");
            return;
        }

        editor.exit();
        sender().sendMessage(C.GREEN + "Exited Jigsaw Editor");
    }

    @Decree(description = "Save & Exit the current jigsaw editor")
    public void save() {
        JigsawEditor editor = JigsawEditor.editors.get(player());

        if (editor == null) {
            sender().sendMessage(C.GOLD + "You don't have any pieces open to save!");
            return;
        }

        editor.close();
        sender().sendMessage(C.GREEN + "Saved & Exited Jigsaw Editor");
    }
}
