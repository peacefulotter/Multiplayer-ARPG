package ch.epfl.cs107.play.Networking.utils;

import java.util.Random;

public class IdGenerator {
    public static final Random random = new Random();
    public static int generateId(){
        return random.nextInt();
    }
}
