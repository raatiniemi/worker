package me.raatiniemi.worker.presentation.project.model;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class TimesheetGroupModelTest {
    @Test
    public void getId() {
        Date date = new Date();
        TimesheetGroupModel groupModel = new TimesheetGroupModel(date);

        assertEquals(date.getTime(), groupModel.getId());
    }
}
