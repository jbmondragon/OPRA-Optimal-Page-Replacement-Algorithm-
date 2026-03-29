// SecondChanceAlgorithm.java
package algorithms;

import java.util.*;

public class SecondChanceAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("Second Chance", referenceString, frameSize);

        SecondChanceFrame[] frames = new SecondChanceFrame[frameSize];
        for (int i = 0; i < frameSize; i++) {
            frames[i] = new SecondChanceFrame(-1);
        }

        int hand = 0;
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean isFault = false;
            boolean found = false;

            // Check if page is already in frames
            for (int j = 0; j < frameSize; j++) {
                if (frames[j].getPageNumber() == page) {
                    frames[j].setReferenceBit(true);
                    frames[j].setSecondChance(true);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Page fault - need to replace
                isFault = true;

                while (true) {
                    if (frames[hand].getPageNumber() == -1) {
                        // Empty frame found
                        frames[hand] = new SecondChanceFrame(page);
                        frames[hand].setReferenceBit(true);
                        frames[hand].setSecondChance(true);
                        hand = (hand + 1) % frameSize;
                        break;
                    }

                    if (frames[hand].hasSecondChance()) {
                        // Give second chance
                        frames[hand].setSecondChance(false);
                        frames[hand].setReferenceBit(false);
                        hand = (hand + 1) % frameSize;
                    } else {
                        // Replace this page
                        frames[hand] = new SecondChanceFrame(page);
                        frames[hand].setReferenceBit(true);
                        frames[hand].setSecondChance(true);
                        hand = (hand + 1) % frameSize;
                        break;
                    }
                }
            }

            // Update current frames array
            for (int j = 0; j < frameSize; j++) {
                currentFrames[j] = frames[j].getPageNumber();
            }

            result.addStep(i, page, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    // Inner class for Second Chance frames
    private static class SecondChanceFrame {
        private int pageNumber;
        private boolean secondChance;
        private boolean referenceBit;

        public SecondChanceFrame(int pageNumber) {
            this.pageNumber = pageNumber;
            this.secondChance = true;
            this.referenceBit = true;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public boolean hasSecondChance() {
            return secondChance;
        }

        public void setSecondChance(boolean chance) {
            this.secondChance = chance;
        }

        public boolean hasReferenceBit() {
            return referenceBit;
        }

        public void setReferenceBit(boolean bit) {
            this.referenceBit = bit;
        }
    }
}