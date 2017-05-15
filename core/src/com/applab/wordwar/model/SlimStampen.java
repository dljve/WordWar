package com.applab.wordwar.model;

import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the SlimStampen algorithm
 * Based on Nijboer's "Optimal fact learning: Applying presentation scheduling to realistic conditions."
 *
 * - Model doesn't use personalized fixed time, it could be added but Koelewijn (2010) shows
 * that the addition of personalized initial alpha and fixed time make no significant difference
 *
 * @author Douwe van Erp
 *
 * References
 * --------------------------
 * Nijboer, M. E. N. N. O. "Optimal fact learning: Applying presentation scheduling to realistic
 *   conditions." Groningen, The Netherlands: Unpublished master's thesis, University of Groningen (2011).
 *
 * van Thiel, W. G. E. "Optimize learning with reaction time based spacing." (2010).
 */

public class SlimStampen {

    private Random random;
    private boolean randomNovel;
    private long startTime;

    private Map<Item, ArrayList<BigDecimal >> d; // Decay values
    private Map<Item, ArrayList<BigDecimal >> a; // Alpha values
    private Map<Item, ArrayList<BigDecimal >> t; // Time points
    private Map<Item, ArrayList<BigDecimal >> RT;// Observed reaction times

    private ArrayList<Item> itemSet;        // Novel item set
    private ArrayList<Item> presentationSet;// Presentation set

    private BigDecimal n, c, F, f, threshold;   // Model parameters
    private MathContext mc;
    private int p = 12;

    /**
     * Start new SlimStampen session
     * @param itemSet the item set to be scheduled
     * @param randomNovel <code>true</code> to choose novel items for the user
     *                    <code>false</code> to let the user pick novel items
     * @param n Lookahead time in seconds to try to select items before they are forgotten
     * @param c Scaling parameter that determines the strength of the spacing effect
     * @param F Reaction Time (RT) scale parameter
     * @param f Fixed time for non-memory related processes, i.e. process the visual information
     *          of the stimuli and motor control required to react (e.g. press a key)
     * @param threshold Retrieval threshold for activation value, usually between -0.5 and -0.8
     *                  (Van Woudenberg, 2008; Van Thiel, 2010). This gives the maximum spacing,
     *                  while retaining the benefit of the testing effect (Carrier and Pashler, 1992).
     */
    public SlimStampen(ArrayList<Item> itemSet, boolean randomNovel,
                       BigDecimal n, BigDecimal c, BigDecimal f, BigDecimal F, BigDecimal threshold) {
        random = new Random();
        this.randomNovel = randomNovel;
        this.itemSet = itemSet;
        presentationSet = new ArrayList();
        this.n = n; this.c = c;
        this.F = F; this.f = f;
        this.threshold = threshold;
        mc = new MathContext(20, RoundingMode.HALF_UP);

        // Initialize lists
        a = new LinkedHashMap<Item,ArrayList<BigDecimal>>();
        t = new LinkedHashMap<Item,ArrayList<BigDecimal>>();
        RT = new LinkedHashMap<Item,ArrayList<BigDecimal>>();
        for ( Item item : itemSet ) {
            a.put(item, new ArrayList());
            t.put(item, new ArrayList());
            RT.put(item, new ArrayList());
        }

        // First session?
        startTime = System.currentTimeMillis();
        if (isFirstSession()) {
            // Adjust all items according to psychological time
        }
    }

    public ArrayList<Item> getForgottenTrials() {
        ArrayList<Item> forgotten = new ArrayList<Item>();

        BigDecimal T = getTime();
        for (int i = 0; i < presentationSet.size(); i++) {
            Item item = presentationSet.get(i);
            BigDecimal m_item = m(item, T); // Get activation value at current time
            // Negative infinity or below threshold
            if (m_item != null ) {
                if (m_item.compareTo(threshold) < 0) {
                    forgotten.add(item);
                }
            }
        }
        return forgotten;
    }

