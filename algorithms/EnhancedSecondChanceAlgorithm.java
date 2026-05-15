// EnhancedSecondChanceAlgorithm.java
package algorithms;

import java.util.Arrays;

public class EnhancedSecondChanceAlgorithm implements Algorithm {

    // Page classes based on (referenceBit, modifyBit) pair:
    //   Class 0: (0, 0) — not recently used, not modified   → best victim
    //   Class 1: (0, 1) — not recently used, but modified
    //   Class 2: (1, 0) — recently used, not modified
    //   Class 3: (1, 1) — recently used, and modified        → worst victim
    private static final int CLASS_0 = 0;
    private static final int CLASS_1 = 1;
    private static final int CLASS_2 = 2;
    private static final int CLASS_3 = 3;

    private int hand; // circular clock pointer

    public EnhancedSecondChanceAlgorithm() {
        this.hand = 0;
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("Enhanced Second Chance", referenceString, frameSize);

        EnhancedFrame[] frames = new EnhancedFrame[frameSize];
        for (int i = 0; i < frameSize; i++) {
            frames[i] = new EnhancedFrame(-1);
        }

        hand = 0; // reset hand at the start of each simulation

        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean isFault = false;
            boolean found = false;

            // Check if page is already in frames
            for (int j = 0; j < frameSize; j++) {
                if (frames[j].getPageNumber() == page) {
                    // Page hit: set reference bit. Modify bit is left as-is
                    // (a real OS would set it if this access is a write;
                    //  here we conservatively leave it unchanged on hits).
                    frames[j].setReferenceBit(true);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Page fault - find the best victim using the NRU/ESC multi-pass scan
                isFault = true;
                int replaceIndex = findPageToReplace(frames, frameSize);

                // Replace the page; new page enters with ref=1, modify=0
                frames[replaceIndex] = new EnhancedFrame(page);
                frames[replaceIndex].setReferenceBit(true);
                frames[replaceIndex].setModifyBit(false);

                // Advance hand past the newly inserted page
                hand = (replaceIndex + 1) % frameSize;
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

    /**
     * Finds the index of the page to replace using the Enhanced Second Chance algorithm.
     *
     * Four passes are made through the circular buffer (starting at 'hand'):
     *   Pass 1 (target class 0): find a (0,0) page — replace immediately.
     *   Pass 2 (target class 1): find a (0,1) page — replace immediately.
     *   Pass 3 (target class 2): find a (1,0) page — clear ref bits of all
     *                            pages passed over so they fall to a lower class.
     *   Pass 4 (target class 3): find any remaining page.
     *
     * Passes 1 and 2 do NOT modify any bits.
     * Passes 3 and 4 clear reference bits of every page scanned, giving those
     * pages a lower class on future replacements.
     */
    private int findPageToReplace(EnhancedFrame[] frames, int frameSize) {
        // Pass 1 & 2: read-only scan — look for class 0, then class 1
        for (int targetClass = CLASS_0; targetClass <= CLASS_1; targetClass++) {
            int idx = hand;
            for (int count = 0; count < frameSize; count++) {
                if (frames[idx].getPageNumber() == -1) {
                    return idx; // empty slot: always use it
                }
                if (getPageClass(frames[idx]) == targetClass) {
                    return idx;
                }
                idx = (idx + 1) % frameSize;
            }
        }

        // Pass 3: look for class 2, clearing reference bits of every page scanned
        {
            int idx = hand;
            for (int count = 0; count < frameSize; count++) {
                if (frames[idx].getPageNumber() == -1) {
                    return idx;
                }
                if (getPageClass(frames[idx]) == CLASS_2) {
                    return idx;
                }
                // Clear the reference bit so this page falls to class 0 or 1 next time
                frames[idx].setReferenceBit(false);
                idx = (idx + 1) % frameSize;
            }
        }

        // Pass 4: all pages now have ref=0 (cleared in pass 3), so pick first class 1
        // (originally class 3 with ref cleared → now class 1), falling back to hand
        {
            int idx = hand;
            for (int count = 0; count < frameSize; count++) {
                if (frames[idx].getPageNumber() == -1) {
                    return idx;
                }
                if (getPageClass(frames[idx]) == CLASS_1) {
                    return idx;
                }
                idx = (idx + 1) % frameSize;
            }
        }

        // Fallback: should never be reached with a full frame set
        return hand;
    }

    private int getPageClass(EnhancedFrame frame) {
        int ref = frame.hasReferenceBit() ? 1 : 0;
        int mod = frame.hasModifyBit()    ? 1 : 0;
        // (ref=0,mod=0)=0, (ref=0,mod=1)=1, (ref=1,mod=0)=2, (ref=1,mod=1)=3
        return ref * 2 + mod;
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