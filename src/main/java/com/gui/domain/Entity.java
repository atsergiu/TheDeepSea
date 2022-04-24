package com.gui.domain;

import java.io.Serializable;

/**
 * Define an abstract type called entity
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     * Get the ID of a entity
     */
    public ID getId() {
        return id;
    }

    /**
     * change the id of a entity
     */
    public void setId(ID id) {
        this.id = id;
    }
}