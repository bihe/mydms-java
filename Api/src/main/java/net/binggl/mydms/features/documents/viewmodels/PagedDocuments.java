package net.binggl.mydms.features.documents.viewmodels;

import java.util.ArrayList;
import java.util.List;

public class PagedDocuments {

    private List<DocumentViewModel> documents;
    private long totalEntries;

    public PagedDocuments() {
        this.documents = new ArrayList<>();
    }

    public PagedDocuments(List<DocumentViewModel> documents, long totalEntries, long entries) {
        this.documents = documents;
        this.totalEntries = totalEntries;
    }

    public List<DocumentViewModel> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentViewModel> documents) {
        this.documents = documents;
    }

    public long getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(long totalEntries) {
        this.totalEntries = totalEntries;
    }

    public long getEntries() {
        return this.documents != null ? this.documents.size() : 0;
    }
}
