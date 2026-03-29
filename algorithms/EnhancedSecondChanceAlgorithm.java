// EnhancedSecondChanceAlgorithm.java
package algorithms;

import java.util.*;

public class EnhancedSecondChanceAlgorithm implements Algorithm {

    private static final int CLASS_0 = 0; // (ref=0, mod=0) - best to replace
    private static final int CLASS_1 = 1; // (ref=0, mod=1)
    private static final int CLASS_2 = 2; // (ref=1, mod=0)
    private static final int CLASS_3 = 3; // (ref=1, mod=1) - worst to replace

    private Random random;

    public EnhancedSecondChanceAlgorithm() {
        this.random = new Random();
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("Enhanced Second Chance", referenceString, frameSize);

        EnhancedFrame[] frames = new EnhancedFrame[frameSize];
        for (int i = 0; i < frameSize; i++) {
            frames[i] = new EnhancedFrame(-1);
        }

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
                    // Simulate possible write operation (modify bit set)
                    if (random.nextDouble() < 0.3) {
                        frames[j].setModifyBit(true);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Page fault - need to replace
                isFault = true;

                // Find the best page to replace using enhanced algorithm
                int replaceIndex = findPageToReplace(frames);

                // Replace the page
                frames[replaceIndex] = new EnhancedFrame(page);
                frames[replaceIndex].setReferenceBit(true);
                frames[replaceIndex].setModifyBit(false);
            }

            // Update current frames array
            for (int j = 0; j < frameSize; j++) {
                currentFrames[j] = frames[j].getPageNumber();
            }

            result.addStep(i, page, currentFrames, isFault);

            // Periodically clear reference bits (simulate clock interrupt)
            if (i > 0 && i % 10 == 0) {
                for (EnhancedFrame frame : frames) {
                    if (frame.getPageNumber() != -1) {
                        frame.setReferenceBit(false);
                    }
                }
            }
        }

        result.calculateRatios();
        return result;
    }

    private int findPageToReplace(EnhancedFrame[] frames) {
        // First pass: look for class 0 pages (best to replace)
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].getPageNumber() == -1) {
                return i;
            }
            if (getPageClass(frames[i]) == CLASS_0) {
                return i;
            }
        }

        // Second pass: look for class 1 pages
        for (int i = 0; i < frames.length; i++) {
            if (getPageClass(frames[i]) == CLASS_1) {
                return i;
            }
        }

        // Third pass: look for class 2 pages
        for (int i = 0; i < frames.length; i++) {
            if (getPageClass(frames[i]) == CLASS_2) {
                return i;
            }
        }

        // Finally, return class 3 page
        return 0;
    }

    private int getPageClass(EnhancedFrame frame) {
        int ref = frame.hasReferenceBit() ? 1 : 0;
        int mod = frame.hasModifyBit() ? 1 : 0;

        // Class mapping: (ref, mod) -> class
        if (ref == 0 && mod == 0)
            return CLASS_0;
        if (ref == 0 && mod == 1)
            return CLASS_1;
        if (ref == 1 && mod == 0)
            return CLASS_2;
        return CLASS_3;
    }

    // Inner class for Enhanced Second Chance frames
    private static class EnhancedFrame {
        private int pageNumber;
        private boolean referenceBit;
        private boolean modifyBit;

        public EnhancedFrame(int pageNumber) {
            this.pageNumber = pageNumber;
            this.referenceBit = false;
            this.modifyBit = false;
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

        public boolean hasModifyBit() {
            return modifyBit;
        }

        public void setModifyBit(boolean bit) {
            this.modifyBit = bit;
        }
    }
}