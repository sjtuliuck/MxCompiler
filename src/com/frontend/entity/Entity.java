package com.frontend.entity;

import com.utility.Location;

abstract public class Entity {
    private Location location;
    private String name;

    public Entity(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
