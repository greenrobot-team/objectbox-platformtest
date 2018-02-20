package com.example.objectbox_platformtest;

import android.util.Log;

import com.example.objectbox_platformtest.model.MyObjectBox;

import org.junit.Test;

import java.io.File;

import io.objectbox.BoxStore;
import timber.log.Timber;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CreateTestDatabaseTest {

    @Test
    public void createTestDatabase() throws Exception {
        Timber.plant(new TestTree());

        BoxStore store = MyObjectBox.builder().baseDirectory(new File("tests")).build();

        MainActivity.doObjectBoxThings(store);
    }

    private static class TestTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            String level = null;
            switch (priority) {
                case Log.VERBOSE:
                    level = "VERBOSE";
                    break;
                case Log.DEBUG:
                    level = "DEBUG";
                    break;
                case Log.INFO:
                    level = "INFO";
                    break;
                case Log.WARN:
                    level = "WARN";
                    break;
                case Log.ERROR:
                    level = "ERROR";
                    break;
            }

            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                System.out.println(level + "/" + tag + ": " + message);
            } else {
                System.err.println(level + "/" + tag + ": " + message);
            }
        }
    }

}