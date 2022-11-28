package com.milica.customer;

public record CustomerRegistrationRequest (
        String firstName,
        String lastName,
        String email) {
}
