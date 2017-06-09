package org.eclipse.yasson.adapters;

import org.eclipse.yasson.adapters.model.LocalPolymorphicAdapter;
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
    @Ignore
    //add some context of adapters in stack to avoid adapter cycling.
    public void testAdapter() {
        JsonbConfig config = new JsonbConfig().withAdapters(new AnimalAdapter()).withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);

        Animals animals = new Animals();
        animals.listOfAnimals.add(new Dog("Hunting"));
        animals.listOfAnimals.add(new Dog("Watching"));
        animals.listOfAnimals.add(new Cat("Sleeping"));
        animals.listOfAnimals.add(new Cat("Playing"));

        System.out.println("Wrapper object: ");
        final String s = jsonb.toJson(animals, new ArrayList<Animal>(){}.getClass().getGenericSuperclass());
        System.out.println(s);

    }
}
