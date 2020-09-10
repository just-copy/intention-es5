package com.justcp.es5.controller;

import com.justcp.es5.service.EsQueryService;
import com.justcp.es5.service.EsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/es")
public class EsController {

    @Resource
    private EsService esService;

    @Resource
    private EsQueryService esQueryService;

    @PutMapping(value = "/insert")
    public void insertEs() {
        esService.insertBatch();
    }

    @DeleteMapping(value = "/delete")
    public void deleteEs() {
        esService.deleteByQuery();
    }

    @GetMapping(value = "/searchQuery")
    public void searchQuery() {
        esQueryService.searchQuery();
    }
}
