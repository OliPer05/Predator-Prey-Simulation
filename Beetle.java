//Name: Oliver Perez

public class Beetle extends Creature
{
    private int sinceBreed;
    private int sinceMeal;

    public Beetle() { super('B'); this.sinceBreed = 0; this.sinceMeal = 0; }
    public Beetle(char g) { super(g); this.sinceBreed = 0; this.sinceMeal = 0; }

    public int move(int dN, int dE, int dS, int dW,
                    boolean canN, boolean canE, boolean canS, boolean canW)
    {
        int min = Integer.MAX_VALUE;
        if (canN) min = Math.min(min, dN);
        if (canE) min = Math.min(min, dE);
        if (canS) min = Math.min(min, dS);
        if (canW) min = Math.min(min, dW);

        if (min != Integer.MAX_VALUE)
        {
            if (canN && dN == min) return N;
            if (canE && dE == min) return E;
            if (canS && dS == min) return S;
            if (canW && dW == min) return W;
            return STAY;
        }

        // no ant visible: caller will refine, but default is NESW if needed
        if (canN) return N;
        if (canE) return E;
        if (canS) return S;
        if (canW) return W;
        return STAY;
    }

    public boolean breed()
    {
        sinceBreed++;
        if (sinceBreed >= 8)
        {
            sinceBreed = 0;
            return true;
        }
        return false;
    }

    public void ate() { sinceMeal = 0; }
    public void noMeal() { sinceMeal++; }
    public int starve() { return sinceMeal; }
}
