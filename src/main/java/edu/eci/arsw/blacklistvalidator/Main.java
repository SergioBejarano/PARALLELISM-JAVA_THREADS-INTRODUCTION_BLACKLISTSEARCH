/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * 
 * @author sergio.bejarano-r
 * @author laura.rsanchez
 */
public class Main {
    
    public static void main(String a[]) {
        HostBlackListsValidator hblv = new HostBlackListsValidator();

        System.out.println("Checking host: 200.24.34.55");
        List<Integer> blackListOccurrences = hblv.checkHost("200.24.34.55", 10);
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);

        System.out.println("Checking host: 202.24.34.55");
        blackListOccurrences = hblv.checkHost("202.24.34.55", 10);
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);

        System.out.println("Checking host: 212.24.24.55");
        blackListOccurrences = hblv.checkHost("212.24.24.55", 10);
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
    }
    
}
