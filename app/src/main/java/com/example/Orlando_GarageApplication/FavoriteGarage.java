package com.example.Orlando_GarageApplication;

import android.provider.BaseColumns;

public class FavoriteGarage {


    private FavoriteGarage() {
    }

    public static final class FavoriteGarageEntry implements BaseColumns {

        public static final String TABLE_NAME = "favoriteGarageList";
        public static final String COLUMN_PERSONID = "personId";
        public static final String COLUMN_FAVORITETITLE = "favoriteTitle";
        public static final String COLUMN_GARAGENAME = "garageName";
        public static final String COLUMN_ACTIVATIONDATE = "activationDate";


    }
}
