package com.example.leoguti.riskassessment.modelo;

/**
 * Created by leoguti on 09/05/2017.
 */

public enum NivelAlerta {

    VERDE ("Verde"),
    AMARILLA ("Amarilla"),
    NARANJA ("Naranja"),
    ROJA ("Roja");

    private final String text;

    /**
     * @param text
     */
    private NivelAlerta(final String text) {
        this.text = text;
    }

    public static NivelAlerta getByName(String id) {
        for(NivelAlerta e : values()) {
            if(e.name().equals(id.toUpperCase())) return e;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
