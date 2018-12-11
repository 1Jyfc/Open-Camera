package net.sourceforge.opencamera.test;

import junit.framework.Assert;

import net.sourceforge.opencamera.NewFunction.FavoriteActivity;

public class FavoriteTest {
    FavoriteActivity favoriteActivity;

    @org.junit.Test
    public void test() {
        favoriteActivity = new FavoriteActivity();
        int result = favoriteActivity.getSize();
        Assert.assertEquals(result, -1);
    }
}
