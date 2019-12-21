package com.xin.utils.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: TODO
 * @date 2019-08-29 10:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {

    private Long total;

    private int current;

    private int size;

    private List<T> records = new ArrayList<>();
}
