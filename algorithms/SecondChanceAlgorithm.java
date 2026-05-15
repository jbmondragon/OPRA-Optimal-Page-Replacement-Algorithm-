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
                    // Page hit: set its reference bit to give it a second chance
                    frames[j].setReferenceBit(true);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Page fault - find a victim using the clock hand
                isFault = true;

                while (true) {
                    if (frames[hand].getPageNumber() == -1) {
                        // Empty frame: load page here
                        frames[hand] = new SecondChanceFrame(page);
                        frames[hand].setReferenceBit(true);
                        hand = (hand + 1) % frameSize;
                        break;
                    }

                    if (frames[hand].hasReferenceBit()) {
                        // Page has a second chance: clear its bit and move on
                        frames[hand].setReferenceBit(false);
                        hand = (hand + 1) % frameSize;
                    } else {
                        // Reference bit is 0: replace this page
                        frames[hand] = new SecondChanceFrame(page);
                        frames[hand].setReferenceBit(true);
                        hand = (hand + 1) % frameSize;
                        break;
                    }
                }
            }

            // Update current frames array for result recording
            for (int j = 0; j < frameSize; j++) {
                currentFrames[j] = frames[j].getPageNumber();
            }

            result.addStep(i, page, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    // Inner class for Second Chance frames.
    // Uses a single reference bit: true = page gets a second chance, false = eligible for replacement.
    private static class SecondChanceFrame {
        private int pageNumber;
        private boolean referenceBit;

        public SecondChanceFrame(int pageNumber) {
            this.pageNumber = pageNumber;
            this.referenceBit = false;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public boolean hasReferenceBit() {
            return referenceBit;
        }

        public void setReferenceBit(boolean bit) {
            this.referenceBit = bit;
        }
    }
}