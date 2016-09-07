package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TimesheetGroupModelTest {
    @Test
    public void getId() {
        Date date = new Date();
        TimesheetGroupModel groupModel = new TimesheetGroupModel(date);

        assertEquals(date.getTime(), groupModel.getId());
    }
}
