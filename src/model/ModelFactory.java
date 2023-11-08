/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import DataTransferObjects.Model;

/**
 * Factoria del modelo.
 *
 * @author bayro
 */
public class ModelFactory {

    private static Model model;

    public static Model getModel() {
        if (model == null) {
            model = new ModelImplementation();
        }
        return model;
    }
}
