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

package id.naturalsmp.nwg.core.service;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.core.commands.CommandIris;
import id.naturalsmp.nwg.core.tools.IrisToolbelt;
import id.naturalsmp.nwg.engine.data.cache.AtomicCache;
import id.naturalsmp.nwg.utilities.collection.KMap;
import id.naturalsmp.nwg.utilities.decree.DecreeContext;
import id.naturalsmp.nwg.utilities.decree.DecreeSystem;
import id.naturalsmp.nwg.utilities.decree.virtual.VirtualDecreeCommand;
import id.naturalsmp.nwg.utilities.format.C;
import id.naturalsmp.nwg.utilities.plugin.IrisService;
import id.naturalsmp.nwg.utilities.plugin.NaturalDevSender;
import id.naturalsmp.nwg.utilities.scheduling.J;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CommandSVC implements IrisService, DecreeSystem {
    private final KMap<String, CompletableFuture<String>> futures = new KMap<>();
    private final transient AtomicCache<VirtualDecreeCommand> commandCache = new AtomicCache<>();
    private CompletableFuture<String> consoleFuture = null;

    @Override
    public void onEnable() {
        NaturalGenerator.instance.getCommand("nwg").setExecutor(this);
        J.a(() -> {
            DecreeContext.touch(NaturalGenerator.getSender());
            try {
                getRoot().cacheAll();
            } finally {
                DecreeContext.remove();
            }
        });
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().startsWith("/") ? e.getMessage().substring(1) : e.getMessage();

        if (msg.startsWith("irisdecree ")) {
            String[] args = msg.split("\\Q \\E");
            CompletableFuture<String> future = futures.get(args[1]);

            if (future != null) {
                future.complete(args[2]);
                e.setCancelled(true);
                return;
            }
        }

        if ((msg.startsWith("locate ") || msg.startsWith("locatebiome ")) && IrisToolbelt.isIrisWorld(e.getPlayer().getWorld())) {
            new NaturalDevSender(e.getPlayer()).sendMessage(C.RED + "Locating biomes & objects is disabled in NaturalGenerator Worlds. Use /naturalworldgen studio goto <biome>");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(ServerCommandEvent e) {
        if (consoleFuture != null && !consoleFuture.isCancelled() && !consoleFuture.isDone()) {
            if (!e.getCommand().contains(" ")) {
                String pick = e.getCommand().trim().toLowerCase(Locale.ROOT);
                consoleFuture.complete(pick);
                e.setCancelled(true);
            }
        }
    }

    @Override
    public VirtualDecreeCommand getRoot() {
        return commandCache.aquireNastyPrint(() -> VirtualDecreeCommand.createRoot(new CommandIris()));
    }

    public void post(String password, CompletableFuture<String> future) {
        futures.put(password, future);
    }

    public void postConsole(CompletableFuture<String> future) {
        consoleFuture = future;
    }
}
