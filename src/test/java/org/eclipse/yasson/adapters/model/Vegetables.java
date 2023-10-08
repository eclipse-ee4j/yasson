package org.eclipse.yasson.adapters.model;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeAdapter;

@JsonbTypeAdapter(VegetablesAdapter.class)
public enum Vegetables {
	@JsonbProperty("Tomato")
	TOMATO,
	@JsonbProperty("Cucumber")
	CUCUMBER
}

