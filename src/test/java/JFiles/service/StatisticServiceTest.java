package JFiles.service;

import JFiles.model.StatisticEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dispatcher-servletForTesting.xml")
public class StatisticServiceTest {

    @Autowired
    @Qualifier(value = "StatisticServiceBean")
    private StatisticService statisticService;

    @Test
    public void getRecordById(){

        StatisticEntity record = statisticService.getRecordById(1);

        Assert.assertNotNull(record);

        System.out.println(record.getId());
        System.out.println(record.getUser());
        System.out.println(record.getVsUser());
        System.out.println(record.getWin());
        System.out.println(record.getLoose());
        System.out.println(record.getEven());
    }

    @Test
    public void getAllRecords(){

        Assert.assertNotNull(statisticService.getAllRecords());

        System.out.println( statisticService.getAllRecords().size());
    }

}