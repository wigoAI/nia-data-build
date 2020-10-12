package org.moara.nia.data.build.testWorks;

public class OpenChecker {
    private boolean primeOpen = false;
    private boolean doublePrimeOpen = false;

    public void openCheck(String str) {

        for(int i = 0 ; i < str.length() ; i++) {
            char target = str.charAt(i);
            if(target == '\'') primeOpen = !primeOpen;
            else if(target == '\"') doublePrimeOpen = !doublePrimeOpen;
        }

    }

    public boolean isOpen() {
        return primeOpen || doublePrimeOpen;
    }

    public void clean() {
        primeOpen = false;
        doublePrimeOpen = false;
    }
}
