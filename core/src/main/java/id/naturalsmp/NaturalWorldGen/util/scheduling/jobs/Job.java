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

package id.naturalsmp.NaturalWorldGen.util.scheduling.jobs;

import id.naturalsmp.NaturalWorldGen.util.format.C;
import id.naturalsmp.NaturalWorldGen.util.format.Form;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import id.naturalsmp.NaturalWorldGen.util.scheduling.J;
import id.naturalsmp.NaturalWorldGen.util.scheduling.PrecisionStopwatch;

import java.util.concurrent.CompletableFuture;

public interface Job {
    String getName();

    void execute();

    void completeWork();

    int getTotalWork();

    default int getWorkRemaining() {
        return getTotalWork() - getWorkCompleted();
    }

    int getWorkCompleted();

    default String getProgressString() {
        return Form.pc(getProgress(), 0);
    }

    default double getProgress() {
        return (double) getWorkCompleted() / (double) getTotalWork();
    }


    default void execute(NaturalDevSender sender) {
        execute(sender, () -> {
        });
    }


    default void execute(NaturalDevSender sender, Runnable whenComplete) {
        execute(sender, false, whenComplete);
    }

    default void execute(NaturalDevSender sender, boolean silentMsg, Runnable whenComplete) {
        PrecisionStopwatch p = PrecisionStopwatch.start();
        CompletableFuture<?> f = J.afut(this::execute);
        int c = J.ar(() -> {
            if (sender.isPlayer()) {
                sender.sendProgress(getProgress(), getName());
            } else {
                sender.sendMessage(getName() + ": " + getProgressString());
            }
        }, sender.isPlayer() ? 0 : 20);
        f.whenComplete((fs, ff) -> {
            J.car(c);
            if (!silentMsg) {
                sender.sendMessage(C.AQUA + "Completed " + getName() + " in " + Form.duration(p.getMilliseconds(), 1));
            }
            whenComplete.run();
        });
    }
}
