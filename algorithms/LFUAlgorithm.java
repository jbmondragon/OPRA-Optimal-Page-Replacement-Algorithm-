// LFUAlgorithm.java
package algorithms;

import java.util.*;

public class LFUAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("LFU", referenceString, frameSize);

        List<LFUPage> frames = new ArrayList<>();
        Map<Integer, LFUPage> pageMap = new HashMap<>();
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int pageNum = referenceString[i];
            boolean isFault = false;

            // Get or create page
            LFUPage currentPage = pageMap.get(pageNum);
            if (currentPage == null) {
                currentPage = new LFUPage(pageNum);
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
                    // Find least frequently used page
                    LFUPage lfuPage = findLFUPage(frames);
                    lfuPage.setLoaded(false);
                    frames.remove(lfuPage);

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

    private LFUPage findLFUPage(List<LFUPage> frames) {
        LFUPage lfu = frames.get(0);
        for (LFUPage page : frames) {
            if (page.getFrequency() < lfu.getFrequency()) {
                lfu = page;
            } else if (page.getFrequency() == lfu.getFrequency()) {
                // Tie-breaker: use LRU
                if (page.getLastUsedTime() < lfu.getLastUsedTime()) {
                    lfu = page;
                }
            }
        }
        return lfu;
    }

    private void updateFramesArray(int[] currentFrames, List<LFUPage> frames) {
        Arrays.fill(currentFrames, -1);
        for (int i = 0; i < frames.size(); i++) {
            currentFrames[i] = frames.get(i).getPageNumber();
        }
    }

    // Inner class for LFU pages
    private static class LFUPage {
        private int pageNumber;
        private int frequency;
        private int lastUsedTime;
        private boolean isLoaded;

        public LFUPage(int pageNumber) {
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