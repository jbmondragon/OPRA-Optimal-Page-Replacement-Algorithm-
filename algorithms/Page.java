package algorithms;

public class Page {
    private int pageNumber;
    private int lastUsedTime;
    private int loadedTime;
    private boolean isLoaded;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.lastUsedTime = -1;
        this.loadedTime = -1;
        this.isLoaded = false;
    }

    // Getters and Setters
    public int getPageNumber() {
        return pageNumber;
    }

    public int getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(int time) {
        this.lastUsedTime = time;
    }

    public int getLoadedTime() {
        return loadedTime;
    }

    public void setLoadedTime(int time) {
        this.loadedTime = time;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public String toString() {
        return String.valueOf(pageNumber);
    }
}
