//Name: Oliver Perez

public class Ant extends Creature
{
    private int sinceBreed;

    public Ant() { super('a'); this.sinceBreed = 0; }
    public Ant(char g) { super(g); this.sinceBreed = 0; }

    public int move(int dN, int dE, int dS, int dW,
                    boolean canN, boolean canE, boolean canS, boolean canW)
    {
        int min = Math.min(Math.min(dN, dE), Math.min(dS, dW));
        if (min == Integer.MAX_VALUE)
        {
            return STAY;
        }

        boolean awayN = (dS == min) && canN;
        boolean awayE = (dW == min) && canE;
        boolean awayS = (dN == min) && canS;
        boolean awayW = (dE == min) && canW;

        if (awayN) return N;
        if (awayE) return E;
        if (awayS) return S;
        if (awayW) return W;
        return STAY;
    }

    public boolean breed()
    {
        sinceBreed++;
        if (sinceBreed >= 3)
        {
            sinceBreed = 0;
            return true;
        }
        return false;
    }
}
