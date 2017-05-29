package eu.leward.elevator

import groovy.transform.CompileStatic

/**
 * Util to trigger async timeouts
 * Code from https://stackoverflow.com/posts/36842856/revisions
 */
@CompileStatic
class Timeout {

    static void setTimeout(Runnable runnable, int delay){
        new Thread({
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

}
