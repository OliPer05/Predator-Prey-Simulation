//Name: Oliver Perez
import java.util.*;
import java.io.*;

public class Main 
{
    public static void main(String[] args)
    {
        // read console input
        Scanner scanner = new Scanner(System.in);

        // read filename
        System.out.print("Enter file name: ");
        String fileName = scanner.next();

        // read output glyphs
        System.out.print("Character to represent ant: ");
        char antChar = scanner.next().charAt(0);
        System.out.print("Character to represent beetle: ");
        char beetChar = scanner.next().charAt(0);

        // read turns
        System.out.print("Enter number of turns to be seen: ");
        int numTurns = scanner.nextInt();

        // stop reading console
        scanner.close();

        // open input file
        Scanner iFile;
        try
        {
            iFile = new Scanner(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File could not be opened.");
            return;
        }

        // create board
        Creature[][] grid = new Creature[10][10];

        // load board from file using user glyphs
        populateGrid(grid, antChar, beetChar, iFile);

        // close file
        iFile.close();

        // run simulation
        for (int turn = 0; turn < numTurns; turn++)
        {
            //beetles move toward nearest orthogonal ant
            moveBeetles(grid);

            //ants move away from nearest orthogonal beetle
            moveAnts(grid);

            // remove any beetle with 5 or more turns since the last meal
            starveBeetles(grid);

            //ants breed every 3 turns N,E,S,W
            breedAnts(grid, antChar);

            // beetles breed every 8 turns
            breedBeetles(grid, beetChar);

            // print turn
            System.out.println("TURN " + (turn + 1));

            // print board
            displayGrid(grid);
        }
    }

    // move all beetles column first one move per beetle
    public static void moveBeetles(Creature[][] grid)
    {
        boolean[][] moved = new boolean[10][10];

        for (int c = 0; c < 10; c++)
        {
            for (int r = 0; r < 10; r++)
            {
                if (!(grid[r][c] instanceof Beetle) || moved[r][c])
                {
                    continue;
                }

                Beetle bNow = (Beetle)grid[r][c];

                int dN = distanceTo(grid, r, c, Creature.N, true);
                int dE = distanceTo(grid, r, c, Creature.E, true);
                int dS = distanceTo(grid, r, c, Creature.S, true);
                int dW = distanceTo(grid, r, c, Creature.W, true);

                boolean canN = canStepBeetle(grid, r, c, Creature.N);
                boolean canE = canStepBeetle(grid, r, c, Creature.E);
                boolean canS = canStepBeetle(grid, r, c, Creature.S);
                boolean canW = canStepBeetle(grid, r, c, Creature.W);

                int dir = bNow.move(dN, dE, dS, dW, canN, canE, canS, canW);

                dir = beetleRefineNoAnt(dir, r, c, canN, canE, canS, canW, dN, dE, dS, dW);

                int nr = r + dRow(dir);
                int nc = c + dCol(dir);

                if (dir == Creature.STAY || !inBounds(nr, nc))
                {
                    bNow.noMeal();
                    moved[r][c] = true;
                    continue;
                }

                if (grid[nr][nc] instanceof Beetle)
                {
                    bNow.noMeal();
                    moved[r][c] = true;
                    continue;
                }

                boolean eats = (grid[nr][nc] instanceof Ant);

                grid[nr][nc] = grid[r][c];
                grid[r][c] = null;

                Beetle b = (Beetle)grid[nr][nc];
                if (eats)
                {
                    b.ate();
                }
                else
                {
                    b.noMeal();
                }

                moved[nr][nc] = true;
            }
        }
    }

    // move all ants column first one move per ant
    public static void moveAnts(Creature[][] grid)
    {
        boolean[][] moved = new boolean[10][10];

        for (int c = 0; c < 10; c++)
        {
            for (int r = 0; r < 10; r++)
            {
                if (!(grid[r][c] instanceof Ant) || moved[r][c])
                {
                    continue;
                }

                Ant aNow = (Ant)grid[r][c];

                int dN = distanceTo(grid, r, c, Creature.N, false);
                int dE = distanceTo(grid, r, c, Creature.E, false);
                int dS = distanceTo(grid, r, c, Creature.S, false);
                int dW = distanceTo(grid, r, c, Creature.W, false);

                boolean canN = canStepAnt(grid, r, c, Creature.N);
                boolean canE = canStepAnt(grid, r, c, Creature.E);
                boolean canS = canStepAnt(grid, r, c, Creature.S);
                boolean canW = canStepAnt(grid, r, c, Creature.W);

                int dir = aNow.move(dN, dE, dS, dW, canN, canE, canS, canW);

                int nr = r + dRow(dir);
                int nc = c + dCol(dir);

                if (dir == Creature.STAY || !inBounds(nr, nc))
                {
                    moved[r][c] = true;
                    continue;
                }

                if (grid[nr][nc] != null)
                {
                    moved[r][c] = true;
                    continue;
                }

                grid[nr][nc] = grid[r][c];
                grid[r][c] = null;

                moved[nr][nc] = true;
            }
        }
    }

    // breed ants every 3 turns N then E S W
    public static void breedAnts(Creature[][] grid, char antChar)
    {
        int[][] coords = new int[100][2];
        int count = 0;

        for (int c = 0; c < 10; c++)
        {
            for (int r = 0; r < 10; r++)
            {
                if (grid[r][c] instanceof Ant)
                {
                    coords[count][0] = r;
                    coords[count][1] = c;
                    count++;
                }
            }
        }

        for (int i = 0; i < count; i++)
        {
            int r = coords[i][0];
            int c = coords[i][1];

            if (!(grid[r][c] instanceof Ant))
            {
                continue;
            }

            Ant a = (Ant)grid[r][c];
            if (a.breed())
            {
                if (tryPlaceAnt(grid, r - 1, c, antChar)) { continue; }
                if (tryPlaceAnt(grid, r, c + 1, antChar)) { continue; }
                if (tryPlaceAnt(grid, r + 1, c, antChar)) { continue; }
                tryPlaceAnt(grid, r, c - 1, antChar);
            }
        }
    }

    // remove beetles that have not eaten in 5 or more turns
    public static void starveBeetles(Creature[][] grid)
    {
        for (int r = 0; r < 10; r++)
        {
            for (int c = 0; c < 10; c++)
            {
                if (grid[r][c] instanceof Beetle)
                {
                    Beetle b = (Beetle)grid[r][c];
                    if (b.starve() >= 5)
                    {
                        grid[r][c] = null;
                    }
                }
            }
        }
    }

    //Breed beetles every 8 turns
    public static void breedBeetles(Creature[][] grid, char beetChar)
    {
        int[][] coords = new int[100][2];
        int count = 0;

        for (int c = 0; c < 10; c++)
        {
            for (int r = 0; r < 10; r++)
            {
                if (grid[r][c] instanceof Beetle)
                {
                    coords[count][0] = r;
                    coords[count][1] = c;
                    count++;
                }
            }
        }

        for (int i = 0; i < count; i++)
        {
            int r = coords[i][0];
            int c = coords[i][1];

            if (!(grid[r][c] instanceof Beetle))
            {
                continue;
            }

            Beetle b = (Beetle)grid[r][c];
            if (b.breed())
            {
                if (tryPlaceBeetle(grid, r - 1, c, beetChar)) { continue; }
                if (tryPlaceBeetle(grid, r, c + 1, beetChar)) { continue; }
                if (tryPlaceBeetle(grid, r + 1, c, beetChar)) { continue; }
                tryPlaceBeetle(grid, r, c - 1, beetChar);
            }
        }
    }

    public static void populateGrid(Creature[][] grid, char antChar, char beetChar, Scanner iFile)
    {
        for (int r = 0; r < 10; r++)
        {
            String line = iFile.hasNextLine() ? iFile.nextLine() : "";

            if (line.length() < 10)
            {
                StringBuilder sb = new StringBuilder(line);
                while (sb.length() < 10)
                {
                    sb.append(' ');
                }
                line = sb.toString();
            }

            for (int c = 0; c < 10; c++)
            {
                char ch = line.charAt(c);
                if (ch == 'a')
                {
                    grid[r][c] = new Ant(antChar);
                }
                else if (ch == 'B')
                {
                    grid[r][c] = new Beetle(beetChar);
                }
                else
                {
                    grid[r][c] = null;
                }
            }
        }
    }
    
    public static void displayGrid(Creature[][] grid)
    {
        for (int r = 0; r < 10; r++)
        {
            for (int c = 0; c < 10; c++)
            {
                if (grid[r][c] == null)
                {
                    System.out.print(" ");
                }
                else
                {
                    System.out.print(grid[r][c]);
                }
            }
            System.out.println();
        }
     }

    public static boolean inBounds(int r, int c)
    {
        if (r >= 0 && r < 10 && c >= 0 && c < 10) { return true; }
        else { return false; }
    }

    public static int dRow(int dir)
    {
        if (dir == Creature.N) { return -1; }
        if (dir == Creature.S) { return 1; }
        return 0;
    }

    public static int dCol(int dir)
    {
        if (dir == Creature.E) { return 1; }
        if (dir == Creature.W) { return -1; }
        return 0;
    }

    public static int distanceTo(Creature[][] grid, int r, int c, int dir, boolean targetIsAnt)
    {
        int rr = r + dRow(dir);
        int cc = c + dCol(dir);
        int dist = 1;
        while (inBounds(rr, cc))
        {
            if (targetIsAnt ? (grid[rr][cc] instanceof Ant) : (grid[rr][cc] instanceof Beetle))
            {
                return dist;
            }
            rr += dRow(dir);
            cc += dCol(dir);
            dist++;
        }
        return Integer.MAX_VALUE;
    }

    public static boolean canStepAnt(Creature[][] grid, int r, int c, int dir)
    {
        int nr = r + dRow(dir);
        int nc = c + dCol(dir);
        if (inBounds(nr, nc) && grid[nr][nc] == null) { return true; }
        else { return false; }
    }

    public static boolean canStepBeetle(Creature[][] grid, int r, int c, int dir)
    {
        int nr = r + dRow(dir);
        int nc = c + dCol(dir);
        if (inBounds(nr, nc) && !(grid[nr][nc] instanceof Beetle)) { return true; }
        else { return false; }
    }

    public static boolean tryPlaceAnt(Creature[][] grid, int r, int c, char antChar)
    {
        if (!inBounds(r, c)) { return false; }
        if (grid[r][c] != null) { return false; }
        grid[r][c] = new Ant(antChar);
        return true;
    }

    public static boolean tryPlaceBeetle(Creature[][] grid, int r, int c, char beetChar)
    {
        if (!inBounds(r, c)) { return false; }
        if (grid[r][c] != null) { return false; }
        grid[r][c] = new Beetle(beetChar);
        return true;
    }

    private static int beetleRefineNoAnt(int dir, int r, int c,
                                         boolean canN, boolean canE, boolean canS, boolean canW,
                                         int dN, int dE, int dS, int dW)
    {
        boolean sees = (dN != Integer.MAX_VALUE) || (dE != Integer.MAX_VALUE) ||
                       (dS != Integer.MAX_VALUE) || (dW != Integer.MAX_VALUE);
        if (sees) return dir;

        int best = Creature.STAY;
        int bestScore = -1;

        if (canN)
        {
            int s = nearestEdgeDistance(r - 1, c);
            if (s > bestScore) { bestScore = s; best = Creature.N; }
        }
        if (canE)
        {
            int s = nearestEdgeDistance(r, c + 1);
            if (s > bestScore || (s == bestScore && best != Creature.N)) { bestScore = s; best = Creature.E; }
        }
        if (canS)
        {
            int s = nearestEdgeDistance(r + 1, c);
            if (s > bestScore || (s == bestScore && best != Creature.N && best != Creature.E)) { bestScore = s; best = Creature.S; }
        }
        if (canW)
        {
            int s = nearestEdgeDistance(r, c - 1);
            if (s > bestScore || (s == bestScore && best != Creature.N && best != Creature.E && best != Creature.S)) { bestScore = s; best = Creature.W; }
        }
        if (best != Creature.STAY) return best;

        if (canN) return Creature.N;
        if (canE) return Creature.E;
        if (canS) return Creature.S;
        if (canW) return Creature.W;
        return Creature.STAY;
    }

    private static int nearestEdgeDistance(int r, int c)
    {
        int up = r, down = 9 - r, left = c, right = 9 - c;
        int nearest = Math.min(Math.min(up, down), Math.min(left, right));
        return nearest;
    }
}
