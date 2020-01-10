package com.github.anrimian.musicplayer.data.database;

import android.app.Instrumentation;
import android.content.Context;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MigrationsTest {

    private static final String TEST_DB_NAME = "music_player_database";

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);

    private Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    private Context context = instrumentation.getContext();

    @Rule
    public MigrationTestHelper testHelper =
            new MigrationTestHelper(
                    instrumentation,
                    AppDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    private SupportSQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        db = testHelper.createDatabase(TEST_DB_NAME, 1);
        db.close();
    }

    @Test
    public void testMigrationFrom1To2() throws Exception {
        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME,
                2,
                false,
                Migrations.getMigration1_2(context));
    }
}