    /**
     * Returns the next trial the user should attempt
     * See also Figure 2.5 in Nijboer (2011)
     * @return a Trial object
     */
    public Trial nextTrial() {
        Item item;
        Trial.TrialType type;

        // Find the item with the lowest activation time over n seconds
        System.out.println("_____________________________________");
        BigDecimal T = getTime(), lookahead = T.add(n), m_item, m_lowest = null;
        Item lowest = null;
        for (int i = 0; i < presentationSet.size(); i++) {
            item = presentationSet.get(i);
            if ( lowest == null ) {
                lowest = item;
            } else {
                m_item = m(item, lookahead);
                m_lowest = m(lowest, lookahead);
                if (m_item == null || // Negative infinity
                        m_item.compareTo(m_lowest) < 0) // smaller than lowest item
                    lowest = item;
            }

            BigDecimal m = m(lowest, lookahead);
            System.out.println(item + "\t\t" + (m==null?"-∞":m));
        }
        System.out.println("_____________________________________");

        // Below threshold? (if there is a lowest item AND it's -infinity or below threshold)
        if (lowest != null) m_lowest = m(lowest, lookahead);
        if (lowest != null && (m_lowest == null || m_lowest.compareTo(threshold) < 0) ) {
            //System.out.println("below threshold");
            type = Trial.TrialType.TEST;
            item = lowest;
        } else if (!itemSet.isEmpty()) {  // Any new items remaining?
            //System.out.println("new item");
            // Add a new item to the presentation set
            type = Trial.TrialType.STUDY;
            if (randomNovel) { // Select a random new item
                item = itemSet.get(random.nextInt(itemSet.size()));
                addNewItem(item);
            } else item = null; // The user can select a new item and call addNewItem() later
        } else {
            //System.out.println("get lowest item");
            // Get the item with the lowest activation
            type = Trial.TrialType.TEST;
            item = lowest;
        }

        // Present the item
        return new Trial(item, type);
    }

    /**
     * Add a novel item to the presentation set
     * This function should also be called if the user manually chooses a new item
     * @param i the novel item
     */
    public void addNewItem(Item i) {
        i = findItem(i);
        presentationSet.add(i);
        itemSet.remove(i);
    }

    /**
     * Call this to when the test trial is presented to the user to store the time point
     */
    public void practiceEvent(Item i, long timestamp) {
        i = findItem(i);
        BigDecimal timepoint = BigDecimal.valueOf((double)(timestamp-startTime)/1000);
        t.get(i).add(timepoint);
    }

    public Item findItem(Item i) {
        Item found = null;
        for (Item item : itemSet) {
            if (item.equals(i))
                found = item;
        }
        for (Item item : presentationSet) {
            if (item.equals(i))
                found = item;
        }
        return found;
    }

    /**
     * Updates the decay and alpha values of the model (call this at the first key press or reaction)
     * The numbers in the comments behind the formulas refer to the equations in Nijboer (2011)
     * @param i
     */

