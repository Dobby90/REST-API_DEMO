package me.hycho.demorestapi.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import me.hycho.demorestapi.index.indexController;

public class ErrorsResource extends EntityModel<Errors> {

    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        add(linkTo(methodOn(indexController.class).index()).withRel("index"));
    }
    
}
