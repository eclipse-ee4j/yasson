package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class CreatorWithoutJsonbProperty1 {

    private final String par1;
    private final String par2;
    private byte par3;

    @JsonbCreator
    public CreatorWithoutJsonbProperty1(String par1, @JsonbProperty("s2") String par2, byte par3) {
        this.par1 = par1;
        this.par2 = par2;
        this.par3 = par3;
    }

    public String getPar1() {
        return par1;
    }

    public String getPar2() {
        return par2;
    }

    public byte getPar3() {
        return par3;
    }
}
