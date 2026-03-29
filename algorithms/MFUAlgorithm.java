// MFUAlgorithm.java
package algorithms;

import java.util.*;

public class MFUAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("MFU", referenceString, frameSize);

        List<MFUPage> frames = new ArrayList<>();
        Map<Integer, MFUPage> pageMap = new HashMap<>();
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int pageNum = referenceString[i];
            boolean isFault = false;

            // Get or create page
            MFUPage currentPage = pageMap.get(pageNum);
            if (currentPage == null) {
                currentPage = new MFUPage(pageNum);
                pageMap.put(pageNum, currentPage);
            }

            // Check if page is in frames
            if (!currentPage.isLoaded()) {
                // Page fault
                isFault = true;

                if (frames.size() < frameSize) {
                    // Add to empty frame
                    currentPage.setLoaded(true);
                    currentPage.incrementFrequency();
                    frames.add(currentPage);
                } else {
                    // Find most frequently used page
                    MFUPage mfuPage = findMFUPage(frames);
                    mfuPage.setLoaded(false);
                    frames.remove(mfuPage);

                    // Add new page
                    currentPage.setLoaded(true);
                    currentPage.incrementFrequency();
                    frames.add(currentPage);
                }
            } else {
                // Page hit - increment frequency
                currentPage.incrementFrequency();
                currentPage.setLastUsedTime(i);
            }

            // Update current frames array
            updateFramesArray(currentFrames, frames);
            result.addStep(i, pageNum, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    private MFUPage findMFUPage(List<MFUPage> frames) {
        MFUPage mfu = frames.get(0);
        for (MFUPage page : frames) {
            if (page.getFrequency() > mfu.getFrequency()) {
                mfu = page;
            } else if (page.getFrequency() == mfu.getFrequency()) {
                // Tie-breaker: use LRU
                if (page.getLastUsedTime() < mfu.getLastUsedTime()) {
                    mfu = page;
                }
            }
        }
        return mfu;
    }

    private void updateFramesArray(int[] currentFrames, List<MFUPage> frames) {
        Arrays.fill(currentFrames, -1);
        for (int i = 0; i < frames.size(); i++) {
            currentFrames[i] = frames.get(i).getPageNumber();
        }
    }

    // Inner class for MFU pages
    private static class MFUPage {
        private int pageNumber;
        private int frequency;
        private int lastUsedTime;
        private boolean isLoaded;

        public MFUPage(int pageNumber) {
            this.pageNumber = pageNumber;
            this.frequency = 0;
            this.lastUsedTime = -1;
            this.isLoaded = false;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getFrequency() {
            return frequency;
        }

        public void incrementFrequency() {
            this.frequency++;
        }

        public int getLastUsedTime() {
            return lastUsedTime;
        }

        public void setLastUsedTime(int time) {
            this.lastUsedTime = time;
        }

        public boolean isLoaded() {
            return isLoaded;
        }

        public void setLoaded(boolean loaded) {
            this.isLoaded = loaded;
        }
    }
}