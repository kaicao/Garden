package com.kaicao.garden.controllers;

import play.mvc.*;

/**
 * Created by kaicao on 25/10/14.
 */
public class GardenController extends Controller {

    public static Result index() {
        return ok("Hello");
    }
}
