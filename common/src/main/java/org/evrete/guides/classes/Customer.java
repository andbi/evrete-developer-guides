package org.evrete.guides.classes;

import java.util.Map;

public record Customer(int id, int status, String name, Map<String, String> properties) {
}
