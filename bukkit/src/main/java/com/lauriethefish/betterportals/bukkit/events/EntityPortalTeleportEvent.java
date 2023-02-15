package com.lauriethefish.betterportals.bukkit.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class EntityPortalTeleportEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private final Entity entity;
    private final Location destination;
    private final Vector velocity;
    private boolean			cancelled	= false;
    public EntityPortalTeleportEvent(Entity entity, Location destination, Vector velocity) {
        this.entity = entity;
        this.destination = destination;
        this.velocity = velocity;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Entity getEntity() {
        return entity;
    }

    public Location getDestination() {
        return destination;
    }

    public Vector getVelocity() {
        return velocity;
    }
}