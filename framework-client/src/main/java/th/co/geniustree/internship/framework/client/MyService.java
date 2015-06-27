/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.internship.framework.client;

import th.co.geniustree.internship.framework.Service;
import th.co.geniustree.internship.framework.Transactional;

/**
 *
 * @author pramoth
 */
@Service // Annotation service tell instantiate it and manage it dependency
@Transactional //Framework will add transaction before call method of this class
public class MyService {
    public void callInTx(){
        System.out.println("===================callInTx=================");
    }
}
