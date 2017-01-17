package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TimesheetGroupItemTest {
    @Test
    public void getId() {
        Date date = new Date();
        long days = date.getTime() / 1000 / 60 / 60 / 24;

        TimesheetGroupItem groupItem = new TimesheetGroupItem(date);

        assertEquals(days, groupItem.getId());
    }
}
