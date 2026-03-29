// LRUAlgorithm.java
package algorithms;

import java.util.*;

public class LRUAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("LRU", referenceString, frameSize);

        List<Page> frames = new ArrayList<>(frameSize);
        Map<Integer, Page> pageMap = new HashMap<>();
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int pageNum = referenceString[i];
            boolean isFault = false;

            // Get or create page
            Page currentPage = pageMap.get(pageNum);
            if (currentPage == null) {
                currentPage = new Page(pageNum);
                pageMap.put(pageNum, currentPage);
            }

            // Check if page is in frames
            if (!currentPage.isLoaded()) {
                // Page fault
                isFault = true;

                if (frames.size() < frameSize) {
                    // Add to empty frame
                    currentPage.setLoaded(true);
                    currentPage.setLoadedTime(i);
                    currentPage.setLastUsedTime(i);
                    frames.add(currentPage);
                } else {
                    // Find least recently used page
                    Page lruPage = findLRUPage(frames);
                    lruPage.setLoaded(false);
                    frames.remove(lruPage);

                    // Add new page
                    currentPage.setLoaded(true);
                    currentPage.setLoadedTime(i);
                    currentPage.setLastUsedTime(i);
                    frames.add(currentPage);
                }
            } else {
                // Page hit - update last used time
                currentPage.setLastUsedTime(i);
            }

            // Update current frames array
            updateFramesArray(currentFrames, frames);
            result.addStep(i, pageNum, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    private Page findLRUPage(List<Page> frames) {
        Page lru = frames.get(0);
        for (Page page : frames) {
            if (page.getLastUsedTime() < lru.getLastUsedTime()) {
                lru = page;
            }
        }
        return lru;
    }

    private void updateFramesArray(int[] currentFrames, List<Page> frames) {
        Arrays.fill(currentFrames, -1);
        for (int i = 0; i < frames.size(); i++) {
            currentFrames[i] = frames.get(i).getPageNumber();
        }
    }
}