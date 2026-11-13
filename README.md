# Predator-Prey-Simulation

A turn-based predator–prey simulation in Java. Ants (prey) and beetles (predators) live on a 10×10 grid: beetles hunt the nearest ant, ants flee the nearest beetle, both species breed, and beetles starve if they go too long without eating. The board is printed after every turn.

## Rules

Each turn runs in this order:

1. **Beetles move** toward the nearest ant they can see in a straight line (N, E, S, W). Moving onto an ant eats it.
2. **Ants move** away from the nearest beetle in a straight line.
3. **Starvation** – a beetle that hasn't eaten in 5 turns dies.
4. **Ants breed** every 3 turns into an empty adjacent cell (checking N, E, S, W).
5. **Beetles breed** every 8 turns.

Creatures are processed column by column, and each creature moves at most once per turn.

## Input

The program prompts for:

1. The name of a world file
2. The character to display for ants
3. The character to display for beetles
4. The number of turns to simulate

The world file is 10 lines of 10 characters, where `a` is an ant, `B` is a beetle, and a space is an empty cell. Short lines are padded with spaces.

Example `world.txt`:

```
          
  a       
     B    
          
   a   a  
          
 B        
      a   
          
          
```

## Build and run

```bash
javac *.java
java Main
```

```
Enter file name: world.txt
Character to represent ant: a
Character to represent beetle: B
Enter number of turns to be seen: 2
TURN 1
  a
...
```

## Files

| File | Description |
|------|-------------|
| `Main.java` | Loads the world, runs the simulation loop, and prints each turn |
| `Creature.java` | Abstract base class with directions and `move` / `breed` methods |
| `Ant.java` | Prey: moves away from beetles, breeds every 3 turns |
| `Beetle.java` | Predator: moves toward ants, breeds every 8 turns, starves after 5 turns without food |
