package org.alixar.api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDTO {
    private int page;
    private int itemsPerPage;
    private String order;
    private String orderBy;
    private String search;
    private int totalPages;
}
