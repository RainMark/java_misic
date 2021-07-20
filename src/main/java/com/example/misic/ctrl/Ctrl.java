package com.example.misic.ctrl;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rain
 * @date 2021/4/14
 */
@RestController
@RequestMapping(value = "/")
@Slf4j
public class Ctrl {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<String> get(@RequestParam(value = "id") String id) {
        log.info("example request");
        return Arrays.asList("Example", id);
    }
}
