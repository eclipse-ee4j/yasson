package org.eclipse.yasson.adapters.model;

public class Chain {
    
    private String name;
    private Chain linksTo;
    private Foo has;
    
    public Chain(String name) {
        this.name = name;
    }
    
    public Chain() {
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Chain getLinksTo() {
        return linksTo;
    }
    public void setLinksTo(Chain linksTo) {
        this.linksTo = linksTo;
    }
    public Foo getHas() {
        return has;
    }
    public void setHas(Foo has) {
        this.has = has;
    }
    
}
