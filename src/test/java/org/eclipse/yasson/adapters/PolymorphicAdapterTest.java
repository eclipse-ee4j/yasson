package org.eclipse.yasson.adapters;

import org.eclipse.yasson.adapters.model.LocalPolymorphicAdapter;
import org.eclipse.yasson.adapters.model.LocalTypeWrapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 08/06/2017.
 */
public class PolymorphicAdapterTest {

    public static class AnimalAdapter extends LocalPolymorphicAdapter<Animal> {
        public AnimalAdapter() {
            super(Dog.class, Cat.class);
        }

        @Override
        protected void populateInstance(Animal instance, LocalTypeWrapper<Animal> obj) {
            instance.name = obj.getInstance().name;
        }
    }

    public static class Animal {
        public Animal(String name) {
            this.name = name;
        }

        public Animal() {
            this.name = "NoName animal";
        }
        public String name;
    }

    public static class Dog extends Animal {
        public Dog() {}
        public Dog(String dogProperty) {
            this.dogProperty = dogProperty;
        }
        public String dogProperty;
    }

    public static class Cat extends Animal {
        public Cat() {}
        public Cat(String catProperty) {
            this.catProperty = catProperty;
        }
        public String catProperty;
    }

    public static class Animals {
        public List<Animal> listOfAnimals = new ArrayList<>();
    }

    @Test
    public void testAdapter() {
        JsonbConfig config = new JsonbConfig().withAdapters(new AnimalAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);

        Animals animals = new Animals();
        animals.listOfAnimals.add(new Dog("Hunting"));
        animals.listOfAnimals.add(new Cat("Playing"));

        String expectedJson =  "{\"listOfAnimals\":[{\"className\":\"org.eclipse.yasson.adapters.PolymorphicAdapterTest$Dog\",\"instance\":{\"name\":\"NoName animal\",\"dogProperty\":\"Hunting\"}},{\"className\":\"org.eclipse.yasson.adapters.PolymorphicAdapterTest$Cat\",\"instance\":{\"name\":\"NoName animal\",\"catProperty\":\"Playing\"}}]}";

        Assert.assertEquals(expectedJson, jsonb.toJson(animals, new ArrayList<Animal>(){}.getClass().getGenericSuperclass()));

        Animals reuslt = jsonb.fromJson(expectedJson, Animals.class);
        Assert.assertTrue(reuslt.listOfAnimals.get(0) instanceof Dog);
        Assert.assertTrue(reuslt.listOfAnimals.get(1) instanceof Cat);
    }
}
