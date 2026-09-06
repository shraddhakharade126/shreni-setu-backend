package com.shrenisetu.model;

public class VerificationStatuses {

    public enum CraftStatus {
        NOT_STARTED,
        PENDING,
        COMPLETED
    }

    public enum PortfolioStatus {
        NOT_STARTED,
        PENDING,
        SUBMITTED
    }

    public enum ProcessStatus {
        NOT_STARTED,
        PENDING,
        SUBMITTED
    }

    public enum OverallStatus {
        IN_PROGRESS,
        VERIFIED,
        REJECTED,
        REQUIRES_REVIEW
    }
}
