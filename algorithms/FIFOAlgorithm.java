// FIFOAlgorithm.java
package algorithms;

import java.util.*;

public class FIFOAlgorithm implements Algorithm {

    @Override
    public SimulationResult simulate(int[] referenceString, int frameSize) {
        SimulationResult result = new SimulationResult("FIFO", referenceString, frameSize);

        Queue<Integer> frameQueue = new LinkedList<>();
        Set<Integer> frameSet = new HashSet<>();
        int[] currentFrames = new int[frameSize];
        Arrays.fill(currentFrames, -1); // -1 indicates empty frame

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean isFault = false;

            // Check if page is already in frames
            if (!frameSet.contains(page)) {
                // Page fault occurred
                isFault = true;

                if (frameQueue.size() < frameSize) {
                    // Add to empty frame
                    frameQueue.add(page);
                    frameSet.add(page);
                    currentFrames[frameQueue.size() - 1] = page;
                } else {
                    // Remove oldest page
                    int oldestPage = frameQueue.poll();
                    frameSet.remove(oldestPage);

                    // Add new page
                    frameQueue.add(page);
                    frameSet.add(page);

                    // Update current frames array
                    updateFramesArray(currentFrames, frameQueue);
                }
            }

            result.addStep(i, page, currentFrames, isFault);
        }

        result.calculateRatios();
        return result;
    }

    private void updateFramesArray(int[] currentFrames, Queue<Integer> frameQueue) {
        Arrays.fill(currentFrames, -1);
        int index = 0;
        for (Integer page : frameQueue) {
            currentFrames[index++] = page;
        }
    }
}