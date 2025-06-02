package Model;

public class Event {
    private final String myFilename;
    private final String myEventType;
    private final String myTimestamp;
    private final String myExtension;
    private final String myDirectory;

    public Event(final String theFilename, final String theEventType,
                 final String theTimestamp, final String theExtension, final String theDirectory) {
        myFilename = theFilename;
        myEventType = theEventType;
        myTimestamp = theTimestamp;
        myExtension = theExtension;
        myDirectory = theDirectory;
    }

    public String getFilename() { return myFilename; }
    public String getEventType() { return myEventType; }
    public String getTimestamp() { return myTimestamp; }
    public String getExtension() { return myExtension; }
    public String getDirectory() { return myDirectory; }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s",
                myFilename,
                myEventType,
                myTimestamp,
                myExtension,
                myDirectory);
    }
}

