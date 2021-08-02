package it.unipd.stage.sl.services;

import io.quarkus.arc.profile.IfBuildProfile;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@IfBuildProfile("dev")
public class TestServiceImpl {

    public String saySomething() {
        return "ciao";
    }
}
