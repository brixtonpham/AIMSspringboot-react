package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LPDTO extends ProductDTO {
    private String artist;
    private String recordLabel;
    private String musicType;
    private String releaseDate;
    private String tracklist;
    private Integer rpm;
    private Double sizeInches;
    private String vinylCondition;
    private String sleeveCondition;
}