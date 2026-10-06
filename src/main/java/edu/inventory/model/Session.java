package edu.inventory.model;
public record Session(long userId, String username, Role role, Long supplierId) {
    public Session(long userId, String username, Role role) { this(userId, username, role, null); }
}
