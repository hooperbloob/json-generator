package com.comcast.datafill;

import java.time.Duration;

/**
 *
 * @author bremed200
 */
public class Limits {

    public final Long maxFileCount;
    public final Long maxRecordCount;
    public final Long maxSize;
    public final Duration maxTime;

    private Limits(Builder builder) {
        maxFileCount = builder.maxFileCount;
        maxRecordCount = builder.maxRecordCount;
        maxTime = builder.maxTime;
        maxSize = builder.maxSize;
    }

    boolean isOverdue(Status status) {

        if (maxTime == null) return false;

        long elapsed = System.currentTimeMillis() - status.startTime;
        return elapsed > maxTime.toMillis();
    }

    boolean canContinue(Status status) {

        if (maxFileCount != null && status.filesCreated() > maxFileCount) return false;
        if (maxRecordCount != null && status.recordsCreated() > maxRecordCount) return false;
        if (maxSize != null && status.filespaceConsumed() > maxSize) return false;

        if (isOverdue(status)) return false;

        return true;
    }

    public static class Builder {
        private Long maxFileCount;
        private Long maxRecordCount;
        private Long maxSize;
        private Duration maxTime;
        public static Builder newInstance() { return new Builder(); }

        public Builder maxSize(long theSize) { maxSize = theSize; return this; }
        public Builder maxFileCount(long theCount) { maxFileCount = theCount; return this; }
        public Builder maxRecordCount(long theCount) { maxRecordCount = theCount; return this; }
        public Builder maxTime(Duration theDuration) { maxTime = theDuration; return this; }

        public Limits build() { return new Limits(this); }
    }
}
