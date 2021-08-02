package it.unipd.stage.sl.springrest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Just an empty controller that maps to the index.html file
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {

    // no mapping for the index necessary
}
