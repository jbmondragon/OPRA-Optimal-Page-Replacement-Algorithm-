
package algorithms;

import java.util.*;

public class SimulationResult {
    private String algorithmName;
    private int[] referenceString;
    private int frameSize;
    private int pageFaults;
    private int pageHits;
    private List<Step> steps;
    private double hitRatio;
    private double faultRatio;

    public SimulationResult(String algorithmName, int[] referenceString, int frameSize) {
        this.algorithmName = algorithmName;
        this.referenceString = referenceString;
        this.frameSize = frameSize;
        this.steps = new ArrayList<>();
        this.pageFaults = 0;
        this.pageHits = 0;
    }

    public void addStep(int pageIndex, int pageNumber, int[] frameState, boolean isFault) {
        steps.add(new Step(pageIndex, pageNumber, frameState.clone(), isFault));
        if (isFault) {
            pageFaults++;
        } else {
            pageHits++;
        }
    }

    public void calculateRatios() {
        int total = referenceString.length;
        hitRatio = (double) pageHits / total * 100;
        faultRatio = (double) pageFaults / total * 100;
    }

    // Getters
    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getPageFaults() {
        return pageFaults;
    }

    public int getPageHits() {
        return pageHits;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public double getHitRatio() {
        return hitRatio;
    }

    public double getFaultRatio() {
        return faultRatio;
    }

    // Step inner class
    public static class Step {
        private int pageIndex;
        private int pageNumber;
        private int[] frameState;
        private boolean isFault;
        private int timestamp;

        public Step(int pageIndex, int pageNumber, int[] frameState, boolean isFault) {
            this.pageIndex = pageIndex;
            this.pageNumber = pageNumber;
            this.frameState = frameState;
            this.isFault = isFault;
            this.timestamp = (int) System.currentTimeMillis();
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int[] getFrameState() {
            return frameState;
        }

        public boolean isFault() {
            return isFault;
        }

        public int getTimestamp() {
            return timestamp;
        }
    }
}