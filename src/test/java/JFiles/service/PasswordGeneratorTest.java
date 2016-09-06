package JFiles.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;


public class PasswordGeneratorTest {

    @Test
    public void generate(){

        int passwordLength = 6;

        PasswordGenerator pg = new PasswordGenerator();
        pg.setPasswordLength( passwordLength);
        pg.setAddSpecialChar(true);

        Assert.assertEquals(passwordLength, pg.generate().length());

        //.* any qty of any symbols, \\d - number, $ - and of line
        Assert.assertTrue( Pattern.matches(".*[*&%|/^$#@].*\\d$", pg.generate()) );
        Assert.assertTrue( Pattern.matches(".*[*&%|/^$#@].*\\d$", pg.generate()) );
        Assert.assertTrue( Pattern.matches(".*[*&%|/^$#@].*\\d$", pg.generate()) );

        System.out.println( pg.generate());
        System.out.println( pg.generate());
        System.out.println( pg.generate());
    }

}