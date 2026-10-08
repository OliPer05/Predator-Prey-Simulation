//Name: Oliver Perez

public abstract class Creature
{
    public static final int N = 0;
    public static final int E = 1;
    public static final int S = 2;
    public static final int W = 3;
    public static final int STAY = 4;

    protected char glyph;

    public Creature() { this.glyph = ' '; }
    public Creature(char g) { this.glyph = g; }

    public abstract int move(int dN, int dE, int dS, int dW,
                             boolean canN, boolean canE, boolean canS, boolean canW);

    public abstract boolean breed();

    public String toString()
    {
        return Character.toString(glyph);
    }
}