    public void updateModel(Item i, long timestamp) {
        i = findItem(i);

        int n = t.get(i).size(); // Number of previous rehearsals
        int J = n-1; // Index of last rehearsal
        BigDecimal T = BigDecimal.valueOf((double)(timestamp-startTime)/1000);
        //BigDecimal T = getTime(); // The current time t

        // Calculate a maximum reaction time as (2.11) [1.5*(F*e^(-threshold)+f)]
        BigDecimal RT_max = BigDecimal.valueOf(1.5).multiply(F.multiply(BigDecimalMath.exp(threshold.negate())).add(f));

        // Determine the observed reaction time
        BigDecimal RT_obs = RT_max.min( T.subtract(t.get(i).get(J)) );
        RT.get(i).add(RT_obs);

        // Observed activation of the last rehearsal (2.5) [-Math.log((RT_obs-f)/F)]
        BigDecimal m_obs = BigDecimalMath.log(RT_obs.subtract(f).divide(F, mc).max(BigDecimal.ZERO)).negate();
        m_obs.setScale(p, BigDecimal.ROUND_HALF_UP);

        // Activation not contributing to the previous rehearsal (Up to t_n-1)
        BigDecimal m = BigDecimal.ZERO;
        m.setScale(p, BigDecimal.ROUND_HALF_UP);
        m = m(i, t.get(i).get(n-1) );
        m = (m==null) ? BigDecimal.ZERO : BigDecimalMath.exp(m); // exp.

        // Calculate the decay (2.9)
        BigDecimal decay = BigDecimalMath.log( BigDecimalMath.exp(m_obs).subtract(m) )
                .divide( BigDecimalMath.log( T.subtract(t.get(i).get(J)) ), mc).negate();

        // Alpha optimization
        // See Figure 2.2 in Van Thiel (2010) for a flowchart of the basic process
        BigDecimal alpha, a1, a2, a_mean;
        if (n < 2) {
            // After the first rehearsal, the standard alpha of 0.3 is returned
            alpha = BigDecimal.valueOf(0.3);
        } else {
            // Calculate the alpha (2.10)
            alpha = decay.subtract( c.multiply(BigDecimalMath.exp( m(i,t.get(i).get(J)) ) ) );

            // New method to estimate new alpha by Nijboer (2011)
            // Slow estimated reaction time means too high decay, so alpha should be lowered
            BigDecimal RT_est = F.multiply( c.divide( decay.subtract(alpha), mc) ).add(f); // or F*Math.exp(-m(i,t.get(i).get(J)))+f;

            a1 = a.get(i).get(J - 1);
            a2 = (RT.get(i).get(J).compareTo(RT_est) < 0) ?
                    a1.subtract(BigDecimal.valueOf(0.05)) : a1.add(BigDecimal.valueOf(0.05));
            a_mean = a1.add(a2).divide(BigDecimal.valueOf(2), mc);

            // Do a binary search between a1 and a2 to converge towards an optimal alpha
            // Repeat six times, as an acceptable compromise between speed and precision
            for (int x = 0; x < 6; x++) {
                int window_size = 4;
                BigDecimal  E_a1 = BigDecimal.ZERO, E_a2 = BigDecimal.ZERO, RT_a1, RT_a2; //E_a_mean = 0;

                // We can rewrite (2.2) as m = ln[(d_ij-a)/c]. Plugging this into (2.3) we get
                // RT = F*(c/(d_ij-a))+f for the estimated reaction time. Then we calculate, for
                // all 3 alphas, the (absolute) error between the estimated and observed response
                // times for the last 4 rehearsals (i.e. a window size of 4, see Nijboer (2011)).
                // The first test trial (j=0) is not used as it contains no useful information.
                for (int j = J; j > 0 && j > J - window_size; j--) {
                    RT_a1 = F.multiply( c.divide(decay.subtract(a1),mc) ).add(f);
                    E_a1 = E_a1.add( RT_a1.subtract(RT.get(i).get(j)).abs() );
                    RT_a2 = F.multiply( c.divide(decay.subtract(a2),mc) ).add(f);
                    E_a2 = E_a2.add( RT_a2.subtract(RT.get(i).get(j)).abs() );
                }
                a1 = (E_a1.compareTo(E_a2) < 0) ? a1 : a2; // Get a1 = min_arg(E_a1, E_a2)
                a2 = a_mean;
                a_mean = a1.add(a2).divide(BigDecimal.valueOf(2),mc);
            }
            alpha = a_mean; // Use the last mean alpha
        }
        a.get(i).add(alpha);
        printItem(i);
    }
    /* // Old method to estimate new alpha by Van Thiel (2010)
    if (n == 2) {
        // Look for an alpha that fits the decay of the first two rehearsals
        a1 = 0.01;
        a2 = 0.5;
        a_mean = 0.2505;
    } else {
        // Find the alpha with the optimal alpha of previous rehearsal
        a1 = a.get(i).get(J - 1);
        a2 = alpha;
        a_mean = (a1 + a2)/2;
    }*/

    /**
     * Base-level activation equation (see Equation 2.1 in Nijboer (2011))
     * @param i an item
     * @param T a time point
     * @return activation m
     */
    private BigDecimal m(Item i, BigDecimal T) {
        BigDecimal m = BigDecimal.ZERO, decay, m_ij;
        m.setScale(p, BigDecimal.ROUND_HALF_UP);
        int n = t.get(i).size();

        for (int j = 0; j < n && t.get(i).get(j).compareTo(T) < 0; j++) {
            m_ij = m(i,t.get(i).get(j));
            if (m_ij == null) { // if the activation is -∞, just return the alpha
                decay = a.get(i).get(j);
            } else {
                decay = c.multiply( BigDecimalMath.exp(m_ij) ).add(a.get(i).get(j));
            }
            m = m.add( BigDecimalMath.pow( T.subtract(t.get(i).get(j)), decay.negate() ) );
        }
        if (m.equals(BigDecimal.ZERO))
            return null; // Negative infinity
        return BigDecimalMath.log(m);
    }

    private boolean isFirstSession() {
        return true;
    }

    private void endSession() {
        // TODO: Save final alpha values for user? See Nijboer discussion
        // See eq. 2.16 on page 20. Maybe in later version, not prototype
        return;
    }

    /**
     * Return the time in seconds elapsed since the creation of the model
     * @return time in milliseconds
     */
    private BigDecimal getTime() {
        return BigDecimal.valueOf((double)(System.currentTimeMillis()-startTime)/1000);
    }

    // TODO: remove this
    private void printItem(Item i) {
        String strt = "", stra="", strRT="", strd="";
        for (int j=0;j<t.get(i).size();j++) strt += t.get(i).get(j).toPlainString() + " ";
        for (int j=0;j<RT.get(i).size();j++) strRT += RT.get(i).get(j).toPlainString() + " ";
        for (int j=0;j<a.get(i).size();j++) stra += a.get(i).get(j).toPlainString() + " ";
        System.out.println("-----------------------------");
        System.out.println("t: " + strt + " ");
        System.out.println("RT: " + strRT  + " ");
        System.out.println("a: " + stra  + " ");
    }


}
