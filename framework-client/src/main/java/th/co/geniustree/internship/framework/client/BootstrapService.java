/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.internship.framework.client;

import th.co.geniustree.internship.framework.AutoWired;
import th.co.geniustree.internship.framework.PostConstruct;
import th.co.geniustree.internship.framework.Service;

/**
 *
 * @author pramoth
 */
@Service// Annotation service tell instantiate it and manage it dependency
public class BootstrapService {
    @AutoWired
    private MyService myService;
    @PostConstruct
    public void postConstruct(){
        myService.callInTx();
    }
}
