// OptimalAlgorithm.java
package algorithms;

import java.util.*;

public class OptimalAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("Optimal", referenceString, frameSize);

        Set<Integer> frameSet = new HashSet<>();
        List<Integer> frames = new ArrayList<>();
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1);

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean isFault = false;

            if (!frameSet.contains(page)) {
                // Page fault
                isFault = true;

                if (frameSet.size() < frameSize) {
                    // Add to empty frame
                    frameSet.add(page);
                    frames.add(page);
                } else {
                    // Find page to replace (the one used farthest in future)
                    int pageToReplace = findOptimalReplacement(referenceString, i, frames);
                    frameSet.remove(pageToReplace);
                    frames.remove((Integer) pageToReplace);

                    // Add new page
                    frameSet.add(page);
                    frames.add(page);
                }
            }

            // Update current frames array
            updateFramesArray(currentFrames, frames);
            result.addStep(i, page, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    private int findOptimalReplacement(int[] referenceString, int currentIndex, List<Integer> frames) {
        Map<Integer, Integer> nextUse = new HashMap<>();

        // Initialize next use as infinity
        for (int page : frames) {
            nextUse.put(page, Integer.MAX_VALUE);
        }

        // Find next use for each page in frames
        for (int i = currentIndex + 1; i < referenceString.length; i++) {
            int page = referenceString[i];
            if (frames.contains(page) && nextUse.get(page) == Integer.MAX_VALUE) {
                nextUse.put(page, i);
            }
        }

        // Find page with farthest next use
        int farthestPage = frames.get(0);
        int farthestDistance = nextUse.get(farthestPage);

        for (int page : frames) {
            if (nextUse.get(page) > farthestDistance) {
                farthestDistance = nextUse.get(page);
                farthestPage = page;
            }
        }

        return farthestPage;
    }

    private void updateFramesArray(int[] currentFrames, List<Integer> frames) {
        Arrays.fill(currentFrames, -1);
        for (int i = 0; i < frames.size(); i++) {
            currentFrames[i] = frames.get(i);
        }
    }
}