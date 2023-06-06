package org.whitefat.unmanned.model;

import lombok.Data;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2023/6/6 6:04 下午
 **/
@Data
public class Book {

    private String id;
    private String name;
    private Integer pageCount;
    private Author author;

}
