package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author misl
 */
public class CreatorConstructorPojoOptional {

    public String str1;

    public Optional<String> str2;

    public BigDecimal bigDec;

    public CreatorFactoryMethodPojo innerFactoryCreator;

    @JsonbCreator
    public CreatorConstructorPojoOptional( @JsonbProperty("str1") String str1, @JsonbProperty("str2") Optional<String> str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

}
