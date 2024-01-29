package com.testehan.ecommerce.backend.util.paging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {

    private ModelAndViewContainer model;
    private String listName;
    private String moduleURL;
    private String sortField;
    private String sortOrder;
    private String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String moduleURL, String sortField, String sortOrder, String keyword) {
        this.model = model;
        this.listName = listName;
        this.moduleURL = moduleURL;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.keyword = keyword;
    }

    public void updateModelAttributes(int pageNum, Page<?> page) {

        List<?> listItems = page.getContent();
        int pageSize = page.getSize();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);
        model.addAttribute("moduleURL", moduleURL);

    }

    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repo) {

        Pageable pageable = createPageable(pageSize, pageNum);
        Page<?> page = null;

        if (keyword != null) {
            page = repo.findAll(keyword, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        updateModelAttributes(pageNum, page);
    }

    public Pageable createPageable(int pageSize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        return PageRequest.of(pageNum - 1, pageSize, sort);
    }


    public String getListName() {
        return listName;
    }

    public String getModuleURL() {
        return moduleURL;
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getKeyword() {
        return keyword;
    }
}
